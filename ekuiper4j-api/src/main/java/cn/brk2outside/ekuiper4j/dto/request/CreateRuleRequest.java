package cn.brk2outside.ekuiper4j.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating or updating a rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRuleRequest {

    /**
     * Rule identifier
     */
    @NotBlank(message = "Rule id cannot be blank")
    @JsonProperty("id")
    private String id;

    /**
     * SQL statement for the rule
     */
    @NotBlank(message = "SQL statement cannot be blank")
    @JsonProperty("sql")
    private String sql;

    /**
     * Actions to be executed when the rule conditions are met
     */
    @NotEmpty(message = "At least one action must be specified")
    @JsonProperty("actions")
    private List<Map<String, Object>> actions;
} 