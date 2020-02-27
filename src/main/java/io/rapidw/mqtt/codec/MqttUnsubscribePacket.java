package io.rapidw.mqtt.codec;

import lombok.*;

import java.util.List;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttUnsubscribePacket extends MqttPacket {

    private int packetId;
    private List<String> topicFilters;

    MqttUnsubscribePacket() {
        super(MqttPacketType.UNSUBSCRIBE);
    }

    @Builder
    private MqttUnsubscribePacket(@Singular List<String> topicFilters, int packetId) {
        this();
        this.topicFilters = topicFilters;
        this.packetId = packetId;
    }
}
