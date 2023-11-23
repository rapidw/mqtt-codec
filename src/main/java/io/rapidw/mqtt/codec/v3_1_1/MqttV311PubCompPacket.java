package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311PubCompPacket extends MqttV311Packet {
    private int packetId;

    MqttV311PubCompPacket(short flags, int remainingLength) {
        super(MqttV311PacketType.PUBCOMP);
        if (flags != 0) {
            throw new DecoderException("invalid PUBACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }

    private MqttV311PubCompPacket() {
        super(MqttV311PacketType.PUBCOMP);
    }

    private MqttV311PubCompPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static MqttV311PubCompPacket.Builder builder() {
        return new MqttV311PubCompPacket.Builder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public static class Builder {
        private int packetId;

        Builder() {
        }

        public MqttV311PubCompPacket.Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311PubCompPacket build() {
            return new MqttV311PubCompPacket(packetId);
        }

        public String toString() {
            return "MqttV311PubCompPacket.MqttV311PubCompPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
