package cn.brk2outside.ekuiper4j.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of supported MQTT protocol versions.
 */
@Getter
@RequiredArgsConstructor
public enum MqttProtocolVersion {
    MQTT_3_1("3.1"),
    MQTT_3_1_1("3.1.1");

    private final String version;

    /**
     * Parse a string version into the corresponding enum value.
     * 
     * @param version the version string to parse
     * @return the protocol version enum, or null if not recognized
     */
    public static MqttProtocolVersion fromString(String version) {
        if (version == null) {
            return null;
        }
        
        for (MqttProtocolVersion protocolVersion : values()) {
            if (protocolVersion.getVersion().equals(version)) {
                return protocolVersion;
            }
        }
        
        return null;
    }
} 