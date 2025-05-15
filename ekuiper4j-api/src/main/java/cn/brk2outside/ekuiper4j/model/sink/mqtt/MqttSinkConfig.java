package cn.brk2outside.ekuiper4j.model.sink.mqtt;

import cn.brk2outside.ekuiper4j.model.MqttConnProps;
import cn.brk2outside.ekuiper4j.model.ReusableConn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration model for MQTT sink in eKuiper.
 * Represents the configuration parameters for publishing messages to MQTT brokers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttSinkConfig implements ReusableConn {
    /**
     * Common MQTT connection properties
     */
    private MqttConnProps connProps;

    /**
     * MQTT topic to publish to, e.g., "analysis/result"
     * Can also be a dynamic attribute, e.g., "{{.mytopic}}" or "$.col"
     */
    private String topic;

    /**
     * If set to true, the broker will store the last retained message
     * for each topic and QoS. Default: false
     */
    private Boolean retained;

    /**
     * Compress the payload with the specified compression method.
     * Supported algorithms: zlib, gzip, flate, zstd
     */
    private String compression;
    
    /**
     * Gets the connection selector from connProps if available
     */
    @Override
    public String getConnectionSelector() {
        return connProps != null ? connProps.getConnectionSelector() : null;
    }
}
