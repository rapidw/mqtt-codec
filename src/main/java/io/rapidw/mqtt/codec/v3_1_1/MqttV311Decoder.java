/*
 * Copyright 2020 Rapidw
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

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static io.rapidw.mqtt.codec.v3_1_1.MqttV311ValidationUtils.validatePacketId;
import static io.rapidw.mqtt.codec.v3_1_1.MqttV311ValidationUtils.validateTopicFilter;

public class MqttV311Decoder extends ReplayingDecoder<MqttV311Decoder.DecoderState> {

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
        switch (state()) {
            case READ_FIXED_HEADER:
                short b1 = in.readUnsignedByte();
                this.flags = (short) (b1 & 0x0F);
                this.remainingLength = readRemainingLength(in);
                switch (MqttV311PacketType.of(b1 >> 4)) {
                    case CONNECT:
                        this.packet = new MqttV311ConnectPacket();
                        validateConnect();
                        break;
                    case CONNACK:
                        this.packet = new MqttV311ConnAckPacket();
                        validateConnAck();
                        break;
                    case PUBLISH:
                        this.packet = new MqttV311PublishPacket();
                        // PUBLISH packet does not need to validate
                        break;
                    case PUBACK:
                        this.packet = new MqttV311PubAckPacket();
                        validatePubAck();
                        break;
                    case SUBSCRIBE:
                        this.packet = new MqttV311SubscribePacket();
                        validateSubscribe();
                        break;
                    case SUBACK:
                        this.packet = new MqttV311SubAckPacket();
                        validateSubAck();
                        break;
                    case UNSUBSCRIBE:
                        this.packet = new MqttV311UnsubscribePacket();
                        validateUnsubscribe();
                        break;
                    case UNSUBACK:
                        this.packet = new MqttV311UnsubAckPacket();
                        validateUnsubAck();
                        break;
                    case PINGREQ:
                        this.packet = MqttV311PingReqPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload();
                        break;
                    case PINGRESP:
                        this.packet = MqttV311PingRespPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload();
                        break;
                    case DISCONNECT:
                        this.packet = MqttV311DisconnectPacket.INSTANCE;
                        validatePacketWithoutVariableHeaderAndPayload();
                        break;
                }
                checkpoint(DecoderState.READ_VARIABLE_HEADER);
            case READ_VARIABLE_HEADER:
                switch (this.packet.getType()) {
                    case CONNECT:
                        readConnectVariableHeader(in, (MqttV311ConnectPacket) this.packet);
                        break;
                    case CONNACK:
                        readConnAckVariableHeader(in, (MqttV311ConnAckPacket) this.packet);
                        break;
                    case PUBLISH:
                        readPublishVariableHeader(in, (MqttV311PublishPacket) this.packet);
                        break;
                    case PUBACK:
                        readPubAckVariableHeader(in, (MqttV311PubAckPacket) this.packet);
                        break;
                    case SUBSCRIBE:
                        readSubscribeVariableHeader(in, (MqttV311SubscribePacket) this.packet);
                        break;
                    case SUBACK:
                        readSubAckVariableHeader(in, (MqttV311SubAckPacket) this.packet);
                        break;
                    case UNSUBSCRIBE:
                        readUnsubscribeVariableHeader(in, (MqttV311UnsubscribePacket) this.packet);
                        break;
                    case UNSUBACK:
                        readUnsubAckVariableHeader(in, (MqttV311UnsubAckPacket) this.packet);
                        break;
                }
                checkpoint(DecoderState.READ_PAYLOAD);
            case READ_PAYLOAD:
                switch (this.packet.getType()) {
                    case CONNECT:
                        readConnectPayload(in, (MqttV311ConnectPacket) this.packet);
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

    // --------------------------------------------------------------

    private void validatePacketWithoutVariableHeaderAndPayload() {
        if (this.flags != 0 || this.remainingLength != 0) {
            throw new DecoderException("invalid packet without varheader and payload");
        }
    }

    private void validateConnect() {
        if ((flags & 0x0F) != 0) {
            throw new DecoderException("[MQTT-3.1.2-3] CONNECT packet reversed flag is not zero");
        }
    }

    private void validateConnAck() {
        if (flags != 0) {
            throw new DecoderException("invalid CONACK fixed header flags");
        }
        if (this.remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }

    private void validatePubAck() {
        if (flags != 0) {
            throw new DecoderException("invalid PUBACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }

    private void validateSubscribe() {
        if (this.flags != 2) {
            throw new DecoderException("[MQTT-3.8.1-1] invalid SUBSCRIBE flags");
        }
    }

    private void validateSubAck() {
        if (this.flags != 0) {
            throw new DecoderException("invalid SUBACK packet flags");
        }
    }

    private void validateUnsubscribe() {
        if (this.flags != 2) {
            throw new DecoderException("[MQTT-3.10.1-1] invalid unsubscribe flags");
        }
    }

    private void validateUnsubAck() {
        if (this.flags != 0) {
            throw new DecoderException("invalid unsuback flags");
        }
        if (this.remainingLength != 2) {
            throw new DecoderException("invalid unsuback remaining length");
        }
    }

    // -------------------------------------------------

    private void readConnectVariableHeader(ByteBuf buf, MqttV311ConnectPacket packet) {
        DecodedResult<String> protocolName = readString(buf);
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
            MqttV311Will.MqttV311WillBuilder willBuilder = MqttV311Will.builder();
            willBuilder.qosLevel(MqttV311QosLevel.of((b & 0x18) >> 3));
            willBuilder.retain(isSet(b, 5));
            packet.setWillBuilder(willBuilder);
        } else if (isSet(b, 3) || isSet(b, 4) || isSet(b, 5)) {
            throw new DecoderException(
                "[MQTT-3.1.2-11] If the Will Flag is set to 0 the Will QoS and Will Retain fields in the Connect Flags MUST be set to zero");
        }

        DecodedResult<Integer> keepaliveSeconds = readMsbLsb(buf);
        packet.setKeepaliveSeconds(keepaliveSeconds.getValue());
        this.remainingLength -= 10;
    }

    private void readConnectPayload(ByteBuf buf, MqttV311ConnectPacket packet) {
        DecodedResult<String> clientId = readString(buf);

        packet.setClientId(clientId.value);
        this.remainingLength -= clientId.bytesConsumed;

        MqttV311Will.MqttV311WillBuilder willBuilder = packet.getWillBuilder();
        if (willBuilder != null) {
            DecodedResult<String> willTopic = readString(buf);
            willBuilder.topic(willTopic.value);
            this.remainingLength -= willTopic.bytesConsumed;

            DecodedResult<byte[]> willMessage = readByteArray(buf);
            willBuilder.message(willMessage.value);
            this.remainingLength -= willMessage.bytesConsumed;

            packet.setWill(willBuilder.build());
        }
        if (packet.isUsernameFlag()) {
            DecodedResult<String> username = readString(buf);
            packet.setUsername(username.value);
            this.remainingLength -= username.bytesConsumed;
        }
        if (packet.isPasswordFlag()) {
            DecodedResult<byte[]> password = readByteArray(buf);
            packet.setPassword(password.value);
            this.remainingLength -= password.bytesConsumed;
        }
        if (this.remainingLength != 0) {
            throw new DecoderException("invalid remaining length in connect packet");
        }
    }

    private void readConnAckVariableHeader(ByteBuf buf, MqttV311ConnAckPacket packet) {
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
    }

    private void readSubAckVariableHeader(ByteBuf buf, MqttV311SubAckPacket packet) {
        DecodedResult<Integer> packetId = readPacketId(buf);
        this.remainingLength -= packetId.bytesConsumed;
        packet.setPacketId((packetId.value));
    }

    private void readSubAckPayload(ByteBuf buf, MqttV311SubAckPacket packet) {
        LinkedList<MqttV311QosLevel> qosLevelList = new LinkedList<>();
        for (int i = this.remainingLength; i > 0; i--) {
            qosLevelList.add(MqttV311QosLevel.of(buf.readByte()));
        }
        packet.setQosLevels(qosLevelList);
    }

    private void readPublishVariableHeader(ByteBuf buf, MqttV311PublishPacket packet) {
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
        packet.setTopic(topic.value);
        this.remainingLength -= topic.bytesConsumed;
        if (packet.getQosLevel() == MqttV311QosLevel.AT_LEAST_ONCE
            || packet.getQosLevel() == MqttV311QosLevel.EXACTLY_ONCE) {
            DecodedResult<Integer> packetId = readPacketId(buf);
            packet.setPacketId(packetId.getValue());
            this.remainingLength -= packetId.bytesConsumed;
        }

        byte[] payload = new byte[this.remainingLength];
        buf.readBytes(payload);
        packet.setPayload(payload);
    }

    private void readSubscribeVariableHeader(ByteBuf buf, MqttV311SubscribePacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.getValue());
        this.remainingLength -= packetId.bytesConsumed;
    }

    private void readSubscribePayload(ByteBuf buf, MqttV311SubscribePacket packet) {
        boolean finish = false;
        while (!finish) {
            DecodedResult<String> topicFilter = readString(buf);
            this.remainingLength -= topicFilter.bytesConsumed;
            short b = buf.readUnsignedByte();
            if ((b & 0xFC) != 0) {
                throw new DecoderException("[MQTT-3-8.3-4] Reserved bits in the payload must be zero");
            }
            this.remainingLength -= 1;
            packet
                .getMqttV311TopicAndQosLevels()
                .add(new MqttV311TopicAndQosLevel(topicFilter.getValue(), MqttV311QosLevel.of(b & 0x03)));
            if (remainingLength == 0) {
                finish = true;
            }
            if (remainingLength < 0) {
                throw new DecoderException("invalid subscribe remaining length");
            }
        }
    }

    private void readUnsubscribeVariableHeader(ByteBuf buf, MqttV311UnsubscribePacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.value);
        this.remainingLength -= packetId.bytesConsumed;
    }

    private void readUnsubscribePayload(ByteBuf buf, MqttV311UnsubscribePacket packet) {
        List<String> topicFilters = packet.getTopicFilters();
        while (this.remainingLength > 0) {
            DecodedResult<String> topicFiler = readString(buf);

            topicFilters.add(validateTopicFilter(topicFiler.value));
            this.remainingLength -= topicFiler.bytesConsumed;
        }

        if (this.remainingLength != 0) {
            throw new DecoderException("invalid unsub length");
        }
    }

    private void readUnsubAckVariableHeader(ByteBuf buf, MqttV311UnsubAckPacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.value);
    }

    private void readPubAckVariableHeader(ByteBuf buf, MqttV311PubAckPacket packet) {
        DecodedResult<Integer> packetId = readMsbLsb(buf);
        packet.setPacketId(packetId.value);
    }

    // ------------------------------------------------------------------------------------------------

    private static DecodedResult<String> readString(ByteBuf buffer) {
        DecodedResult<Integer> decodedSize = readMsbLsb(buffer);
        int size = decodedSize.value;
        int bytesConsumed = decodedSize.bytesConsumed;

        String s = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_8);
        buffer.skipBytes(size);
        bytesConsumed += size;
        return new DecodedResult<>(s, bytesConsumed);
    }

    private static DecodedResult<Integer> readMsbLsb(ByteBuf buffer) {
        short msbSize = buffer.readUnsignedByte();
        short lsbSize = buffer.readUnsignedByte();
        int bytesConsumed = 2;
        int result = msbSize << 8 | lsbSize;
        if (result < 0 || result > 65535) {
            throw new DecoderException("invalid MSB LSB value: " + result);
        }
        return new DecodedResult<>(result, bytesConsumed);
    }

    private static DecodedResult<byte[]> readByteArray(ByteBuf buffer) {
        DecodedResult<Integer> decodedSize = readMsbLsb(buffer);
        int size = decodedSize.value;
        byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        return new DecodedResult<>(bytes, decodedSize.bytesConsumed + size);
    }

    private static DecodedResult<Integer> readPacketId(ByteBuf buffer) {
        final DecodedResult<Integer> packetId = readMsbLsb(buffer);
        validatePacketId(packetId.getValue());
        return packetId;
    }

    private static boolean isSet(short b, int pos) {
        return (b & (1 << pos)) != 0;
    }

    private int readRemainingLength(ByteBuf buf) {
        int remainingLength = 0;
        int multiplier = 1;
        short digit;
        int loops = 0;
        do {
            digit = buf.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            loops++;
        } while ((digit & 128) != 0 && loops < 4);

        // MQTT protocol limits Remaining Length to 4 bytes
        if (loops == 4 && (digit & 128) != 0) {
            throw new DecoderException("remaining length exceeds 4 digits");
        }
        return remainingLength;
    }

    // -------------------------------------------------------------------------------------

    private static final class DecodedResult<T> {

        public static DecodedResult<Void> EMPTY = new DecodedResult<>(null, 0);

        private final T value;
        private final int bytesConsumed;

        public DecodedResult(T value, int bytesConsumed) {
            this.value = value;
            this.bytesConsumed = bytesConsumed;
        }

        public T getValue() {
            return value;
        }

        public int getBytesConsumed() {
            return bytesConsumed;
        }
    }
}
