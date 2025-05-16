package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.constants.MqttProtocolVersion;
import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.dto.response.MqttSourceConfigResponse;
import cn.brk2outside.ekuiper4j.sdk.api.ConfigKeyAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConfigKeyAPI endpoints
 */
public class ConfigKeyApiTest extends BaseApiTest {

    private static final String TEST_BROKER_NAME = "test_broker_api";
    private ConfigKeyAPI configKeyAPI;

    @BeforeEach
    void setUpConfigKeyApi() {
        configKeyAPI = new ConfigKeyAPI(client);
        // Clean up any existing test brokers
        cleanupTestBroker();
    }

    @AfterEach
    void tearDownConfigKey() {
        // Clean up test brokers
        cleanupTestBroker();
    }

    private void cleanupTestBroker() {
        try {
            configKeyAPI.deleteMqttBroker(TEST_BROKER_NAME);
        } catch (Exception e) {
            // Ignore errors if broker doesn't exist
        }
    }

    private MqttSourceConfigRequest createTestBrokerRequest() {
        // Create a test MQTT broker configuration
        MqttSourceConfigRequest request = MqttSourceConfigRequest.builder()
                .server("tcp://localhost:1883")
                .clientid("ekuiper_test_client")
                .qos(1)
                .protocolVersion(MqttProtocolVersion.MQTT_3_1_1.getVersion())
                .username("mqttUser")
                .password("mqttPassword")
                .insecureSkipVerify(true)
                .build();
        return request;
    }

    @Test
    void testCreateAndListMqttBrokers() {
        // Create a broker configuration
        MqttSourceConfigRequest request = createTestBrokerRequest();
        MqttSourceConfigResponse response = configKeyAPI.createOrUpdateMqttBroker(TEST_BROKER_NAME, request);
        
        // Verify the response
        assertNotNull(response);
        assertEquals("tcp://localhost:1883", response.getServer());
        
        // List brokers and verify our broker exists
        Map<String, MqttSourceConfigResponse> brokers = configKeyAPI.listMqttBrokers();
        
        assertNotNull(brokers);
        assertTrue(brokers.containsKey(TEST_BROKER_NAME), "Created broker should be in the list");
        
        // Verify the broker details
        MqttSourceConfigResponse broker = brokers.get(TEST_BROKER_NAME);
        assertNotNull(broker);
        assertEquals("tcp://localhost:1883", broker.getServer());
        assertEquals("ekuiper_test_client", broker.getClientid());
        assertEquals(1, broker.getQos());
        assertEquals("3.1.1", broker.getProtocolVersion());
        assertEquals("mqttUser", broker.getUsername());
        assertEquals("********", broker.getPassword()); // Password should be masked in response
        assertTrue(broker.getInsecureSkipVerify());
    }

    @Test
    void testUpdateMqttBroker() {
        // First create a broker
        MqttSourceConfigRequest createRequest = createTestBrokerRequest();
        configKeyAPI.createOrUpdateMqttBroker(TEST_BROKER_NAME, createRequest);
        
        // Update the broker with different settings
        MqttSourceConfigRequest updateRequest = MqttSourceConfigRequest.builder()
                .server("tcp://mqtt-broker:1883") // Changed server
                .clientid("ekuiper_updated_client") // Changed client ID
                .qos(2) // Changed QoS
                .protocolVersion(MqttProtocolVersion.MQTT_3_1.getVersion()) // Changed protocol version
                .username("updatedUser") // Changed username
                .password("updatedPassword") // Changed password
                .insecureSkipVerify(false) // Changed skip verify
                .build();
        
        // Update the broker
        MqttSourceConfigResponse updateResponse = configKeyAPI.createOrUpdateMqttBroker(TEST_BROKER_NAME, updateRequest);
        
        assertNotNull(updateResponse);
        assertEquals("tcp://mqtt-broker:1883", updateResponse.getServer());
        assertEquals("ekuiper_updated_client", updateResponse.getClientid());
        assertEquals(2, updateResponse.getQos());
        assertEquals("3.1", updateResponse.getProtocolVersion());
        assertEquals("updatedUser", updateResponse.getUsername());
        assertEquals("********", updateResponse.getPassword()); // Password should be masked in response
        assertFalse(updateResponse.getInsecureSkipVerify());
        
        // Get the broker and verify it was updated
        Map<String, MqttSourceConfigResponse> brokers = configKeyAPI.listMqttBrokers();
        MqttSourceConfigResponse updatedBroker = brokers.get(TEST_BROKER_NAME);
        
        assertNotNull(updatedBroker);
        assertEquals("tcp://mqtt-broker:1883", updatedBroker.getServer());
        assertEquals("ekuiper_updated_client", updatedBroker.getClientid());
        assertEquals(2, updatedBroker.getQos());
        assertEquals("3.1", updatedBroker.getProtocolVersion());
    }

    @Test
    void testDeleteMqttBroker() {
        // First create a broker
        MqttSourceConfigRequest request = createTestBrokerRequest();
        configKeyAPI.createOrUpdateMqttBroker(TEST_BROKER_NAME, request);
        
        // Verify the broker exists
        Map<String, MqttSourceConfigResponse> brokersBefore = configKeyAPI.listMqttBrokers();
        assertTrue(brokersBefore.containsKey(TEST_BROKER_NAME), "Broker should exist before deletion");
        
        // Delete the broker
        String deleteResponse = configKeyAPI.deleteMqttBroker(TEST_BROKER_NAME);
        
        assertNotNull(deleteResponse);
        
        // Verify the broker no longer exists
        Map<String, MqttSourceConfigResponse> brokersAfter = configKeyAPI.listMqttBrokers();
        assertFalse(brokersAfter.containsKey(TEST_BROKER_NAME), "Broker should not exist after deletion");
    }
} 