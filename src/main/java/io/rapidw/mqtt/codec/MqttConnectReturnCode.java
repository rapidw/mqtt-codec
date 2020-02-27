package io.rapidw.mqtt.codec;

import io.netty.handler.codec.DecoderException;

import java.util.HashMap;
import java.util.Map;

public enum MqttConnectReturnCode {

    CONNECTION_ACCEPTED((byte) 0x00),
    CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION((byte) 0X01),
    CONNECTION_REFUSED_IDENTIFIER_REJECTED((byte) 0x02),
    CONNECTION_REFUSED_SERVER_UNAVAILABLE((byte) 0x03),
    CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD((byte) 0x04),
    CONNECTION_REFUSED_NOT_AUTHORIZED((byte) 0x05);

    private static Map<Byte, MqttConnectReturnCode> valueMap = new HashMap<>();

    static {
        for (MqttConnectReturnCode code: values()) {
            valueMap.put(code.byteValue, code);
        }
    }

    private final byte byteValue;

    MqttConnectReturnCode(byte byteValue) {
        this.byteValue = byteValue;
    }

    public byte byteValue() {
        return byteValue;
    }

    public static MqttConnectReturnCode of(byte b) {
        MqttConnectReturnCode code = valueMap.get(b);
        if (!valueMap.containsKey(b)) {
            throw new DecoderException("unknown connect return code: " + (b & 0xFF));
        } else {
            return code;
        }
    }
}
