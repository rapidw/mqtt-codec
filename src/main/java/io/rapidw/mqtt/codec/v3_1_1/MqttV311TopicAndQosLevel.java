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

public class MqttV311TopicAndQosLevel {
    private String topicFilter;
    private MqttV311QosLevel qosLevel;

    public MqttV311TopicAndQosLevel(String topicFilter, MqttV311QosLevel qosLevel) {
        this.topicFilter = topicFilter;
        this.qosLevel = qosLevel;
    }

    public String getTopicFilter() {
        return this.topicFilter;
    }

    public MqttV311QosLevel getQosLevel() {
        return this.qosLevel;
    }

    public void setTopicFilter(String topicFilter) {
        this.topicFilter = topicFilter;
    }

    public void setQosLevel(MqttV311QosLevel qosLevel) {
        this.qosLevel = qosLevel;
    }

    public String toString() {
        return "TopicAndQosLevel(topicFilter=" + this.getTopicFilter() + ", qosLevel=" + this.getQosLevel() + ")";
    }
}
