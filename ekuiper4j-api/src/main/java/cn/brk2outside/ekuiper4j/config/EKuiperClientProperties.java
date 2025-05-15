package cn.brk2outside.ekuiper4j.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the eKuiper HTTP client.
 */
@ConfigurationProperties(prefix = "ekuiper.client")
public class EKuiperClientProperties {
    
    /**
     * Host of the eKuiper server.
     */
    private String host = "localhost";
    
    /**
     * Port of the eKuiper server's REST API.
     */
    private int port = 9081;
    
    /**
     * Connection timeout in milliseconds.
     */
    private int connectTimeout = 5000;
    
    /**
     * Read timeout in milliseconds.
     */
    private int readTimeout = 15000;
    
    /**
     * Whether to enable basic authentication.
     */
    private boolean authEnabled = false;
    
    /**
     * Username for basic authentication.
     */
    private String username;
    
    /**
     * Password for basic authentication.
     */
    private String password;
    
    /**
     * JWT authentication properties.
     */
    private JwtAuth jwt = new JwtAuth();
    
    public static class JwtAuth {
        /**
         * Whether to enable JWT authentication.
         */
        private boolean enabled = false;
        
        /**
         * Path to the private key file (if not specified, will be generated).
         */
        private String privateKeyPath;
        
        /**
         * Path to the public key file (if not specified, will be generated).
         */
        private String publicKeyPath;
        
        /**
         * Issuer (iss) claim for the JWT.
         * Must be the same as the public key filename in etc/mgmt directory.
         */
        private String issuer = "ekuiper4j.pub";
        
        /**
         * Audience (aud) claim for the JWT (should be "eKuiper").
         */
        private String audience = "eKuiper";
        
        /**
         * Token validity duration in seconds.
         */
        private long expirationTimeSeconds = 3600;
        
        /**
         * Path to the eKuiper etc/mgmt directory where the public key should be deployed.
         */
        private String ekuiperMgmtPath = "/etc/mgmt";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPrivateKeyPath() {
            return privateKeyPath;
        }

        public void setPrivateKeyPath(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        public String getPublicKeyPath() {
            return publicKeyPath;
        }

        public void setPublicKeyPath(String publicKeyPath) {
            this.publicKeyPath = publicKeyPath;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public long getExpirationTimeSeconds() {
            return expirationTimeSeconds;
        }

        public void setExpirationTimeSeconds(long expirationTimeSeconds) {
            this.expirationTimeSeconds = expirationTimeSeconds;
        }

        public String getEkuiperMgmtPath() {
            return ekuiperMgmtPath;
        }

        public void setEkuiperMgmtPath(String ekuiperMgmtPath) {
            this.ekuiperMgmtPath = ekuiperMgmtPath;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public JwtAuth getJwt() {
        return jwt;
    }
    
    public void setJwt(JwtAuth jwt) {
        this.jwt = jwt;
    }
} 