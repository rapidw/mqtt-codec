package io.rapidw.mqtt.codec.v5.properties;

public class CorrelationData extends MqttV5Property {
    public CorrelationData(byte[] value) {
        super(Type.CORRELATION_DATA, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
