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

import lombok.*;

@Setter(AccessLevel.PACKAGE)
@Getter
public class MqttConnectPacket extends MqttPacket {

  private boolean cleanSession;
  private int keepaliveSeconds;
  private String clientId;
  private String username;
  private byte[] password;
  private MqttWill will;

  @Getter(AccessLevel.PACKAGE)
  private MqttWill.MqttWillBuilder willBuilder;

  @Getter(AccessLevel.PACKAGE)
  private boolean usernameFlag;

  @Getter(AccessLevel.PACKAGE)
  private boolean passwordFlag;

  MqttConnectPacket() {
    super(MqttPacketType.CONNECT);
  }

  @Builder
  private MqttConnectPacket(
      boolean cleanSession,
      String clientId,
      int keepaliveSeconds,
      String username,
      byte[] password,
      MqttWill will) {
    this();
    this.cleanSession = cleanSession;
    this.clientId = clientId;
    this.keepaliveSeconds = keepaliveSeconds;
    this.username = username;
    this.password = password;
    this.will = will;
  }
}
