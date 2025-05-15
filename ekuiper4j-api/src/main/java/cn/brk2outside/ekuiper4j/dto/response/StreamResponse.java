package cn.brk2outside.ekuiper4j.dto.response;

import cn.brk2outside.ekuiper4j.model.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Response DTO for stream operations</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamResponse {

    /**
     * Stream definition
     */
    private Stream stream;
} 