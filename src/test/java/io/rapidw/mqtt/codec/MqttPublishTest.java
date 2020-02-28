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

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class MqttPublishTest {

  @Test
  public void testMqttPublish() {
    val channel = new EmbeddedChannel(MqttEncoder.INSTANCE, new MqttDecoder());

    val packet =
        MqttPublishPacket.builder()
            .dupFlag(true)
            .packetId(1)
            .payload("payload".getBytes(StandardCharsets.UTF_8))
            .qosLevel(MqttQosLevel.EXACTLY_ONCE)
            .retain(true)
            .topic("topic")
            .build();

    Assertions.assertThat(channel.writeOutbound(packet)).isTrue();

    ByteBuf buf = channel.readOutbound();
    log.info("\n{}", ByteBufUtil.prettyHexDump(buf));

    assertThat(channel.writeInbound(buf)).isTrue();
    assertThat(channel.finish()).isTrue();
    MqttPublishPacket packet1 = channel.readInbound();

    assertThat(packet1).isNotNull();
    assertThat(packet1.getQosLevel()).isEqualTo(MqttQosLevel.EXACTLY_ONCE);
    assertThat(packet1.getTopic()).isEqualTo("topic");
    assertThat(packet1.getPayload()).isEqualTo("payload".getBytes(StandardCharsets.UTF_8));
    assertThat(packet1.getPacketId()).isEqualTo(1);
    assertThat(packet1.isDupFlag()).isTrue();
    assertThat(packet1.isRetain()).isTrue();
  }
}
