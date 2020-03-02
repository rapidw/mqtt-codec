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

import java.util.HashMap;
import java.util.Map;

public enum MqttV311PacketType {
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

    private static Map<Integer, MqttV311PacketType> valueMap = new HashMap<>();

    static {
        for (MqttV311PacketType type : values()) {
            valueMap.put(type.ordinal(), type);
        }
    }

    public static MqttV311PacketType of(int type) {
        MqttV311PacketType messageType = valueMap.get(type);
        if (messageType == null) {
            throw new DecoderException("unknown message type: " + type);
        } else {
            return messageType;
        }
    }
}
