package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.constants.HttpMethods;
import cn.brk2outside.ekuiper4j.sdk.endpoint.ApiEndpoint;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.http.HttpClientException;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Map;

/**
 * Utility class for executing API requests using defined endpoints.
 * Handles the interaction between HTTP client and endpoint definitions.
 */
public final class ApiRequestExecutor {

    private ApiRequestExecutor() {
        // Utility class, do not instantiate
    }

    /**
     * Executes an API request defined by the endpoint with path variables.
     *
     * @param httpClient The HTTP client to use
     * @param endpoint The API endpoint definition
     * @param pathVariables Path variables to replace in the endpoint path
     * @param <T> Request body type
     * @param <R> Response type
     * @return The converted response
     * @throws HttpClientException if the request fails
     */
    public static <T, R> R execute(HttpClient httpClient, ApiEndpoint<T, R> endpoint, Object... pathVariables) throws HttpClientException {
        validatePathVariableCount(endpoint, pathVariables);
        return executeBodyAndQs(httpClient, endpoint, null, null, pathVariables);
    }

    /**
     * Executes an API request defined by the endpoint with a request body and path variables.
     *
     * @param httpClient The HTTP client to use
     * @param endpoint The API endpoint definition
     * @param requestBody The request body (can be null)
     * @param pathVariables Path variables to replace in the endpoint path
     * @param <T> Request body type
     * @param <R> Response type
     * @return The converted response
     * @throws HttpClientException if the request fails
     */
    public static <T, R> R executeBody(HttpClient httpClient, ApiEndpoint<T, R> endpoint, T requestBody, Object... pathVariables) throws HttpClientException {
        validatePathVariableCount(endpoint, pathVariables);
        return executeBodyAndQs(httpClient, endpoint, requestBody, null, pathVariables);
    }

    /**
     * Executes an API request defined by the endpoint with path variables and query parameters.
     *
     * @param httpClient The HTTP client to use
     * @param endpoint The API endpoint definition
     * @param queryParams Query parameters to append to the URL
     * @param pathVariables Path variables to replace in the endpoint path
     * @param <T> Request body type
     * @param <R> Response type
     * @return The converted response
     * @throws HttpClientException if the request fails
     */
    public static <T, R> R executeQs(HttpClient httpClient, ApiEndpoint<T, R> endpoint,
                                     Map<String, Object> queryParams, Object... pathVariables) throws HttpClientException {
        validatePathVariableCount(endpoint, pathVariables);
        return executeBodyAndQs(httpClient, endpoint, null, queryParams, pathVariables);
    }

    /**
     * Executes an API request defined by the endpoint with a request body, path variables, and query parameters.
     *
     * @param httpClient The HTTP client to use
     * @param endpoint The API endpoint definition
     * @param requestBody The request body (can be null)
     * @param queryParams Query parameters to append to the URL
     * @param pathVariables Path variables to replace in the endpoint path
     * @param <T> Request body type
     * @param <R> Response type
     * @return The converted response
     * @throws HttpClientException if the request fails
     */
    public static <T, R> R executeBodyAndQs(HttpClient httpClient, ApiEndpoint<T, R> endpoint, T requestBody,
                                            Map<String, Object> queryParams, Object... pathVariables) throws HttpClientException {
        validatePathVariableCount(endpoint, pathVariables);

        String path = endpoint.getEndpoint();
        HttpMethods method = endpoint.getMethod();
        ParameterizedTypeReference<R> responseType = endpoint.getResponseClz();

        switch (method) {
            case GET:
                if (queryParams != null && !queryParams.isEmpty()) {
                    return httpClient.get(path, queryParams, responseType, pathVariables);
                } else {
                    return httpClient.get(path, responseType, pathVariables);
                }
            case POST:
                if (queryParams != null && !queryParams.isEmpty()) {
                    return httpClient.post(path, requestBody, queryParams, responseType, pathVariables);
                } else {
                    return httpClient.post(path, requestBody, responseType, pathVariables);
                }
            case PUT:
                if (queryParams != null && !queryParams.isEmpty()) {
                    return httpClient.put(path, requestBody, queryParams, responseType, pathVariables);
                } else {
                    return httpClient.put(path, requestBody, responseType, pathVariables);
                }
            case DELETE:
                if (queryParams != null && !queryParams.isEmpty()) {
                    return httpClient.delete(path, queryParams, responseType, pathVariables);
                } else {
                    return httpClient.delete(path, responseType, pathVariables);
                }
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    /**
     * Validates that the provided path variables match the expected count from the endpoint definition.
     *
     * @param endpoint       The API endpoint definition
     * @param pathVariables  The path variables provided for the request
     * @throws IllegalArgumentException if the path variable count doesn't match the endpoint's expected count
     */
    private static void validatePathVariableCount(ApiEndpoint<?, ?> endpoint, Object[] pathVariables) {
        int expectedCount = endpoint.getPathVariableCount();
        int actualCount = pathVariables != null ? pathVariables.length : 0;

        if (expectedCount != actualCount) {
            throw new IllegalArgumentException(
                    String.format("Path variable count mismatch for endpoint %s: expected %d but got %d",
                            endpoint.getEndpoint(), expectedCount, actualCount));
        }
    }
}