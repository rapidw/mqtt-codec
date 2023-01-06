package io.rapidw.mqtt.codec.v5.properties;

public class ContentType extends MqttV5Property {
    public ContentType(String value) {
        super(Type.CONTENT_TYPE, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
