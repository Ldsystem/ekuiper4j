package cn.brk2outside.ekuiper4j.http.auth;

import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import cn.brk2outside.ekuiper4j.BaseEKuiperTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test JWT authentication with eKuiper.
 */
class JwtAuthTest extends BaseEKuiperTest {
    
    private EKuiperClientProperties properties;
    private JwtTokenManager tokenManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        // Ensure management directory exists
        Path mgmtDir = EKUIPER.getMgmtDirectory();
        if (!Files.exists(mgmtDir)) {
            Files.createDirectories(mgmtDir);
        }
        
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
        jwtAuth.setEkuiperMgmtPath(mgmtDir.toString());
        
        properties.setJwt(jwtAuth);
        
        // Create token manager
        tokenManager = new JwtTokenManager(properties);
    }

    @Test
    void testDecodeJwtToken() throws Exception {
        // Get a token
        String token = tokenManager.getToken();
        assertNotNull(token, "Token should not be null");
        
        // Split the token into parts
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
        
        // Decode the payload (second part)
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        System.out.println("JWT Payload: " + payload);
        
        // Read the public key from the management directory
        Path publicKeyPath = EKUIPER.getMgmtDirectory().resolve(properties.getJwt().getIssuer());
        String publicKeyPem = Files.readString(publicKeyPath);
        
        // Remove PEM headers and whitespace
        String publicKeyBase64 = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        // Create RSA public key
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        
        // Parse the JWT token
        var parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
        var parsedJwt = parser.parseClaimsJws(token);
        
        // Print JWT header and claims
        System.out.println("JWT Header: " + parsedJwt.getHeader());
        System.out.println("JWT Claims: " + parsedJwt.getBody());
        
        Claims claims = parsedJwt.getBody();
        
        // Print individual claims
        System.out.println("Issuer (iss): " + claims.getIssuer());
        System.out.println("Audience (aud): " + claims.getAudience());
        System.out.println("Issued At (iat): " + claims.getIssuedAt());
        System.out.println("Expiration (exp): " + claims.getExpiration());
        System.out.println("JWT ID (jti): " + claims.getId());
        
        // Verify claims match our configuration
        assertEquals(properties.getJwt().getIssuer(), claims.getIssuer());
        assertEquals(properties.getJwt().getAudience(), claims.getAudience());
    }
}