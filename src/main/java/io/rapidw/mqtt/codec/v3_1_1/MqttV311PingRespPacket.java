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
package io.rapidw.mqtt.codec.v3_1_1;

public class MqttV311PingRespPacket extends MqttV311Packet {

    public static final MqttV311PingRespPacket INSTANCE = new MqttV311PingRespPacket();

    private MqttV311PingRespPacket() {
        super(MqttV311PacketType.PINGRESP);
    }
}
