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
package io.rapidw.mqtt.codec.v5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.rapidw.mqtt.codec.utils.DecoderUtils;
import io.rapidw.mqtt.codec.v3_1_1.MqttV311QosLevel;

import java.util.List;

import static io.rapidw.mqtt.codec.utils.DecoderUtils.*;

public class MqttV5Decoder extends ReplayingDecoder<MqttV5Decoder.DecoderState> {

    enum DecoderState {
        READ_FIXED_HEADER,
        READ_VARIABLE_HEADER,
        READ_PAYLOAD
    }

    private MqttV5Packet packet;
    private short flags;
    private int remainingLength;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int bytesConsumed = 0;
        switch (state()) {
            case READ_FIXED_HEADER:
                short b1 = in.readUnsignedByte();
                this.flags = (short) (b1 & 0x0F);
                this.remainingLength = DecoderUtils.readRemainingLength(in);
                switch (MqttV5PacketType.of(b1 >> 4)) {
                    case CONNECT:
                        this.packet = new MqttV5ConnectPacket(flags);
                        break;
                    case CONNACK:
                        this.packet = new MqttV5ConnAckPacket(flags, remainingLength);
                        break;
                }
                checkpoint(MqttV5Decoder.DecoderState.READ_VARIABLE_HEADER);

            case READ_VARIABLE_HEADER:
                switch (this.packet.getType()) {
                    case CONNECT:
                        bytesConsumed = readConnectVariableHeader(in, (MqttV5ConnectPacket) this.packet);
                        break;
                }
                checkpoint(MqttV5Decoder.DecoderState.READ_PAYLOAD);
                this.remainingLength -= bytesConsumed;
            case READ_PAYLOAD:
                switch (this.packet.getType()) {

                }
                checkpoint(MqttV5Decoder.DecoderState.READ_FIXED_HEADER);
                out.add(this.packet);
        }
    }

        private int readConnectVariableHeader(ByteBuf buf, MqttV5ConnectPacket packet) {
            DecoderUtils.DecodedResult<String> protocolName = readString(buf);
            if (!protocolName.getValue().equals("MQTT")) {
                throw new DecoderException("[MQTT-3.1.2-1] invalid protocol name");
            }

            if (!(buf.readUnsignedByte() == 0x05)) {
                throw new DecoderException("[MQTT-3.1.2-1] invalid protocol level");
            }

            short b = buf.readUnsignedByte();
            packet.setCleanStart(isSet(b, 1));

            boolean usernameFlag = isSet(b, 7);
            boolean passwordFlag = isSet(b, 6);
            if (!usernameFlag && passwordFlag) {
                throw new DecoderException(
                    "invalid connect packet: username not present but password present");
            }
            packet.setUsernameFlag(usernameFlag);
            packet.setPasswordFlag(passwordFlag);

            if (isSet(b, 2)) {
                MqttV5Will.Builder willBuilder = MqttV5Will.builder();
                willBuilder.qosLevel(MqttV311QosLevel.of((b & 0x18) >> 3));
                willBuilder.retain(isSet(b, 5));
                packet.setWillBuilder(willBuilder);
            } else if (isSet(b, 3) || isSet(b, 4) || isSet(b, 5)) {
                throw new DecoderException(
                    "[MQTT-3.1.2-11] If the Will Flag is set to 0 the Will QoS and Will Retain fields in the Connect Flags MUST be set to zero");
            }

            DecoderUtils.DecodedResult<Integer> keepaliveSeconds = readMsbLsb(buf);
            packet.setKeepAliveSeconds(keepaliveSeconds.getValue());
            return 10;
        }
}
