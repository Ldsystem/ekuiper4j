package cn.brk2outside.ekuiper4j.sdk.endpoint;

import cn.brk2outside.ekuiper4j.constants.HttpMethods;
import cn.brk2outside.ekuiper4j.sdk.util.ResponseConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;

@Getter
@RequiredArgsConstructor
public class ApiEndpoint<T, R> {

    protected final HttpMethods method;
    protected final String endpoint;
    protected final Class<T> requestClz;
    protected final ParameterizedTypeReference<R> responseClz;
    protected final int pathVariableCount;

}
