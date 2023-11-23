/**
 * Copyright 2023 Rapidw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.rapidw.mqtt.codec.utils.DecoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import static io.rapidw.mqtt.codec.utils.DecoderUtils.*;
import static io.rapidw.mqtt.codec.utils.MqttV311ValidationUtils.*;

public class MqttV311Decoder extends ReplayingDecoder<MqttV311Decoder.DecoderState> {

    private static Logger logger = LoggerFactory.getLogger(MqttV311Decoder.class);

    enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD
    }

    private MqttV311Packet packet;
    private short flags;
    private int remainingLength;

    public MqttV311Decoder() {
        super(DecoderState.READ_FIXED_HEADER);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int bytesConsumed = 0;
        switch (state()) {
            case READ_FIXED_HEADER:
                short b1 = in.readUnsignedByte();
                this.flags = (short) (b1 & 0x0F);
                this.remainingLength = DecoderUtils.readRemainingLength(in);
                switch (MqttV311PacketType.of(b1 >> 4)) {
                    case CONNECT:
                        this.packet = new MqttV311ConnectPacket(flags);
                        break;
                    case CONNACK:
                        this.packet = new MqttV311ConnAckPacket(flags, remainingLength);
                        break;
                    case PUBLISH:
                        this.packet = new MqttV311PublishPacket();
                        break;
                    case PUBACK:
                        this.packet = new MqttV311PubAckPacket(flags, remainingLength);
                        break;
                    case PUBREC:
                        this.packet = new MqttV311PubRecPacket(flags, remainingLength);
                        break;
                    case PUBREL:
                        this.packet = new MqttV311PubRelPacket(flags, remainingLength);
                        break;
                    case PUBCOMP:
                        this.packet = new MqttV311PubCompPacket(flags, remainingLength);
                        break;
                    case SUBSCRIBE:
                        this.packet = new MqttV311SubscribePacket(flags);
                        break;
                    case SUBACK:
                        this.packet = new MqttV311SubAckPacket(flags);
                        break;
                    case UNSUBSCRIBE:
                        this.packet = new MqttV311UnsubscribePacket(flags);
                        break;
                    case UNSUBACK:
                        this.packet = new MqttV311UnsubAckPacket(flags, remainingLength);
                        break;
                    case PINGREQ:
                        this.packet = MqttV311PingReqPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload(flags, remainingLength);
                        break;
                    case PINGRESP:
                        this.packet = MqttV311PingRespPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload(flags, remainingLength);
                        break;
                    case DISCONNECT:
                        this.packet = MqttV311DisconnectPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload(flags, remainingLength);
                        break;
                }
                checkpoint(DecoderState.READ_VARIABLE_HEADER);
            case READ_VARIABLE_HEADER:
                switch (this.packet.getType()) {
                    case CONNECT:
                        bytesConsumed = readConnectVariableHeader(in, (MqttV311ConnectPacket) this.packet);
                        break;
                    case CONNACK:
                        bytesConsumed = readConnAckVariableHeader(in, (MqttV311ConnAckPacket) this.packet);
                        break;
                    case PUBLISH:
                        bytesConsumed = readPublishVariableHeader(in, (MqttV311PublishPacket) this.packet);
                        break;
                    case PUBACK:
                        bytesConsumed = readPubAckVariableHeader(in, (MqttV311PubAckPacket) this.packet);
                        break;
                    case SUBSCRIBE:
                        bytesConsumed = readSubscribeVariableHeader(in, (MqttV311SubscribePacket) this.packet);
                        break;
                    case SUBACK:
                        bytesConsumed = readSubAckVariableHeader(in, (MqttV311SubAckPacket) this.packet);
                        break;
                    case UNSUBSCRIBE:
                        bytesConsumed = readUnsubscribeVariableHeader(in, (MqttV311UnsubscribePacket) this.packet);
                        break;
                    case UNSUBACK:
                        bytesConsumed = readUnsubAckVariableHeader(in, (MqttV311UnsubAckPacket) this.packet);
                        break;
                }
                checkpoint(DecoderState.READ_PAYLOAD);
                this.remainingLength -= bytesConsumed;
            case READ_PAYLOAD:
                switch (this.packet.getType()) {
                    case CONNECT:
                        readConnectPayload(in, (MqttV311ConnectPacket) this.packet);
                        break;
                    case PUBLISH:
                        readPublishPayload(in, (MqttV311PublishPacket) this.packet);
                        break;
                    case SUBSCRIBE:
                        readSubscribePayload(in, (MqttV311SubscribePacket) this.packet);
                        break;
                    case SUBACK:
                        readSubAckPayload(in, (MqttV311SubAckPacket) this.packet);
                        break;
                    case UNSUBSCRIBE:
                        readUnsubscribePayload(in, (MqttV311UnsubscribePacket) this.packet);
                        break;
                }
                checkpoint(DecoderState.READ_FIXED_HEADER);
                out.add(this.packet);
        }
    }

    // -------------------------------------------------

    private int readConnectVariableHeader(ByteBuf buf, MqttV311ConnectPacket packet) {
        DecoderUtils.DecodedResult<String> protocolName = readString(buf);
        if (!protocolName.getValue().equals("MQTT")) {
            throw new DecoderException("[MQTT-3.1.2-1] invalid protocol name");
        }

        if (!(buf.readUnsignedByte() == 0x04)) {
            throw new DecoderException("[MQTT-3.1.2-1] invalid protocol level");
        }

        short b = buf.readUnsignedByte();
        packet.setCleanSession(isSet(b, 1));

        boolean usernameFlag = isSet(b, 7);
        boolean passwordFlag = isSet(b, 6);
        if (!usernameFlag && passwordFlag) {
            throw new DecoderException(
                "invalid connect packet: username not present but password present");
        }
        packet.setUsernameFlag(usernameFlag);
        packet.setPasswordFlag(passwordFlag);

        if (isSet(b, 2)) {
            MqttV311Will.Builder willBuilder = MqttV311Will.builder();
            willBuilder.qosLevel(MqttV311QosLevel.of((b & 0x18) >> 3));
            willBuilder.retain(isSet(b, 5));
            packet.setWillBuilder(willBuilder);
        } else if (isSet(b, 3) || isSet(b, 4) || isSet(b, 5)) {
            throw new DecoderException(
                "[MQTT-3.1.2-11] If the Will Flag is set to 0 the Will QoS and Will Retain fields in the Connect Flags MUST be set to zero");
        }

        DecodedResult<Integer> keepaliveSeconds = readMsbLsb(buf);
        packet.setKeepAliveSeconds(keepaliveSeconds.getValue());
        return 10;
    }

    private void readConnectPayload(ByteBuf buf, MqttV311ConnectPacket packet) {
        int bytesConsumed = 0;
        DecodedResult<String> clientId = readString(buf);

        packet.setClientId(clientId.getValue());
        bytesConsumed += clientId.getBytesConsumed();

        MqttV311Will.Builder willBuilder = packet.getWillBuilder();
        if (willBuilder != null) {
            DecodedResult<String> willTopic = readString(buf);
            willBuilder.topic(willTopic.getValue());
            bytesConsumed += willTopic.getBytesConsumed();

            DecodedResult<byte[]> willMessage = readByteArray(buf);
            willBuilder.message(willMessage.getValue());
            bytesConsumed += willMessage.getBytesConsumed();

            packet.setWill(willBuilder.build());
        }
        if (packet.isUsernameFlag()) {
            DecodedResult<String> username = readString(buf);
            packet.setUsername(username.getValue());
            bytesConsumed += username.getBytesConsumed();
        }
        if (packet.isPasswordFlag()) {
            DecodedResult<byte[]> password = readByteArray(buf);
            packet.setPassword(password.getValue());
            bytesConsumed += password.getBytesConsumed();
        }
        if (this.remainingLength - bytesConsumed != 0) {
            throw new DecoderException("invalid remaining length in connect packet");
        }
    }

    private int readConnAckVariableHeader(ByteBuf buf, MqttV311ConnAckPacket packet) {
        short b1 = buf.readUnsignedByte();
        if ((b1 & 0xFE) != 0) {
            throw new DecoderException("invalid conack flags");
        }
        boolean sessionPresent = isSet(b1, 0);
        byte b2 = buf.readByte();
        MqttV311ConnectReturnCode code = MqttV311ConnectReturnCode.of(b2);
        if (code != MqttV311ConnectReturnCode.CONNECTION_ACCEPTED && sessionPresent) {
            throw new DecoderException(
                "[MQTT-3.2.2-4] CONNACK packet containing a non-zero return code it MUST set Session Present to 0");
        }
        packet.setSessionPresent(sessionPresent);
        packet.setConnectReturnCode(code);
        return 2;
    }

    private int readSubAckVariableHeader(ByteBuf buf, MqttV311SubAckPacket packet) {
        DecodedResult<Integer> packetId = readPacketId(buf);
        packet.setPacketId((packetId.getValue()));
        return packetId.getBytesConsumed();
    }

    private void readSubAckPayload(ByteBuf buf, MqttV311SubAckPacket packet) {
        LinkedList<MqttV311QosLevel> qosLevelList = new LinkedList<>();
        for (int i = this.remainingLength; i > 0; i--) {
            qosLevelList.add(MqttV311QosLevel.of(buf.readByte()));
        }
        packet.setQosLevels(qosLevelList);
    }

    private int readPublishVariableHeader(ByteBuf buf, MqttV311PublishPacket packet) {
        int bytesConsumed = 0;
        if (isSet(flags, 3)) {
            packet.setDupFlag(true);
        }
        if (isSet(flags, 0)) {
            packet.setRetain(true);
        }
        MqttV311QosLevel qosLevel = MqttV311QosLevel.of((flags & 0x06) >> 1);
        if (qosLevel == MqttV311QosLevel.AT_MOST_ONCE && packet.isDupFlag()) {
            throw new DecoderException(
                "[MQTT-3.3.1-2] The DUP flag MUST be set to 0 for all QoS 0 messages");
        }
        packet.setQosLevel(qosLevel);

        DecodedResult<String> topic = readString(buf);
        packet.setTopic(topic.getValue());
        bytesConsumed += topic.getBytesConsumed();
        if (packet.getQosLevel() == MqttV311QosLevel.AT_LEAST_ONCE
            || packet.getQosLevel() == MqttV311QosLevel.EXACTLY_ONCE) {
            DecodedResult<Integer> packetId = readPacketId(buf);
            packet.setPacketId(packetId.getValue());
            bytesConsumed += packetId.getBytesConsumed();
        }
        return bytesConsumed;
    }

    private void readPublishPayload(ByteBuf buf, MqttV311PublishPacket packet) {
        byte[] payload = new byte[this.remainingLength];
        buf.readBytes(payload);
        packet.setPayload(payload);
    }

    private int readSubscribeVariableHeader(ByteBuf buf, MqttV311SubscribePacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.getValue());
        return packetId.getBytesConsumed();
    }

    private void readSubscribePayload(ByteBuf buf, MqttV311SubscribePacket packet) {
        int bytesConsumed = 0;
        boolean finish = false;
        while (!finish) {
            DecodedResult<String> topicFilter = readString(buf);
            bytesConsumed += topicFilter.getBytesConsumed();
            short b = buf.readUnsignedByte();
            if ((b & 0xFC) != 0) {
                throw new DecoderException("[MQTT-3-8.3-4] Reserved bits in the payload must be zero");
            }
            bytesConsumed += 1;
            packet.getTopicAndQosLevels().add(new MqttV311TopicAndQosLevel(topicFilter.getValue(), MqttV311QosLevel.of(b & 0x03)));
            if (this.remainingLength - bytesConsumed == 0) {
                finish = true;
            }
            if (remainingLength - bytesConsumed < 0) {
                throw new DecoderException("invalid subscribe remaining length");
            }
        }
    }

    private int readUnsubscribeVariableHeader(ByteBuf buf, MqttV311UnsubscribePacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.getValue());
        return packetId.getBytesConsumed();
    }

    private void readUnsubscribePayload(ByteBuf buf, MqttV311UnsubscribePacket packet) {
        int bytesConsumed = 0;
        List<String> topicFilters = packet.getTopicFilters();
        while (this.remainingLength - bytesConsumed > 0) {
            DecodedResult<String> topicFiler = readString(buf);

            topicFilters.add(validateTopicFilter(topicFiler.getValue()));
            bytesConsumed += topicFiler.getBytesConsumed();
        }

        if (this.remainingLength - bytesConsumed != 0) {
            throw new DecoderException("invalid unsub length");
        }
    }

    private int readUnsubAckVariableHeader(ByteBuf buf, MqttV311UnsubAckPacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.getValue());
        return packetId.getBytesConsumed();
    }

    private int readPubAckVariableHeader(ByteBuf buf, MqttV311PubAckPacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.getValue());
        return packetId.getBytesConsumed();
    }
}
