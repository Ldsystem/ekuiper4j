package cn.brk2outside.ekuiper4j.sdk.endpoint;

import cn.brk2outside.ekuiper4j.constants.Endpoints;
import cn.brk2outside.ekuiper4j.constants.HttpMethods;
import cn.brk2outside.ekuiper4j.dto.request.CreateConnectionRequest;
import cn.brk2outside.ekuiper4j.dto.request.CreateRuleRequest;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.dto.request.UpdateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.KuiperInfo;
import cn.brk2outside.ekuiper4j.dto.response.MqttSourceConfigResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleListResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleStatusResponse;
import cn.brk2outside.ekuiper4j.dto.response.StreamSchemaResponse;
import cn.brk2outside.ekuiper4j.model.stream.Stream;
import cn.brk2outside.ekuiper4j.sdk.util.TypeUtil;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

/**
 * Standard endpoints for eKuiper REST API.
 * Each enum value represents a specific API endpoint with its method, path, request/response types,
 * and response format.
 */
public enum StandardEndpoints {

    // Server information endpoints
    GET_SERVER_INFO(HttpMethods.GET, Endpoints.Overview.GET, Void.class, TypeUtil.of(KuiperInfo.class), 0),
    PING(HttpMethods.GET, Endpoints.Overview.PING, Void.class, TypeUtil.of(String.class), 0),

    // Stream endpoints
    CREATE_STREAM(HttpMethods.POST, Endpoints.Stream.CREATE_STREAM_ENDPOINT, CreateStreamRequest.class, TypeUtil.of(String.class), 0),
    LIST_STREAMS(HttpMethods.GET, Endpoints.Stream.LIST_STREAM_ENDPOINT, Void.class, TypeUtil.listOf(String.class), 0),
    GET_STREAM_DETAILS(HttpMethods.GET, Endpoints.Stream.GET_STREAM_DETAIL, Void.class, TypeUtil.of(Stream.class), 1),
    GET_STREAM_SCHEMA(HttpMethods.GET, Endpoints.Stream.GET_STREAM_SCHEMA, Void.class, TypeUtil.of(StreamSchemaResponse.class), 1),
    UPDATE_STREAM(HttpMethods.PUT, Endpoints.Stream.UPDATE_STREAM, UpdateStreamRequest.class, TypeUtil.of(String.class), 1),
    DELETE_STREAM(HttpMethods.DELETE, Endpoints.Stream.DELETE_STREAM, Void.class, TypeUtil.of(String.class), 1),

    // Connection endpoints
    CREATE_CONNECTION(HttpMethods.POST, Endpoints.Connection.CREATE_CONNECTION, CreateConnectionRequest.class, TypeUtil.of(String.class), 0),
    UPDATE_CONNECTION(HttpMethods.PUT, Endpoints.Connection.UPDATE_CONNECTION, CreateConnectionRequest.class, TypeUtil.of(String.class), 1),
    LIST_CONNECTIONS(HttpMethods.GET, Endpoints.Connection.LIST_CONNECTIONS, Void.class, TypeUtil.listOf(Map.class), 0),
    GET_CONNECTION_INFO(HttpMethods.GET, Endpoints.Connection.GET_CONNECTION_STATUS, Void.class, TypeUtil.mapOf(String.class, Object.class), 1),
    DELETE_CONNECTION(HttpMethods.DELETE, Endpoints.Connection.DELETE_CONNECTION, Void.class, TypeUtil.of(String.class), 1),
    SINK_CONNECTION_CHECK(HttpMethods.POST, Endpoints.Connection.SINK_CONNECTION_CHECK, Map.class, TypeUtil.of(String.class), 1),
    SOURCE_CONNECTION_CHECK(HttpMethods.POST, Endpoints.Connection.SOURCE_CONNECTION_CHECK, Map.class, TypeUtil.of(String.class), 1),
    MQTT_SOURCE_CONNECTION_CHECK(HttpMethods.POST, Endpoints.Connection.MQTT_SOURCE_CONNECTION_CHECK, MqttSourceConfigRequest.class, TypeUtil.of(Void.class), 0),

    // Config key of mqtt endpoints
    CONF_LIST_MQTT_SOURCES(HttpMethods.GET, Endpoints.ConfigKey.LIST_MQTT_BROKERS, Void.class, TypeUtil.mapOf(String.class, MqttSourceConfigResponse.class), 0),
    CONF_DELETE_MQTT_BROKER(HttpMethods.DELETE, Endpoints.ConfigKey.DELETE_MQTT_BROKER, Void.class, TypeUtil.of(Void.class), 1),
    // returns nothing
    CONF_CREATE_MQTT_BROKER(HttpMethods.PUT, Endpoints.ConfigKey.PUT_MQTT_BROKER, Map.class, TypeUtil.of(Void.class), 1),

    // Rule endpoints
    CREATE_RULE(HttpMethods.POST, Endpoints.Rules.CREATE_RULE, CreateRuleRequest.class, TypeUtil.of(String.class), 0),
    LIST_RULES(HttpMethods.GET, Endpoints.Rules.LIST_RULES, Void.class, TypeUtil.listOf(RuleListResponse.class), 0),
    GET_RULE(HttpMethods.GET, Endpoints.Rules.GET_RULE, Void.class, TypeUtil.of(RuleResponse.class), 1),
    UPDATE_RULE(HttpMethods.PUT, Endpoints.Rules.UPDATE_RULE, CreateRuleRequest.class, TypeUtil.of(String.class), 1),
    DELETE_RULE(HttpMethods.DELETE, Endpoints.Rules.DELETE_RULE, Void.class, TypeUtil.of(String.class), 1),
    START_RULE(HttpMethods.POST, Endpoints.Rules.START_RULE, Void.class, TypeUtil.of(String.class), 1),
    STOP_RULE(HttpMethods.POST, Endpoints.Rules.STOP_RULE, Void.class, TypeUtil.of(String.class), 1),
    RESTART_RULE(HttpMethods.POST, Endpoints.Rules.RESTART_RULE, Void.class, TypeUtil.of(String.class), 1),
    GET_RULE_STATUS(HttpMethods.GET, Endpoints.Rules.GET_RULE_STATUS, Void.class, TypeUtil.of(RuleStatusResponse.class), 1),
    GET_ALL_RULES_STATUS(HttpMethods.GET, Endpoints.Rules.GET_ALL_RULES_STATUS, Void.class, TypeUtil.mapOf(String.class, RuleStatusResponse.class), 0),
    VALIDATE_RULE(HttpMethods.POST, Endpoints.Rules.VALIDATE_RULE, CreateRuleRequest.class, TypeUtil.of(String.class), 0),
    EXPLAIN_RULE(HttpMethods.GET, Endpoints.Rules.EXPLAIN_RULE, Void.class, TypeUtil.of(String.class), 1),
    GET_RULES_CPU_USAGE(HttpMethods.GET, Endpoints.Rules.GET_RULES_CPU_USAGE, Void.class, TypeUtil.of(String.class), 0),

    ;

    private final ApiEndpoint<?, ?> endpoint;

    /**
     * Creates a new standard endpoint.
     *
     * @param method The HTTP method
     * @param path The API path
     * @param requestClz The request class type
     * @param responseClz The response class type
     * @param pathVariableCount The number of path variables expected in the path
     */
    <T, R> StandardEndpoints(HttpMethods method, String path, Class<T> requestClz, ParameterizedTypeReference<R> responseClz, int pathVariableCount) {
        this.endpoint = new ApiEndpoint<>(method, path, requestClz, responseClz, pathVariableCount);
    }

    /**
     * Gets the API endpoint.
     *
     * @param <T> Request body type
     * @param <R> Response type
     * @return The API endpoint
     */
    @SuppressWarnings("unchecked")
    public <T, R> ApiEndpoint<T, R> getEndpoint() {
        return (ApiEndpoint<T, R>) endpoint;
    }
} 