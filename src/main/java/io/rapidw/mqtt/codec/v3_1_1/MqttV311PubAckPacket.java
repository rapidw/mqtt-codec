package io.rapidw.mqtt.codec.v3_1_1;

public class MqttV311PubAckPacket extends MqttV311Packet {

    private int packetId;

    MqttV311PubAckPacket() {
        super(MqttV311PacketType.PUBACK);
    }

    private MqttV311PubAckPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static MqttV311PubAckPacketBuilder builder() {
        return new MqttV311PubAckPacketBuilder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public static class MqttV311PubAckPacketBuilder {
        private int packetId;

        MqttV311PubAckPacketBuilder() {
        }

        public MqttV311PubAckPacket.MqttV311PubAckPacketBuilder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311PubAckPacket build() {
            return new MqttV311PubAckPacket(packetId);
        }

        public String toString() {
            return "MqttV311PubAckPacket.MqttV311PubAckPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
