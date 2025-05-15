package cn.brk2outside.ekuiper4j.model.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>StreamOptions representing the configuration options for a eKuiper stream</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamOptions {

    /**
     * Data source for the stream (e.g., MQTT topic)
     */
    private String datasource;
    
    /**
     * Format of the incoming data, supports "JSON", "PROTOBUF", and "BINARY"
     */
    private String format;
    
    /**
     * Schema ID used for decoding, currently only used when format is PROTOBUF
     */
    private String schemaid;
    
    /**
     * Delimiter used when format is "delimited"
     */
    private String delimiter;
    
    /**
     * Key field for the stream
     */
    private String key;
    
    /**
     * Source type, default is "mqtt" if not specified
     */
    private String type;
    
    /**
     * Controls validation behavior of message fields against stream schema
     */
    private Boolean strictValidation;
    
    /**
     * Configuration key if additional configuration items are needed
     */
    private String confKey;
    
    /**
     * Whether to share source instance among rules using this stream
     */
    private Boolean shared;
    
    /**
     * Field name representing the event timestamp
     */
    private String timestamp;
    
    /**
     * Default format used for string and time format conversion
     */
    private String timestampFormat;
}
