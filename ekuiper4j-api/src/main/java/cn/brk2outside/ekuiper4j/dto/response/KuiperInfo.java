package cn.brk2outside.ekuiper4j.dto.response;


import lombok.experimental.FieldNameConstants;

import java.util.Map;

/**
 * <p> </p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/12
 */
@FieldNameConstants
public record KuiperInfo (
        String version,
        String os,
        int upTimeSeconds
) {

}
