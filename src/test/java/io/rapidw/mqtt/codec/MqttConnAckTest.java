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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class MqttConnAckTest {

  @Test
  public void testMqttConnAck() {
    EmbeddedChannel channel = new EmbeddedChannel(MqttEncoder.INSTANCE, new MqttDecoder());

    val packet =
        MqttConnAckPacket.builder()
            .connectReturnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
            .sessionPresent(true)
            .build();

    Assertions.assertThat(channel.writeOutbound(packet)).isTrue();

    ByteBuf buf = channel.readOutbound();
    log.info("\n{}", ByteBufUtil.prettyHexDump(buf));

    assertThat(channel.writeInbound(buf)).isTrue();
    assertThat(channel.finish()).isTrue();
    MqttConnAckPacket packet1 = channel.readInbound();

    assertThat(packet1).isNotNull();
    assertThat(packet1.getConnectReturnCode()).isEqualTo(MqttConnectReturnCode.CONNECTION_ACCEPTED);
    assertThat(packet1.isSessionPresent()).isTrue();
  }
}
