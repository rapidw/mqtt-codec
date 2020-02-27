package io.rapidw.mqtt.codec;

import lombok.*;

@Setter(AccessLevel.PACKAGE)
@Getter
public class MqttConnectPacket extends MqttPacket {

    private boolean cleanSession;
    private int keepaliveSeconds;
    private String clientId;
    private String username;
    private byte[] password;
    private MqttWill will;

    @Getter(AccessLevel.PACKAGE)
    private MqttWill.MqttWillBuilder willBuilder;
    @Getter(AccessLevel.PACKAGE)
    private boolean usernameFlag;
    @Getter(AccessLevel.PACKAGE)
    private boolean passwordFlag;

    MqttConnectPacket() {
        super(MqttPacketType.CONNECT);
    }

    @Builder
    private MqttConnectPacket(boolean cleanSession, String clientId, int keepaliveSeconds, String username, byte[] password, MqttWill will) {
        this();
        this.cleanSession = cleanSession;
        this.clientId = clientId;
        this.keepaliveSeconds = keepaliveSeconds;
        this.username = username;
        this.password = password;
        this.will = will;
    }
}
