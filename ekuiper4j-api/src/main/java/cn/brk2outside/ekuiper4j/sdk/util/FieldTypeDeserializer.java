package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Custom deserializer for StreamField.FieldType to handle different complex representations
 * of field types in the API response from eKuiper.
 * 
 * The deserializer handles the following cases:
 * 1. Simple types: {"FieldType": "bigint"}
 * 2. Array types: {"FieldType": {"Type": "array", "ElementType": "string"}}
 * 3. Struct types: {"FieldType": {"Type": "struct", "Fields": [...]}}
 */
public class FieldTypeDeserializer extends JsonDeserializer<StreamField.FieldType> {

    @Override
    public StreamField.FieldType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        StreamField.FieldType fieldType = new StreamField.FieldType();
        
        // Get the JsonNode from the parser
        JsonNode node = p.getCodec().readTree(p);
        
        // If it's a simple string, it's a basic data type
        if (node.isTextual()) {
            String typeStr = node.asText().toLowerCase();
            fieldType.setType(getDataTypeFromString(typeStr));
            return fieldType;
        }
        
        // If it's an object, it's a complex type (array or struct)
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            
            // Get the type
            if (objNode.has("Type")) {
                String typeStr = objNode.get("Type").asText().toLowerCase();
                fieldType.setType(getDataTypeFromString(typeStr));
                
                // Handle array type
                if (fieldType.getType() == StreamConstants.DataType.ARRAY && objNode.has("ElementType")) {
                    JsonNode elementTypeNode = objNode.get("ElementType");
                    StreamField.FieldType elementType = new StreamField.FieldType();
                    
                    if (elementTypeNode.isTextual()) {
                        String elementTypeStr = elementTypeNode.asText().toLowerCase();
                        elementType.setType(getDataTypeFromString(elementTypeStr));
                    } else if (elementTypeNode.isObject()) {
                        // Recursively deserialize complex element type
                        ObjectMapper mapper = (ObjectMapper) p.getCodec();
                        elementType = mapper.treeToValue(elementTypeNode, StreamField.FieldType.class);
                    }
                    
                    fieldType.setElementType(elementType);
                }
                
                // Handle struct type
                if (fieldType.getType() == StreamConstants.DataType.STRUCT && objNode.has("Fields")) {
                    JsonNode fieldsNode = objNode.get("Fields");
                    if (fieldsNode.isArray()) {
                        List<StreamField> fields = new ArrayList<>();
                        ArrayNode arrayNode = (ArrayNode) fieldsNode;
                        
                        ObjectMapper mapper = (ObjectMapper) p.getCodec();
                        for (JsonNode fieldNode : arrayNode) {
                            StreamField field = mapper.treeToValue(fieldNode, StreamField.class);
                            fields.add(field);
                        }
                        
                        fieldType.setFields(fields);
                    }
                }
            }
            
            return fieldType;
        }
        
        // Handle the case where the FieldType is an object with field type properties
        if (node.has("type")) {
            String typeStr = node.get("type").asText().toLowerCase();
            fieldType.setType(getDataTypeFromString(typeStr));
            
            if (node.has("elementType")) {
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                StreamField.FieldType elementType = mapper.treeToValue(node.get("elementType"), StreamField.FieldType.class);
                fieldType.setElementType(elementType);
            }
            
            if (node.has("fields")) {
                List<StreamField> fields = new ArrayList<>();
                ObjectMapper mapper = (ObjectMapper) p.getCodec();
                
                JsonNode fieldsNode = node.get("fields");
                if (fieldsNode.isArray()) {
                    for (JsonNode fieldNode : fieldsNode) {
                        StreamField field = mapper.treeToValue(fieldNode, StreamField.class);
                        fields.add(field);
                    }
                    fieldType.setFields(fields);
                }
            }
            
            return fieldType;
        }
        
        return fieldType;
    }
    
    /**
     * Convert a string data type name to the corresponding enum value
     * 
     * @param typeStr String representation of the data type
     * @return The corresponding DataType enum value
     */
    private StreamConstants.DataType getDataTypeFromString(String typeStr) {
        switch (typeStr.toLowerCase()) {
            case "bigint":
                return StreamConstants.DataType.BIGINT;
            case "float":
                return StreamConstants.DataType.FLOAT;
            case "string":
                return StreamConstants.DataType.STRING;
            case "datetime":
                return StreamConstants.DataType.DATETIME;
            case "boolean":
                return StreamConstants.DataType.BOOLEAN;
            case "bytea":
                return StreamConstants.DataType.BYTEA;
            case "array":
                return StreamConstants.DataType.ARRAY;
            case "struct":
                return StreamConstants.DataType.STRUCT;
            default:
                // Default to string for unknown types
                return StreamConstants.DataType.STRING;
        }
    }
} 