package cn.brk2outside.ekuiper4j.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for rule details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {

    /**
     * SQL statement for the rule
     */
    @JsonProperty("sql")
    private String sql;

    /**
     * Actions to be executed when the rule conditions are met
     */
    @JsonProperty("actions")
    private List<Map<String, Object>> actions;
} 