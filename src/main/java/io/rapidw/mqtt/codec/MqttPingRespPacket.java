package io.rapidw.mqtt.codec;

public class MqttPingRespPacket extends MqttPacket {

    public final static MqttPingRespPacket INSTANCE = new MqttPingRespPacket();

    private MqttPingRespPacket() {
        super(MqttPacketType.PINGRESP);
    }
}
