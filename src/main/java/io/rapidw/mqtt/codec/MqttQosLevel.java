package io.rapidw.mqtt.codec;

import io.netty.handler.codec.DecoderException;

import java.util.HashMap;
import java.util.Map;

public enum MqttQosLevel {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2),
    FAILURE(0x80);

    private final int value;

    private static Map<Integer, MqttQosLevel> valueMap = new HashMap<>();

    static {
        for (MqttQosLevel qosLevel: values()) {
            valueMap.put(qosLevel.value, qosLevel);
        }
    }

    MqttQosLevel(int value) {
        this.value = value;
    }

    public static MqttQosLevel of(int value) {
        MqttQosLevel qosLevel = valueMap.get(value);
        if (qosLevel == null) {
            throw new DecoderException("invalid QoS: " + value);
        } else {
            return qosLevel;
        }
    }
}
