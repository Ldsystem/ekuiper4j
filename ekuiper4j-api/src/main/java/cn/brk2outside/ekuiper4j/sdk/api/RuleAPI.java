package cn.brk2outside.ekuiper4j.sdk.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateRuleRequest;
import cn.brk2outside.ekuiper4j.dto.response.RuleListResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleResponse;
import cn.brk2outside.ekuiper4j.dto.response.RuleStatusResponse;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * API for managing eKuiper rules
 */
@RequiredArgsConstructor
public class RuleAPI {

    private final HttpClient client;

    /**
     * Create a new rule
     *
     * @param request the rule creation request
     * @return success message
     */
    public String createRule(CreateRuleRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.CREATE_RULE.getEndpoint(), request);
    }

    /**
     * List all rules
     *
     * @return list of rules
     */
    public List<RuleListResponse> listRules() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.LIST_RULES.getEndpoint());
    }

    /**
     * Get details of a specific rule
     *
     * @param ruleName the name of the rule
     * @return rule details
     */
    public RuleResponse getRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_RULE.getEndpoint(), ruleName);
    }

    /**
     * Update an existing rule
     *
     * @param ruleName the name of the rule to update
     * @param request the update request
     * @return success message
     */
    public String updateRule(String ruleName, CreateRuleRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.UPDATE_RULE.getEndpoint(), request, ruleName);
    }

    /**
     * Delete a rule
     *
     * @param ruleName the name of the rule to delete
     * @return success message
     */
    public String deleteRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.DELETE_RULE.getEndpoint(), ruleName);
    }

    /**
     * Start a rule
     *
     * @param ruleName the name of the rule to start
     * @return success message
     */
    public String startRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.START_RULE.getEndpoint(), ruleName);
    }

    /**
     * Stop a rule
     *
     * @param ruleName the name of the rule to stop
     * @return success message
     */
    public String stopRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.STOP_RULE.getEndpoint(), ruleName);
    }

    /**
     * Restart a rule
     *
     * @param ruleName the name of the rule to restart
     * @return success message
     */
    public String restartRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.RESTART_RULE.getEndpoint(), ruleName);
    }

    /**
     * Get status of a specific rule
     *
     * @param ruleName the name of the rule
     * @return rule status
     */
    public RuleStatusResponse getRuleStatus(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_RULE_STATUS.getEndpoint(), ruleName);
    }

    /**
     * Get status of all rules
     *
     * @return map of rule statuses
     */
    public Map<String, RuleStatusResponse> getAllRulesStatus() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_ALL_RULES_STATUS.getEndpoint());
    }

    /**
     * Validate a rule
     *
     * @param request the rule to validate
     * @return validation result
     */
    public String validateRule(CreateRuleRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.VALIDATE_RULE.getEndpoint(), request);
    }

    /**
     * Get explanation of a rule
     *
     * @param ruleName the name of the rule
     * @return explanation of the rule
     */
    public String explainRule(String ruleName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.EXPLAIN_RULE.getEndpoint(), ruleName);
    }

    /**
     * Get CPU usage of all rules
     *
     * @return CPU usage information
     */
    public String getRulesCpuUsage() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_RULES_CPU_USAGE.getEndpoint());
    }
} 