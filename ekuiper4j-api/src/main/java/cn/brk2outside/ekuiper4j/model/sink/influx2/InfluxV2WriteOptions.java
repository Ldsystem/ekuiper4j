package cn.brk2outside.ekuiper4j.model.sink.influx2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <p>Write options for InfluxDB v2</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/13
 */
@Data
public class InfluxV2WriteOptions {

    @NotNull
    @Valid
    private InfluxV2Properties connectionInfo;

    @NotBlank(message = "The measurement of the InfluxDB is required")
    private String measurement;
    
    private Map<String, String> tags;
    
    private List<String> fields;
    
    @Pattern(regexp = "ns|us|ms|s", message = "precision must be one of: ns, us, ms, s")
    private String precision = "ms";
    
    private String tsFieldName;
    
    private boolean useLineProtocol = false;

}
