package io.rapidw.mqtt.codec.v5.properties;

public class MaximumQos extends MqttV5Property {
    public MaximumQos(byte value) {
        super(Type.MAXIMUM_QOS, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
