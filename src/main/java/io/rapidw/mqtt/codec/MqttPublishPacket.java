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

@Getter
@Setter(AccessLevel.PACKAGE)
public class MqttPublishPacket extends MqttPacket {

  private boolean dupFlag;
  private MqttQosLevel qosLevel;
  private boolean retain;
  private String topic;
  private int packetId;
  private byte[] payload;

  MqttPublishPacket() {
    super(MqttPacketType.PUBLISH);
  }

  @Builder
  private MqttPublishPacket(
      boolean dupFlag,
      MqttQosLevel qosLevel,
      boolean retain,
      String topic,
      int packetId,
      byte[] payload) {
    super(MqttPacketType.PUBLISH);
    this.dupFlag = dupFlag;
    this.qosLevel = qosLevel;
    this.retain = retain;
    this.topic = topic;
    this.packetId = packetId;
    this.payload = payload;
  }
}
