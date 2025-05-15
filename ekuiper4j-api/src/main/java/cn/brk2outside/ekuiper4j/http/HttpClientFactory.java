package cn.brk2outside.ekuiper4j.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Factory class for creating HTTP clients with different configurations.
 */
public class HttpClientFactory {

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000; // 5 seconds
    private static final int DEFAULT_READ_TIMEOUT = 15000;   // 15 seconds
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9081; // eKuiper default REST port

    /**
     * Creates a default HTTP client with standard timeouts and localhost configuration.
     *
     * @return A new HttpClient instance
     */
    public static HttpClient createDefault() {
        return createDefault(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * Creates a default HTTP client with standard timeouts and specified host/port.
     *
     * @param host The eKuiper host
     * @param port The eKuiper port
     * @return A new HttpClient instance
     */
    public static HttpClient createDefault(String host, int port) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT);
        
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new RestTemplateHttpClient(host, port, restTemplate);
    }

    /**
     * Creates an HTTP client with custom timeouts.
     *
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param readTimeoutMs    Read timeout in milliseconds
     * @return A new HttpClient instance with the specified timeouts
     */
    public static HttpClient createWithTimeouts(int connectTimeoutMs, int readTimeoutMs) {
        return createWithTimeouts(DEFAULT_HOST, DEFAULT_PORT, connectTimeoutMs, readTimeoutMs);
    }
    
    /**
     * Creates an HTTP client with custom timeouts and specified host/port.
     *
     * @param host             The eKuiper host
     * @param port             The eKuiper port
     * @param connectTimeoutMs Connection timeout in milliseconds
     * @param readTimeoutMs    Read timeout in milliseconds
     * @return A new HttpClient instance with the specified timeouts
     */
    public static HttpClient createWithTimeouts(String host, int port, int connectTimeoutMs, int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);
        
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new RestTemplateHttpClient(host, port, restTemplate);
    }

    /**
     * Creates an HTTP client with custom headers.
     *
     * @param headers Custom HTTP headers to include with every request
     * @return A new HttpClient instance with the specified headers
     */
    public static HttpClient createWithHeaders(HttpHeaders headers) {
        return createWithHeaders(DEFAULT_HOST, DEFAULT_PORT, headers);
    }
    
    /**
     * Creates an HTTP client with custom headers and specified host/port.
     *
     * @param host    The eKuiper host
     * @param port    The eKuiper port
     * @param headers Custom HTTP headers to include with every request
     * @return A new HttpClient instance with the specified headers
     */
    public static HttpClient createWithHeaders(String host, int port, HttpHeaders headers) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT);
        
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new RestTemplateHttpClient(host, port, restTemplate, headers);
    }

    /**
     * Creates an HTTP client with a custom request factory.
     *
     * @param requestFactory Custom request factory for the RestTemplate
     * @return A new HttpClient instance with the specified request factory
     */
    public static HttpClient createWithRequestFactory(ClientHttpRequestFactory requestFactory) {
        return createWithRequestFactory(DEFAULT_HOST, DEFAULT_PORT, requestFactory);
    }
    
    /**
     * Creates an HTTP client with a custom request factory and specified host/port.
     *
     * @param host           The eKuiper host
     * @param port           The eKuiper port
     * @param requestFactory Custom request factory for the RestTemplate
     * @return A new HttpClient instance with the specified request factory
     */
    public static HttpClient createWithRequestFactory(String host, int port, ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new RestTemplateHttpClient(host, port, restTemplate);
    }
} 