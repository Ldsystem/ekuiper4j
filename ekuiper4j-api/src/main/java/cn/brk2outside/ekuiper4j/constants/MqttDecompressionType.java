package cn.brk2outside.ekuiper4j.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of supported MQTT payload decompression methods.
 */
@Getter
@RequiredArgsConstructor
public enum MqttDecompressionType {
    ZLIB("zlib"),
    GZIP("gzip"),
    FLATE("flate"),
    ZSTD("zstd");

    private final String type;

    /**
     * Parse a string type into the corresponding enum value.
     * 
     * @param type the decompression type string to parse
     * @return the decompression type enum, or null if not recognized
     */
    public static MqttDecompressionType fromString(String type) {
        if (type == null) {
            return null;
        }
        
        for (MqttDecompressionType decompressionType : values()) {
            if (decompressionType.getType().equals(type)) {
                return decompressionType;
            }
        }
        
        return null;
    }
} 