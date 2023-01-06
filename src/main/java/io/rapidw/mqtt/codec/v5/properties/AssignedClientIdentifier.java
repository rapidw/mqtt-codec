package io.rapidw.mqtt.codec.v5.properties;

public class AssignedClientIdentifier extends MqttV5Property {
    public AssignedClientIdentifier(String value) {
        super(Type.ASSIGNED_CLIENT_IDENTIFIER, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
