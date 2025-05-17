package cn.brk2outside.ekuiper4j.model.stream;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for StreamField deserialization, particularly for complex field types
 */
public class StreamFieldDeserializerTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testDeserializeSimpleFieldType() throws IOException {
        // Test for simple field type
        String json = "{\"Name\":\"id\",\"FieldType\":\"bigint\"}";
        
        StreamField field = objectMapper.readValue(json, StreamField.class);
        
        assertNotNull(field);
        assertEquals("id", field.getName());
        assertNotNull(field.getFieldType());
        assertEquals(StreamConstants.DataType.BIGINT, field.getFieldType().getType());
    }
    
    @Test
    void testDeserializeArrayFieldType() throws IOException {
        // Test for array field type
        String json = "{\"Name\":\"nicknames\",\"FieldType\":{\"Type\":\"array\",\"ElementType\":\"string\"}}";
        
        StreamField field = objectMapper.readValue(json, StreamField.class);
        
        assertNotNull(field);
        assertEquals("nicknames", field.getName());
        assertNotNull(field.getFieldType());
        assertEquals(StreamConstants.DataType.ARRAY, field.getFieldType().getType());
        assertNotNull(field.getFieldType().getElementType());
        assertEquals(StreamConstants.DataType.STRING, field.getFieldType().getElementType().getType());
    }
    
    @Test
    void testDeserializeStructFieldType() throws IOException {
        // Test for struct field type
        String json = "{\"Name\":\"address\",\"FieldType\":{\"Type\":\"struct\",\"Fields\":[{\"Name\":\"street\",\"FieldType\":\"string\"},{\"Name\":\"number\",\"FieldType\":\"bigint\"}]}}";
        
        StreamField field = objectMapper.readValue(json, StreamField.class);
        
        assertNotNull(field);
        assertEquals("address", field.getName());
        assertNotNull(field.getFieldType());
        assertEquals(StreamConstants.DataType.STRUCT, field.getFieldType().getType());
        assertNotNull(field.getFieldType().getFields());
        assertEquals(2, field.getFieldType().getFields().size());
        
        // Verify first nested field
        StreamField streetField = field.getFieldType().getFields().get(0);
        assertEquals("street", streetField.getName());
        assertEquals(StreamConstants.DataType.STRING, streetField.getFieldType().getType());
        
        // Verify second nested field
        StreamField numberField = field.getFieldType().getFields().get(1);
        assertEquals("number", numberField.getName());
        assertEquals(StreamConstants.DataType.BIGINT, numberField.getFieldType().getType());
    }
    
    @Test
    void testDeserializeNestedComplexTypes() throws IOException {
        // Test for nested complex types (array of structs, struct with arrays, etc.)
        String json = "{\"Name\":\"contacts\",\"FieldType\":{\"Type\":\"array\",\"ElementType\":{\"Type\":\"struct\",\"Fields\":[{\"Name\":\"name\",\"FieldType\":\"string\"},{\"Name\":\"phones\",\"FieldType\":{\"Type\":\"array\",\"ElementType\":\"string\"}}]}}}";
        
        StreamField field = objectMapper.readValue(json, StreamField.class);
        
        assertNotNull(field);
        assertEquals("contacts", field.getName());
        assertNotNull(field.getFieldType());
        assertEquals(StreamConstants.DataType.ARRAY, field.getFieldType().getType());
        
        // Verify element type is a struct
        assertNotNull(field.getFieldType().getElementType());
        assertEquals(StreamConstants.DataType.STRUCT, field.getFieldType().getElementType().getType());
        
        // Verify struct fields
        assertNotNull(field.getFieldType().getElementType().getFields());
        assertEquals(2, field.getFieldType().getElementType().getFields().size());
        
        // Verify first field of struct
        StreamField nameField = field.getFieldType().getElementType().getFields().get(0);
        assertEquals("name", nameField.getName());
        assertEquals(StreamConstants.DataType.STRING, nameField.getFieldType().getType());
        
        // Verify second field of struct (which is an array)
        StreamField phonesField = field.getFieldType().getElementType().getFields().get(1);
        assertEquals("phones", phonesField.getName());
        assertEquals(StreamConstants.DataType.ARRAY, phonesField.getFieldType().getType());
        assertEquals(StreamConstants.DataType.STRING, phonesField.getFieldType().getElementType().getType());
    }
    
    @Test
    void testDeserializeCompleteStreamObject() throws IOException {
        // Test deserializing a complete stream object with mixed field types
        String json = "{\"Name\":\"test_stream\",\"StreamFields\":[{\"Name\":\"id\",\"FieldType\":\"bigint\"},{\"Name\":\"temperature\",\"FieldType\":\"float\"},{\"Name\":\"arrField\",\"FieldType\":{\"Type\":\"array\",\"ElementType\":\"string\"}},{\"Name\":\"structField\",\"FieldType\":{\"Type\":\"struct\",\"Fields\":[{\"Name\":\"age\",\"FieldType\":\"bigint\"}]}}],\"Options\":{\"datasource\":\"test/topic\",\"format\":\"JSON\"}}";
        
        Stream stream = objectMapper.readValue(json, Stream.class);
        
        assertNotNull(stream);
        assertEquals("test_stream", stream.getName());
        assertNotNull(stream.getStreamFields());
        assertEquals(4, stream.getStreamFields().size());
        
        // Verify simple types
        StreamField idField = stream.getStreamFields().get(0);
        assertEquals("id", idField.getName());
        assertEquals(StreamConstants.DataType.BIGINT, idField.getFieldType().getType());
        
        StreamField tempField = stream.getStreamFields().get(1);
        assertEquals("temperature", tempField.getName());
        assertEquals(StreamConstants.DataType.FLOAT, tempField.getFieldType().getType());
        
        // Verify array type
        StreamField arrayField = stream.getStreamFields().get(2);
        assertEquals("arrField", arrayField.getName());
        assertEquals(StreamConstants.DataType.ARRAY, arrayField.getFieldType().getType());
        assertEquals(StreamConstants.DataType.STRING, arrayField.getFieldType().getElementType().getType());
        
        // Verify struct type
        StreamField structField = stream.getStreamFields().get(3);
        assertEquals("structField", structField.getName());
        assertEquals(StreamConstants.DataType.STRUCT, structField.getFieldType().getType());
        assertEquals(1, structField.getFieldType().getFields().size());
        assertEquals("age", structField.getFieldType().getFields().get(0).getName());
        assertEquals(StreamConstants.DataType.BIGINT, structField.getFieldType().getFields().get(0).getFieldType().getType());
        
        // Verify options
        assertNotNull(stream.getOptions());
        assertEquals("test/topic", stream.getOptions().getDatasource());
        assertEquals("JSON", stream.getOptions().getFormat());
    }
} 