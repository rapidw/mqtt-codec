package io.rapidw.mqtt.codec;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class MqttWill {

    @NonNull
    private String topic;
    private MqttQosLevel qosLevel;
    @NonNull
    private byte[] message;
    private boolean retain;
}
