package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for validating MQTT source configurations beyond Jakarta validation annotations.
 */
public final class MqttConfigValidator {

    private MqttConfigValidator() {
        // Utility class, do not instantiate
    }

    /**
     * Validates the MQTT source configuration for deeper business rules.
     *
     * @param config the MQTT source configuration to validate
     * @return a list of validation error messages (empty if valid)
     */
    public static List<String> validateConfig(MqttSourceConfigRequest config) {
        List<String> errors = new ArrayList<>();

        if (config == null) {
            errors.add("Configuration cannot be null");
            return errors;
        }

        // If connectionSelector is provided, many connection-related fields could be skipped
        boolean hasConnectionSelector = config.getConnectionSelector() != null 
                && !config.getConnectionSelector().trim().isEmpty();

        // Validate server URL if provided
        if (config.getServer() != null && !config.getServer().trim().isEmpty()) {
            try {
                URI serverUri = new URI(config.getServer());
                if (serverUri.getHost() == null) {
                    errors.add("Server URL must have a valid host");
                }
                if (serverUri.getPort() == -1) {
                    errors.add("Server URL must specify a port");
                }
            } catch (URISyntaxException e) {
                errors.add("Server URL is not a valid URI: " + e.getMessage());
            }
        } else if (!hasConnectionSelector) {
            // Server is required if no connection selector is provided
            errors.add("Server URL is required when connection selector is not provided");
        }

        // Validate certificate paths if SSL/TLS appears to be in use
        if (config.getServer() != null && config.getServer().startsWith("ssl://")) {
            validateCertPaths(config, errors, hasConnectionSelector);
        }

        // Validate KubeEdge specific configuration
        if (config.getKubeedgeModelFile() != null && !config.getKubeedgeModelFile().isEmpty()) {
            if (config.getKubeedgeVersion() == null || config.getKubeedgeVersion().isEmpty()) {
                errors.add("KubeEdge version must be specified when KubeEdge model file is provided");
            }
        }

        return errors;
    }

    private static void validateCertPaths(MqttSourceConfigRequest config, List<String> errors, boolean hasConnectionSelector) {
        // Skip validation if using connection selector
        if (hasConnectionSelector) {
            return;
        }

        // When using SSL but skipping verification, certificates are optional
        if (Boolean.TRUE.equals(config.getInsecureSkipVerify())) {
            return;
        }

        // Check if certificate paths exist if provided
        validateFilePath(config.getCertificationPath(), "Certification path", errors);
        validateFilePath(config.getPrivateKeyPath(), "Private key path", errors);
        validateFilePath(config.getRootCaPath(), "Root CA path", errors);

        // SSL requires at least the root CA certificate if not skipping verification
        if (config.getRootCaPath() == null || config.getRootCaPath().isEmpty()) {
            errors.add("Root CA path is required for SSL connections when not skipping verification");
        }
    }

    private static void validateFilePath(String path, String fieldName, List<String> errors) {
        if (path == null || path.isEmpty()) {
            // Path is optional, skip validation
            return;
        }

        // For file:// scheme, check if file exists
        if (path.startsWith("file://")) {
            String filePath = path.substring(7);
            File file = new File(filePath);
            if (!file.exists()) {
                errors.add(fieldName + " points to a non-existent file: " + filePath);
            } else if (!file.isFile()) {
                errors.add(fieldName + " points to a directory, not a file: " + filePath);
            }
        } else if (!path.startsWith("http://") && !path.startsWith("https://")) {
            // For local paths without scheme, check if file exists
            File file = new File(path);
            if (!file.exists()) {
                errors.add(fieldName + " points to a non-existent file: " + path);
            } else if (!file.isFile()) {
                errors.add(fieldName + " points to a directory, not a file: " + path);
            }
        }
        // For http/https, we can't validate existence here
    }
} 