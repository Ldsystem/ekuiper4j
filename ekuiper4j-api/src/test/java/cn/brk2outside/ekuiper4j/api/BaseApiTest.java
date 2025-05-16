package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.http.BaseEKuiperTest;
import cn.brk2outside.ekuiper4j.http.RestTemplateHttpClient;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base test class for API tests that sets up the HTTP client using the test container.
 */
public abstract class BaseApiTest extends BaseEKuiperTest {

    protected RestTemplateHttpClient client;
    
    @BeforeEach
    void setUp() {
        client = new RestTemplateHttpClient(
                EKUIPER.getEkuiperHost(),
                EKUIPER.getEkuiperPort()
        );
    }
} 