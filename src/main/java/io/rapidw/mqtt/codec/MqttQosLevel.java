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
    for (MqttQosLevel qosLevel : values()) {
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
