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

import static io.rapidw.mqtt.codec.ValidationUtils.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import lombok.var;

public class MqttDecoder extends ReplayingDecoder<MqttDecoder.DecoderState> {

  enum DecoderState {
    READ_FIXED_HEADER,
    READ_VARIABLE_HEADER,
    READ_PAYLOAD
  }

  private MqttPacket packet;
  private short flags;
  private int remainingLength;

  public MqttDecoder() {
    super(DecoderState.READ_FIXED_HEADER);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    switch (state()) {
      case READ_FIXED_HEADER:
        short b1 = in.readUnsignedByte();
        this.flags = (short) (b1 & 0x0F);
        this.remainingLength = readRemainingLength(in);
        switch (MqttPacketType.of(b1 >> 4)) {
          case CONNECT:
            this.packet = new MqttConnectPacket();
            validateConnect();
            break;
          case CONNACK:
            this.packet = new MqttConnAckPacket();
            validateConnAck();
            break;
          case PUBLISH:
            this.packet = new MqttPublishPacket();
            break;
          case SUBSCRIBE:
            this.packet = new MqttSubscribePacket();
            break;
          case SUBACK:
            this.packet = new MqttSubAckPacket();
            validateSubAck();
            break;
          case UNSUBSCRIBE:
            this.packet = new MqttUnsubscribePacket();
            validateUnsubscribe();
            break;
          case UNSUBACK:
            this.packet = new MqttUnsubAckPacket();
            validateUnsubAck();
            break;
          case PINGREQ:
            this.packet = MqttPingReqPacket.INSTANTCE;
            validatePacketWithoutVariableHeaderAndPayload();
            break;
          case PINGRESP:
            this.packet = MqttPingRespPacket.INSTANCE;
            validatePacketWithoutVariableHeaderAndPayload();
            break;
          case DISCONNECT:
            this.packet = MqttDisconnectPacket.INSTANCE;
            validatePacketWithoutVariableHeaderAndPayload();
            break;
        }
        checkpoint(DecoderState.READ_VARIABLE_HEADER);
      case READ_VARIABLE_HEADER:
        switch (this.packet.getType()) {
          case CONNECT:
            readConnectVariableHeader(in, (MqttConnectPacket) this.packet);
            break;
          case CONNACK:
            readConnAckVariableHeader(in, (MqttConnAckPacket) this.packet);
            break;
          case PUBLISH:
            readPublishVariableHeader(in, (MqttPublishPacket) this.packet);
            break;
          case SUBSCRIBE:
            readSubscribeVariableHeader(in, (MqttSubscribePacket) this.packet);
            break;
          case SUBACK:
            readSubAckVariableHeader(in, (MqttSubAckPacket) this.packet);
            break;
          case UNSUBSCRIBE:
            readUnsubscribeVariableHeader(in, (MqttUnsubscribePacket) this.packet);
            break;
          case UNSUBACK:
            readUnsubAckVariableHeader(in, (MqttUnsubAckPacket) this.packet);
            break;
        }
        checkpoint(DecoderState.READ_PAYLOAD);
      case READ_PAYLOAD:
        switch (this.packet.getType()) {
          case CONNECT:
            readConnectPayload(in, (MqttConnectPacket) this.packet);
            break;
          case SUBSCRIBE:
            readSubscribePayload(in, (MqttSubscribePacket) this.packet);
            break;
          case SUBACK:
            readSubAckPayload(in, (MqttSubAckPacket) this.packet);
            break;
          case UNSUBSCRIBE:
            readUnsubscribePayload(in, (MqttUnsubscribePacket) this.packet);
            break;
        }
        checkpoint(DecoderState.READ_FIXED_HEADER);
        out.add(this.packet);
    }
  }

  // --------------------------------------------------------------

  private void validatePacketWithoutVariableHeaderAndPayload() {
    if (this.flags != 0 || this.remainingLength != 0) {
      throw new DecoderException("invalid packet without varheader and payload");
    }
  }

  private void validateConnect() {
    if ((flags & 0x0F) != 0) {
      throw new DecoderException("[MQTT-3.1.2-3] connect packet reversed flag is not zero");
    }
  }

  private void validateConnAck() {
    if (flags != 0) {
      throw new DecoderException("invalid conack fixedheader flags");
    }
    if (this.remainingLength != 2) {
      throw new DecoderException("invalid conack remaining length");
    }
  }

  private void validateSubscribe() {
    if (this.flags != 2) {
      throw new DecoderException("[MQTT-3.8.1-1] invalid subscribe flags");
    }
  }

  private void validateSubAck() {
    if (this.flags != 0) {
      throw new DecoderException("invalid suback packet flags");
    }
  }

  private void validateUnsubscribe() {
    if (this.flags != 2) {
      throw new DecoderException("[MQTT-3.10.1-1] invalid unsubscribe flags");
    }
  }

  private void validateUnsubAck() {
    if (this.flags != 0) {
      throw new DecoderException("invalid unsuback flags");
    }
    if (this.remainingLength != 2) {
      throw new DecoderException("invalid unsuback remaining length");
    }
  }

  // -------------------------------------------------

  private void readConnectVariableHeader(ByteBuf buf, MqttConnectPacket packet) {
    DecodedResult<String> protocolName = readString(buf);
    if (!protocolName.getValue().equals("MQTT")) {
      throw new DecoderException("[MQTT-3.1.2-1] invalid protocol name");
    }

    if (!(buf.readUnsignedByte() == 0x04)) {
      throw new DecoderException("[MQTT-3.1.2-1] invalid protocol level");
    }

    val b = buf.readUnsignedByte();
    packet.setCleanSession(isSet(b, 1));

    val usernameFlag = isSet(b, 7);
    val passwordFlag = isSet(b, 6);
    if (!usernameFlag && passwordFlag) {
      throw new DecoderException(
          "invalid connect packet: username not present but password present");
    }
    packet.setUsernameFlag(usernameFlag);
    packet.setPasswordFlag(passwordFlag);

    if (isSet(b, 2)) {
      MqttWill.MqttWillBuilder willBuilder = MqttWill.builder();
      willBuilder.qosLevel(MqttQosLevel.of((b & 0x18) >> 3));
      willBuilder.retain(isSet(b, 5));
      packet.setWillBuilder(willBuilder);
    } else if (!isSet(b, 3) && !isSet(b, 4) && !isSet(b, 5)) {
      throw new DecoderException(
          "[MQTT-3.1.2-11] If the Will Flag is set to 0 the Will QoS and Will Retain fields in the Connect Flags MUST be set to zero");
    }

    DecodedResult<Integer> keepaliveSeconds = readMsbLsb(buf);
    packet.setKeepaliveSeconds(keepaliveSeconds.getValue());
    this.remainingLength -= 10;
  }

  private void readConnectPayload(ByteBuf buf, MqttConnectPacket packet) {
    val clientId = readString(buf);

    packet.setClientId(clientId.value);
    this.remainingLength -= clientId.bytesConsumed;

    val willBuilder = packet.getWillBuilder();
    if (willBuilder != null) {
      val willTopic = readString(buf);
      willBuilder.topic(willTopic.value);
      this.remainingLength -= willTopic.bytesConsumed;

      val willMessage = readByteArray(buf);
      willBuilder.message(willMessage.value);
      this.remainingLength -= willMessage.bytesConsumed;

      packet.setWill(willBuilder.build());
    }
    if (packet.isUsernameFlag()) {
      val username = readString(buf);
      packet.setUsername(username.value);
      this.remainingLength -= username.bytesConsumed;
    }
    if (packet.isPasswordFlag()) {
      val password = readByteArray(buf);
      packet.setPassword(password.value);
      this.remainingLength -= password.bytesConsumed;
    }
    if (this.remainingLength != 0) {
      throw new DecoderException("invalid remaining length in connect packet");
    }
  }

  private void readConnAckVariableHeader(ByteBuf buf, MqttConnAckPacket packet) {
    val b1 = buf.readUnsignedByte();
    if ((b1 & 0xFE) != 0) {
      throw new DecoderException("invalid conack flags");
    }
    val sessionPresent = isSet(b1, 0);
    byte b2 = buf.readByte();
    val code = MqttConnectReturnCode.of(b2);
    if (code != MqttConnectReturnCode.CONNECTION_ACCEPTED && sessionPresent) {
      throw new DecoderException(
          "[MQTT-3.2.2-4] CONNACK packet containing a non-zero return code it MUST set Session Present to 0");
    }
    packet.setSessionPresent(sessionPresent);
    packet.setConnectReturnCode(code);
  }

  private void readSubAckVariableHeader(ByteBuf buf, MqttSubAckPacket packet) {
    DecodedResult<Integer> packetId = readPacketId(buf);
    this.remainingLength -= packetId.bytesConsumed;
    packet.setPacketId((packetId.value));
  }

  private void readSubAckPayload(ByteBuf buf, MqttSubAckPacket packet) {
    val qosLevelList = new LinkedList<MqttQosLevel>();
    for (int i = this.remainingLength; i > 0; i--) {
      qosLevelList.add(MqttQosLevel.of(buf.readByte()));
    }
    packet.setQosLevels(qosLevelList);
  }

  private void readPublishVariableHeader(ByteBuf buf, MqttPublishPacket packet) {
    if (isSet(flags, 3)) {
      packet.setDupFlag(true);
    }
    if (isSet(flags, 0)) {
      packet.setRetain(true);
    }
    val qosLevel = MqttQosLevel.of((flags & 0x06) >> 1);
    if (qosLevel == MqttQosLevel.AT_MOST_ONCE && packet.isDupFlag()) {
      throw new DecoderException(
          "[MQTT-3.3.1-2] The DUP flag MUST be set to 0 for all QoS 0 messages");
    }
    packet.setQosLevel(qosLevel);

    DecodedResult<String> topic = readString(buf);
    packet.setTopic(topic.value);
    this.remainingLength -= topic.bytesConsumed;
    if (packet.getQosLevel() == MqttQosLevel.AT_LEAST_ONCE
        || packet.getQosLevel() == MqttQosLevel.EXACTLY_ONCE) {
      DecodedResult<Integer> packetId = readPacketId(buf);
      packet.setPacketId(packetId.getValue());
      this.remainingLength -= packetId.bytesConsumed;
    }

    byte[] payload = new byte[this.remainingLength];
    buf.readBytes(payload);
    packet.setPayload(payload);
  }

  private void readSubscribeVariableHeader(ByteBuf buf, MqttSubscribePacket packet) {
    val packetId = readMsbLsb(buf);
    packet.setPacketId(packetId.getValue());
    this.remainingLength -= packetId.bytesConsumed;
  }

  private void readSubscribePayload(ByteBuf buf, MqttSubscribePacket packet) {
    var finish = false;
    while (!finish) {
      val topicFilter = readString(buf);
      this.remainingLength -= topicFilter.bytesConsumed;
      val b = buf.readUnsignedByte();
      if ((b & 0xFC) != 0) {
        throw new DecoderException("[MQTT-3-8.3-4] Reserved bits in the payload must be zero");
      }
      this.remainingLength -= 1;
      packet
          .getMqttTopicAndQosLevels()
          .add(new MqttTopicAndQosLevel(topicFilter.getValue(), MqttQosLevel.of(b & 0x03)));
      if (remainingLength == 0) {
        finish = true;
      }
      if (remainingLength < 0) {
        throw new DecoderException("invalid subscribe remaining length");
      }
    }
  }

  private void readUnsubscribeVariableHeader(ByteBuf buf, MqttUnsubscribePacket packet) {
    val packetId = readMsbLsb(buf);
    packet.setPacketId(packetId.value);
    this.remainingLength -= packetId.bytesConsumed;
  }

  private void readUnsubscribePayload(ByteBuf buf, MqttUnsubscribePacket packet) {
    val topicFilters = packet.getTopicFilters();
    while (this.remainingLength > 0) {
      val topicFiler = readString(buf);

      topicFilters.add(validateTopicFilter(topicFiler.value));
      this.remainingLength -= topicFiler.bytesConsumed;
    }

    if (this.remainingLength != 0) {
      throw new DecoderException("invalid unsub length");
    }
  }

  private void readUnsubAckVariableHeader(ByteBuf buf, MqttUnsubAckPacket packet) {
    val packetId = readMsbLsb(buf);
    packet.setPacketId(packetId.value);
  }

  // ------------------------------------------------------------------------------------------------

  private static DecodedResult<String> readString(ByteBuf buffer) {
    val decodedSize = readMsbLsb(buffer);
    val size = decodedSize.value;
    var bytesConsumed = decodedSize.bytesConsumed;

    String s = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_8);
    buffer.skipBytes(size);
    bytesConsumed += size;
    return new DecodedResult<>(s, bytesConsumed);
  }

  private static DecodedResult<Integer> readMsbLsb(ByteBuf buffer) {
    val msbSize = buffer.readUnsignedByte();
    val lsbSize = buffer.readUnsignedByte();
    val bytesConsumed = 2;
    val result = msbSize << 8 | lsbSize;
    if (result < 0 || result > 65535) {
      throw new DecoderException("invalid MSB LSB value: " + result);
    }
    return new DecodedResult<>(result, bytesConsumed);
  }

  private static DecodedResult<byte[]> readByteArray(ByteBuf buffer) {
    DecodedResult<Integer> decodedSize = readMsbLsb(buffer);
    int size = decodedSize.value;
    byte[] bytes = new byte[size];
    buffer.readBytes(bytes);
    return new DecodedResult<>(bytes, decodedSize.bytesConsumed + size);
  }

  private static DecodedResult<Integer> readPacketId(ByteBuf buffer) {
    final DecodedResult<Integer> packetId = readMsbLsb(buffer);
    validatePacketId(packetId.getValue());
    return packetId;
  }

  private static boolean isSet(short b, int pos) {
    return (b & (1 << pos)) != 0;
  }

  private int readRemainingLength(ByteBuf buf) {
    int remainingLength = 0;
    int multiplier = 1;
    short digit;
    int loops = 0;
    do {
      digit = buf.readUnsignedByte();
      remainingLength += (digit & 127) * multiplier;
      multiplier *= 128;
      loops++;
    } while ((digit & 128) != 0 && loops < 4);

    // MQTT protocol limits Remaining Length to 4 bytes
    if (loops == 4 && (digit & 128) != 0) {
      throw new DecoderException("remaining length exceeds 4 digits");
    }
    return remainingLength;
  }

  // -------------------------------------------------------------------------------------

  @Getter
  @AllArgsConstructor
  private static final class DecodedResult<T> {

    public static DecodedResult<Void> EMPTY = new DecodedResult<>(null, 0);

    private final T value;
    private final int bytesConsumed;
  }
}
