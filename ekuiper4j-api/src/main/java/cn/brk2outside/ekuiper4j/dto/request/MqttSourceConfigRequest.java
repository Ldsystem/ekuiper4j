package cn.brk2outside.ekuiper4j.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for MQTT source configuration.
 * Contains validation constraints for the MQTT source parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttSourceConfigRequest {
    /**
     * Specify the source to reuse the connection defined in connection configuration.
     */
    private String connectionSelector;

    /**
     * The server for MQTT message broker.
     * Default: tcp://127.0.0.1:1883
     */
    @Pattern(regexp = "^(tcp|ssl|ws|wss)://.*", message = "Server must start with valid protocol (tcp/ssl/ws/wss)")
    private String server;

    /**
     * The username for MQTT connection.
     */
    private String username;

    /**
     * The password for MQTT connection.
     */
    private String password;

    /**
     * MQTT protocol version. 3.1 (also refer as MQTT 3) or 3.1.1 (also refer as MQTT 4).
     * Default: 3.1.1
     */
    @Pattern(regexp = "^(3\\.1|3\\.1\\.1)$", message = "Protocol version must be 3.1 or 3.1.1")
    private String protocolVersion;

    /**
     * The client id for MQTT connection. If not specified, an uuid will be used.
     */
    private String clientid;

    /**
     * The default subscription QoS level.
     * Default: 1
     * Allowed values: 0, 1, 2
     */
    @Min(value = 0, message = "QoS must be at least 0")
    @Max(value = 2, message = "QoS must be at most 2")
    private Integer qos;

    /**
     * The location of certification path. It can be an absolute path, or a relative path.
     */
    private String certificationPath;

    /**
     * The location of private key path. It can be an absolute path, or a relative path.
     */
    private String privateKeyPath;

    /**
     * The location of root ca path. It can be an absolute path, or a relative path.
     */
    private String rootCaPath;

    /**
     * Control if to skip the certification verification.
     * If true, skip certification verification; otherwise, verify the certification.
     * Default: false
     */
    private Boolean insecureSkipVerify;

    /**
     * Kubeedge version number. Different version numbers correspond to different file contents.
     */
    private String kubeedgeVersion;

    /**
     * The name of KubeEdge template file. The file is located in the specified etc/sources folder.
     */
    private String kubeedgeModelFile;

    /**
     * Decompress the MQTT payload with the specified compression method.
     * Allowed values: zlib, gzip, flate, zstd
     */
    @Pattern(regexp = "^(zlib|gzip|flate|zstd)$", message = "Decompression must be one of: zlib, gzip, flate, zstd")
    private String decompression;
} 