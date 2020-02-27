package io.rapidw.mqtt.codec;

public class MqttPingReqPacket extends MqttPacket {

    public static MqttPingReqPacket INSTANTCE = new MqttPingReqPacket();

    private MqttPingReqPacket() {
        super(MqttPacketType.PINGREQ);
    }
}
