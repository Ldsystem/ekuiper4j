package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.http.auth.JwtAwareHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Base test class for API tests that sets up the HTTP client using the test container.
 */
public abstract class BaseApiTest extends BaseEKuiperAuthTest {

    protected HttpClient client;
    
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        properties.setHost(EKUIPER.getEkuiperHost());
        properties.setPort(EKUIPER.getEkuiperPort());

        HttpHeaders baseHeaders = new HttpHeaders();
        baseHeaders.set("Content-Type", "application/json");
        baseHeaders.set("Accept", "application/json");

        client = new JwtAwareHttpClient(
                properties.getHost(),
                properties.getPort(),
                new RestTemplate(),
                baseHeaders,
                tokenManager
        );
    }
} 