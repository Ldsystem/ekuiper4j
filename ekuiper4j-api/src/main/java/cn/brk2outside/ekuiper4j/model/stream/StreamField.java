package cn.brk2outside.ekuiper4j.model.stream;


import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.sdk.util.EnumSerializer;
import cn.brk2outside.ekuiper4j.sdk.util.FieldTypeDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

/**
 * <p>StreamField representing a field in a eKuiper stream definition</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
public class StreamField {

    /**
     * Name of the field
     */
    @JsonProperty("Name")
    private String name;
    
    /**
     * Type of the field
     */
    @JsonProperty("FieldType")
    @JsonDeserialize(using = FieldTypeDeserializer.class)
    private FieldType fieldType;

    /**
     * Field type definition which contains the data type information
     */
    @Data
    @JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldType {
        /**
         * Data type of the field
         */
        @JsonSerialize(using = EnumSerializer.EnumNameDirectSerializer.class)
        @JsonDeserialize(using = EnumSerializer.EnumNameDirectDeserializer.class)
        private StreamConstants.DataType type;
        
        /**
         * For ARRAY type, the element type
         */
        private FieldType elementType;
        
        /**
         * For STRUCT type, the field definitions
         */
        private List<StreamField> fields;
    }
}
