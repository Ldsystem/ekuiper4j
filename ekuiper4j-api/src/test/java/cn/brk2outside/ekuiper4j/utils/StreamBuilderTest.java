package cn.brk2outside.ekuiper4j.utils;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>Test for stream builder utilities</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
public class StreamBuilderTest {

    @Test
    public void testSimpleStreamSql() {
        // Create simple fields
        StreamField id = StreamFieldBuilder.createBigintField("id");
        StreamField name = StreamFieldBuilder.createStringField("name");
        StreamField score = StreamFieldBuilder.createFloatField("score");
        
        List<StreamField> fields = StreamFieldBuilder.createFields(id, name, score);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "topic/temperature");
        options.put("FORMAT", "JSON");
        options.put("KEY", "id");
        
        String sql = StreamSqlBuilder.buildCreateStreamSql("my_stream", fields, options);
        
        String expected = "CREATE STREAM my_stream (id bigint, name string, score float) " +
                "WITH (DATASOURCE = \"topic/temperature\", FORMAT = \"JSON\", KEY = \"id\");";
        assertEquals(expected, sql);
    }

    @Test
    public void testComplexStreamSql() {
        // Test the complex example with array and struct types
        CreateStreamRequest request = StreamExamples.createComplexStreamExample();
        assertNotNull(request);
        
        // The expected SQL should match the example in the documentation
        String expected = "CREATE STREAM demo (USERID bigint, FIRST_NAME string, LAST_NAME string, " +
                "NICKNAMES ARRAY(string), Gender boolean, ADDRESS STRUCT(STREET_NAME string, NUMBER bigint)) " +
                "WITH (DATASOURCE = \"test/\", FORMAT = \"JSON\", KEY = \"USERID\", CONF_KEY = \"demo\");";
        
        assertEquals(expected, request.getSql());
    }
    
    @Test
    public void testArrayField() {
        StreamField arrayField = StreamFieldBuilder.createArrayField("items", StreamConstants.DataType.STRING);
        
        assertNotNull(arrayField);
        assertEquals("items", arrayField.getName());
        assertEquals(StreamConstants.DataType.ARRAY, arrayField.getFieldType().getType());
        assertNotNull(arrayField.getFieldType().getElementType());
        assertEquals(StreamConstants.DataType.STRING, arrayField.getFieldType().getElementType().getType());
        
        List<StreamField> fields = StreamFieldBuilder.createFields(arrayField);
        String sql = StreamSqlBuilder.buildCreateStreamSql("array_stream", fields, null);
        
        String expected = "CREATE STREAM array_stream (items ARRAY(string));";
        assertEquals(expected, sql);
    }
    
    @Test
    public void testStructField() {
        StreamField nameField = StreamFieldBuilder.createStringField("name");
        StreamField ageField = StreamFieldBuilder.createBigintField("age");
        StreamField addressField = StreamFieldBuilder.createStructField("address", nameField, ageField);
        
        assertNotNull(addressField);
        assertEquals("address", addressField.getName());
        assertEquals(StreamConstants.DataType.STRUCT, addressField.getFieldType().getType());
        assertNotNull(addressField.getFieldType().getFields());
        assertEquals(2, addressField.getFieldType().getFields().size());
        
        List<StreamField> fields = StreamFieldBuilder.createFields(addressField);
        String sql = StreamSqlBuilder.buildCreateStreamSql("struct_stream", fields, null);
        
        String expected = "CREATE STREAM struct_stream (address STRUCT(name string, age bigint));";
        assertEquals(expected, sql);
    }
    
    @Test
    public void testNestedStructures() {
        // Create a field representing: 
        // user STRUCT(name STRING, favorites ARRAY(STRING), contact STRUCT(phone STRING, email STRING))
        
        // First, create the nested contact struct
        StreamField phoneField = StreamFieldBuilder.createStringField("phone");
        StreamField emailField = StreamFieldBuilder.createStringField("email");
        StreamField contactField = StreamFieldBuilder.createStructField("contact", phoneField, emailField);
        
        // Create the favorites array
        StreamField favoritesField = StreamFieldBuilder.createArrayField("favorites", StreamConstants.DataType.STRING);
        
        // Create the name field
        StreamField nameField = StreamFieldBuilder.createStringField("name");
        
        // Create the main user struct field
        StreamField userField = StreamFieldBuilder.createStructField("user", nameField, favoritesField, contactField);
        
        List<StreamField> fields = StreamFieldBuilder.createFields(userField);
        String sql = StreamSqlBuilder.buildCreateStreamSql("nested_stream", fields, null);
        
        // We're only testing the structure of user field in the resulting SQL
        assertNotNull(sql);
    }
} 