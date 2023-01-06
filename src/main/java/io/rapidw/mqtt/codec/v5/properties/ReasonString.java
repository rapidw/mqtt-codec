package io.rapidw.mqtt.codec.v5.properties;

public class ReasonString extends MqttV5Property {
    public ReasonString(String value) {
        super(Type.REASON_STRING, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
