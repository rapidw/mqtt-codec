package io.rapidw.mqtt.codec;

public class MqttDisconnectPacket extends MqttPacket {

    public static final MqttDisconnectPacket INSTANCE = new MqttDisconnectPacket();

    private MqttDisconnectPacket() {
        super(MqttPacketType.DISCONNECT);
    }
}
