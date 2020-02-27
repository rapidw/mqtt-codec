package io.rapidw.mqtt.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class MqttEncoder extends MessageToMessageEncoder<MqttPacket> {

    public static final MqttEncoder INSTANCE = new MqttEncoder();
    private static byte[] PROTOCOL_NAME_BYTES = ValidationUtils.validateAndEncodeString("MQTT", "protocol name");

    private MqttEncoder() { }

    @Override
    protected void encode(ChannelHandlerContext ctx, MqttPacket packet, List<Object> out) {
        out.add(doEncode(ctx.alloc(), packet));
    }

    static ByteBuf doEncode(ByteBufAllocator byteBufAllocator, MqttPacket packet) {

        switch (packet.getType()) {
            case CONNECT:
                return encodeConnect(byteBufAllocator, (MqttConnectPacket) packet);
            case CONNACK:
                return encodeConnAck(byteBufAllocator, (MqttConnAckPacket) packet);
            case PUBLISH:
                return encodePublish(byteBufAllocator, (MqttPublishPacket) packet);
            case SUBSCRIBE:
                return encodeSubscribe(byteBufAllocator, (MqttSubscribePacket) packet);
            case SUBACK:
                return encodeSubAck(byteBufAllocator, (MqttSubAckPacket) packet);
            case UNSUBSCRIBE:
                return encodeUnsubscribe(byteBufAllocator, (MqttUnsubscribePacket) packet);
            case UNSUBACK:
                return encodeUnsubAck(byteBufAllocator, (MqttUnsubAckPacket) packet);
            case PINGREQ:
                return encodePingReq(byteBufAllocator, (MqttPingReqPacket) packet);
            case PINGRESP:
                return encodePingResp(byteBufAllocator, (MqttPingRespPacket) packet);
            case DISCONNECT:
                return encodeDisconnect(byteBufAllocator, (MqttDisconnectPacket) packet);
            default:
                throw new EncoderException("Unknown message type");
        }
    }

    private static ByteBuf encodeConnect(ByteBufAllocator byteBufAllocator, @NonNull MqttConnectPacket packet) {
        int variableHeaderSize = 2 + PROTOCOL_NAME_BYTES.length + 1 + 1 + 2;

        val clientIdBytes = ValidationUtils.validateAndEncodeString(packet.getClientId(), "client id");
        val clientIdSize = 2 + clientIdBytes.length;

        var willSize = 0;
        byte[] willTopicBytes = null;
        byte[] willMessage = null;

        // will
        val will = packet.getWill();
        if (will != null) {
            willTopicBytes = ValidationUtils.validateAndEncodeString(will.getTopic(), "will topic");
            willMessage = ValidationUtils.requireNonNull(will.getMessage(), "will message");
            willSize += (2 + willTopicBytes.length);
            willSize += (2 + willMessage.length);
        }

        // username and password

        var usernamePasswordSize = 0;
        byte[] usernameBytes = null;
        byte[] passwordBytes = null;
        val username = packet.getUsername();
        val password = packet.getPassword();
        if (username != null) {
            usernameBytes = ValidationUtils.validateAndEncodeString(username, "username");
            usernamePasswordSize += (2 + usernameBytes.length);
            if (password != null) {
                passwordBytes = ValidationUtils.validateByteArray(packet.getPassword(), "password");
                usernamePasswordSize += (2 + passwordBytes.length);
            }
        } else {
            ValidationUtils.requireNull(packet.getPassword(), "password");
        }

        val variablePartSize = variableHeaderSize + clientIdSize + willSize + usernamePasswordSize;
        val fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variableHeaderSize + variablePartSize);

        val buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);

        // first byte
        buf.writeByte(0x10);
        writeVariablePartLength(buf, variablePartSize);

        writeStringBytes(buf, PROTOCOL_NAME_BYTES, "protocol name");

        // protocol level
        buf.writeByte(4);

        var b = 0;
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

        if (packet.getKeepaliveSeconds() < 0 || packet.getKeepaliveSeconds() > 65535) {
            throw new EncoderException("invalid keepalive seconds");
        }
        buf.writeShort(packet.getKeepaliveSeconds());

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

    private static ByteBuf encodePingReq(ByteBufAllocator byteBufAllocator, @NonNull MqttPingReqPacket packet) {
        val buf = byteBufAllocator.buffer(2);
        buf.writeByte(0xC0);
        buf.writeByte(0);
        return buf;
    }

    private static ByteBuf encodeSubscribe(ByteBufAllocator byteBufAllocator, @NonNull MqttSubscribePacket packet) {
        val topicFilterBytesList = new LinkedList<byte[]>();
        var variablePartSize = 2;
        var payloadCount = 0;
        for (MqttTopicAndQosLevel payload : ValidationUtils.requireNonNull(packet.getMqttTopicAndQosLevels(), "topic and qos list")) {
            byte[] topicFilterBytes = ValidationUtils.validateAndEncodeTopicFilter(payload.getTopicFilter());
            if (payload.getQosLevel() == MqttQosLevel.FAILURE) {
                throw new EncoderException("[MQTT-3.3.1-4] PUBLISH Packet MUST NOT have both QoS bits set to 1");
            }
            topicFilterBytesList.add(topicFilterBytes);
            variablePartSize += (2 + topicFilterBytes.length + 1);
            payloadCount++;
        }
        if (payloadCount == 0) {
            throw new EncoderException("[MQTT-3.8.3-3] The payload of a SUBSCRIBE packet MUST contain at least one Topic Filter / QoS pair");
        }
        val fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        val buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);

        buf.writeByte(0x82);
        writeVariablePartLength(buf, variablePartSize);

        buf.writeShort(ValidationUtils.validatePacketId(packet.getPacketId()));

        for (int i = 0; i < packet.getMqttTopicAndQosLevels().size(); i++) {
            MqttTopicAndQosLevel payload = packet.getMqttTopicAndQosLevels().get(i);
            byte[] topicFilterBytes = topicFilterBytesList.get(i);

            writeStringBytes(buf, topicFilterBytes, "topic filter");
            buf.writeByte(payload.getQosLevel().ordinal());
        }
        return buf;
    }

    private static ByteBuf encodePublish(ByteBufAllocator byteBufAllocator, @NonNull MqttPublishPacket packet) {
        val topicBytes = ValidationUtils.validateAndEncodeTopicName(packet.getTopic());
        ValidationUtils.requireNonNull(packet.getPayload(), "payload");

        var variablePartSize = 2 + topicBytes.length + packet.getPayload().length;
        if (packet.getQosLevel() == MqttQosLevel.AT_LEAST_ONCE || packet.getQosLevel() == MqttQosLevel.EXACTLY_ONCE) {
            variablePartSize += 2;
        }
        val fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        val buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        var b = 0;
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
        if (packet.getQosLevel() == MqttQosLevel.AT_LEAST_ONCE || packet.getQosLevel() == MqttQosLevel.EXACTLY_ONCE) {
            buf.writeShort(ValidationUtils.validatePacketId(packet.getPacketId()));
        }
        log.debug("payload: {}", packet.getPayload());
        buf.writeBytes(packet.getPayload());
        return buf;
    }


    private static ByteBuf encodeDisconnect(ByteBufAllocator byteBufAllocator, @NonNull MqttDisconnectPacket packet) {
        val buf = byteBufAllocator.buffer(2);
        buf.writeByte(0xE0);
        buf.writeByte(0);
        return buf;
    }

    private static ByteBuf encodeConnAck(ByteBufAllocator byteBufAllocator, @NonNull MqttConnAckPacket packet) {
        val buf = byteBufAllocator.buffer(4);
        buf.writeByte(0x20);
        buf.writeByte(0x02);
        if (packet.getConnectReturnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED && packet.isSessionPresent()) {
            throw new EncoderException("[MQTT-3.2.2-4] CONNACK packet containing a non-zero return code it MUST set Session Present to 0");
        }
        if (packet.isSessionPresent()) {
            buf.writeByte(0x01);
        } else {
            buf.writeByte(0);
        }
        buf.writeByte(packet.getConnectReturnCode().byteValue());
        return buf;
    }

    private static ByteBuf encodeSubAck(ByteBufAllocator byteBufAllocator, @NonNull MqttSubAckPacket packet) {
        val variablePartSize = 2 + packet.getQosLevels().size();
        val fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);

        val buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        buf.writeByte(0x90);
        writeVariablePartLength(buf, variablePartSize);
        buf.writeShort(packet.getPacketId());
        for (val qos : packet.getQosLevels()) {
            buf.writeByte(qos.ordinal());
        }
        return buf;
    }

    private static ByteBuf encodeUnsubscribe(ByteBufAllocator byteBufAllocator, @NonNull MqttUnsubscribePacket packet) {
        var variablePartSize = 2;

        val topicFilterBytes = new LinkedList<byte[]>();
        for (val topicFilter : packet.getTopicFilters()) {
            val bytes = ValidationUtils.validateAndEncodeString(topicFilter, "topic filter");
            variablePartSize += (2 + bytes.length);
            topicFilterBytes.add(bytes);
        }

        val fixedHeaderSize = 1 + getAndValidateVariablePartLengthSize(variablePartSize);
        val buf = byteBufAllocator.buffer(fixedHeaderSize + variablePartSize);
        buf.writeByte(0xA2);
        writeVariablePartLength(buf, variablePartSize);
        buf.writeShort(packet.getPacketId());
        for (byte[] bytes : topicFilterBytes) {
            writeStringBytes(buf, bytes, "topic filter");
        }
        return buf;
    }

    private static ByteBuf encodeUnsubAck(ByteBufAllocator byteBufAllocator, @NonNull MqttUnsubAckPacket packet) {
        val buf = byteBufAllocator.buffer(4);
        buf.writeByte(0xB0);
        buf.writeByte(0x02);
        buf.writeShort(packet.getPacketId());
        return buf;
    }

    private static ByteBuf encodePingResp(ByteBufAllocator byteBufAllocator, @NonNull MqttPingRespPacket packet) {
        val buf = byteBufAllocator.buffer(2);
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

    private static void writeStringBytes(ByteBuf buf, byte[] stringBytes, @NonNull String name) {
        ValidationUtils.requireNonNull(stringBytes, name);
        buf.writeShort(stringBytes.length);
        buf.writeBytes(stringBytes);
    }


    private static void writeByteArray(ByteBuf buf, byte[] bytes, @NonNull String name) {
        ValidationUtils.requireNonNull(bytes, name);
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
