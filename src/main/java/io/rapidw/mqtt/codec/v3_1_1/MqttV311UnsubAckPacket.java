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

public class MqttV311UnsubAckPacket extends MqttV311Packet {

    private int packetId;

    MqttV311UnsubAckPacket() {
        super(MqttV311PacketType.UNSUBACK);
    }

    private MqttV311UnsubAckPacket(int packetId) {
        this();
        this.packetId = packetId;
    }

    public static MqttV311UnsubAckPacketBuilder builder() {
        return new MqttV311UnsubAckPacketBuilder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public static class MqttV311UnsubAckPacketBuilder {
        private int packetId;

        MqttV311UnsubAckPacketBuilder() {
        }

        public MqttV311UnsubAckPacket.MqttV311UnsubAckPacketBuilder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311UnsubAckPacket build() {
            return new MqttV311UnsubAckPacket(packetId);
        }

        public String toString() {
            return "MqttV311UnsubAckPacket.MqttV311UnsubAckPacketBuilder(packetId=" + this.packetId + ")";
        }
    }
}
