package io.rapidw.mqtt.codec.v5.properties;

public class SharedSubscriptionAvailable extends MqttV5Property {
    public SharedSubscriptionAvailable(byte value) {
        super(Type.SHARED_SUBSCRIPTION_AVAILABLE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
