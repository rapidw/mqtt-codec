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

import java.util.List;

public class MqttV311SubscribePacket extends MqttV311Packet {

    private int packetId;
    private List<MqttV311TopicAndQosLevel> mqttV311TopicAndQosLevels;

    MqttV311SubscribePacket(short flags) {
        super(MqttV311PacketType.SUBSCRIBE);
        if (flags != 2) {
            throw new DecoderException("[MQTT-3.8.1-1] invalid SUBSCRIBE flags");
        }
    }

    private MqttV311SubscribePacket() {
        super(MqttV311PacketType.SUBSCRIBE);
    }

    private MqttV311SubscribePacket(int packetId, List<MqttV311TopicAndQosLevel> topicAndQosLevels) {
        this();
        this.packetId = packetId;
        this.mqttV311TopicAndQosLevels = topicAndQosLevels;
    }

    public static MqttV311SubscribePacketBuilder builder() {
        return new MqttV311SubscribePacketBuilder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    public List<MqttV311TopicAndQosLevel> getMqttV311TopicAndQosLevels() {
        return this.mqttV311TopicAndQosLevels;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    void setMqttV311TopicAndQosLevels(List<MqttV311TopicAndQosLevel> mqttV311TopicAndQosLevels) {
        this.mqttV311TopicAndQosLevels = mqttV311TopicAndQosLevels;
    }

    public static class MqttV311SubscribePacketBuilder {
        private int packetId;
        private List<MqttV311TopicAndQosLevel> topicAndQosLevels;

        MqttV311SubscribePacketBuilder() {
        }

        public MqttV311SubscribePacket.MqttV311SubscribePacketBuilder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311SubscribePacket.MqttV311SubscribePacketBuilder topicAndQosLevels(List<MqttV311TopicAndQosLevel> topicAndQosLevels) {
            this.topicAndQosLevels = topicAndQosLevels;
            return this;
        }

        public MqttV311SubscribePacket build() {
            return new MqttV311SubscribePacket(packetId, topicAndQosLevels);
        }

        public String toString() {
            return "MqttV311SubscribePacket.MqttV311SubscribePacketBuilder(packetId=" + this.packetId + ", topicAndQosLevels=" + this.topicAndQosLevels + ")";
        }
    }
}
