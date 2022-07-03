/**
 * Copyright 2020 Rapidw
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
package io.rapidw.mqtt.codec.v5.packet;

import io.netty.handler.codec.DecoderException;
import io.rapidw.mqtt.codec.v5.MqttV5PacketType;
import io.rapidw.mqtt.codec.v5.MqttV5Will;

public class MqttV5ConnectPacket extends MqttV5Packet {

    private boolean cleanStart;
    private int keepAliveSeconds;
    private String clientId;
    private String username;
    private byte[] password;
    private MqttV5Will will;

    private MqttV5Will.Builder willBuilder;

    private boolean usernameFlag;

    private boolean passwordFlag;

    MqttV5ConnectPacket(short flags) {
        super(MqttV5PacketType.CONNECT);
        if ((flags & 0x0F) != 0) {
            throw new DecoderException("[MQTT-3.1.2-3] CONNECT packet reversed flag is not zero");
        }
    }

    private MqttV5ConnectPacket() {
        super(MqttV5PacketType.CONNECT);
    }

    private MqttV5ConnectPacket(boolean cleanStart, String clientId, int keepAliveSeconds, String username,
                                byte[] password, MqttV5Will will) {
        this();
        this.cleanStart = cleanStart;
        this.clientId = clientId;
        this.keepAliveSeconds = keepAliveSeconds;
        this.username = username;
        this.password = password;
        this.will = will;
    }

    public static MqttV5ConnectPacket.MqttV5ConnectPacketBuilder builder() {
        return new MqttV5ConnectPacket.MqttV5ConnectPacketBuilder();
    }

    public boolean isCleanStart() {
        return this.cleanStart;
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

    public MqttV5Will getWill() {
        return this.will;
    }

    void setCleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
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

    void setWill(MqttV5Will will) {
        this.will = will;
    }

    void setWillBuilder(MqttV5Will.Builder willBuilder) {
        this.willBuilder = willBuilder;
    }

    void setUsernameFlag(boolean usernameFlag) {
        this.usernameFlag = usernameFlag;
    }

    void setPasswordFlag(boolean passwordFlag) {
        this.passwordFlag = passwordFlag;
    }

    MqttV5Will.Builder getWillBuilder() {
        return this.willBuilder;
    }

    boolean isUsernameFlag() {
        return this.usernameFlag;
    }

    boolean isPasswordFlag() {
        return this.passwordFlag;
    }

    public static class MqttV5ConnectPacketBuilder {
        private boolean cleanSession;
        private String clientId;
        private int keepAliveSeconds;
        private String username;
        private byte[] password;
        private MqttV5Will will;

        MqttV5ConnectPacketBuilder() {
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder keepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
            return this;
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder password(byte[] password) {
            this.password = password;
            return this;
        }

        public MqttV5ConnectPacket.MqttV5ConnectPacketBuilder will(MqttV5Will will) {
            this.will = will;
            return this;
        }

        public MqttV5ConnectPacket build() {
            return new MqttV5ConnectPacket(cleanSession, clientId, keepAliveSeconds, username, password, will);
        }

        public String toString() {
            return "MqttV5ConnectPacket.MqttV5ConnectPacketBuilder(cleanSession=" + this.cleanSession + ", clientId=" + this.clientId + ", keepaliveSeconds=" + this.keepAliveSeconds + ", username=" + this.username + ", password=" + java.util.Arrays.toString(this.password) + ", will=" + this.will + ")";
        }
    }
}
