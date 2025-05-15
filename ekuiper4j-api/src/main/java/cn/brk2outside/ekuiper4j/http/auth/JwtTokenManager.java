package cn.brk2outside.ekuiper4j.http.auth;

import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Manages JWT tokens for eKuiper authentication, handling token generation and automatic renewal.
 */
public class JwtTokenManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenManager.class);
    
    private final JwtTokenGenerator tokenGenerator;
    private final EKuiperClientProperties properties;
    private final AtomicReference<String> currentToken = new AtomicReference<>();
    
    /**
     * Creates a new JwtTokenManager with the provided properties.
     *
     * @param properties Client configuration properties
     */
    public JwtTokenManager(EKuiperClientProperties properties) {
        this.properties = properties;
        this.tokenGenerator = new JwtTokenGenerator(properties);
        
        // Generate initial token if JWT auth is enabled
        if (properties.getJwt().isEnabled()) {
            refreshToken();
        }
    }
    
    /**
     * Gets the current JWT token, generating a new one if needed.
     *
     * @return The current JWT token, or null if JWT authentication is disabled
     */
    public String getToken() {
        if (!properties.getJwt().isEnabled()) {
            return null;
        }
        
        String token = currentToken.get();
        if (token == null) {
            refreshToken();
            token = currentToken.get();
        }
        
        return token;
    }
    
    /**
     * Refreshes the JWT token.
     * This is called periodically to ensure the token doesn't expire.
     */
    @Scheduled(fixedDelayString = "${ekuiper.client.jwt.refresh-interval-ms:600000}")
    public void refreshToken() {
        if (!properties.getJwt().isEnabled()) {
            return;
        }
        
        try {
            String newToken = tokenGenerator.generateToken();
            currentToken.set(newToken);
            LOGGER.debug("JWT token refreshed");
        } catch (Exception e) {
            LOGGER.error("Failed to refresh JWT token", e);
        }
    }
} 