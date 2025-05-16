package cn.brk2outside.ekuiper4j.sdk.api;

import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.dto.response.MqttSourceConfigResponse;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * API for managing eKuiper configuration keys
 */
@RequiredArgsConstructor
public class ConfigKeyAPI {

    private final HttpClient client;

    /**
     * List all MQTT sources/brokers
     *
     * @return map of MQTT brokers
     */
    public Map<String, MqttSourceConfigResponse> listMqttBrokers() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.CONF_LIST_MQTT_SOURCES.getEndpoint());
    }

    /**
     * Delete an MQTT broker configuration
     *
     * @param brokerName the name of the broker to delete
     * @return success message
     */
    public String deleteMqttBroker(String brokerName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.CONF_DELETE_MQTT_BROKER.getEndpoint(), brokerName);
    }

    /**
     * Create or update an MQTT broker configuration
     *
     * @param brokerName the name of the broker
     * @param request the MQTT broker configuration
     * @return the created/updated MQTT broker configuration
     */
    public MqttSourceConfigResponse createOrUpdateMqttBroker(String brokerName, MqttSourceConfigRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.CONF_CREATE_MQTT_BROKER.getEndpoint(), Map.of(brokerName, request), brokerName);
    }
} 