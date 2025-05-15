package cn.brk2outside.ekuiper4j.config;

import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.http.RestTemplateHttpClient;
import cn.brk2outside.ekuiper4j.http.auth.JwtAwareHttpClient;
import cn.brk2outside.ekuiper4j.http.auth.JwtTokenManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Auto-configuration for the eKuiper HTTP client.
 */
@Configuration
@EnableConfigurationProperties(EKuiperClientProperties.class)
@EnableScheduling
public class EKuiperClientAutoConfiguration {

    /**
     * Creates a JwtTokenManager bean if JWT authentication is enabled.
     *
     * @param properties The eKuiper client properties
     * @return The JWT token manager
     */
    @Bean
    @ConditionalOnProperty(value = "ekuiper.client.jwt.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public JwtTokenManager jwtTokenManager(EKuiperClientProperties properties) {
        return new JwtTokenManager(properties);
    }

    /**
     * Creates the HTTP client bean if not already defined.
     *
     * @param properties The eKuiper client properties
     * @param jwtTokenManager JWT token manager (optional, injected if JWT auth is enabled)
     * @return A configured HttpClient instance
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpClient ekuiperHttpClient(
            EKuiperClientProperties properties,
            @org.springframework.beans.factory.annotation.Autowired(required = false) JwtTokenManager jwtTokenManager) {
        
        // Create the request factory with configured timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        
        // Create the RestTemplate with the configured request factory
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        
        // Create base headers
        HttpHeaders baseHeaders = new HttpHeaders();
        baseHeaders.set("Content-Type", "application/json");
        baseHeaders.set("Accept", "application/json");
        
        // JWT authentication takes precedence if enabled
        if (properties.getJwt().isEnabled() && jwtTokenManager != null) {
            // Use JwtAwareHttpClient that gets fresh tokens for each request
            return new JwtAwareHttpClient(
                    properties.getHost(),
                    properties.getPort(),
                    restTemplate,
                    baseHeaders,
                    jwtTokenManager
            );
        }
        // Basic authentication 
        else if (properties.isAuthEnabled() && properties.getUsername() != null && properties.getPassword() != null) {
            // Create a supplier that provides headers with basic auth
            final HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.putAll(baseHeaders);
            authHeaders.setBasicAuth(properties.getUsername(), properties.getPassword());
            
            return new RestTemplateHttpClient(properties.getHost(), properties.getPort(), restTemplate) {
                @Override
                protected HttpHeaders getHeaders() {
                    return authHeaders;
                }
            };
        }
        // No authentication
        else {
            return new RestTemplateHttpClient(
                    properties.getHost(),
                    properties.getPort(),
                    restTemplate,
                    baseHeaders
            );
        }
    }
} 