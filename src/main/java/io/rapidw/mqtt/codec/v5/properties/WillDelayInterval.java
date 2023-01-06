package io.rapidw.mqtt.codec.v5.properties;

public class WillDelayInterval extends MqttV5Property {
    public WillDelayInterval(long value) {
        super(Type.WILL_DELAY_INTERVAL, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
