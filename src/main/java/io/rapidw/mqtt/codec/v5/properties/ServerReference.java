package io.rapidw.mqtt.codec.v5.properties;

public class ServerReference extends MqttV5Property {
    public ServerReference(String value) {
        super(Type.SERVER_REFERENCE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
