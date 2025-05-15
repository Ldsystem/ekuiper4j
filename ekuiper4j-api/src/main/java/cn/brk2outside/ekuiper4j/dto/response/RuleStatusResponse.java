package cn.brk2outside.ekuiper4j.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for rule status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleStatusResponse {

    /**
     * Last time the rule was started (Unix timestamp)
     */
    @JsonProperty("lastStartTimestamp")
    private Long lastStartTimestamp;

    /**
     * Last time the rule was stopped (Unix timestamp)
     */
    @JsonProperty("lastStopTimestamp")
    private Long lastStopTimestamp;

    /**
     * Next time the rule will start (Unix timestamp) for periodic rules
     */
    @JsonProperty("nextStartTimestamp")
    private Long nextStartTimestamp;

    /**
     * Additional metrics, dynamically added based on the rule configuration
     * Keys are metric names like source_demo_0_records_in_total, op_filter_0_records_in_total, etc.
     */
    private Map<String, Object> metrics;
} 