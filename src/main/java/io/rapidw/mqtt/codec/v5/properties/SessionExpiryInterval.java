package io.rapidw.mqtt.codec.v5.properties;

public class SessionExpiryInterval extends MqttV5Property {
    public SessionExpiryInterval(int value) {
        super(Type.SESSION_EXPIRY_INTERVAL, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
