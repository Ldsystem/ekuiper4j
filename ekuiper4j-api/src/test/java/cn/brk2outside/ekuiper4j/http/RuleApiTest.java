package cn.brk2outside.ekuiper4j.http;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.dto.request.CreateRuleRequest;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.RuleListResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleStatusResponse;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import cn.brk2outside.ekuiper4j.utils.StreamFieldBuilder;
import cn.brk2outside.ekuiper4j.utils.StreamSqlBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Rule API endpoints
 */
public class RuleApiTest extends BaseEKuiperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleApiTest.class);
    private static final String TEST_RULE_NAME = "test_rule";
    private static final String TEST_STREAM_NAME = "test_stream"; // We'll create this stream for testing
    
    private RestTemplateHttpClient client;

    @BeforeEach
    void setUp() {
        client = new RestTemplateHttpClient(
                EKUIPER.getEkuiperHost(),
                EKUIPER.getEkuiperPort()
        );
        
        // Create test stream
        createTestStream();
        
        // Clean up any existing test rules
        cleanupTestRule();
    }

    @AfterEach
    void tearDown() {
        // Clean up test rules and stream
        cleanupTestRule();
        cleanupTestStream();
    }
    
    private void createTestStream() {
        try {
            // Create a stream with id and temperature fields
            StreamField idField = StreamFieldBuilder.createBigintField("id");
            StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
            List<StreamField> fields = StreamFieldBuilder.createFields(idField, tempField);
            
            Map<String, String> options = new HashMap<>();
            options.put("DATASOURCE", "test/topic");
            options.put("FORMAT", "JSON");
            
            String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
            
            CreateStreamRequest request = new CreateStreamRequest();
            request.setSql(sql);
            
            // Create the stream
            ApiRequestExecutor.executeBody(client,
                    StandardEndpoints.CREATE_STREAM.getEndpoint(), 
                    request);
            
            LOGGER.info("Created test stream: {}", TEST_STREAM_NAME);
        } catch (Exception e) {
            LOGGER.error("Failed to create test stream", e);
            throw e;
        }
    }
    
    private void cleanupTestStream() {
        try {
            // Delete the test stream
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_STREAM.getEndpoint(), 
                    TEST_STREAM_NAME);
            LOGGER.info("Deleted test stream: {}", TEST_STREAM_NAME);
        } catch (HttpClientException e) {
            // Ignore errors if stream doesn't exist
            LOGGER.warn("Failed to delete test stream (might not exist): {}", e.getMessage());
        }
    }
    
    private void cleanupTestRule() {
        try {
            // Delete the test rule
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
            LOGGER.info("Deleted test rule: {}", TEST_RULE_NAME);
        } catch (HttpClientException e) {
            // Ignore errors if rule doesn't exist
            LOGGER.warn("Failed to delete test rule (might not exist): {}", e.getMessage());
        }
    }

    private CreateRuleRequest createTestRuleRequest() {
        // Create a log action
        Map<String, Object> logAction = new HashMap<>();
        logAction.put("log", new HashMap<>());
        
        List<Map<String, Object>> actions = new ArrayList<>();
        actions.add(logAction);
        
        return CreateRuleRequest.builder()
                .id(TEST_RULE_NAME)
                .sql("SELECT * FROM " + TEST_STREAM_NAME)
                .actions(actions)
                .build();
    }

    @Test
    void testCreateAndListRules() {
        // Create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        String response = ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        // Verify the response
        assertNotNull(response);
        
        // List rules and verify our rule exists
        List<RuleListResponse> rules = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_RULES.getEndpoint());
        
        assertNotNull(rules);
        boolean foundRule = rules.stream()
                .anyMatch(rule -> TEST_RULE_NAME.equals(rule.getId()));
        
        assertTrue(foundRule, "Created rule should be in the list");
    }
    
    @Test
    void testGetRuleDetails() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(),
                request);
        
        try {
            // Get rule details
            RuleResponse ruleDetails = ApiRequestExecutor.execute(client,
                    StandardEndpoints.GET_RULE.getEndpoint(),
                    TEST_RULE_NAME);
            
            // Verify details
            assertNotNull(ruleDetails);
            assertEquals("SELECT * FROM " + TEST_STREAM_NAME, ruleDetails.getSql());
            
            // Verify actions
            assertNotNull(ruleDetails.getActions());
            assertEquals(1, ruleDetails.getActions().size());
            assertTrue(ruleDetails.getActions().get(0).containsKey("log"));
            
        } finally {
            // Clean up
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
        }
    }
    
    @Test
    void testUpdateRule() {
        // First create a rule
        CreateRuleRequest createRequest = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                createRequest);
        
        // Modify the rule - change SQL and add another action
        Map<String, Object> logAction = new HashMap<>();
        logAction.put("log", new HashMap<>());
        
        Map<String, Object> mqttAction = new HashMap<>();
        Map<String, String> mqttConfig = new HashMap<>();
        mqttConfig.put("server", "tcp://127.0.0.1:1883");
        mqttConfig.put("topic", "test/topic");
        mqttAction.put("mqtt", mqttConfig);
        
        List<Map<String, Object>> updatedActions = new ArrayList<>();
        updatedActions.add(logAction);
        updatedActions.add(mqttAction);
        
        CreateRuleRequest updateRequest = CreateRuleRequest.builder()
                .id(TEST_RULE_NAME)
                .sql("SELECT id, temperature FROM " + TEST_STREAM_NAME + " WHERE temperature > 30")
                .actions(updatedActions)
                .build();
        
        // Update the rule
        String updateResponse = ApiRequestExecutor.executeBody(client,
                StandardEndpoints.UPDATE_RULE.getEndpoint(), 
                updateRequest,
                TEST_RULE_NAME);
        
        assertNotNull(updateResponse);
        
        // Get rule details and verify update
        RuleResponse updatedRule = ApiRequestExecutor.execute(client,
                StandardEndpoints.GET_RULE.getEndpoint(), 
                TEST_RULE_NAME);
        
        // Verify details of updated rule
        assertNotNull(updatedRule);
        assertEquals("SELECT id, temperature FROM " + TEST_STREAM_NAME + " WHERE temperature > 30", 
                updatedRule.getSql());
        
        // Verify actions were updated
        assertNotNull(updatedRule.getActions());
        assertEquals(2, updatedRule.getActions().size());
        
        // Check that MQTT action was added
        boolean hasMqttAction = updatedRule.getActions().stream()
                .anyMatch(action -> action.containsKey("mqtt"));
        
        assertTrue(hasMqttAction, "Updated rule should have an MQTT action");
    }
    
    @Test
    void testRuleLifecycle() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        try {
            // Check initial status - rule should be running after creation
            List<RuleListResponse> rules = ApiRequestExecutor.execute(client,
                    StandardEndpoints.LIST_RULES.getEndpoint());
            
            RuleListResponse createdRule = rules.stream()
                    .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(createdRule);
            
            // Stop the rule
            String stopResponse = ApiRequestExecutor.execute(client,
                    StandardEndpoints.STOP_RULE.getEndpoint(), 
                    TEST_RULE_NAME);

            assertNotNull(stopResponse);
            
            // Verify rule is stopped
            rules = ApiRequestExecutor.execute(client,
                    StandardEndpoints.LIST_RULES.getEndpoint());
            System.out.println(rules);
            RuleListResponse stoppedRule = rules.stream()
                    .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(stoppedRule);
            assertTrue(stoppedRule.getStatus().startsWith("stopped"),
                    "Rule status should indicate stopped");
            
            // Start the rule
            String startResponse = ApiRequestExecutor.execute(client,
                    StandardEndpoints.START_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
            
            assertNotNull(startResponse);
            
            // Verify rule is running
            rules = ApiRequestExecutor.execute(client,
                    StandardEndpoints.LIST_RULES.getEndpoint());
            
            RuleListResponse startedRule = rules.stream()
                    .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(startedRule);
            assertEquals("running", startedRule.getStatus());

            // Restart the rule
            String restartResponse = ApiRequestExecutor.execute(client,
                    StandardEndpoints.RESTART_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
            
            assertNotNull(restartResponse);
            
            // Get rule status
            RuleStatusResponse statusResponse = ApiRequestExecutor.execute(client,
                    StandardEndpoints.GET_RULE_STATUS.getEndpoint(), 
                    TEST_RULE_NAME);
            
            assertNotNull(statusResponse);
            assertNotNull(statusResponse.getLastStartTimestamp());
            
        } finally {
            // Clean up
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
        }
    }
    
    @Test
    void testGetAllRulesStatus() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        try {
            // Get all rules status
            Map<String, RuleStatusResponse> allStatus = ApiRequestExecutor.execute(client,
                    StandardEndpoints.GET_ALL_RULES_STATUS.getEndpoint());
            
            assertNotNull(allStatus);
            assertTrue(allStatus.containsKey(TEST_RULE_NAME), 
                    "All rules status should include our test rule");
            
            RuleStatusResponse testRuleStatus = allStatus.get(TEST_RULE_NAME);
            assertNotNull(testRuleStatus);
            
        } finally {
            // Clean up
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
        }
    }
    
    @Test
    void testValidateRule() {
        // Create a valid rule request
        CreateRuleRequest validRequest = createTestRuleRequest();
        
        // Validate the rule
        String validationResponse = ApiRequestExecutor.executeBody(client,
                StandardEndpoints.VALIDATE_RULE.getEndpoint(), 
                validRequest);
        
        assertNotNull(validationResponse);
        
        // Create an invalid rule with syntax error
        CreateRuleRequest invalidRequest = CreateRuleRequest.builder()
                .id(TEST_RULE_NAME)
                .sql("SELECT * FROMM " + TEST_STREAM_NAME) // Intentional typo in FROMM
                .actions(validRequest.getActions())
                .build();
        
        // Validating should throw exception for invalid rule
        assertThrows(HttpClientException.class, () -> {
            ApiRequestExecutor.executeBody(client,
                    StandardEndpoints.VALIDATE_RULE.getEndpoint(), 
                    invalidRequest);
        });
    }
    
    @Test
    void testDeleteRule() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        // Verify the rule exists
        List<RuleListResponse> rulesBefore = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_RULES.getEndpoint());
        
        boolean hasRule = rulesBefore.stream()
                .anyMatch(r -> TEST_RULE_NAME.equals(r.getId()));
        
        assertTrue(hasRule, "Rule should exist before deletion");
        
        // Delete the rule
        String deleteResponse = ApiRequestExecutor.execute(client,
                StandardEndpoints.DELETE_RULE.getEndpoint(), 
                TEST_RULE_NAME);
        
        assertNotNull(deleteResponse);
        
        // Verify the rule no longer exists
        List<RuleListResponse> rulesAfter = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_RULES.getEndpoint());
        
        boolean stillHasRule = rulesAfter.stream()
                .anyMatch(r -> TEST_RULE_NAME.equals(r.getId()));
        
        assertFalse(stillHasRule, "Rule should not exist after deletion");
    }
    
    @Test
    void testExplainRule() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        try {
            // Get rule plan explanation
            String explanation = ApiRequestExecutor.execute(client,
                    StandardEndpoints.EXPLAIN_RULE.getEndpoint(),
                    TEST_RULE_NAME);
            
            // Verify explanation
            assertNotNull(explanation);
            assertFalse(explanation.isEmpty(), "Explanation should not be empty");
            
        } finally {
            // Clean up
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
        }
    }
    
    @Test
    void testGetRulesCpuUsage() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        
        // Create the rule
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_RULE.getEndpoint(), 
                request);
        
        try {
            // Let the rule run for a bit to generate some CPU usage
            Thread.sleep(2000);
            
            // Get CPU usage
            String cpuUsage = ApiRequestExecutor.execute(client,
                    StandardEndpoints.GET_RULES_CPU_USAGE.getEndpoint());

            System.out.println(cpuUsage);
            // Verify CPU usage response
            assertNotNull(cpuUsage);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test was interrupted");
        } finally {
            // Clean up
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_RULE.getEndpoint(), 
                    TEST_RULE_NAME);
        }
    }
}