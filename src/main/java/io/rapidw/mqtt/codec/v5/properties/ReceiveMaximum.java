package io.rapidw.mqtt.codec.v5.properties;

public class ReceiveMaximum extends MqttV5Property {
    public ReceiveMaximum(int value) {
        super(Type.RECEIVE_MAXIMUM, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
