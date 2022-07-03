package io.rapidw.mqtt.codec.v5;

public enum MqttV5PropertyType {

    PAYLOAD_TYPE_INDICATOR,
    MESSAGE_EXPIRY_INTERVAL,
    CONTENT_TYPE,
    RESPONSE_TOPIC,
    CORRELATION_DATA,
    SUBSCRIPTION_IDENTIFIER,
    SESSION_EXPIRY_INTERVAL,
    ASSIGNED_CLIENT_IDENTIFIER,
    SERVER_KEEP_ALIVE,
    AUTHENTICATION_METHOD,
    AUTHENTICATION_DATA,
    REQUEST_PROBLEM_INFORMATION,
    WILL_DELAY_INTERVAL,
    REQUEST_RESPONSE_INFORMATION,
    RESPONSE_INFORMATION,
    SERVER_REFERENCE,
    REASON_STRING,
    RECEIVE_MAXIMUM,
    TOPIC_ALIAS_MAXIMUM,
    TOPIC_ALIAS,
    MAXIMUM_QOS,
    RETAIN_AVAILABLE,
    USER_PROPERTY,
    MAXIMUM_PACKET_SIZE,
    WILDCARD_SUBSCRIPTION_AVAILABLE,
    SUBSCRIPTION_IDENTIFIER_AVAILABLE,
    SHARED_SUBSCRIPTION_AVAILABLE;

}