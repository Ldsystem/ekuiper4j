package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateConnectionRequest;
import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.http.HttpClientException;
import cn.brk2outside.ekuiper4j.model.MqttConnProps;
import cn.brk2outside.ekuiper4j.sdk.api.ConnectionAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConnectionAPI endpoints
 */
public class ConnectionApiTest extends BaseApiTest {

    private static final String TEST_CONNECTION_NAME = "test_connection_api";
    private ConnectionAPI connectionAPI;

    @BeforeEach
    void setUpConnectionApi() {
        connectionAPI = new ConnectionAPI(client);
        // Clean up any existing test connections
        cleanupTestConnection();
    }

    @AfterEach
    void tearDownConnection() {
        // Clean up test connections
        cleanupTestConnection();
    }

    private void cleanupTestConnection() {
        try {
            connectionAPI.deleteConnection(TEST_CONNECTION_NAME);
        } catch (Exception e) {
            // Ignore errors if connection doesn't exist
        }
    }

    private CreateConnectionRequest<MqttConnProps> createTestConnectionRequest() {
        // Create an MQTT connection
        MqttConnProps mqttProps = MqttConnProps.builder()
                .server(getMqttBrokerInternalUrl())  // Use internal MQTT broker URL
                .clientid("ekuiper_test_client")
                .qos(1)
                .protocolVersion(null) // Default to 3.1.1
                .build();
        
        CreateConnectionRequest<MqttConnProps> request = new CreateConnectionRequest<>();
        request.setId(TEST_CONNECTION_NAME);
        request.setTyp("mqtt");
        request.setProps(mqttProps);
        return request;
    }

    @Test
    void testCreateAndListConnections() {
        // Create a connection
        CreateConnectionRequest<MqttConnProps> request = createTestConnectionRequest();
        String response = connectionAPI.createConnection(request);
        
        // Verify the response
        assertNotNull(response);
        
        // List connections and verify our connection exists
        List<Map<String, Object>> connections = connectionAPI.listConnections();
        
        assertNotNull(connections);
        boolean foundConnection = connections.stream()
                .anyMatch(conn -> TEST_CONNECTION_NAME.equals(conn.get("id")));
        
        assertTrue(foundConnection, "Created connection should be in the list");
    }

    @Test
    void testGetConnectionInfo() {
        // First create a connection
        CreateConnectionRequest<MqttConnProps> request = createTestConnectionRequest();
        String response = connectionAPI.createConnection(request);

        assertEquals("success", response);

        // Get connection details
        Map<String, Object> connectionInfo = connectionAPI.getConnectionInfo(TEST_CONNECTION_NAME);
        
        // Verify details
        assertNotNull(connectionInfo);
        assertEquals(TEST_CONNECTION_NAME, connectionInfo.get("id"));
        assertEquals("mqtt", connectionInfo.get("typ"));
        
        // Verify props
        Map<String, Object> props = (Map<String, Object>) connectionInfo.get("props");
        assertNotNull(props);
        assertEquals(getMqttBrokerInternalUrl(), props.get("server"));  // Use internal URL
    }

    @Test
    void testUpdateConnection() {
        // First create a connection
        CreateConnectionRequest<MqttConnProps> createRequest = createTestConnectionRequest();
        connectionAPI.createConnection(createRequest);
        
        // Update the connection
        MqttConnProps updatedProps = MqttConnProps.builder()
                .server("tcp://updated-broker:1883") // Changed server
                .clientid("ekuiper_updated_client") // Changed client ID
                .qos(2) // Changed QoS
                .build();
        
        CreateConnectionRequest<MqttConnProps> updateRequest = new CreateConnectionRequest<>();
        updateRequest.setId(TEST_CONNECTION_NAME);
        updateRequest.setTyp("mqtt");
        updateRequest.setProps(updatedProps);

        // Update the connection
        String updateResponse = connectionAPI.updateConnection(TEST_CONNECTION_NAME, updateRequest);

        assertEquals("success", updateResponse);

        // Get connection details and verify update
        Map<String, Object> connectionInfo = connectionAPI.getConnectionInfo(TEST_CONNECTION_NAME);

        // Verify updated details
        Map<String, Object> props = (Map<String, Object>) connectionInfo.get("props");
        assertNotNull(props);
        assertEquals("tcp://updated-broker:1883", props.get("server"));
        assertEquals("ekuiper_updated_client", props.get("clientid"));
        assertEquals(2, props.get("qos"));
    }

    @Test
    void testDeleteConnection() {
        // First create a connection
        CreateConnectionRequest<MqttConnProps> request = createTestConnectionRequest();
        connectionAPI.createConnection(request);
        
        // Verify the connection exists
        List<Map<String, Object>> connectionsBefore = connectionAPI.listConnections();
        boolean hasConnection = connectionsBefore.stream()
                .anyMatch(conn -> TEST_CONNECTION_NAME.equals(conn.get("id")));
        
        assertTrue(hasConnection, "Connection should exist before deletion");
        
        // Delete the connection
        String deleteResponse = connectionAPI.deleteConnection(TEST_CONNECTION_NAME);
        
        assertNotNull(deleteResponse);
        
        // Verify the connection no longer exists
        List<Map<String, Object>> connectionsAfter = connectionAPI.listConnections();
        boolean stillHasConnection = connectionsAfter.stream()
                .anyMatch(conn -> TEST_CONNECTION_NAME.equals(conn.get("id")));
        
        assertFalse(stillHasConnection, "Connection should not exist after deletion");
    }

    @Test
    void testCheckConnections() {
        // Create a MQTT sink config for testing
        Map<String, Object> sinkConfig = new HashMap<>();
        sinkConfig.put("type", "mqtt");
        sinkConfig.put("name", "test_sink");
        Map<String, Object> connConfig = new HashMap<>();
        connConfig.put("server", getMqttBrokerInternalUrl());  // Use internal URL
        connConfig.put("topic", "test/topic");
        connConfig.put("qos", 1);
        sinkConfig.putAll(connConfig);

        // Check sink connection - may throw exception, which is fine
        connectionAPI.checkSinkConnection(sinkConfig);

        // Create a MQTT source config for testing
        Map<String, Object> sourceConfig = new HashMap<>();
        sourceConfig.put("type", "mqtt");
        sourceConfig.put("name", "test_source");
        sourceConfig.putAll(connConfig);
        
        assertDoesNotThrow(() -> connectionAPI.checkSourceConnection(sourceConfig));
    }

    @Test
    void testCheckMqttConnection() {
        // Create MQTT config request for testing
        MqttSourceConfigRequest mqttRequest = new MqttSourceConfigRequest();
        mqttRequest.setServer(getMqttBrokerInternalUrl());  // Use internal URL
        mqttRequest.setQos(1);

        // Check the MQTT connection
        assertDoesNotThrow(() -> connectionAPI.checkMqttSourceConnection(mqttRequest));
    }
} 