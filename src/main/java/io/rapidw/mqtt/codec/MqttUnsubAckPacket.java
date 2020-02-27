package io.rapidw.mqtt.codec;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttUnsubAckPacket extends MqttPacket {

    private int packetId;

    MqttUnsubAckPacket() {
        super(MqttPacketType.UNSUBACK);
    }

    @Builder
    private MqttUnsubAckPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

}
