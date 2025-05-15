package cn.brk2outside.ekuiper4j.utils;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Examples for creating streams with different field types</p>
 *
 * @author liushenglong_8597@outlook.com
 * @since 2025/5/15
 */
public class StreamExamples {

    /**
     * Create a complex stream example with array and struct types
     * 
     * <p>Example:
     * <pre>
     * demo (
     *     USERID BIGINT,
     *     FIRST_NAME STRING,
     *     LAST_NAME STRING,
     *     NICKNAMES ARRAY(STRING),
     *     Gender BOOLEAN,
     *     ADDRESS STRUCT(STREET_NAME STRING, NUMBER BIGINT),
     * ) WITH (DATASOURCE="test/", FORMAT="JSON", KEY="USERID", CONF_KEY="demo");
     * </pre>
     * </p>
     * 
     * @return CreateStreamRequest for the complex example
     */
    public static CreateStreamRequest createComplexStreamExample() {
        // Create simple fields
        StreamField userId = StreamFieldBuilder.createBigintField("USERID");
        StreamField firstName = StreamFieldBuilder.createStringField("FIRST_NAME");
        StreamField lastName = StreamFieldBuilder.createStringField("LAST_NAME");
        StreamField gender = StreamFieldBuilder.createBooleanField("Gender");
        
        // Create array field (NICKNAMES ARRAY(STRING))
        StreamField nicknames = StreamFieldBuilder.createArrayField("NICKNAMES", StreamConstants.DataType.STRING);
        
        // Create struct field (ADDRESS STRUCT(STREET_NAME STRING, NUMBER BIGINT))
        StreamField streetName = StreamFieldBuilder.createStringField("STREET_NAME");
        StreamField number = StreamFieldBuilder.createBigintField("NUMBER");
        StreamField address = StreamFieldBuilder.createStructField("ADDRESS", streetName, number);
        
        // Create a list of all fields
        List<StreamField> fields = StreamFieldBuilder.createFields(
            userId, firstName, lastName, nicknames, gender, address
        );
        
        // Create options for the stream
        Map<String, String> additionalOptions = new HashMap<>();
        additionalOptions.put("CONF_KEY", "demo");
        
        Map<String, String> options = StreamSqlBuilder.buildOptions(
            "test/", 
            StreamConstants.StreamFormat.JSON, 
            "USERID", 
            additionalOptions
        );
        
        // Build the SQL statement
        String sql = StreamSqlBuilder.buildCreateStreamSql("demo", fields, options);
        
        // Create the request
        return CreateStreamRequest.builder()
            .sql(sql)
            .build();
    }
} 