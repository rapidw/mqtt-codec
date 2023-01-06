package io.rapidw.mqtt.codec.v5.properties;

public class RequestProblemInformation extends MqttV5Property {
    public RequestProblemInformation(byte value) {
        super(Type.REQUEST_PROBLEM_INFORMATION, value);
    }

    @Override
    protected byte[] getBytes() {
        return new byte[0];
    }
}
