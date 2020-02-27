package io.rapidw.mqtt.codec;

import io.netty.handler.codec.EncoderException;
import lombok.val;
import lombok.var;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

class ValidationUtils {

    public static <T> T requireNonNull(T obj, String name) {
        if (obj == null) {
            throw new EncoderException(name + " must not be null");
        }
        return obj;
    }

    public static <T> void requireNull(T obj, String name) {
        if (obj != null) {
            throw new EncoderException(name + " must be null");
        }
    }

    public static byte[] validateByteArray(byte[] value, String name) {
        requireNonNull(value, name);
        if (value.length > 65535) {
            throw new IllegalArgumentException("length of" + name +" must be < 65535");
        }
        return value;
    }

    public static byte[] validateAndEncodeString(String value, String name) {
        requireNonNull(value, name);
        val bytes = value.getBytes(StandardCharsets.UTF_8);
        int length = bytes.length;
        if (length > 65535) {
            throw new IllegalArgumentException("length of" + name +" must be < 65535");
        }

        for (int i = 0; i < value.codePointCount(0, value.length()); i++) {
            if (value.codePointAt(i) >= 0xD800 && value.codePointAt(i) <= 0xDFFF) {
                throw new IllegalArgumentException("[MQTT-1.5.3-1] string must not contains code point between U+D800 and U+DFFF");
            }
            if (value.codePointAt(i) == 0) {
                throw new IllegalArgumentException("[MQTT-1.5.3-2] string must not contains code point U+0000");
            }
        }
        return bytes;
    }

    public static String validateTopicFilter(String topicFilter) {
        val levels = splitTopic(topicFilter);
        for (var i = 0; i < levels.length; i++) {
            if (containNullCharacter(levels[i])) {
                throw new IllegalArgumentException("[MQTT-4.7.3-2] topic filter must not contain Unicode U+0000)");
            }
            if (levels[i].contains("#")) {
                if (i != (levels.length - 1)) {
                    throw new IllegalArgumentException("[MQTT-4.7.1-2] # must be last char in topic filter");
                } else {
                    if (levels[i].length() != 1) {
                        throw new IllegalArgumentException("[MQTT-4.7.1-2] # must follows a topic level separator");
                    }
                }
                if (i != levels.length - 1) {
                    throw new IllegalArgumentException("[MQTT-4.7.1-2] # must be last char in topic filter");
                }
            }
            if (levels[i].contains("+") && levels[i].length() != 1) {
                throw new IllegalArgumentException("[MQTT-4.7.1-3] + must occupy an entire level of the filter");
            }
        }
        return topicFilter;
    }

    public static byte[] validateAndEncodeTopicFilter(String topicFilter) {
        requireNonNull(topicFilter, "topic filter");

        val bytes = topicFilter.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 1 ||bytes.length > 65535) {
            throw new IllegalArgumentException("[MQTT-4.7.3-1] topic filter length must be > 1 and < 65535");
        }

        validateTopicFilter(topicFilter);
        return bytes;
    }

    public static String validateTopicName(String topicName) {
        if (topicName.contains("+") || topicName.contains("#")) {
            throw new IllegalArgumentException("[MQTT-4.7.1-1] wildcard characters can be used in Topic Filters, but MUST NOT be used within a Topic Name");
        }
        if (containNullCharacter(topicName)) {
            throw new IllegalArgumentException("[MQTT-4.7.3-2] topic filter must not contain Unicode U+0000)");
        }
        return topicName;
    }

    public static byte[] validateAndEncodeTopicName(String topicName) {
        requireNonNull(topicName, "topic name");
        val bytes = topicName.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 1 ||bytes.length > 65535) {
            throw new IllegalArgumentException("[MQTT-4.7.3-1] topic name length must be > 1 and < 65535");
        }

        validateTopicName(topicName);
        return bytes;
    }

    public static int validatePacketId(int packetId) {
        if (packetId < 1 || packetId > 65535) {
            throw new IllegalArgumentException("[MQTT-2.3.1-1] messageId: " + packetId + " (expected: 1 ~ 65535)");
        }
        return packetId;
    }

    private static String[] splitTopic(String topic) {
        List<String> strings = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < topic.length(); i++) {
            char c = topic.charAt(i);
            if (c != '/') {
                builder.append(c);
            } else {
                strings.add(builder.toString());
                builder = new StringBuilder();
            }
        }
        strings.add(builder.toString());
        return strings.toArray(new String[0]);
    }

    private static boolean containNullCharacter(String str) {
        return str.chars().anyMatch(value -> value == Character.MIN_VALUE);
    }
}
