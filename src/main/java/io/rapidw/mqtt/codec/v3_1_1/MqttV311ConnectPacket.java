/**
 * Copyright 2023 Rapidw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311ConnectPacket extends MqttV311Packet {

    private boolean cleanSession;
    private int keepAliveSeconds;
    private String clientId;
    private String username;
    private byte[] password;
    private MqttV311Will will;

    private MqttV311Will.Builder willBuilder;

    private boolean usernameFlag;

    private boolean passwordFlag;

    MqttV311ConnectPacket(short flags) {
        super(MqttV311PacketType.CONNECT);
        if ((flags & 0x0F) != 0) {
            throw new DecoderException("[MQTT-3.1.2-3] CONNECT packet reversed flag is not zero");
        }
    }

    private MqttV311ConnectPacket() {
        super(MqttV311PacketType.CONNECT);
    }

    private MqttV311ConnectPacket(boolean cleanSession, String clientId, int keepAliveSeconds, String username,
                                  byte[] password, MqttV311Will will) {
        this();
        this.cleanSession = cleanSession;
        this.clientId = clientId;
        this.keepAliveSeconds = keepAliveSeconds;
        this.username = username;
        this.password = password;
        this.will = will;
    }

    public static MqttV311ConnectPacketBuilder builder() {
        return new MqttV311ConnectPacketBuilder();
    }

    public boolean isCleanSession() {
        return this.cleanSession;
    }

    public int getKeepAliveSeconds() {
        return this.keepAliveSeconds;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getUsername() {
        return this.username;
    }

    public byte[] getPassword() {
        return this.password;
    }

    public MqttV311Will getWill() {
        return this.will;
    }

    void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    void setClientId(String clientId) {
        this.clientId = clientId;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(byte[] password) {
        this.password = password;
    }

    void setWill(MqttV311Will will) {
        this.will = will;
    }

    void setWillBuilder(MqttV311Will.Builder willBuilder) {
        this.willBuilder = willBuilder;
    }

    void setUsernameFlag(boolean usernameFlag) {
        this.usernameFlag = usernameFlag;
    }

    void setPasswordFlag(boolean passwordFlag) {
        this.passwordFlag = passwordFlag;
    }

    MqttV311Will.Builder getWillBuilder() {
        return this.willBuilder;
    }

    boolean isUsernameFlag() {
        return this.usernameFlag;
    }

    boolean isPasswordFlag() {
        return this.passwordFlag;
    }

    public static class MqttV311ConnectPacketBuilder {
        private boolean cleanSession;
        private String clientId;
        private int keepAliveSeconds;
        private String username;
        private byte[] password;
        private MqttV311Will will;

        MqttV311ConnectPacketBuilder() {
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder keepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder password(byte[] password) {
            this.password = password;
            return this;
        }

        public MqttV311ConnectPacket.MqttV311ConnectPacketBuilder will(MqttV311Will will) {
            this.will = will;
            return this;
        }

        public MqttV311ConnectPacket build() {
            return new MqttV311ConnectPacket(cleanSession, clientId, keepAliveSeconds, username, password, will);
        }

        public String toString() {
            return "MqttV311ConnectPacket.MqttV311ConnectPacketBuilder(cleanSession=" + this.cleanSession + ", clientId=" + this.clientId + ", keepaliveSeconds=" + this.keepAliveSeconds + ", username=" + this.username + ", password=" + java.util.Arrays.toString(this.password) + ", will=" + this.will + ")";
        }
    }
}
