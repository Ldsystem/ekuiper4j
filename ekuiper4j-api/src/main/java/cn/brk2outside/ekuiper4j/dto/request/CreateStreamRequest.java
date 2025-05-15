package cn.brk2outside.ekuiper4j.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Request DTO for creating a stream</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStreamRequest {

    /**
     * SQL statement to create a stream
     * Example: create stream my_stream (id bigint, name string, score float) WITH 
     * ( datasource = "topic/temperature", FORMAT = "json", KEY = "id")
     */
    @NotBlank(message = "SQL statement cannot be blank")
    @JsonProperty("sql")
    private String sql;
} 