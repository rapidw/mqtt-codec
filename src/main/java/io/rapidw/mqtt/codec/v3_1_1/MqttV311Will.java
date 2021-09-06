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

import java.util.Objects;

public class MqttV311Will {

    private String topic;
    private MqttV311QosLevel qosLevel;
    private byte[] message;
    private boolean retain;

    MqttV311Will(String topic, MqttV311QosLevel qosLevel, byte[] message, boolean retain) {
        this.topic = Objects.requireNonNull(topic);
        this.qosLevel = qosLevel;
        this.message = Objects.requireNonNull(message);
        this.retain = retain;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTopic() {
        return this.topic;
    }

    public MqttV311QosLevel getQosLevel() {
        return this.qosLevel;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public boolean isRetain() {
        return this.retain;
    }

    public static class Builder {
        private String topic;
        private MqttV311QosLevel qosLevel;
        private byte[] message;
        private boolean retain;

        Builder() {
        }

        public Builder topic(String topic) {
            this.topic = Objects.requireNonNull(topic);
            return this;
        }

        public Builder qosLevel(MqttV311QosLevel qosLevel) {
            this.qosLevel = qosLevel;
            return this;
        }

        public Builder message(byte[] message) {
            this.message = Objects.requireNonNull(message);
            return this;
        }

        public Builder retain(boolean retain) {
            this.retain = retain;
            return this;
        }

        public MqttV311Will build() {
            return new MqttV311Will(topic, qosLevel, message, retain);
        }

        public String toString() {
            return "MqttV311Will.MqttV311WillBuilder(topic=" + this.topic + ", qosLevel=" + this.qosLevel + ", message=" + java.util.Arrays.toString(this.message) + ", retain=" + this.retain + ")";
        }
    }
}
