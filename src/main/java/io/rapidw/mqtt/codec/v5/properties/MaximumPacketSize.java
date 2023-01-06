package io.rapidw.mqtt.codec.v5.properties;

public class MaximumPacketSize extends MqttV5Property {
    public MaximumPacketSize(long value) {
        super(Type.MAXIMUM_PACKET_SIZE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
