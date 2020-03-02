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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MqttV311TopicAndQosLevel)) return false;
        final MqttV311TopicAndQosLevel other = (MqttV311TopicAndQosLevel) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$topicFilter = this.getTopicFilter();
        final Object other$topicFilter = other.getTopicFilter();
        if (this$topicFilter == null ? other$topicFilter != null : !this$topicFilter.equals(other$topicFilter))
            return false;
        final Object this$qosLevel = this.getQosLevel();
        final Object other$qosLevel = other.getQosLevel();
        if (this$qosLevel == null ? other$qosLevel != null : !this$qosLevel.equals(other$qosLevel)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MqttV311TopicAndQosLevel;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $topicFilter = this.getTopicFilter();
        result = result * PRIME + ($topicFilter == null ? 43 : $topicFilter.hashCode());
        final Object $qosLevel = this.getQosLevel();
        result = result * PRIME + ($qosLevel == null ? 43 : $qosLevel.hashCode());
        return result;
    }

    public String toString() {
        return "MqttV311TopicAndQosLevel(topicFilter=" + this.getTopicFilter() + ", qosLevel=" + this.getQosLevel() + ")";
    }
}
