package cn.brk2outside.ekuiper4j.model.source.mqtt;

import cn.brk2outside.ekuiper4j.constants.MqttDecompressionType;
import cn.brk2outside.ekuiper4j.model.MqttConnProps;
import cn.brk2outside.ekuiper4j.model.ReusableConn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration model for MQTT source in eKuiper.
 * Represents the configuration parameters for MQTT connections.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttSourceConfig implements ReusableConn {
    /**
     * Common MQTT connection properties
     */
    private MqttConnProps connProps;

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
     * Possible values: zlib, gzip, flate, zstd
     */
    private MqttDecompressionType decompression;

    /**
     * Buffer length for maximum cached message count
     */
    private Integer bufferLength;

    /**
     * Gets the connection selector from connProps if available
     */
    @Override
    public String getConnectionSelector() {
        return connProps != null ? connProps.getConnectionSelector() : null;
    }
} 