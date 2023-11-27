package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311PubRecPacket extends MqttV311Packet {
    private int packetId;

    MqttV311PubRecPacket(short flags, int remainingLength) {
        super(MqttV311PacketType.PUBREC);
        if (flags != 0) {
            throw new DecoderException("invalid PUBREC fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONREC remaining length");
        }
    }

    private MqttV311PubRecPacket() {
        super(MqttV311PacketType.PUBREC);
    }

    private MqttV311PubRecPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static MqttV311PubRecPacket.Builder builder() {
        return new MqttV311PubRecPacket.Builder();
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

        public MqttV311PubRecPacket.Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311PubRecPacket build() {
            return new MqttV311PubRecPacket(packetId);
        }

        public String toString() {
            return "MqttV311PubRecPacket.MqttV311PubRecPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
