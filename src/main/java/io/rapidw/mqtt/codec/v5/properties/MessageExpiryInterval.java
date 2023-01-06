package io.rapidw.mqtt.codec.v5.properties;

public class MessageExpiryInterval extends MqttV5Property {
    public MessageExpiryInterval(long value) {
        super(Type.MESSAGE_EXPIRY_INTERVAL, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
