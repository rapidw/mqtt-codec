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

public class MqttV311SubAckPacket extends MqttV311Packet {

    private int packetId;
    private List<MqttV311QosLevel> qosLevels;

    MqttV311SubAckPacket(short flags) {
        super(MqttV311PacketType.SUBACK);
        if (flags != 0) {
            throw new DecoderException("invalid SUBACK packet flags");
        }
    }

    private MqttV311SubAckPacket() {
        super(MqttV311PacketType.SUBACK);
    }

    private MqttV311SubAckPacket(int packetId, List<MqttV311QosLevel> qosLevels) {
        this();
        this.packetId = packetId;
        this.qosLevels = qosLevels;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getPacketId() {
        return this.packetId;
    }

    public List<MqttV311QosLevel> getQosLevels() {
        return this.qosLevels;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    void setQosLevels(List<MqttV311QosLevel> qosLevels) {
        this.qosLevels = qosLevels;
    }

    public static class Builder {
        private int packetId;
        private ArrayList<MqttV311QosLevel> qosLevels;

        Builder() {
        }

        public Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public Builder qosLevel(MqttV311QosLevel qosLevel) {
            if (this.qosLevels == null) this.qosLevels = new ArrayList<MqttV311QosLevel>();
            this.qosLevels.add(qosLevel);
            return this;
        }

        public Builder qosLevels(Collection<? extends MqttV311QosLevel> qosLevels) {
            if (this.qosLevels == null) this.qosLevels = new ArrayList<MqttV311QosLevel>();
            this.qosLevels.addAll(qosLevels);
            return this;
        }

        public Builder clearQosLevels() {
            if (this.qosLevels != null)
                this.qosLevels.clear();
            return this;
        }

        public MqttV311SubAckPacket build() {
            List<MqttV311QosLevel> qosLevels;
            switch (this.qosLevels == null ? 0 : this.qosLevels.size()) {
                case 0:
                    qosLevels = java.util.Collections.emptyList();
                    break;
                case 1:
                    qosLevels = java.util.Collections.singletonList(this.qosLevels.get(0));
                    break;
                default:
                    qosLevels = java.util.Collections.unmodifiableList(new ArrayList<MqttV311QosLevel>(this.qosLevels));
            }

            return new MqttV311SubAckPacket(packetId, qosLevels);
        }

        public String toString() {
            return "MqttV311SubAckPacket.MqttV311SubAckPacketBuilder(packetId=" + this.packetId + ", qosLevels=" + this.qosLevels + ")";
        }
    }
}
