package io.rapidw.mqtt.codec.v5.properties;

public class WildcardSubscriptionAvailable extends MqttV5Property {
    public WildcardSubscriptionAvailable(byte value) {
        super(Type.WILDCARD_SUBSCRIPTION_AVAILABLE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
