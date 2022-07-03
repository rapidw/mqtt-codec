package io.rapidw.mqtt.codec.v5.packet;

import io.netty.handler.codec.DecoderException;
import io.rapidw.mqtt.codec.v5.MqttV5PacketType;

public class MqttV5ConnAckPacket extends MqttV5Packet {
    private boolean sessionPresent;
    private MqttV5ConnectReturnCode connectReturnCode;

    MqttV5ConnAckPacket(short flags, int remainingLength) {
        super(MqttV5PacketType.CONNACK);
        if (flags != 0) {
            throw new DecoderException("invalid CONACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }
    private MqttV5ConnAckPacket() {
        super(MqttV5PacketType.CONNACK);
    }

    private MqttV5ConnAckPacket(boolean sessionPresent, MqttV5ConnectReturnCode connectReturnCode) {
        this();
        this.sessionPresent = sessionPresent;
        this.connectReturnCode = connectReturnCode;
    }

    public static MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder builder() {
        return new MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder();
    }

    public boolean isSessionPresent() {
        return this.sessionPresent;
    }

    public MqttV5ConnectReturnCode getConnectReturnCode() {
        return this.connectReturnCode;
    }

    void setSessionPresent(boolean sessionPresent) {
        this.sessionPresent = sessionPresent;
    }

    void setConnectReturnCode(MqttV5ConnectReturnCode connectReturnCode) {
        this.connectReturnCode = connectReturnCode;
    }

    public static class MqttV5ConnAckPacketBuilder {
        private boolean sessionPresent;
        private MqttV5ConnectReturnCode connectReturnCode;

        MqttV5ConnAckPacketBuilder() {
        }

        public MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder sessionPresent(boolean sessionPresent) {
            this.sessionPresent = sessionPresent;
            return this;
        }

        public MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder connectReturnCode(MqttV5ConnectReturnCode connectReturnCode) {
            this.connectReturnCode = connectReturnCode;
            return this;
        }

        public MqttV5ConnAckPacket build() {
            return new MqttV5ConnAckPacket(sessionPresent, connectReturnCode);
        }

        public String toString() {
            return "MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder(sessionPresent=" + this.sessionPresent + ", connectReturnCode=" + this.connectReturnCode + ")";
        }
    }
}
