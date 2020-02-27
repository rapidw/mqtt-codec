package io.rapidw.jmh.mqtt.codec;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.*;
import io.rapidw.mqtt.codec.MqttConnectPacket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.nio.charset.StandardCharsets;

@Slf4j
public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }

    private static EmbeddedChannelWriteReleaseHandlerContext nettyEncoderContext = new EmbeddedChannelWriteReleaseHandlerContext(PooledByteBufAllocator.DEFAULT, MqttEncoder.INSTANCE) {
        @Override
        protected void handleException(Throwable t) {
            log.error("error", t);
        }
    };
    private static EmbeddedChannelWriteReleaseHandlerContext waferEncoderContext = new EmbeddedChannelWriteReleaseHandlerContext(PooledByteBufAllocator.DEFAULT, io.rapidw.mqtt.codec.MqttEncoder.INSTANCE) {
        @Override
        protected void handleException(Throwable t) {
            log.error("error", t);
        }
    };

    private static byte[] bytes = ByteBufUtil.decodeHexDump("103900044d51545404ee001e0009636c69656e742069640005746f70696300076d6573736167650008757365726e616d65000870617373776f7264");

    private static MqttConnectMessage nettyPacket = newMqttConnectMessage();

    private static MqttConnectMessage newMqttConnectMessage() {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader variableHeader = new MqttConnectVariableHeader("MQTT", 4, true, true, false, 0, false, true, 0);
        MqttConnectPayload payload = new MqttConnectPayload("client", null, null, "username", "password".getBytes(StandardCharsets.UTF_8));
        return new MqttConnectMessage(fixedHeader, variableHeader, payload);
    }

    private static MqttConnectPacket waferPacket = newMqttConnectPacket();

    private static MqttConnectPacket newMqttConnectPacket() {
        return MqttConnectPacket.builder()
            .username("username")
            .password("password".getBytes(StandardCharsets.UTF_8))
            .clientId("client")
            .keepaliveSeconds(0)
            .cleanSession(true)
            .build();
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @SneakyThrows
    public void benchmarkNettyEncoder() {
        MqttEncoder.INSTANCE.write(nettyEncoderContext, nettyPacket, nettyEncoderContext.voidPromise());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @SneakyThrows
    public void benchmarkWaferEncoder() {
        io.rapidw.mqtt.codec.MqttEncoder.INSTANCE.write(waferEncoderContext, waferPacket, waferEncoderContext.voidPromise());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkNettyDecoder() {
        val channel = new EmbeddedChannel(new MqttDecoder());
        channel.writeInbound((Object) bytes);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkWaferDecoder() {
        val channel = new EmbeddedChannel(new io.rapidw.mqtt.codec.MqttDecoder());
        channel.writeInbound((Object) bytes);
    }
}
