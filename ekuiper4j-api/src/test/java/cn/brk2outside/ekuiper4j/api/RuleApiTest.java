package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateRuleRequest;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.RuleListResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleStatusResponse;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import cn.brk2outside.ekuiper4j.sdk.api.RuleAPI;
import cn.brk2outside.ekuiper4j.sdk.api.StreamAPI;
import cn.brk2outside.ekuiper4j.utils.StreamFieldBuilder;
import cn.brk2outside.ekuiper4j.utils.StreamSqlBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RuleAPI endpoints
 */
public class RuleApiTest extends BaseApiTest {

    private static final String TEST_RULE_NAME = "test_rule_api";
    private static final String TEST_STREAM_NAME = "test_stream_for_rule_api";
    
    private RuleAPI ruleAPI;
    private StreamAPI streamAPI;

    @BeforeEach
    void setUpRuleApi() {
        ruleAPI = new RuleAPI(client);
        streamAPI = new StreamAPI(client);
        
        // Clean up any existing test rules and streams
        cleanupTestRule();
        cleanupTestStream();
        
        // Create test stream for rules to use
        createTestStream();
    }

    @AfterEach
    void tearDownRule() {
        // Clean up test rules and streams
        cleanupTestRule();
        cleanupTestStream();
    }

    private void cleanupTestRule() {
        try {
            ruleAPI.deleteRule(TEST_RULE_NAME);
        } catch (Exception e) {
            // Ignore errors if rule doesn't exist
        }
    }

    private void cleanupTestStream() {
        try {
            streamAPI.deleteStream(TEST_STREAM_NAME);
        } catch (Exception e) {
            // Ignore errors if stream doesn't exist
        }
    }

    private void createTestStream() {
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
        
        streamAPI.createStream(request);
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
        String response = ruleAPI.createRule(request);
        
        // Verify the response
        assertNotNull(response);
        
        // List rules and verify our rule exists
        List<RuleListResponse> rules = ruleAPI.listRules();
        
        assertNotNull(rules);
        boolean foundRule = rules.stream()
                .anyMatch(rule -> TEST_RULE_NAME.equals(rule.getId()));
        
        assertTrue(foundRule, "Created rule should be in the list");
    }

    @Test
    void testGetRuleDetails() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        ruleAPI.createRule(request);
        
        // Get rule details
        RuleResponse ruleDetails = ruleAPI.getRule(TEST_RULE_NAME);
        
        // Verify details
        assertNotNull(ruleDetails);
        assertEquals("SELECT * FROM " + TEST_STREAM_NAME, ruleDetails.getSql());
        
        // Verify actions
        assertNotNull(ruleDetails.getActions());
        assertEquals(1, ruleDetails.getActions().size());
        assertTrue(ruleDetails.getActions().get(0).containsKey("log"));
    }

    @Test
    void testUpdateRule() {
        // First create a rule
        CreateRuleRequest createRequest = createTestRuleRequest();
        ruleAPI.createRule(createRequest);
        
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
        String updateResponse = ruleAPI.updateRule(TEST_RULE_NAME, updateRequest);
        
        assertNotNull(updateResponse);
        
        // Get rule details and verify update
        RuleResponse updatedRule = ruleAPI.getRule(TEST_RULE_NAME);
        
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
    void testRuleLifecycle() throws InterruptedException {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        ruleAPI.createRule(request);
        
        // Check initial status - rule should be running after creation
        List<RuleListResponse> rules = ruleAPI.listRules();
        
        RuleListResponse createdRule = rules.stream()
                .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(createdRule);
        
        // Stop the rule
        String stopResponse = ruleAPI.stopRule(TEST_RULE_NAME);
        assertNotNull(stopResponse);
        
        // Wait a moment for status to update
        Thread.sleep(1000);
        
        // Verify rule is stopped
        rules = ruleAPI.listRules();
        RuleListResponse stoppedRule = rules.stream()
                .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(stoppedRule);
        assertTrue(stoppedRule.getStatus().startsWith("stopped"),
                "Rule status should indicate stopped");
        
        // Start the rule
        String startResponse = ruleAPI.startRule(TEST_RULE_NAME);
        assertNotNull(startResponse);
        
        // Wait a moment for status to update
        Thread.sleep(1000);
        
        // Verify rule is running
        rules = ruleAPI.listRules();
        RuleListResponse startedRule = rules.stream()
                .filter(r -> TEST_RULE_NAME.equals(r.getId()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(startedRule);
        assertEquals("running", startedRule.getStatus());
        
        // Restart the rule
        String restartResponse = ruleAPI.restartRule(TEST_RULE_NAME);
        assertNotNull(restartResponse);
        
        // Wait a moment for status to update
        Thread.sleep(1000);
        
        // Get rule status
        RuleStatusResponse statusResponse = ruleAPI.getRuleStatus(TEST_RULE_NAME);
        
        assertNotNull(statusResponse);
        assertNotNull(statusResponse.getLastStartTimestamp());
    }

    @Test
    void testGetAllRulesStatus() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        ruleAPI.createRule(request);
        
        // Get all rules status
        Map<String, RuleStatusResponse> allStatus = ruleAPI.getAllRulesStatus();
        
        assertNotNull(allStatus);
        assertTrue(allStatus.containsKey(TEST_RULE_NAME), 
                "All rules status should include our test rule");
        
        RuleStatusResponse testRuleStatus = allStatus.get(TEST_RULE_NAME);
        assertNotNull(testRuleStatus);
    }

    @Test
    void testValidateRule() {
        // Create a valid rule request
        CreateRuleRequest validRequest = createTestRuleRequest();
        
        // Validate the rule
        String validationResponse = ruleAPI.validateRule(validRequest);
        
        assertNotNull(validationResponse);
        
        // Create an invalid rule with syntax error
        CreateRuleRequest invalidRequest = CreateRuleRequest.builder()
                .id(TEST_RULE_NAME)
                .sql("SELECT * FROMM " + TEST_STREAM_NAME) // Intentional typo in FROMM
                .actions(validRequest.getActions())
                .build();
        
        // Validating should throw exception for invalid rule
        assertThrows(Exception.class, () -> {
            ruleAPI.validateRule(invalidRequest);
        });
    }

    @Test
    void testExplainRule() {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        ruleAPI.createRule(request);
        
        // Get rule plan explanation
        String explanation = ruleAPI.explainRule(TEST_RULE_NAME);
        
        // Verify explanation
        assertNotNull(explanation);
        assertFalse(explanation.isEmpty(), "Explanation should not be empty");
    }

    @Test
    void testGetRulesCpuUsage() throws InterruptedException {
        // First create a rule
        CreateRuleRequest request = createTestRuleRequest();
        ruleAPI.createRule(request);
        
        // Let the rule run for a bit to generate some CPU usage
        Thread.sleep(2000);
        
        // Get CPU usage
        String cpuUsage = ruleAPI.getRulesCpuUsage();
        
        // Verify CPU usage response
        assertNotNull(cpuUsage);
    }
} 