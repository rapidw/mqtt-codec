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

import java.util.ArrayList;
import java.util.List;

public class MqttV311SubscribePacket extends MqttV311Packet {

    private int packetId;
    private List<MqttV311TopicAndQosLevel> topicAndQosLevels;

    MqttV311SubscribePacket(short flags) {
        super(MqttV311PacketType.SUBSCRIBE);
        if (flags != 2) {
            throw new DecoderException("[MQTT-3.8.1-1] invalid SUBSCRIBE flags");
        }
        this.topicAndQosLevels = new ArrayList<>();
    }

    private MqttV311SubscribePacket(int packetId, List<MqttV311TopicAndQosLevel> topicAndQosLevels) {
        super(MqttV311PacketType.SUBSCRIBE);
        this.packetId = packetId;
        this.topicAndQosLevels = topicAndQosLevels;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    public List<MqttV311TopicAndQosLevel> getTopicAndQosLevels() {
        return this.topicAndQosLevels;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    void setTopicAndQosLevels(List<MqttV311TopicAndQosLevel> mqttV311TopicAndQosLevels) {
        this.topicAndQosLevels = mqttV311TopicAndQosLevels;
    }

    public static class Builder {
        private int packetId;
        private List<MqttV311TopicAndQosLevel> topicAndQosLevels;

        Builder() {
        }

        public Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public Builder topicAndQosLevel(MqttV311TopicAndQosLevel topicAndQosLevels) {
            if (this.topicAndQosLevels == null) {
                this.topicAndQosLevels = new ArrayList<>();
            }
            this.topicAndQosLevels.add(topicAndQosLevels);
            return this;
        }

        public Builder topicAndQosLevels(List<MqttV311TopicAndQosLevel> topicAndQosLevels) {
            if (this.topicAndQosLevels == null) {
                this.topicAndQosLevels = new ArrayList<>();
            }
            this.topicAndQosLevels.addAll(topicAndQosLevels);
            return this;
        }

        public Builder clearTopicAndQosLevels() {
            if (this.topicAndQosLevels != null) {
                this.topicAndQosLevels.clear();
            }
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
