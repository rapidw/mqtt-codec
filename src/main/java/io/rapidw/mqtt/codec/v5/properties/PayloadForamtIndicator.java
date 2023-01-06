package io.rapidw.mqtt.codec.v5.properties;

public class PayloadForamtIndicator extends MqttV5Property {

    public enum Type {
        BYTES,
        UTF8
    }

    public PayloadForamtIndicator(Type type) {
        super(MqttV5Property.Type.PAYLOAD_TYPE_INDICATOR, type);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
