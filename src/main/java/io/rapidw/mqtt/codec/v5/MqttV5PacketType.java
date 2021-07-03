package io.rapidw.mqtt.codec.v5;

import io.netty.handler.codec.DecoderException;
import io.rapidw.mqtt.codec.v3_1_1.MqttV311PacketType;

import java.util.HashMap;
import java.util.Map;

public enum MqttV5PacketType {
    RESERVED,
    CONNECT,
    CONNACK,
    PUBLISH,
    PUBACK,
    PUBREC,
    PUBREL,
    PUBCOMP,
    SUBSCRIBE,
    SUBACK,
    UNSUBSCRIBE,
    UNSUBACK,
    PINGREQ,
    PINGRESP,
    DISCONNECT,
    AUTH;

    private static Map<Integer, MqttV5PacketType> valueMap = new HashMap<>();

    static {
        for (MqttV5PacketType type : values()) {
            valueMap.put(type.ordinal(), type);
        }
    }

    public static MqttV5PacketType of(int type) {
        MqttV5PacketType messageType = valueMap.get(type);
        if (messageType == null) {
            throw new DecoderException("unknown message type: " + type);
        } else {
            return messageType;
        }
    }
}
