package io.rapidw.mqtt.codec.v5.properties;

public class RetainAvailable extends MqttV5Property {
    public RetainAvailable(byte value) {
        super(Type.RETAIN_AVAILABLE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
