package cn.brk2outside.ekuiper4j.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Response DTO for stream schema</p>
 * <p>The schema is a JSON-Schema-like structure representing the inferred structure after merging
 * physical and logical schemas.</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamSchemaResponse {

    /**
     * Dynamic schema properties where key is the field name and value is the schema definition
     */
    private Map<String, SchemaProperty> properties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, SchemaProperty> getProperties() {
        return properties;
    }

    @JsonAnySetter
    public void setProperty(String name, SchemaProperty value) {
        properties.put(name, value);
    }

    /**
     * Schema property definition
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemaProperty {
        /**
         * Type of the property (bigint, float, string, etc.)
         */
        private String type;
        
        /**
         * For array types, defines the items in the array
         */
        private SchemaProperty items;
        
        /**
         * For struct types, defines the nested properties
         */
        private Map<String, SchemaProperty> properties;
    }
} 