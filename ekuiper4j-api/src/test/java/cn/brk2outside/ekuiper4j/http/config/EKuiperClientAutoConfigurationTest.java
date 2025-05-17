package cn.brk2outside.ekuiper4j.http.config;

import cn.brk2outside.ekuiper4j.config.EKuiperClientAutoConfiguration;
import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import cn.brk2outside.ekuiper4j.http.BaseEKuiperTest;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.http.HttpClientException;
import cn.brk2outside.ekuiper4j.http.RestTemplateHttpClient;
import cn.brk2outside.ekuiper4j.http.auth.JwtAwareHttpClient;
import cn.brk2outside.ekuiper4j.http.auth.JwtTokenManager;
import cn.brk2outside.ekuiper4j.sdk.util.TypeUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the EKuiperClientAutoConfiguration class.
 */
class EKuiperClientAutoConfigurationTest extends BaseEKuiperTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(EKuiperClientAutoConfiguration.class));

    private final ParameterizedTypeReference<List<String>> stringArrTypeRef = TypeUtil.parameterizedType(new TypeReference<>() {
    });

    @Test
    void testNoAuthConfiguration() {
        contextRunner
                .withPropertyValues(
                        "ekuiper.client.host=" + EKUIPER.getEkuiperHost(),
                        "ekuiper.client.port=" + EKUIPER.getEkuiperPort(),
                        "ekuiper.client.auth-enabled=false",
                        "ekuiper.client.jwt.enabled=false"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpClient.class);
                    HttpClient client = context.getBean(HttpClient.class);
                    assertThat(client).isInstanceOf(RestTemplateHttpClient.class);
                    
                    // Test the client works
                    Object response = client.get("rules", stringArrTypeRef);
                    assertNotNull(response);
                    assertTrue(response instanceof List<?>, "Response should be a List");
                });
    }
    
    @Test
    void testBasicAuthConfiguration() {
        contextRunner
                .withPropertyValues(
                        "ekuiper.client.host=" + EKUIPER.getEkuiperHost(),
                        "ekuiper.client.port=" + EKUIPER.getEkuiperPort(),
                        "ekuiper.client.auth-enabled=true",
                        "ekuiper.client.username=admin",
                        "ekuiper.client.password=public",
                        "ekuiper.client.jwt.enabled=false"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpClient.class);
                    HttpClient client = context.getBean(HttpClient.class);
                    assertThat(client).isInstanceOf(RestTemplateHttpClient.class);
                    
                    // Test the client works (note: eKuiper might not require auth in test mode)
                    List<?> response = client.get("rules", stringArrTypeRef);
                    assertNotNull(response);
                });
    }
    
    @Test
    void testJwtAuthConfiguration() {
        contextRunner
                .withPropertyValues(
                        "ekuiper.client.host=" + EKUIPER.getEkuiperHost(),
                        "ekuiper.client.port=" + EKUIPER.getEkuiperPort(),
                        "ekuiper.client.auth-enabled=false",
                        "ekuiper.client.jwt.enabled=true",
                        "ekuiper.client.jwt.issuer=auto-config-test.pub",
                        "ekuiper.client.jwt.audience=eKuiper",
                        "ekuiper.client.jwt.ekuiper-mgmt-path=" + EKUIPER.getMgmtDirectory()
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpClient.class);
                    assertThat(context).hasSingleBean(JwtTokenManager.class);
                    
                    HttpClient client = context.getBean(HttpClient.class);
                    assertThat(client).isInstanceOf(JwtAwareHttpClient.class);
                    
                    // Test the client works
                    List<String> rules = assertDoesNotThrow(() -> client.get("rules", stringArrTypeRef));
                    assertNotNull(rules);
                });
    }
    
    @Test
    void testCustomHttpClientBean() {
        contextRunner
                .withPropertyValues(
                        "ekuiper.client.host=" + EKUIPER.getEkuiperHost(),
                        "ekuiper.client.port=" + EKUIPER.getEkuiperPort()
                )
                .withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(HttpClient.class);
                    HttpClient client = context.getBean(HttpClient.class);
                    
                    // Should be our custom implementation from the configuration
                    assertThat(client).isInstanceOf(CustomHttpClient.class);
                });
    }
    
    /**
     * Test configuration with a custom HttpClient bean.
     */
    @Configuration
    @EnableConfigurationProperties(EKuiperClientProperties.class)
    static class CustomClientConfiguration {
        
        @Bean
        public HttpClient ekuiperHttpClient(EKuiperClientProperties properties) {
            return new CustomHttpClient(properties.getHost(), properties.getPort());
        }
    }
    
    /**
     * Custom HttpClient implementation for testing.
     */
    static class CustomHttpClient implements HttpClient {
        private final RestTemplateHttpClient delegate;
        
        public CustomHttpClient(String host, int port) {
            this.delegate = new RestTemplateHttpClient(host, port);
        }
        
        @Override
        public <T> T get(String path, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
            return delegate.get(path, responseType, pathVariables);
        }
        
        @Override
        public <T> T get(String path, Map<String, Object> queryParams, ParameterizedTypeReference<T> responseType, Object... pathVariables) throws HttpClientException {
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
} 