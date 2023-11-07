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
package io.rapidw.mqtt.codec.v5;

import io.netty.handler.codec.DecoderException;

public class MqttV5ConnAckPacket extends MqttV5Packet {
    private boolean sessionPresent;
    private     MqttV5ReasonCode reasonCode;

    MqttV5ConnAckPacket(short flags, int remainingLength) {
        super(MqttV5PacketType.CONNACK);
        if (flags != 0) {
            throw new DecoderException("invalid CONACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }
    private MqttV5ConnAckPacket() {
        super(MqttV5PacketType.CONNACK);
    }

    private MqttV5ConnAckPacket(boolean sessionPresent, MqttV5ReasonCode reasonCode) {
        this();
        this.sessionPresent = sessionPresent;
        this.reasonCode = reasonCode;
    }

    public static MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder builder() {
        return new MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder();
    }

    public boolean isSessionPresent() {
        return this.sessionPresent;
    }

    public MqttV5ReasonCode getReasonCode() {
        return this.reasonCode;
    }

    void setSessionPresent(boolean sessionPresent) {
        this.sessionPresent = sessionPresent;
    }

    void setReasonCode(MqttV5ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    public static class MqttV5ConnAckPacketBuilder {
        private boolean sessionPresent;
        private MqttV5ReasonCode reasonCode;

        MqttV5ConnAckPacketBuilder() {
        }

        public MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder sessionPresent(boolean sessionPresent) {
            this.sessionPresent = sessionPresent;
            return this;
        }

        public MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder connectReturnCode(MqttV5ReasonCode connectReturnCode) {
            this.reasonCode = connectReturnCode;
            return this;
        }

        public MqttV5ConnAckPacket build() {
            return new MqttV5ConnAckPacket(sessionPresent, reasonCode);
        }

        public String toString() {
            return "MqttV5ConnAckPacket.MqttV5ConnAckPacketBuilder(sessionPresent=" + this.sessionPresent + ", connectReturnCode=" + this.reasonCode + ")";
        }
    }
}
