package io.rapidw.mqtt.codec.v5.properties;

import java.util.Map;

public class UserProperty extends MqttV5Property {
    public UserProperty(Map<String, String> value) {
        super(Type.USER_PROPERTY, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
