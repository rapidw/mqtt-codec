package io.rapidw.mqtt.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MqttPublishTest {

    @Test
    public void testMqttPublish() {
        val channel = new EmbeddedChannel(MqttEncoder.INSTANCE, new MqttDecoder());

        val packet = MqttPublishPacket.builder()
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
