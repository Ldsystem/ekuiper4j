package cn.brk2outside.ekuiper4j.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p> </p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/14
 */
public interface ReusableConn {

    @JsonProperty
    String getConnectionSelector();

}
