package cn.brk2outside.ekuiper4j.http.auth;

import cn.brk2outside.ekuiper4j.http.BaseEKuiperTest;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test JWT authentication with eKuiper.
 */
class JwtAuthTest extends BaseEKuiperTest {
    
    private EKuiperClientProperties properties;
    private JwtTokenManager tokenManager;
    private HttpClient jwtClient;
    
    @BeforeEach
    void setUp() {
        // Configure properties
        properties = new EKuiperClientProperties();
        properties.setHost(EKUIPER.getEkuiperHost());
        properties.setPort(EKUIPER.getEkuiperPort());
        
        // Configure JWT
        EKuiperClientProperties.JwtAuth jwtAuth = new EKuiperClientProperties.JwtAuth();
        jwtAuth.setEnabled(true);
        jwtAuth.setIssuer("test-client.pub");
        jwtAuth.setAudience("eKuiper");
        jwtAuth.setExpirationTimeSeconds(3600);
        jwtAuth.setEkuiperMgmtPath(EKUIPER.getMgmtDirectory().toString());
        
        properties.setJwt(jwtAuth);
        
        // Create token manager and client
        tokenManager = new JwtTokenManager(properties);
        
        HttpHeaders baseHeaders = new HttpHeaders();
        baseHeaders.set("Content-Type", "application/json");
        baseHeaders.set("Accept", "application/json");
        
        jwtClient = new JwtAwareHttpClient(
                properties.getHost(),
                properties.getPort(),
                new RestTemplate(),
                baseHeaders,
                tokenManager
        );
    }
    
    @Test
    void testTokenGeneration() {
        // Verify token was generated
        String token = tokenManager.getToken();
        assertNotNull(token);

        // Check that public key file was created in management directory
        Path publicKeyPath = EKUIPER.getMgmtDirectory().resolve(properties.getJwt().getIssuer());
        assertTrue(Files.exists(publicKeyPath), "Public key file should exist in management directory");
    }

} 