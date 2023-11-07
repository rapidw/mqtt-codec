/**
 * Copyright 2023 Rapidw
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
