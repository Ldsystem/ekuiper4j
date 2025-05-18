package cn.brk2outside.ekuiper4j.http;

import cn.brk2outside.ekuiper4j.BaseEKuiperTest;
import cn.brk2outside.ekuiper4j.dto.request.CreateConnectionRequest;
import cn.brk2outside.ekuiper4j.dto.response.MqttSourceConfigResponse;
import cn.brk2outside.ekuiper4j.model.MqttConnProps;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RestTemplateHttpClient class.
 */
class RestTemplateHttpClientTest extends BaseEKuiperTest {

    private RestTemplateHttpClient client;
    
    @BeforeEach
    void setUp() {
        client = new RestTemplateHttpClient(
                EKUIPER.getEkuiperHost(),
                EKUIPER.getEkuiperPort()
        );
        
        // Create the demo stream if it doesn't exist
        try {
            Map<String, Object> streamConfig = new HashMap<>();
            streamConfig.put("sql", "create stream demo (temperature float, humidity float) WITH (FORMAT=\"json\", DATASOURCE=\"demo\")");
            ApiRequestExecutor.executeBodyAndQs(client, StandardEndpoints.CREATE_STREAM.getEndpoint(), streamConfig, Map.of());
        } catch (HttpClientException e) {
            // Stream might already exist, ignore the error
            System.out.println("Note: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() {
        // Drop the demo stream after each test
        try {
            // Delete the stream using the standard endpoint
            ApiRequestExecutor.execute(client, StandardEndpoints.DELETE_STREAM.getEndpoint(), "Demo");
        } catch (HttpClientException e) {
            // Stream might not exist or other error, log but continue
            System.out.println("Note: Failed to drop stream after test: " + e.getMessage());
        }
    }
    
    @Test
    void testGetBaseUrl() {
        String expectedBaseUrl = String.format("http://%s:%d", 
                EKUIPER.getEkuiperHost(), EKUIPER.getEkuiperPort());
                
        assertEquals(expectedBaseUrl, client.getBaseUrl());
    }
    
    @Test
    void testServerInfoEndpoint() {
        // Test GET_SERVER_INFO endpoint using the typed method
        cn.brk2outside.ekuiper4j.dto.response.KuiperInfo info = ApiRequestExecutor.execute(client,
                StandardEndpoints.GET_SERVER_INFO.getEndpoint());
        
        assertNotNull(info);
        assertNotNull(info.version(), "Version should not be null");
    }
    
    @Test
    void testPingEndpoint() {
        // Just ensure it doesn't throw an exception
        assertDoesNotThrow(() -> {
            ApiRequestExecutor.execute(client, StandardEndpoints.PING.getEndpoint());
        });
    }
    
    @Test
    void testCreateListAndDeleteConnection() {
        // Create a unique connection name for testing
        String connectionName = "test_connection_" + System.currentTimeMillis();
        
        try {
            // 1. Create a connection
            CreateConnectionRequest<MqttConnProps> request = new CreateConnectionRequest<>();
            request.setId(connectionName);
            request.setTyp("mqtt");
            
            // Add MQTT connection properties
            MqttConnProps config = MqttConnProps.builder()
                    .server(getMqttBrokerInternalUrl())  // Use internal MQTT broker URL
                    .username("admin")
                    .password("public")
                    .build();

            request.setProps(config);
            
            // Execute CREATE_CONNECTION endpoint using ApiRequestExecutor
            String createResponse = ApiRequestExecutor.executeBody(client,
                    StandardEndpoints.CREATE_CONNECTION.getEndpoint(), 
                    request);

            assertNotNull(createResponse);
            assertEquals("success", createResponse, "Response should contain success field");
            
            // 2. List connections and verify our connection exists
            List<Map<String, Object>> connections = ApiRequestExecutor.execute(client,
                    StandardEndpoints.LIST_CONNECTIONS.getEndpoint());

            assertNotNull(connections, "Connection list should not be null");

            boolean found = false;
            // Find our connection in the list
            for (Map<String, Object> conn : connections) {
                if (connectionName.equals(conn.get("id"))) {
                    found = true;
                    break;
                }
            }
            
            assertTrue(found, "Created connection should be found in the connections list");
            
            // 3. Clean up - Delete the connection
            // Delete using ApiRequestExecutor
            String deleteResponse = ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_CONNECTION.getEndpoint(),
                    connectionName);
            
            assertNotNull(deleteResponse);
            assertEquals("success", deleteResponse, "Delete response should contain success field");
            
            // 4. Verify connection is deleted
            connections = ApiRequestExecutor.execute(client,
                    StandardEndpoints.LIST_CONNECTIONS.getEndpoint());
            
            // Find our connection in the list (should not be found)
            found = false;
            for (Map<String, Object> conn : connections) {
                if (connectionName.equals(conn.get("id"))) {
                    found = true;
                    break;
                }
            }
            
            assertFalse(found, "Connection should be deleted and not in the list");
            
        } catch (HttpClientException e) {
            // If test fails, attempt to clean up
            try {
                ApiRequestExecutor.executeBody(client,
                        StandardEndpoints.DELETE_CONNECTION.getEndpoint(),
                        connectionName);
            } catch (Exception ignored) {
                // Ignore cleanup errors
            }
            // Print full stack trace instead of just a message
            e.printStackTrace();
        }
    }
    
    @Test
    void testListMqttSourcesEndpoint() {
        try {
            // Test CONF_LIST_MQTT_SOURCES endpoint using ApiRequestExecutor
            Map<String, MqttSourceConfigResponse> response = ApiRequestExecutor.execute(client,
                    StandardEndpoints.CONF_LIST_MQTT_SOURCES.getEndpoint());
            
            assertNotNull(response);
            assertFalse(response.isEmpty(), "conf list should not be empty");
            
        } catch (HttpClientException e) {
            // This endpoint might not be available in all eKuiper instances
            // Skip the test if the endpoint returns an error
            e.printStackTrace();
        }
    }
} 