/**
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
package io.rapidw.mqtt.codec.v5;

import io.netty.handler.codec.DecoderException;
import io.rapidw.mqtt.codec.v5.properties.*;
import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class MqttV5ConnectPacket extends MqttV5Packet {

    private boolean cleanStart;
    private int keepAliveSeconds;
    private String clientId;
    private String username;
    private byte[] password;
    private MqttV5Will will;

    private MqttV5Will.Builder willBuilder;

    private boolean usernameFlag;

    private boolean passwordFlag;

    private SessionExpiryInterval sessionExpiryInterval;
    private ReceiveMaximum receiveMaximum;
    private MaximumPacketSize maximumPacketSize;
    private TopicAliasMaximum topicAliasMaximum;
    private RequestResponseInformation requestResponseInformation;
    private RequestProblemInformation requestProblemInformation;
    private UserProperty userProperty;
    private AuthenticationMethod authenticationMethod;
    private AuthenticationData authenticationData;

    MqttV5ConnectPacket(short flags) {
        super(MqttV5PacketType.CONNECT);
        if ((flags & 0x0F) != 0) {
            throw new DecoderException("[MQTT-3.1.2-3] CONNECT packet reversed flag is not zero");
        }
    }

    private MqttV5ConnectPacket() {
        super(MqttV5PacketType.CONNECT);
    }
}
