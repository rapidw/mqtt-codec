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

public enum MqttConnectReturnCode {
  CONNECTION_ACCEPTED((byte) 0x00),
  CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION((byte) 0X01),
  CONNECTION_REFUSED_IDENTIFIER_REJECTED((byte) 0x02),
  CONNECTION_REFUSED_SERVER_UNAVAILABLE((byte) 0x03),
  CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD((byte) 0x04),
  CONNECTION_REFUSED_NOT_AUTHORIZED((byte) 0x05);

  private static Map<Byte, MqttConnectReturnCode> valueMap = new HashMap<>();

  static {
    for (MqttConnectReturnCode code : values()) {
      valueMap.put(code.byteValue, code);
    }
  }

  private final byte byteValue;

  MqttConnectReturnCode(byte byteValue) {
    this.byteValue = byteValue;
  }

  public byte byteValue() {
    return byteValue;
  }

  public static MqttConnectReturnCode of(byte b) {
    MqttConnectReturnCode code = valueMap.get(b);
    if (!valueMap.containsKey(b)) {
      throw new DecoderException("unknown connect return code: " + (b & 0xFF));
    } else {
      return code;
    }
  }
}
