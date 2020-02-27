package io.rapidw.mqtt.codec;

import lombok.*;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttPublishPacket extends MqttPacket {

    private boolean dupFlag;
    private MqttQosLevel qosLevel;
    private boolean retain;
    private String topic;
    private int packetId;
    private byte[] payload;

    MqttPublishPacket() {
        super(MqttPacketType.PUBLISH);
    }

    @Builder
    private MqttPublishPacket(boolean dupFlag, MqttQosLevel qosLevel, boolean retain, String topic, int packetId, byte[] payload) {
        super(MqttPacketType.PUBLISH);
        this.dupFlag = dupFlag;
        this.qosLevel = qosLevel;
        this.retain = retain;
        this.topic = topic;
        this.packetId = packetId;
        this.payload = payload;
    }
}
