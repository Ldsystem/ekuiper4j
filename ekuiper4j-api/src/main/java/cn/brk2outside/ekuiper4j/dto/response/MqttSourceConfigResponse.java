package cn.brk2outside.ekuiper4j.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for MQTT source configuration.
 * Contains the configuration parameters for MQTT source as returned by the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttSourceConfigResponse {
    /**
     * Specify the source to reuse the connection defined in connection configuration.
     * 
     * @example ""
     */
    private String connectionSelector;

    /**
     * The server for MQTT message broker.
     * 
     * @example "tcp://127.0.0.1:1883"
     */
    private String server;

    /**
     * The username for MQTT connection.
     * 
     * @example "mqttUser"
     */
    private String username;

    /**
     * The password for MQTT connection.
     * 
     * @example "********"
     */
    private final String password = "********";

    /**
     * MQTT protocol version. 3.1 (also refer as MQTT 3) or 3.1.1 (also refer as MQTT 4).
     * 
     * @example "3.1.1"
     */
    private String protocolVersion;

    /**
     * The client id for MQTT connection.
     * 
     * @example "ekuiper-client-001"
     */
    private String clientid;

    /**
     * The default subscription QoS level.
     * 
     * @example 1
     */
    private Integer qos;

    /**
     * The location of certification path.
     * 
     * @example "/var/ekuiper/certs/cert.pem"
     */
    private String certificationPath;

    /**
     * The location of private key path.
     * 
     * @example "/var/ekuiper/certs/key.pem"
     */
    private String privateKeyPath;

    /**
     * The location of root CA path.
     * 
     * @example "/var/ekuiper/certs/ca.pem"
     */
    private String rootCaPath;

    /**
     * Control if to skip the certification verification.
     * 
     * @example false
     */
    private Boolean insecureSkipVerify;

    /**
     * Kubeedge version number.
     * 
     * @example "1.12.0"
     */
    private String kubeedgeVersion;

    /**
     * The name of KubeEdge template file.
     * 
     * @example "kubeedge.yaml"
     */
    private String kubeedgeModelFile;

    /**
     * Decompress the MQTT payload with the specified compression method.
     * 
     * @example "gzip"
     */
    private String decompression;
    
    /**
     * Status of the MQTT source connection.
     * 
     * @example "connected"
     */
    private String status;
} 