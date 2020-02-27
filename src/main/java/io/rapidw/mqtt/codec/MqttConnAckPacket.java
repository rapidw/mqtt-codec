package io.rapidw.mqtt.codec;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttConnAckPacket extends MqttPacket {
    private boolean sessionPresent;
    private MqttConnectReturnCode connectReturnCode;

    MqttConnAckPacket() {
        super(MqttPacketType.CONNACK);
    }

    @Builder
    private MqttConnAckPacket(boolean sessionPresent, MqttConnectReturnCode connectReturnCode) {
        this();
        this.sessionPresent = sessionPresent;
        this.connectReturnCode = connectReturnCode;
    }
}
