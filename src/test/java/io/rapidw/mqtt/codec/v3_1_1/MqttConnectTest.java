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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class MqttConnectTest {
    private static Logger log = LoggerFactory.getLogger(MqttConnectTest.class);

    @Test
    public void testMqttConnect() {
        EmbeddedChannel channel = new EmbeddedChannel(MqttV311Encoder.INSTANCE, new MqttV311Decoder());

        MqttV311ConnectPacket packet =
            MqttV311ConnectPacket.builder()
                .username("username")
                .password("password".getBytes(StandardCharsets.UTF_8))
                .cleanSession(true)
                .keepAliveSeconds(30)
                .clientId("client id")
                .will(
                    MqttV311Will.builder()
                        .qosLevel(MqttV311QosLevel.AT_LEAST_ONCE)
                        .retain(true)
                        .topic("topic")
                        .message("message".getBytes(StandardCharsets.UTF_8))
                        .build())
                .build();
        Assertions.assertThat(channel.writeOutbound(packet)).isTrue();

        ByteBuf buf = channel.readOutbound();
        log.info("\n{}", ByteBufUtil.prettyHexDump(buf));

        assertThat(channel.writeInbound(buf)).isTrue();
        assertThat(channel.finish()).isTrue();
        MqttV311ConnectPacket packet1 = channel.readInbound();

        assertThat(packet1).isNotNull();
        assertThat(packet1.getUsername()).isEqualTo("username");
        assertThat(packet1.getPassword()).isEqualTo("password".getBytes(StandardCharsets.UTF_8));
        assertThat(packet1.getClientId()).isEqualTo("client id");
        assertThat(packet1.getKeepAliveSeconds()).isEqualTo(30);
        assertThat(packet1.isCleanSession()).isTrue();
        assertThat(packet1.getWill()).isNotNull();
        MqttV311Will will = packet1.getWill();
        assertThat(will.getMessage()).isEqualTo("message".getBytes(StandardCharsets.UTF_8));
        assertThat(will.getQosLevel()).isEqualTo(MqttV311QosLevel.AT_LEAST_ONCE);
        assertThat(will.getTopic()).isEqualTo("topic");
        assertThat(will.isRetain()).isTrue();
    }
}
