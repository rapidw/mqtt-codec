package io.rapidw.mqtt.codec.v5.properties;

public class SubscriptionIdentifier extends MqttV5Property {
    public SubscriptionIdentifier(int value) {
        super(Type.SUBSCRIPTION_IDENTIFIER, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
