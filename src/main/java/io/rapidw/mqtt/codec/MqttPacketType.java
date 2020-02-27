package io.rapidw.mqtt.codec;

import io.netty.handler.codec.DecoderException;

import java.util.HashMap;
import java.util.Map;

public enum MqttPacketType {

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
    DISCONNECT;

    private static Map<Integer, MqttPacketType> valueMap = new HashMap<>();

    static {
        for (MqttPacketType type: values()) {
            valueMap.put(type.ordinal(), type);
        }
    }

    public static MqttPacketType of(int type) {
        MqttPacketType messageType = valueMap.get(type);
        if (messageType == null) {
            throw new DecoderException("unknown message type: " + type);
        } else {
            return messageType;
        }
    }
}
