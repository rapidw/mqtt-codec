package io.rapidw.mqtt.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MqttConnAckTest {

    @Test
    public void testMqttConnAck() {
        EmbeddedChannel channel = new EmbeddedChannel(MqttEncoder.INSTANCE, new MqttDecoder());

        val packet = MqttConnAckPacket.builder()
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
