package io.rapidw.mqtt.codec.v5.properties;

public class TopicAlias extends MqttV5Property {
    public TopicAlias(int value) {
        super(Type.TOPIC_ALIAS, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
