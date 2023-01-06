package io.rapidw.mqtt.codec.v5.properties;

public class TopicAliasMaximum extends MqttV5Property {
    public TopicAliasMaximum(int value) {
        super(Type.TOPIC_ALIAS_MAXIMUM, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
