/*
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
package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311ConnAckPacket extends MqttV311Packet {
    private boolean sessionPresent;
    private MqttV311ConnectReturnCode connectReturnCode;

    MqttV311ConnAckPacket(short flags, int remainingLength) {
        super(MqttV311PacketType.CONNACK);
        if (flags != 0) {
            throw new DecoderException("invalid CONACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }
    private MqttV311ConnAckPacket() {
        super(MqttV311PacketType.CONNACK);
    }

    private MqttV311ConnAckPacket(boolean sessionPresent, MqttV311ConnectReturnCode connectReturnCode) {
        this();
        this.sessionPresent = sessionPresent;
        this.connectReturnCode = connectReturnCode;
    }

    public static MqttV311ConnAckPacketBuilder builder() {
        return new MqttV311ConnAckPacketBuilder();
    }

    public boolean isSessionPresent() {
        return this.sessionPresent;
    }

    public MqttV311ConnectReturnCode getConnectReturnCode() {
        return this.connectReturnCode;
    }

    void setSessionPresent(boolean sessionPresent) {
        this.sessionPresent = sessionPresent;
    }

    void setConnectReturnCode(MqttV311ConnectReturnCode connectReturnCode) {
        this.connectReturnCode = connectReturnCode;
    }

    public static class MqttV311ConnAckPacketBuilder {
        private boolean sessionPresent;
        private MqttV311ConnectReturnCode connectReturnCode;

        MqttV311ConnAckPacketBuilder() {
        }

        public MqttV311ConnAckPacket.MqttV311ConnAckPacketBuilder sessionPresent(boolean sessionPresent) {
            this.sessionPresent = sessionPresent;
            return this;
        }

        public MqttV311ConnAckPacket.MqttV311ConnAckPacketBuilder connectReturnCode(MqttV311ConnectReturnCode connectReturnCode) {
            this.connectReturnCode = connectReturnCode;
            return this;
        }

        public MqttV311ConnAckPacket build() {
            return new MqttV311ConnAckPacket(sessionPresent, connectReturnCode);
        }

        @Override
        public String toString() {
            return "MqttV311ConnAckPacket.MqttV311ConnAckPacketBuilder(sessionPresent=" + this.sessionPresent + ", connectReturnCode=" + this.connectReturnCode + ")";
        }
    }
}
