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

public class MqttV311PublishPacket extends MqttV311Packet {

    private boolean dupFlag;
    private MqttV311QosLevel qosLevel;
    private boolean retain;
    private String topic;
    private int packetId;
    private byte[] payload;

    MqttV311PublishPacket() {
        super(MqttV311PacketType.PUBLISH);
    }

    private MqttV311PublishPacket(
        boolean dupFlag,
        MqttV311QosLevel qosLevel,
        boolean retain,
        String topic,
        int packetId,
        byte[] payload) {
        super(MqttV311PacketType.PUBLISH);
        this.dupFlag = dupFlag;
        this.qosLevel = qosLevel;
        this.retain = retain;
        this.topic = topic;
        this.packetId = packetId;
        this.payload = payload;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isDupFlag() {
        return this.dupFlag;
    }

    public MqttV311QosLevel getQosLevel() {
        return this.qosLevel;
    }

    public boolean isRetain() {
        return this.retain;
    }

    public String getTopic() {
        return this.topic;
    }

    public int getPacketId() {
        return this.packetId;
    }

    public byte[] getPayload() {
        return this.payload;
    }

    void setDupFlag(boolean dupFlag) {
        this.dupFlag = dupFlag;
    }

    void setQosLevel(MqttV311QosLevel qosLevel) {
        this.qosLevel = qosLevel;
    }

    void setRetain(boolean retain) {
        this.retain = retain;
    }

    void setTopic(String topic) {
        this.topic = topic;
    }

    void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public static class Builder {
        private boolean dupFlag;
        private MqttV311QosLevel qosLevel;
        private boolean retain;
        private String topic;
        private int packetId;
        private byte[] payload;

        Builder() {
        }

        public Builder dupFlag(boolean dupFlag) {
            this.dupFlag = dupFlag;
            return this;
        }

        public Builder qosLevel(MqttV311QosLevel qosLevel) {
            this.qosLevel = qosLevel;
            return this;
        }

        public Builder retain(boolean retain) {
            this.retain = retain;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder packetId(int packetId) {
            this.packetId = packetId;
            return this;
        }

        public Builder payload(byte[] payload) {
            this.payload = payload;
            return this;
        }

        public MqttV311PublishPacket build() {
            return new MqttV311PublishPacket(dupFlag, qosLevel, retain, topic, packetId, payload);
        }

        public String toString() {
            return "MqttV311PublishPacket.MqttV311PublishPacketBuilder(dupFlag=" + this.dupFlag + ", qosLevel=" + this.qosLevel + ", retain=" + this.retain + ", topic=" + this.topic + ", packetId=" + this.packetId + ", payload=" + java.util.Arrays.toString(this.payload) + ")";
        }
    }
}
