package cn.brk2outside.ekuiper4j.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the HttpClient interface using Spring's RestTemplate.
 */
public class RestTemplateHttpClient implements HttpClient {

    private final RestTemplate restTemplate;
    private final HttpHeaders defaultHeaders;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * -- GETTER --
     *  Gets the base URL of the eKuiper instance.
     *
     * @return The base URL
     */
    @Getter
    private final String baseUrl;

    /**
     * Creates a new RestTemplateHttpClient with default configuration.
     * 
     * @param host The eKuiper host
     * @param port The eKuiper port
     */
    public RestTemplateHttpClient(String host, int port) {
        this.restTemplate = new RestTemplate();
        // Add converters to handle different content types
        this.setupRestTemplate(this.restTemplate);
        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.set("Content-Type", "application/json");
        this.defaultHeaders.set("Accept", "application/json");
        this.baseUrl = buildBaseUrl(host, port);
    }

    /**
     * Creates a new RestTemplateHttpClient with a custom RestTemplate.
     *
     * @param host         The eKuiper host
     * @param port         The eKuiper port
     * @param restTemplate The RestTemplate to use
     */
    public RestTemplateHttpClient(String host, int port, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Add converters to handle different content types
        this.setupRestTemplate(this.restTemplate);
        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.set("Content-Type", "application/json");
        this.defaultHeaders.set("Accept", "application/json");
        this.baseUrl = buildBaseUrl(host, port);
    }

    /**
     * Creates a new RestTemplateHttpClient with a custom RestTemplate and default headers.
     *
     * @param host           The eKuiper host
     * @param port           The eKuiper port
     * @param restTemplate   The RestTemplate to use
     * @param defaultHeaders Default headers to send with each request
     */
    public RestTemplateHttpClient(String host, int port, RestTemplate restTemplate, HttpHeaders defaultHeaders) {
        this.restTemplate = restTemplate;
        this.setupRestTemplate(this.restTemplate);
        this.defaultHeaders = defaultHeaders;
        this.baseUrl = buildBaseUrl(host, port);
    }

    private void setupRestTemplate(RestTemplate restTemplate) {
        if (null != restTemplate) {
            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
            stringHttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.ALL));
            MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
            jackson2HttpMessageConverter.setSupportedMediaTypes(List.of(MediaType.ALL));
            restTemplate.getMessageConverters().addAll(
                    List.of(
                            stringHttpMessageConverter,
                            jackson2HttpMessageConverter
                    )
            );
        }
    }

    /**
     * Builds the base URL from host and port.
     *
     * @param host The eKuiper host
     * @param port The eKuiper port
     * @return The base URL string
     */
    private String buildBaseUrl(String host, int port) {
        return String.format("http://%s:%d", host, port);
    }

    /**
     * Gets the headers to use for HTTP requests.
     * This method can be overridden by subclasses to provide dynamic headers.
     *
     * @return The headers to use for requests
     */
    protected HttpHeaders getHeaders() {
        return defaultHeaders;
    }
    
    @Override
    public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.GET, null, responseType, null, pathVariables);
    }
    
    @Override
    public <T> T get(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.GET, null, responseType, queryParams, pathVariables);
    }

    @Override
    public <T> T post(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.POST, requestBody, responseType, null, pathVariables);
    }
    
    @Override
    public <T> T post(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.POST, requestBody, responseType, queryParams, pathVariables);
    }
    
    @Override
    public <T> T put(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.PUT, requestBody, responseType, null, pathVariables);
    }
    
    @Override
    public <T> T put(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.PUT, requestBody, responseType, queryParams, pathVariables);
    }
    
    @Override
    public <T> T delete(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.DELETE, null, responseType, null, pathVariables);
    }

    @Override
    public <T> T delete(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
        return executeRequest(path, HttpMethod.DELETE, null, responseType, queryParams, pathVariables);
    }

    /**
     * Executes an HTTP request with the given parameters including varargs path variables and expected response type.
     *
     * @param path          The API path relative to the base URL (with path variable placeholders)
     * @param method        The HTTP method to use
     * @param requestBody   The request body (can be null)
     * @param responseType  The expected response type reference (for generic types)
     * @param queryParams   Query parameters to append to the URL (can be null or empty)
     * @param pathVariables Ordered variables to replace in the URL path
     * @param <T>           The expected response type
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    private <T> T executeRequest(String path, HttpMethod method, Object requestBody, 
                               ParameterizedTypeReference<T> responseType, Map<String, Object> queryParams, 
                               Object... pathVariables) throws HttpClientException {
        return executeRequestInternal(path, method, requestBody, responseType, queryParams, pathVariables);
    }

    /**
     * Common implementation for executing HTTP requests with different response type parameters and varargs.
     *
     * @param path           The API path relative to the base URL (with path variable placeholders)
     * @param method         The HTTP method to use
     * @param requestBody    The request body (can be null)
     * @param responseType   The expected response type reference
     * @param queryParams    Query parameters to append to the URL (can be null or empty)
     * @param pathVariables  Ordered variables to replace in the URL path
     * @param <T>            The expected response type
     * @return               The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    private <T> T executeRequestInternal(String path, HttpMethod method, Object requestBody, 
                                       ParameterizedTypeReference<T> responseType, Map<String, Object> queryParams,
                                       Object... pathVariables) throws HttpClientException {
        try {
            // Prepare and execute the request
            URI uri = buildUri(combinePath(baseUrl, path), queryParams, pathVariables);
            HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders());
//            System.out.println(method);
//            System.out.println(uri);
            ResponseEntity<T> response = restTemplate.exchange(uri, method, entity, responseType);
//            System.out.println(response);

            // Handle error responses with bodies that could be eKuiper errors
            if (response.getStatusCode().isError()) {
                // First try to parse as an eKuiper error
                HttpClientException ekuiperException = tryParseEKuiperError(response.getBody(), response.getStatusCode().value());
                if (ekuiperException != null) {
                    throw ekuiperException;
                }
                
                // For other error statuses, throw a generic exception with any available message
                String errorMessage = extractErrorMessage(response.getBody());
                throw new HttpClientException(
                        errorMessage != null ? errorMessage : "HTTP request failed with status code: " + response.getStatusCode(),
                        response.getStatusCode().value());
            }
            
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            // Handle Spring's HTTP exceptions that might contain eKuiper errors
            HttpClientException ekuiperException = tryParseEKuiperError(e.getResponseBodyAsString(), e.getStatusCode().value());
            if (ekuiperException != null) {
                throw ekuiperException;
            }
            
            throw new HttpClientException("HTTP request failed with status code: " + e.getStatusCode(), 
                    e, e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            if (e.getMessage() != null && e.getMessage().contains("Read timed out")) {
                throw HttpClientException.timeout("Connection to " + baseUrl + " timed out", e);
            }
            throw new HttpClientException("Error accessing resource: " + path, e, -1);
        } catch (Exception e) {
            // Preserve our custom exceptions
            if (e instanceof HttpClientException) {
                throw (HttpClientException) e;
            }
            throw new HttpClientException("Error executing HTTP request: " + e.getMessage(), e, -1);
        }
    }
    
    /**
     * Tries to parse an object or response body string as an eKuiper error response.
     * 
     * @param body The response body to parse (can be String or deserialized Object)
     * @param statusCode The HTTP status code
     * @return An HttpClientException if the body is a valid eKuiper error, null otherwise
     */
    private HttpClientException tryParseEKuiperError(Object body, int statusCode) {
        if (body == null) {
            return null;
        }
        
        try {
            // Convert the body to a string if needed
            String jsonBody;
            if (body instanceof String) {
                jsonBody = (String) body;
            } else {
                jsonBody = objectMapper.writeValueAsString(body);
            }
            
            // Try to parse as eKuiper error
            if (jsonBody != null && !jsonBody.isEmpty()) {
                EKuiperErrorResponse errorResponse = objectMapper.readValue(jsonBody, EKuiperErrorResponse.class);
                if (errorResponse.getErrorCode() > 0) {
                    return HttpClientException.ekuiperError(errorResponse, statusCode);
                }
            }
        } catch (Exception ignored) {
            // Not a valid eKuiper error
        }
        
        return null;
    }

    /**
     * Handle eKuiper-specific error responses.
     *
     * @param errorResponse The parsed error response
     * @param statusCode The HTTP status code
     * @throws HttpClientException with detailed eKuiper error information
     */
    private <T> T handleEKuiperError(EKuiperErrorResponse errorResponse, int statusCode) throws HttpClientException {
        throw HttpClientException.ekuiperError(errorResponse, statusCode);
    }

    /**
     * Combines base URL with a path, ensuring proper formatting.
     *
     * @param baseUrl The base URL
     * @param path    The path to append
     * @return The combined URL
     */
    private String combinePath(String baseUrl, String path) {
        if (path == null || path.isEmpty()) {
            return baseUrl;
        }
        
        if (path.startsWith("/")) {
            return baseUrl + path;
        } else {
            return baseUrl + "/" + path;
        }
    }

    /**
     * Builds a URI with the given URL and query parameters (no path variables).
     *
     * @param url         The base URL
     * @param queryParams The query parameters to append (can be null or empty)
     * @return            The built URI
     */
    private URI buildUri(String url, Map<String, Object> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return URI.create(url);
        }
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        queryParams.forEach((key, value) -> {
            if (value != null) {
                builder.queryParam(key, value);
            }
        });
        
        return builder.build().toUri();
    }
    
    /**
     * Builds a URI with the given URL, ordered path variables, and query parameters.
     *
     * @param url           The base URL with indexed path variable placeholders (e.g., "/api/{0}/details/{1}")
     * @param queryParams   The query parameters to append (can be null or empty)
     * @param pathVariables Ordered path variables to replace in the URL in order of appearance
     * @return              The built URI
     */
    private URI buildUri(String url, Map<String, Object> queryParams, Object... pathVariables) {
        // If we have no path variables, just use the regular method
        if (pathVariables == null || pathVariables.length == 0) {
            return buildUri(url, queryParams);
        }
        
        // Use RestTemplate's UriComponentsBuilder for indexed path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> {
                if (value != null) {
                    builder.queryParam(key, value);
                }
            });
        }
        
        // Let Spring handle the URL expansion with varargs
        return builder.buildAndExpand(pathVariables).toUri();
    }

    /**
     * Extracts a meaningful error message from the response body if possible.
     * 
     * @param body The response body
     * @return A meaningful error message if available, null otherwise
     */
    private String extractErrorMessage(Object body) {
        if (body == null) {
            return null;
        }
        
        try {
            // If it's a Map, try to extract a message field
            if (body instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) body;
                if (map.containsKey("message")) {
                    return String.valueOf(map.get("message"));
                }
                if (map.containsKey("error")) {
                    return String.valueOf(map.get("error"));
                }
            }
            
            // Try to convert to string
            return objectMapper.writeValueAsString(body);
        } catch (Exception ignored) {
            return String.valueOf(body);
        }
    }
} 