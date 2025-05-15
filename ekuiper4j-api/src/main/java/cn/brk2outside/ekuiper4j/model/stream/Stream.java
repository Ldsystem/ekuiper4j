package cn.brk2outside.ekuiper4j.model.stream;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Stream model representing a eKuiper stream definition</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
public class Stream {

    /**
     * Name of the stream
     */
    @JsonProperty("Name")
    private String name;
    
    /**
     * List of fields in the stream schema
     */
    @JsonProperty("StreamFields")
    private List<StreamField> streamFields;

    @JsonProperty("StreamType")
    private int streamType;

    @JsonProperty("Statement")
    private String statement;

    /**
     * Stream configuration options
     */
    @JsonProperty("Options")
    private StreamOptions options;
}
