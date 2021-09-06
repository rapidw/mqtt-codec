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
package io.rapidw.mqtt.codec.v3_1_1;

import io.netty.handler.codec.DecoderException;

public class MqttV311PubAckPacket extends MqttV311Packet {

    private int packetId;

    MqttV311PubAckPacket(short flags, int remainingLength) {
        super(MqttV311PacketType.PUBACK);
        if (flags != 0) {
            throw new DecoderException("invalid PUBACK fixed header flags");
        }
        if (remainingLength != 2) {
            throw new DecoderException("invalid CONACK remaining length");
        }
    }

    private MqttV311PubAckPacket() {
        super(MqttV311PacketType.PUBACK);
    }

    private MqttV311PubAckPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public static class Builder {
        private int packetId;

        Builder() {
        }

        public Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311PubAckPacket build() {
            return new MqttV311PubAckPacket(packetId);
        }

        public String toString() {
            return "MqttV311PubAckPacket.MqttV311PubAckPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
