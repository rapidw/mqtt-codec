package io.rapidw.mqtt.codec.v5.properties;

public class AuthenticationMethod extends MqttV5Property {
    public AuthenticationMethod(String value) {
        super(Type.AUTHENTICATION_METHOD, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
