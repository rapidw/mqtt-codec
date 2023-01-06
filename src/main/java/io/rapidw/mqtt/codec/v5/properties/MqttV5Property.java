package io.rapidw.mqtt.codec.v5.properties;

public abstract class MqttV5Property {
    private Type type;
    private Object value;

    protected MqttV5Property(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    protected Type getType() {
        return type;
    }

    protected Object getValue() {
        return value;
    }

    protected abstract byte[] getBytes();

    public enum Type {
        PAYLOAD_TYPE_INDICATOR(0x01),
        MESSAGE_EXPIRY_INTERVAL(0x02),
        CONTENT_TYPE(0x03),
        RESPONSE_TOPIC(0x08),
        CORRELATION_DATA(0x09),
        SUBSCRIPTION_IDENTIFIER(0x0B),
        SESSION_EXPIRY_INTERVAL(0x11),
        ASSIGNED_CLIENT_IDENTIFIER(0x12),
        SERVER_KEEP_ALIVE(0x13),
        AUTHENTICATION_METHOD(0x15),
        AUTHENTICATION_DATA(0x16),
        REQUEST_PROBLEM_INFORMATION(0x17),
        WILL_DELAY_INTERVAL(0x18),
        REQUEST_RESPONSE_INFORMATION(0x19),
        RESPONSE_INFORMATION(0x1A),
        SERVER_REFERENCE(0x1C),
        REASON_STRING(0x1F),
        RECEIVE_MAXIMUM(0x21),
        TOPIC_ALIAS_MAXIMUM(0x22),
        TOPIC_ALIAS(0x23),
        MAXIMUM_QOS(0x24),
        RETAIN_AVAILABLE(0x25),
        USER_PROPERTY(0x26),
        MAXIMUM_PACKET_SIZE(0x27),
        WILDCARD_SUBSCRIPTION_AVAILABLE(0x28),
        SUBSCRIPTION_IDENTIFIER_AVAILABLE(0x29),
        SHARED_SUBSCRIPTION_AVAILABLE(0x2A);

        private int id;

        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
