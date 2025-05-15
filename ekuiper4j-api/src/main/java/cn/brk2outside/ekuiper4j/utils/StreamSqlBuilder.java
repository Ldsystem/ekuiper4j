package cn.brk2outside.ekuiper4j.utils;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Utility class to build stream SQL statements</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamSqlBuilder {

    /**
     * Build a CREATE STREAM SQL statement
     *
     * @param streamName  the name of the stream
     * @param fields      the fields of the stream
     * @param options     the options for the stream
     * @return the SQL statement
     */
    public static String buildCreateStreamSql(String streamName, List<StreamField> fields, Map<String, String> options) {
        StringBuilder sql = new StringBuilder("CREATE STREAM ");
        sql.append(streamName);
        
        // Add fields if provided
        if (fields != null && !fields.isEmpty()) {
            sql.append(" (");
            sql.append(fields.stream()
                    .map(StreamSqlBuilder::formatField)
                    .collect(Collectors.joining(", ")));
            sql.append(")");
        } else {
            sql.append(" ()"); // Schemaless stream
        }
        
        // Add options if provided
        if (options != null && !options.isEmpty()) {
            sql.append(" WITH (");
            sql.append(options.entrySet().stream()
                    .map(entry -> String.format("%s = \"%s\"", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(", ")));
            sql.append(")");
        }
        
        sql.append(";");
        return sql.toString();
    }
    
    /**
     * Build options map from individual parameters
     * 
     * @param datasource MQTT topic or other data source
     * @param format data format (JSON, PROTOBUF, BINARY)
     * @param key key field
     * @param additionalOptions additional options
     * @return map of options
     */
    public static Map<String, String> buildOptions(String datasource, StreamConstants.StreamFormat format, 
                                                  String key, Map<String, String> additionalOptions) {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("DATASOURCE", datasource);
        
        if (format != null) {
            options.put("FORMAT", format.getFormat());
        }
        
        if (key != null && !key.isEmpty()) {
            options.put("KEY", key);
        }
        
        if (additionalOptions != null) {
            options.putAll(additionalOptions);
        }
        
        return options;
    }
    
    /**
     * Format a field for SQL statement
     * 
     * @param field the field to format
     * @return formatted field string
     */
    private static String formatField(StreamField field) {
        StreamField.FieldType fieldType = field.getFieldType();
        StreamConstants.DataType type = fieldType.getType();
        
        switch (type) {
            case ARRAY:
                return formatArrayField(field);
            case STRUCT:
                return formatStructField(field);
            default:
                return String.format("%s %s", field.getName(), type.getDataType());
        }
    }
    
    /**
     * Format an array field for SQL statement
     * 
     * @param field the array field to format
     * @return formatted array field string
     */
    private static String formatArrayField(StreamField field) {
        StreamField.FieldType fieldType = field.getFieldType();
        StreamField.FieldType elementType = fieldType.getElementType();
        
        if (elementType == null) {
            return String.format("%s %s", field.getName(), "ARRAY");
        }
        
        return String.format("%s ARRAY(%s)", field.getName(), elementType.getType().getDataType());
    }
    
    /**
     * Format a struct field for SQL statement
     * 
     * @param field the struct field to format
     * @return formatted struct field string
     */
    private static String formatStructField(StreamField field) {
        StreamField.FieldType fieldType = field.getFieldType();
        List<StreamField> structFields = fieldType.getFields();
        
        if (structFields == null || structFields.isEmpty()) {
            return String.format("%s %s", field.getName(), "STRUCT");
        }
        
        String fieldsStr = structFields.stream()
                .map(f -> {
                    StreamField.FieldType ft = f.getFieldType();
                    return String.format("%s %s", f.getName(), ft.getType().getDataType());
                })
                .collect(Collectors.joining(", "));
        
        return String.format("%s STRUCT(%s)", field.getName(), fieldsStr);
    }
} 