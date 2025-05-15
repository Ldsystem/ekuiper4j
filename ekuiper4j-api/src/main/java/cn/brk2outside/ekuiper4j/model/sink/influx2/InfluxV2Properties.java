package cn.brk2outside.ekuiper4j.model.sink.influx2;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * <p>Properties for InfluxDB v2 connection</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/13
 */
@Data
public class InfluxV2Properties {

    @NotBlank(message = "The addr of the InfluxDB is required")
    private String addr;

    private String token;

    @NotBlank(message = "The InfluxDB organization is required")
    private String org;

    @NotBlank(message = "The InfluxDB bucket is required")
    private String bucket;

    private String certificationPath;

    private String privateKeyPath;

    private String rootCaPath;
    
    @Pattern(regexp = "tls1\\.0|tls1\\.1|tls1\\.2|tls1\\.3", message = "tlsMinVersion must be one of: tls1.0, tls1.1, tls1.2, tls1.3")
    private String tlsMinVersion = "tls1.2";
    
    @Pattern(regexp = "never|once|freely", message = "renegotiationSupport must be one of: never, once, freely")
    private String renegotiationSupport = "never";
    
    private boolean insecureSkipVerify = false;
}
