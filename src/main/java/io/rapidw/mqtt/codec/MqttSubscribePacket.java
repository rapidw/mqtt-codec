package io.rapidw.mqtt.codec;

import lombok.*;

import java.util.List;

@Getter
@Setter(AccessLevel.MODULE)
public class MqttSubscribePacket extends MqttPacket {

    private int packetId;
    @Singular
    private List<MqttTopicAndQosLevel> mqttTopicAndQosLevels;

    MqttSubscribePacket() {
        super(MqttPacketType.SUBSCRIBE);
    }

    @Builder
    private MqttSubscribePacket(int packetId, List<MqttTopicAndQosLevel> topicAndQosLevels) {
        this();
        this.packetId = packetId;
        this.mqttTopicAndQosLevels = topicAndQosLevels;
    }

}
