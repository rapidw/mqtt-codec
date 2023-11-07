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
package io.rapidw.jmh.mqtt.codec;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.mqtt.*;
import io.rapidw.mqtt.codec.v3_1_1.MqttV311ConnectPacket;
import io.rapidw.mqtt.codec.v3_1_1.MqttV311Decoder;
import io.rapidw.mqtt.codec.v3_1_1.MqttV311Encoder;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class BenchmarkRunner {
    private static Logger log = LoggerFactory.getLogger(BenchmarkRunner.class);

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }

    private static EmbeddedChannelWriteReleaseHandlerContext nettyEncoderContext =
        new EmbeddedChannelWriteReleaseHandlerContext(
            PooledByteBufAllocator.DEFAULT, MqttEncoder.INSTANCE) {
            @Override
            protected void handleException(Throwable t) {
                log.error("error", t);
            }
        };
    private static EmbeddedChannelWriteReleaseHandlerContext waferEncoderContext =
        new EmbeddedChannelWriteReleaseHandlerContext(
            PooledByteBufAllocator.DEFAULT, MqttV311Encoder.INSTANCE) {
            @Override
            protected void handleException(Throwable t) {
                log.error("error", t);
            }
        };

    private static byte[] bytes =
        ByteBufUtil.decodeHexDump(
            "103900044d51545404ee001e0009636c69656e742069640005746f70696300076d6573736167650008757365726e616d65000870617373776f7264");

    private static MqttConnectMessage nettyPacket = newMqttConnectMessage();

    private static MqttConnectMessage newMqttConnectMessage() {
        MqttFixedHeader fixedHeader =
            new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnectVariableHeader variableHeader =
            new MqttConnectVariableHeader("MQTT", 4, true, true, false, 0, false, true, 0);
        MqttConnectPayload payload =
            new MqttConnectPayload(
                "client", null, null, "username", "password".getBytes(StandardCharsets.UTF_8));
        return new MqttConnectMessage(fixedHeader, variableHeader, payload);
    }

    private static MqttV311ConnectPacket waferPacket = newMqttConnectPacket();

    private static MqttV311ConnectPacket newMqttConnectPacket() {
        return MqttV311ConnectPacket.builder()
            .username("username")
            .password("password".getBytes(StandardCharsets.UTF_8))
            .clientId("client")
            .keepAliveSeconds(0)
            .cleanSession(true)
            .build();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkNettyEncoder() throws Exception {
        MqttEncoder.INSTANCE.write(nettyEncoderContext, nettyPacket, nettyEncoderContext.voidPromise());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkWaferEncoder() throws Exception {
        MqttV311Encoder.INSTANCE.write(
            waferEncoderContext, waferPacket, waferEncoderContext.voidPromise());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkNettyDecoder() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new MqttDecoder());
        channel.writeInbound((Object) bytes);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void benchmarkWaferDecoder() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new MqttV311Decoder());
        channel.writeInbound((Object) bytes);
    }
}
