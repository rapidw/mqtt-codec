package io.rapidw.mqtt.codec.v5.properties;

public class RequestResponseInformation extends MqttV5Property {
    public RequestResponseInformation(byte value) {
        super(Type.REQUEST_RESPONSE_INFORMATION, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
