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
import java.util.Collection;
import java.util.List;

public class MqttV311UnsubscribePacket extends MqttV311Packet {

    private int packetId;
    private List<String> topicFilters;

    MqttV311UnsubscribePacket(short flags) {
        super(MqttV311PacketType.UNSUBSCRIBE);
        if (flags != 2) {
            throw new DecoderException("[MQTT-3.10.1-1] invalid unsubscribe flags");
        }
    }

    private MqttV311UnsubscribePacket() {
        super(MqttV311PacketType.UNSUBSCRIBE);
    }

    private MqttV311UnsubscribePacket(List<String> topicFilters, int packetId) {
        this();
        this.topicFilters = topicFilters;
        this.packetId = packetId;
    }

    public static MqttV311UnsubscribePacketBuilder builder() {
        return new MqttV311UnsubscribePacketBuilder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    public List<String> getTopicFilters() {
        return this.topicFilters;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    void setTopicFilters(List<String> topicFilters) {
        this.topicFilters = topicFilters;
    }

    public static class MqttV311UnsubscribePacketBuilder {
        private ArrayList<String> topicFilters;
        private int packetId;

        MqttV311UnsubscribePacketBuilder() {
        }

        public MqttV311UnsubscribePacket.MqttV311UnsubscribePacketBuilder topicFilter(String topicFilter) {
            if (this.topicFilters == null) this.topicFilters = new ArrayList<String>();
            this.topicFilters.add(topicFilter);
            return this;
        }

        public MqttV311UnsubscribePacket.MqttV311UnsubscribePacketBuilder topicFilters(Collection<? extends String> topicFilters) {
            if (this.topicFilters == null) this.topicFilters = new ArrayList<String>();
            this.topicFilters.addAll(topicFilters);
            return this;
        }

        public MqttV311UnsubscribePacket.MqttV311UnsubscribePacketBuilder clearTopicFilters() {
            if (this.topicFilters != null)
                this.topicFilters.clear();
            return this;
        }

        public MqttV311UnsubscribePacket.MqttV311UnsubscribePacketBuilder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public MqttV311UnsubscribePacket build() {
            List<String> topicFilters;
            switch (this.topicFilters == null ? 0 : this.topicFilters.size()) {
                case 0:
                    topicFilters = java.util.Collections.emptyList();
                    break;
                case 1:
                    topicFilters = java.util.Collections.singletonList(this.topicFilters.get(0));
                    break;
                default:
                    topicFilters = java.util.Collections.unmodifiableList(new ArrayList<String>(this.topicFilters));
            }

            return new MqttV311UnsubscribePacket(topicFilters, packetId);
        }

        public String toString() {
            return "MqttV311UnsubscribePacket.MqttV311UnsubscribePacketBuilder(topicFilters=" + this.topicFilters + ", packetId=" + this.packetId + ")";
        }
    }
}
