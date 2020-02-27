package io.rapidw.mqtt.codec;

import lombok.*;

import java.util.List;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttSubAckPacket extends MqttPacket {

    private int packetId;
    private List<MqttQosLevel> qosLevels;

    MqttSubAckPacket() {
        super(MqttPacketType.SUBACK);
    }

    @Builder
    private MqttSubAckPacket(int packetId, @Singular List<MqttQosLevel> qosLevels) {
        this();
        this.packetId = packetId;
        this.qosLevels = qosLevels;
    }
}
