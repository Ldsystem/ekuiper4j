package cn.brk2outside.ekuiper4j.model;

import cn.brk2outside.ekuiper4j.constants.MqttProtocolVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common MQTT connection properties used by both sources and sinks.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttConnProps implements ReusableConn {
    /**
     * Specify to reuse the connection defined in connection configuration.
     */
    private String connectionSelector;

    /**
     * The server for MQTT message broker. Default: tcp://127.0.0.1:1883
     */
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
    private MqttProtocolVersion protocolVersion;

    /**
     * The client id for MQTT connection. If not specified, an uuid will be used.
     */
    private String clientid;

    /**
     * The QoS level. Default: 1
     * Possible values: 0, 1, 2
     */
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
     * Base64 encoded certificate content. If certificationPath is also defined, this takes precedence.
     */
    private String certificationRaw;

    /**
     * Base64 encoded private key content. If privateKeyPath is also defined, this takes precedence.
     */
    private String privateKeyRaw;

    /**
     * Base64 encoded root CA content. If rootCaPath is also defined, this takes precedence.
     */
    private String rootCARaw;

    /**
     * Control if to skip the certification verification.
     * If true, skip certification verification; otherwise, verify the certification.
     * Default: false
     */
    private Boolean insecureSkipVerify;
}
