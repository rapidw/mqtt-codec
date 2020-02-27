package io.rapidw.mqtt.codec;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqttTopicAndQosLevel {
    private String topicFilter;
    private MqttQosLevel qosLevel;
}
