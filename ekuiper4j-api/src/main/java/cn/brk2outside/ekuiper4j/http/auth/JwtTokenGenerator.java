package cn.brk2outside.ekuiper4j.http.auth;

import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * Generator for JWT tokens used in eKuiper authentication.
 * Manages key generation, storage, deployment, and token creation.
 */
public class JwtTokenGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenGenerator.class);
    
    private final EKuiperClientProperties properties;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    /**
     * Creates a new JWT token generator with the provided configuration.
     *
     * @param properties Client configuration properties
     */
    public JwtTokenGenerator(EKuiperClientProperties properties) {
        this.properties = properties;
        
        try {
            initializeKeys();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize JWT keys", e);
            throw new RuntimeException("Failed to initialize JWT keys", e);
        }
    }
    
    /**
     * Generates a JWT token for eKuiper authentication.
     *
     * @return JWT token string
     */
    public String generateToken() {
        EKuiperClientProperties.JwtAuth jwtConfig = properties.getJwt();
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (jwtConfig.getExpirationTimeSeconds() * 1000));
        
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "RS256")
                .setIssuer(jwtConfig.getIssuer())
                .setAudience(jwtConfig.getAudience())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
    
    /**
     * Initializes the RSA keys by:
     * 1. Loading existing keys if available
     * 2. Generating new keys if needed
     * 3. Deploying the public key to eKuiper's management directory
     */
    private void initializeKeys() throws Exception {
        EKuiperClientProperties.JwtAuth jwtConfig = properties.getJwt();

        // Create certs directory if it doesn't exist
        Path certsDir = Paths.get("certs");
        if (!Files.exists(certsDir)) {
            Files.createDirectories(certsDir);
        }
        
        // Determine key paths
        String privateKeyPath = jwtConfig.getPrivateKeyPath();
        if (!StringUtils.hasText(privateKeyPath)) {
            privateKeyPath = "certs/ekuiper4j_rsa.key";
        }
        
        String publicKeyPath = jwtConfig.getPublicKeyPath();
        if (!StringUtils.hasText(publicKeyPath)) {
            publicKeyPath = "certs/" + jwtConfig.getIssuer();
        }
        
        File privateKeyFile = new File(privateKeyPath);
        File publicKeyFile = new File(publicKeyPath);
        
        // Check if keys already exist
        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            LOGGER.info("Loading existing RSA keys");
            loadKeys(privateKeyFile, publicKeyFile);
        } else {
            LOGGER.info("Generating new RSA key pair");
            generateKeyPair(privateKeyFile, publicKeyFile);
        }
        
        // Deploy public key to eKuiper if enabled
        if (jwtConfig.isEnabled()) {
            deployPublicKeyToEKuiper(publicKeyFile, jwtConfig.getEkuiperMgmtPath());
        }
    }
    
    /**
     * Loads existing RSA keys from files.
     *
     * @param privateKeyFile Private key file
     * @param publicKeyFile  Public key file
     */
    private void loadKeys(File privateKeyFile, File publicKeyFile) throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(privateKeySpec);
        publicKey = keyFactory.generatePublic(publicKeySpec);
    }
    
    /**
     * Generates a new RSA key pair and saves them to files.
     *
     * @param privateKeyFile Private key file
     * @param publicKeyFile  Public key file
     */
    private void generateKeyPair(File privateKeyFile, File publicKeyFile) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        
        // Save private key
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(privateKey.getEncoded());
        }
        
        // Save public key
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write(publicKey.getEncoded());
        }
        
        LOGGER.info("RSA key pair generated and saved to {} and {}", 
                privateKeyFile.getAbsolutePath(), publicKeyFile.getAbsolutePath());
    }
    
    /**
     * Deploys the public key to eKuiper's management directory.
     *
     * @param publicKeyFile   Public key file
     * @param ekuiperMgmtPath Path to eKuiper's management directory
     */
    private void deployPublicKeyToEKuiper(File publicKeyFile, String ekuiperMgmtPath) throws IOException {
        EKuiperClientProperties.JwtAuth jwtConfig = properties.getJwt();
        
        Path mgmtDir = Paths.get(ekuiperMgmtPath);
        if (!Files.exists(mgmtDir)) {
            LOGGER.warn("eKuiper management directory {} does not exist. Public key will not be deployed.", ekuiperMgmtPath);
            return;
        }
        
        Path targetFile = mgmtDir.resolve(jwtConfig.getIssuer());
        Files.copy(publicKeyFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        
        LOGGER.info("Public key deployed to eKuiper management directory: {}", targetFile);
    }
} 