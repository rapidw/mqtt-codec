package io.rapidw.mqtt.codec;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttPacket {
    protected MqttPacket(MqttPacketType type) {
        this.type = type;
    }

    @Getter
    protected MqttPacketType type;
}
