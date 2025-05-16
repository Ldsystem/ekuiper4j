package cn.brk2outside.ekuiper4j.sdk.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateConnectionRequest;
import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * API for managing eKuiper connections
 */
@RequiredArgsConstructor
public class ConnectionAPI {

    private final HttpClient client;

    /**
     * Create a new connection
     *
     * @param request the connection creation request
     * @return success message
     */
    public String createConnection(CreateConnectionRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.CREATE_CONNECTION.getEndpoint(), request);
    }

    /**
     * Update an existing connection
     *
     * @param connectionName the name of the connection to update
     * @param request the update request
     * @return response map
     */
    public Map<String, Object> updateConnection(String connectionName, CreateConnectionRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.UPDATE_CONNECTION.getEndpoint(), request, connectionName);
    }

    /**
     * List all connections
     *
     * @return list of connections
     */
    public List<Map<String, Object>> listConnections() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.LIST_CONNECTIONS.getEndpoint());
    }

    /**
     * Get details of a specific connection
     *
     * @param connectionName the name of the connection
     * @return connection details
     */
    public Map<String, Object> getConnectionInfo(String connectionName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_CONNECTION_INFO.getEndpoint(), connectionName);
    }

    /**
     * Delete a connection
     *
     * @param connectionName the name of the connection to delete
     * @return success message
     */
    public String deleteConnection(String connectionName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.DELETE_CONNECTION.getEndpoint(), connectionName);
    }

    /**
     * Check sink connection
     *
     * @param config the sink connection configuration
     * @return check result
     */
    public String checkSinkConnection(String type, Map<String, Object> config) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.SINK_CONNECTION_CHECK.getEndpoint(), config, type);
    }

    /**
     * Check source connection
     *
     * @param config the source connection configuration
     * @return check result
     */
    public String checkSourceConnection(Map<String, Object> config) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.SOURCE_CONNECTION_CHECK.getEndpoint(), config);
    }

    /**
     * Check MQTT source connection
     *
     * @param request the MQTT source configuration request
     * @return check result
     */
    public String checkMqttSourceConnection(MqttSourceConfigRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.MQTT_SOURCE_CONNECTION_CHECK.getEndpoint(), request);
    }
} 