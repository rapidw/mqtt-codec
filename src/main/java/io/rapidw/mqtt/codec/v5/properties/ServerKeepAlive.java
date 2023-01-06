package io.rapidw.mqtt.codec.v5.properties;

public class ServerKeepAlive extends MqttV5Property {
    public ServerKeepAlive(int value) {
        super(Type.SERVER_KEEP_ALIVE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
