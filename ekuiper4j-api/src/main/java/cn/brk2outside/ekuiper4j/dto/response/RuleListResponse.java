package cn.brk2outside.ekuiper4j.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for rule list item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleListResponse {

    /**
     * Rule identifier
     */
    @JsonProperty("id")
    private String id;

    /**
     * Rule status
     */
    @JsonProperty("status")
    private String status;
} 