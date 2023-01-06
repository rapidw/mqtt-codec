package io.rapidw.mqtt.codec.v5.properties;

public class AuthenticationData extends MqttV5Property {
    public AuthenticationData(byte[] value) {
        super(Type.AUTHENTICATION_DATA, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
