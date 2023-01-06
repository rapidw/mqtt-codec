package io.rapidw.mqtt.codec.v5.properties;

public class ResponseTopic extends MqttV5Property {
    public ResponseTopic(String value) {
        super(Type.RESPONSE_TOPIC, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
