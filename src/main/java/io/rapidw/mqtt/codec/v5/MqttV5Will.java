package io.rapidw.mqtt.codec.v5;

import io.rapidw.mqtt.codec.v3_1_1.MqttV311QosLevel;

import java.util.Objects;

public class MqttV5Will {
    private final String topic;
    private final MqttV311QosLevel qosLevel;
    private final byte[] message;
    private final boolean retain;

    MqttV5Will(String topic, MqttV311QosLevel qosLevel, byte[] message, boolean retain) {
        this.topic = Objects.requireNonNull(topic);
        this.qosLevel = qosLevel;
        this.message = Objects.requireNonNull(message);
        this.retain = retain;
    }

    public static MqttV5Will.Builder builder() {
        return new MqttV5Will.Builder();
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

        public MqttV5Will.Builder topic(String topic) {
            this.topic = Objects.requireNonNull(topic);
            return this;
        }

        public MqttV5Will.Builder qosLevel(MqttV311QosLevel qosLevel) {
            this.qosLevel = qosLevel;
            return this;
        }

        public MqttV5Will.Builder message(byte[] message) {
            this.message = Objects.requireNonNull(message);
            return this;
        }

        public MqttV5Will.Builder retain(boolean retain) {
            this.retain = retain;
            return this;
        }

        public MqttV5Will build() {
            return new MqttV5Will(topic, qosLevel, message, retain);
        }

        public String toString() {
            return "MqttV311Will.MqttV311WillBuilder(topic=" + this.topic + ", qosLevel=" + this.qosLevel + ", message=" + java.util.Arrays.toString(this.message) + ", retain=" + this.retain + ")";
        }
    }
}
