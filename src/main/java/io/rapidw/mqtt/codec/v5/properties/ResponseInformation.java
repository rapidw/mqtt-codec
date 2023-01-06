package io.rapidw.mqtt.codec.v5.properties;

public class ResponseInformation extends MqttV5Property {
    public ResponseInformation(String value) {
        super(Type.RESPONSE_INFORMATION, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
