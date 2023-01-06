package io.rapidw.mqtt.codec.v5.properties;

public class SubscriptionIdentifierAvailable extends MqttV5Property {
    public SubscriptionIdentifierAvailable(byte value) {
        super(Type.SUBSCRIPTION_IDENTIFIER_AVAILABLE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
