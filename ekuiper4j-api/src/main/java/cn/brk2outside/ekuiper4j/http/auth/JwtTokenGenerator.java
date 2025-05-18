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
import java.util.Base64;
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
        
        // Check key files existence and handle accordingly
        if (!privateKeyFile.exists()) {
            LOGGER.info("Private key not found, generating new RSA key pair");
            generateKeyPair(privateKeyFile, publicKeyFile);
        } else if (!publicKeyFile.exists()) {
            LOGGER.info("Public key not found, generating from existing private key");
            generatePublicKeyFromPrivate(privateKeyFile, publicKeyFile);
        } else {
            LOGGER.info("Loading existing RSA keys");
            loadKeys(privateKeyFile, publicKeyFile);
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
        // Load private key (still in PKCS#8 format)
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        
        // Load public key (now in PEM format)
        String publicKeyPem = Files.readString(publicKeyFile.toPath());
        // Remove PEM headers and whitespace
        String publicKeyBase64 = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
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
        
        // Save private key in PKCS#8 format
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile)) {
            fos.write(privateKey.getEncoded());
        }
        
        // Save public key in PEM format
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
            String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // Split into lines of 64 characters
            for (int i = 0; i < base64Key.length(); i += 64) {
                fos.write(base64Key.substring(i, Math.min(i + 64, base64Key.length())).getBytes());
                fos.write('\n');
            }
            fos.write("-----END PUBLIC KEY-----\n".getBytes());
        }
        
        LOGGER.info("RSA key pair generated and saved to {} and {}", 
                privateKeyFile.getAbsolutePath(), publicKeyFile.getAbsolutePath());
    }
    
    /**
     * Generates a public key from an existing private key file.
     *
     * @param privateKeyFile Private key file
     * @param publicKeyFile  Public key file to write the generated public key
     */
    private void generatePublicKeyFromPrivate(File privateKeyFile, File publicKeyFile) throws Exception {
        // Load private key
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(privateKeySpec);
        
        // Generate public key from private key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        
        // Save public key in PEM format
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write("-----BEGIN PUBLIC KEY-----\n".getBytes());
            String base64Key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            // Split into lines of 64 characters
            for (int i = 0; i < base64Key.length(); i += 64) {
                fos.write(base64Key.substring(i, Math.min(i + 64, base64Key.length())).getBytes());
                fos.write('\n');
            }
            fos.write("-----END PUBLIC KEY-----\n".getBytes());
        }
        
        LOGGER.info("Public key generated from private key and saved to {}", publicKeyFile.getAbsolutePath());
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
        // Copy the PEM formatted public key
        Files.copy(publicKeyFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        
        LOGGER.info("Public key deployed to eKuiper management directory: {}", targetFile);
    }
} 