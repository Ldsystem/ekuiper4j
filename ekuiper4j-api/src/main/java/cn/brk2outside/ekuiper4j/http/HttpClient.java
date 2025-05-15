package cn.brk2outside.ekuiper4j.http;

import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

/**
 * HTTP client interface providing methods for making HTTP requests.
 */
public interface HttpClient {

    /**
     * Performs a GET request to the specified path with path variables and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

    /**
     * Performs a GET request to the specified path with path variables, query parameters, and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param queryParams   Query parameters to append to the URL (can be null or empty)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T get(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

    /**
     * Performs a POST request with a request body, path variables, and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param requestBody   The request body (can be null)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T post(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

    /**
     * Performs a POST request with a request body, path variables, query parameters, and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param requestBody   The request body (can be null)
     * @param queryParams   Query parameters to append to the URL (can be null or empty)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T post(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;
    /**
     * Performs a PUT request with a request body, path variables, and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param requestBody   The request body (can be null)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T put(String path, Object requestBody, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

    /**
     * Performs a PUT request with a request body, path variables, query parameters, and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param requestBody   The request body (can be null)
     * @param queryParams   Query parameters to append to the URL (can be null or empty)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T put(String path, Object requestBody, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

    /**
     * Performs a DELETE request to the specified path with path variables and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T delete(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;
    
    /**
     * Performs a DELETE request to the specified path with path variables, query parameters and expected response type.
     *
     * @param path          The API path to send the request to (with path variable placeholders, e.g., "/api/{id}/details")
     * @param queryParams   Query parameters to append to the URL (can be null or empty)
     * @param responseType  The expected response type class
     * @param <T>           The expected response type
     * @param pathVariables Variables to replace in the URL path in order of appearance
     * @return              The response body as the expected type
     * @throws HttpClientException if the request fails or returns an invalid status code
     */
    <T> T delete(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException;

} 