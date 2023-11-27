package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311PubRelPacket extends MqttV311Packet {
    private int packetId;

    MqttV311PubRelPacket(short flags, int remainingLength) {
        super(MqttV311PacketType.PUBREL);
        if (flags != 0x02) {
            throw new DecoderException("invalid PUBACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }

    private MqttV311PubRelPacket() {
        super(MqttV311PacketType.PUBREL);
    }

    private MqttV311PubRelPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static MqttV311PubRelPacket.Builder builder() {
        return new MqttV311PubRelPacket.Builder();
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

        public MqttV311PubRelPacket.Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311PubRelPacket build() {
            return new MqttV311PubRelPacket(packetId);
        }

        public String toString() {
            return "MqttV311PubRelPacket.MqttV311PubRelPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
