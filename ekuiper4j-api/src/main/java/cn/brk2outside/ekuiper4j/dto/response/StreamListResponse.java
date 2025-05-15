package cn.brk2outside.ekuiper4j.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Response DTO for listing streams</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamListResponse {

    /**
     * List of stream names
     */
    private List<String> streams;
} 