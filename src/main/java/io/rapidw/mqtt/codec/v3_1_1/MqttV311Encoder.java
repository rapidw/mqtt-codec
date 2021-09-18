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
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.rapidw.mqtt.codec.utils.MqttV311ValidationUtils;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@ChannelHandler.Sharable
public class MqttV311Encoder extends MessageToMessageEncoder<MqttV311Packet> {

    public static final MqttV311Encoder INSTANCE = new MqttV311Encoder();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MqttV311Encoder.class);
    private static final byte[] PROTOCOL_NAME_BYTES =
        MqttV311ValidationUtils.validateAndEncodeString("MQTT", "protocol name");

    private MqttV311Encoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MqttV311Packet packet, List<Object> out) {
        out.add(doEncode(ctx.alloc(), packet));
    }

    static ByteBuf doEncode(ByteBufAllocator byteBufAllocator, MqttV311Packet packet) {

        switch (packet.getType()) {
            case CONNECT:
                return encodeConnect(byteBufAllocator, (MqttV311ConnectPacket) packet);
            case CONNACK:
                return encodeConnAck(byteBufAllocator, (MqttV311ConnAckPacket) packet);
            case PUBLISH:
                return encodePublish(byteBufAllocator, (MqttV311PublishPacket) packet);
            case PUBACK:
                return encodePubAck(byteBufAllocator, (MqttV311PubAckPacket) packet);
            case SUBSCRIBE:
                return encodeSubscribe(byteBufAllocator, (MqttV311SubscribePacket) packet);
            case SUBACK:
                return encodeSubAck(byteBufAllocator, (MqttV311SubAckPacket) packet);
            case UNSUBSCRIBE:
                return encodeUnsubscribe(byteBufAllocator, (MqttV311UnsubscribePacket) packet);
            case UNSUBACK:
                return encodeUnsubAck(byteBufAllocator, (MqttV311UnsubAckPacket) packet);
            case PINGREQ:
                return encodePingReq(byteBufAllocator, (MqttV311PingReqPacket) packet);
            case PINGRESP:
                return encodePingResp(byteBufAllocator, (MqttV311PingRespPacket) packet);
            case DISCONNECT:
                return encodeDisconnect(byteBufAllocator, (MqttV311DisconnectPacket) packet);
            default:
                throw new EncoderException("Unknown message type");
        }
    }

    private static ByteBuf encodeConnect(ByteBufAllocator byteBufAllocator, MqttV311ConnectPacket packet) {
        Objects.requireNonNull(packet);
        int variableHeaderSize = 2 + PROTOCOL_NAME_BYTES.length + 1 + 1 + 2;

        byte[] clientIdBytes = MqttV311ValidationUtils.validateAndEncodeString(packet.getClientId(), "client id");
        int clientIdSize = 2 + clientIdBytes.length;

        int willSize = 0;
        byte[] willTopicBytes = null;
        byte[] willMessage = null;

        // will
        MqttV311Will will = packet.getWill();
        if (will != null) {
            willTopicBytes = MqttV311ValidationUtils.validateAndEncodeString(will.getTopic(), "will topic");
            willMessage = MqttV311ValidationUtils.requireNonNull(will.getMessage(), "will message");
            willSize += (2 + willTopicBytes.length);
            willSize += (2 + willMessage.length);
        }

        // username and password

        int usernamePasswordSize = 0;
        byte[] usernameBytes = null;
        byte[] passwordBytes = null;
        String username = packet.getUsername();
        byte[] password = packet.getPassword();
        if (username != null) {
            usernameBytes = MqttV311ValidationUtils.validateAndEncodeString(username, "username");
            usernamePasswordSize += (2 + usernameBytes.length);
            if (password != null) {
                passwordBytes = MqttV311ValidationUtils.validateByteArray(packet.getPassword(), "password");
                usernamePasswordSize += (2 + passwordBytes.length);
            }
        } else {
            MqttV311ValidationUtils.requireNull(packet.getPassword(), "password");
        }

        int variablePartSize = variableHeaderSize + clientIdSize + willSize + usernamePasswordSize;
        int fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variableHeaderSize + variablePartSize);

        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);

        // first byte
        buf.writeByte(0x10);
        writeVariablePartLength(buf, variablePartSize);

        writeStringBytes(buf, PROTOCOL_NAME_BYTES, "protocol name");

        // protocol level
        buf.writeByte(4);

        int b = 0;
        if (packet.isCleanSession()) {
            b |= 0x02;
        }
        if (will != null) {
            b |= 0x04;
            b |= (will.getQosLevel().ordinal() << 3);
            if (will.isRetain()) {
                b |= 0x20;
            }
        }
        if (username != null) {
            b |= 0x40;
        }
        if (password != null) {
            b |= 0x80;
        }
        buf.writeByte(b);

        if (packet.getKeepAliveSeconds() < 0 || packet.getKeepAliveSeconds() > 65535) {
            throw new EncoderException("invalid keepAlive seconds");
        }
        buf.writeShort(packet.getKeepAliveSeconds());

        if (packet.getClientId().length() > 65535) {
            throw new EncoderException("invalid client id");
        }
        writeStringBytes(buf, clientIdBytes, "client id");

        if (will != null) {
            writeStringBytes(buf, willTopicBytes, "will topic");
            writeByteArray(buf, willMessage, "will message");
        }
        if (usernameBytes != null) {
            writeStringBytes(buf, usernameBytes, "username");
        }
        if (passwordBytes != null) {
            writeByteArray(buf, passwordBytes, "password");
        }

        return buf;
    }

    private static ByteBuf encodePingReq(ByteBufAllocator byteBufAllocator, MqttV311PingReqPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(2);
        buf.writeByte(0xC0);
        buf.writeByte(0);
        return buf;
    }

    private static ByteBuf encodeSubscribe(ByteBufAllocator byteBufAllocator, MqttV311SubscribePacket packet) {
        Objects.requireNonNull(packet);

        LinkedList<byte[]> topicFilterBytesList = new LinkedList<>();
        int variablePartSize = 2;
        int payloadCount = 0;
        for (MqttV311TopicAndQosLevel payload :
            MqttV311ValidationUtils.requireNonNull(packet.getTopicAndQosLevels(), "topic and qos list")) {
            byte[] topicFilterBytes =
                MqttV311ValidationUtils.validateAndEncodeTopicFilter(payload.getTopicFilter());
            if (payload.getQosLevel() == MqttV311QosLevel.FAILURE) {
                throw new EncoderException("[MQTT-3.3.1-4] PUBLISH Packet MUST NOT have both QoS bits set to 1");
            }
            topicFilterBytesList.add(topicFilterBytes);
            variablePartSize += (2 + topicFilterBytes.length + 1);
            payloadCount++;
        }
        if (payloadCount == 0) {
            throw new EncoderException(
                "[MQTT-3.8.3-3] The payload of a SUBSCRIBE packet MUST contain at least one Topic Filter / QoS pair");
        }
        int fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);

        buf.writeByte(0x82);
        writeVariablePartLength(buf, variablePartSize);

        buf.writeShort(MqttV311ValidationUtils.validatePacketId(packet.getPacketId()));

        for (int i = 0; i < packet.getTopicAndQosLevels().size(); i++) {
            MqttV311TopicAndQosLevel payload = packet.getTopicAndQosLevels().get(i);
            byte[] topicFilterBytes = topicFilterBytesList.get(i);

            writeStringBytes(buf, topicFilterBytes, "topic filter");
            buf.writeByte(payload.getQosLevel().ordinal());
        }
        return buf;
    }

    private static ByteBuf encodePublish(ByteBufAllocator byteBufAllocator, MqttV311PublishPacket packet) {
        Objects.requireNonNull(packet);
        byte[] topicBytes = MqttV311ValidationUtils.validateAndEncodeTopicName(packet.getTopic());
        MqttV311ValidationUtils.requireNonNull(packet.getPayload(), "payload");

        int variablePartSize = 2 + topicBytes.length + packet.getPayload().length;
        if (packet.getQosLevel() == MqttV311QosLevel.AT_LEAST_ONCE
            || packet.getQosLevel() == MqttV311QosLevel.EXACTLY_ONCE) {
            variablePartSize += 2;
        }
        int fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        int b = 0;
        b |= 0x30;
        if (packet.isRetain()) {
            b |= 0x01;
        }
        b |= (packet.getQosLevel().ordinal() << 1);
        if (packet.isDupFlag()) {
            b |= 0x08;
        }
        buf.writeByte(b);
        writeVariablePartLength(buf, variablePartSize);
        writeStringBytes(buf, topicBytes, "topic name");
        if (packet.getPacketId() != null) {
            if (packet.getQosLevel() == MqttV311QosLevel.AT_LEAST_ONCE || packet.getQosLevel() == MqttV311QosLevel.EXACTLY_ONCE) {
                buf.writeShort(MqttV311ValidationUtils.validatePacketId(packet.getPacketId()));
            } else {
                throw new EncoderException("PacketId is only present in PUBLISH Packets where the QoS level is 1 or 2");
            }
        }
        log.debug("payload: {}", packet.getPayload());
        buf.writeBytes(packet.getPayload());
        return buf;
    }

    private static ByteBuf encodePubAck(ByteBufAllocator byteBufAllocator, MqttV311PubAckPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(4);
        buf.writeByte(0x40);
        buf.writeByte(0x02);
        buf.writeShort(packet.getPacketId());
        return buf;
    }


    private static ByteBuf encodeDisconnect(ByteBufAllocator byteBufAllocator, MqttV311DisconnectPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(2);
        buf.writeByte(0xE0);
        buf.writeByte(0);
        return buf;
    }

    private static ByteBuf encodeConnAck(ByteBufAllocator byteBufAllocator, MqttV311ConnAckPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(4);
        buf.writeByte(0x20);
        buf.writeByte(0x02);
        if (packet.getConnectReturnCode() != MqttV311ConnectReturnCode.CONNECTION_ACCEPTED
            && packet.isSessionPresent()) {
            throw new EncoderException(
                "[MQTT-3.2.2-4] CONNACK packet containing a non-zero return code it MUST set Session Present to 0");
        }
        if (packet.isSessionPresent()) {
            buf.writeByte(0x01);
        } else {
            buf.writeByte(0);
        }
        buf.writeByte(packet.getConnectReturnCode().byteValue());
        return buf;
    }

    private static ByteBuf encodeSubAck(ByteBufAllocator byteBufAllocator, MqttV311SubAckPacket packet) {
        Objects.requireNonNull(packet);
        int variablePartSize = 2 + packet.getQosLevels().size();
        int fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);

        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        buf.writeByte(0x90);
        writeVariablePartLength(buf, variablePartSize);
        buf.writeShort(packet.getPacketId());
        for (MqttV311QosLevel qos : packet.getQosLevels()) {
            buf.writeByte(qos.ordinal());
        }
        return buf;
    }

    private static ByteBuf encodeUnsubscribe(ByteBufAllocator byteBufAllocator, MqttV311UnsubscribePacket packet) {
        Objects.requireNonNull(packet);
        int variablePartSize = 2;

        LinkedList<byte[]> topicFilterBytes = new LinkedList<>();
        for (String topicFilter : packet.getTopicFilters()) {
            byte[] bytes = MqttV311ValidationUtils.validateAndEncodeString(topicFilter, "topic filter");
            variablePartSize += (2 + bytes.length);
            topicFilterBytes.add(bytes);
        }

        int fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        ByteBuf buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        buf.writeByte(0xA2);
        writeVariablePartLength(buf, variablePartSize);
        buf.writeShort(packet.getPacketId());
        for (byte[] bytes : topicFilterBytes) {
            writeStringBytes(buf, bytes, "topic filter");
        }
        return buf;
    }

    private static ByteBuf encodeUnsubAck(ByteBufAllocator byteBufAllocator, MqttV311UnsubAckPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(4);
        buf.writeByte(0xB0);
        buf.writeByte(0x02);
        buf.writeShort(packet.getPacketId());
        return buf;
    }

    private static ByteBuf encodePingResp(ByteBufAllocator byteBufAllocator, MqttV311PingRespPacket packet) {
        Objects.requireNonNull(packet);
        ByteBuf buf = byteBufAllocator.buffer(2);
        buf.writeByte(0xD0);
        buf.writeByte(0);
        return buf;
    }

    // ---------------------------------------------------------------------------------

    private static void writeVariablePartLength(ByteBuf buf, int num) {
        do {
            int digit = num % 128;
            num /= 128;
            if (num > 0) {
                digit |= 0x80;
            }
            buf.writeByte(digit);
        } while (num > 0);
    }

    private static void writeStringBytes(ByteBuf buf, byte[] stringBytes, String name) {
        MqttV311ValidationUtils.requireNonNull(stringBytes, Objects.requireNonNull(name));
        buf.writeShort(stringBytes.length);
        buf.writeBytes(stringBytes);
    }

    private static void writeByteArray(ByteBuf buf, byte[] bytes, String name) {
        MqttV311ValidationUtils.requireNonNull(bytes, Objects.requireNonNull(name));
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    private static int getAndValidateVariablePartLengthSize(int num) {
        int count = 0;
        do {
            num /= 128;
            count++;
        } while (num > 0);
        if (count > 4) {
            throw new IllegalArgumentException("packet too large");
        }
        return count;
    }
}
