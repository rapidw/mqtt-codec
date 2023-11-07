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
package io.rapidw.mqtt.codec.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import java.nio.charset.StandardCharsets;

import static io.rapidw.mqtt.codec.utils.MqttV311ValidationUtils.validatePacketId;

public class DecoderUtils {

    public static int readRemainingLength(ByteBuf buf) {
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

    public static DecodedResult<String> readString(ByteBuf buffer) {
        DecodedResult<Integer> decodedSize = readMsbLsb(buffer);
        int size = decodedSize.value;
        int bytesConsumed = decodedSize.bytesConsumed;

        String s = buffer.toString(buffer.readerIndex(), size, StandardCharsets.UTF_8);
        buffer.skipBytes(size);
        bytesConsumed += size;
        return new DecodedResult<>(s, bytesConsumed);
    }

    public static DecodedResult<Integer> readMsbLsb(ByteBuf buffer) {
        short msbSize = buffer.readUnsignedByte();
        short lsbSize = buffer.readUnsignedByte();
        int bytesConsumed = 2;
        int result = msbSize << 8 | lsbSize;
        if (result < 0 || result > 65535) {
            throw new DecoderException("invalid MSB LSB value: " + result);
        }
        return new DecodedResult<>(result, bytesConsumed);
    }

    public static DecodedResult<byte[]> readByteArray(ByteBuf buffer) {
        DecodedResult<Integer> decodedSize = readMsbLsb(buffer);
        int size = decodedSize.value;
        byte[] bytes = new byte[size];
        buffer.readBytes(bytes);
        return new DecodedResult<>(bytes, decodedSize.bytesConsumed + size);
    }

    public static DecodedResult<Integer> readPacketId(ByteBuf buffer) {
        final DecodedResult<Integer> packetId = readMsbLsb(buffer);
        validatePacketId(packetId.getValue());
        return packetId;
    }

    public static boolean isSet(short b, int pos) {
        return (b & (1 << pos)) != 0;
    }

    public static final class DecodedResult<T> {

        public static DecodedResult<Void> EMPTY = new DecodedResult<>(null, 0);

        private final T value;
        private final int bytesConsumed;

        public DecodedResult(T value, int bytesConsumed) {
            this.value = value;
            this.bytesConsumed = bytesConsumed;
        }

        public T getValue() {
            return value;
        }

        public int getBytesConsumed() {
            return bytesConsumed;
        }
    }
}
