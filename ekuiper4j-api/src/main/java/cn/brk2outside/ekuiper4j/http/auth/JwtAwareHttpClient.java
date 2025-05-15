package cn.brk2outside.ekuiper4j.http.auth;

import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.http.HttpClientException;
import cn.brk2outside.ekuiper4j.http.RestTemplateHttpClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Supplier;

/**
 * HTTP client implementation that uses JwtTokenManager to get fresh tokens for each request.
 */
public class JwtAwareHttpClient implements HttpClient {
    
    private final RestTemplateHttpClient delegate;
    private final JwtTokenManager tokenManager;
    private final Supplier<HttpHeaders> headersSupplier;
    
    /**
     * Creates a new JWT-aware HTTP client.
     *
     * @param host        The eKuiper host
     * @param port        The eKuiper port  
     * @param restTemplate The RestTemplate to use
     * @param baseHeaders Base headers (without Authorization)
     * @param tokenManager The JWT token manager
     */
    public JwtAwareHttpClient(String host, int port, RestTemplate restTemplate, 
                              HttpHeaders baseHeaders, JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
        
        // Create headers supplier that adds fresh token for each call
        this.headersSupplier = () -> {
            HttpHeaders headers = new HttpHeaders();
            // Copy all base headers
            headers.putAll(baseHeaders);
            
            // Add current token
            String token = tokenManager.getToken();
            if (token != null) {
                headers.set("Authorization", token);
            }
            
            return headers;
        };
        
        // Create delegate with dynamic headers
        this.delegate = new RestTemplateHttpClient(host, port, restTemplate) {
            @Override
            protected HttpHeaders getHeaders() {
                return headersSupplier.get();
            }
        };
    }

    @Override
    public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) 
            throws HttpClientException {
        return delegate.get(path, responseType, pathVariables);
    }

    @Override
    public <T> T get(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) 
            throws HttpClientException {
        return delegate.get(path, queryParams, responseType, pathVariables);
    }

    @Override
    public <T> T post(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.post(path, requestBody, responseType, pathVariables);
    }

    @Override
    public <T> T post(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.post(path, requestBody, queryParams, responseType, pathVariables);
    }

    @Override
    public <T> T put(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.put(path, requestBody, responseType, pathVariables);
    }

    @Override
    public <T> T put(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.put(path, requestBody, queryParams, responseType, pathVariables);
    }

    @Override
    public <T> T delete(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.delete(path, responseType, pathVariables);
    }

    @Override
    public <T> T delete(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return delegate.delete(path, queryParams, responseType, pathVariables);
    }

} 