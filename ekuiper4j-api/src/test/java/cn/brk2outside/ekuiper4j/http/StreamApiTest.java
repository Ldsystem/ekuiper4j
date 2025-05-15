package cn.brk2outside.ekuiper4j.http;

import cn.brk2outside.ekuiper4j.constants.StreamConstants;
import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.request.UpdateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.StreamSchemaResponse;
import cn.brk2outside.ekuiper4j.model.stream.Stream;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import cn.brk2outside.ekuiper4j.utils.StreamExamples;
import cn.brk2outside.ekuiper4j.utils.StreamFieldBuilder;
import cn.brk2outside.ekuiper4j.utils.StreamSqlBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Stream API endpoints
 */
public class StreamApiTest extends BaseEKuiperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamApiTest.class);
    private static final String TEST_STREAM_NAME = "test_stream";
    private static final String COMPLEX_STREAM_NAME = "complex_stream";
    
    private RestTemplateHttpClient client;

    @BeforeEach
    void setUp() {
        client = new RestTemplateHttpClient(
                EKUIPER.getEkuiperHost(),
                EKUIPER.getEkuiperPort()
        );
        
        // Clean up any existing test streams
        cleanupTestStreams();
    }

    @AfterEach
    void tearDown() {
        // Clean up test streams
        cleanupTestStreams();
    }
    
    private void cleanupTestStreams() {
        try {
            // Delete the test stream
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_STREAM.getEndpoint(), 
                    TEST_STREAM_NAME);
        } catch (HttpClientException e) {
            // Ignore errors if stream doesn't exist
        }
        
        try {
            // Delete the complex stream
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_STREAM.getEndpoint(), 
                    COMPLEX_STREAM_NAME);
        } catch (HttpClientException e) {
            // Ignore errors if stream doesn't exist
        }
    }

    @Test
    void testCreateAndListStreams() {
        // Create a simple stream
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        List<StreamField> fields = StreamFieldBuilder.createFields(idField, tempField);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/topic");
        options.put("FORMAT", "JSON");
        
        String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
        
        CreateStreamRequest request = new CreateStreamRequest();
        request.setSql(sql);
        
        // Create the stream
        String response = ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_STREAM.getEndpoint(), 
                request);
        
        // Verify the response
        assertNotNull(response);
        
        // List streams and verify our stream exists
        List<String> streams = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_STREAMS.getEndpoint());
        
        assertNotNull(streams);
        assertTrue(streams.contains(TEST_STREAM_NAME), "Created stream should be in the list");
    }
    
    @Test
    void testGetStreamDetails() {
        // First create a stream
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        StreamField arrField = StreamFieldBuilder.createArrayField("arrField", StreamConstants.DataType.STRING);
        StreamField structField = StreamFieldBuilder.createStructField("structField",
                StreamFieldBuilder.createField("age", StreamConstants.DataType.BIGINT)
        );
        List<StreamField> fields = StreamFieldBuilder.createFields(
                idField, tempField, arrField, structField);

        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/topic");
        options.put("FORMAT", "JSON");
        options.put("CONF_KEY", "demoqt");

        String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
        System.out.println(sql);
        CreateStreamRequest request = new CreateStreamRequest();
        request.setSql(sql);

        // Create the stream
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_STREAM.getEndpoint(),
                request);
        try {
            // Get stream details
//            Map<String, Object> streamDetails = ApiRequestExecutor.executeWithPathVars(client,
        Stream streamDetails = ApiRequestExecutor.execute(client,
                    StandardEndpoints.GET_STREAM_DETAILS.getEndpoint(),
                    TEST_STREAM_NAME);
            System.out.println(streamDetails);
        // Verify details
        assertNotNull(streamDetails);
        assertEquals(TEST_STREAM_NAME, streamDetails.getName());
        assertNotNull(streamDetails.getStreamFields());
        assertEquals(4, streamDetails.getStreamFields().size());

        // Verify fields
        boolean hasIdField = streamDetails.getStreamFields().stream()
                .anyMatch(f -> "id".equals(f.getName()));
        boolean hasTempField = streamDetails.getStreamFields().stream()
                .anyMatch(f -> "temperature".equals(f.getName()));

        assertTrue(hasIdField, "Stream should have an id field");
        assertTrue(hasTempField, "Stream should have a temperature field");

        // Verify options
        assertNotNull(streamDetails.getOptions());
        assertEquals("test/topic", streamDetails.getOptions().getDatasource());
        assertEquals("JSON", streamDetails.getOptions().getFormat());
        assertEquals("demoqt", streamDetails.getOptions().getConfKey());

        } finally {
            ApiRequestExecutor.execute(client,
                    StandardEndpoints.DELETE_STREAM.getEndpoint(), TEST_STREAM_NAME);
        }
    }
    
    @Test
    void testUpdateStream() {
        // First create a stream
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        List<StreamField> fields = StreamFieldBuilder.createFields(idField, tempField);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/topic");
        options.put("FORMAT", "JSON");
        
        String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
        
        CreateStreamRequest createRequest = new CreateStreamRequest();
        createRequest.setSql(sql);
        
        // Create the stream
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_STREAM.getEndpoint(), 
                createRequest);
        
        // Update the stream - add a new field
        StreamField idField2 = StreamFieldBuilder.createBigintField("id");
        StreamField tempField2 = StreamFieldBuilder.createFloatField("temperature");
        StreamField humidityField = StreamFieldBuilder.createFloatField("humidity");
        List<StreamField> updatedFields = StreamFieldBuilder.createFields(idField2, tempField2, humidityField);
        
        Map<String, String> updatedOptions = new HashMap<>();
        updatedOptions.put("DATASOURCE", "test/updated");
        updatedOptions.put("FORMAT", "JSON");
        
        String updateSql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, updatedFields, updatedOptions);
        
        UpdateStreamRequest updateRequest = new UpdateStreamRequest();
        updateRequest.setSql(updateSql);
        
        // Update the stream
        String updateResponse = ApiRequestExecutor.executeBody(client,
                StandardEndpoints.UPDATE_STREAM.getEndpoint(), 
                updateRequest,
                TEST_STREAM_NAME);
        
        assertNotNull(updateResponse);
        
        // Get stream details and verify update
        Stream updatedStream = ApiRequestExecutor.execute(client,
                StandardEndpoints.GET_STREAM_DETAILS.getEndpoint(), 
                TEST_STREAM_NAME);
        
        // Verify details of updated stream
        assertNotNull(updatedStream);
        assertEquals(TEST_STREAM_NAME, updatedStream.getName());
        assertNotNull(updatedStream.getStreamFields());
        assertEquals(3, updatedStream.getStreamFields().size());
        
        // Verify the new field is present
        boolean hasHumidityField = updatedStream.getStreamFields().stream()
                .anyMatch(f -> "humidity".equals(f.getName()));
        
        assertTrue(hasHumidityField, "Updated stream should have a humidity field");
        
        // Verify updated options
        assertNotNull(updatedStream.getOptions());
        assertEquals("test/updated", updatedStream.getOptions().getDatasource());
    }
    
    @Test
    void testGetStreamSchema() {
        // First create a complex stream with array and struct types
        CreateStreamRequest request = StreamExamples.createComplexStreamExample();
        request.setSql(request.getSql().replace("demo", COMPLEX_STREAM_NAME)); // Use our test stream name
        
        // Create the stream
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_STREAM.getEndpoint(), 
                request);
        
        // Get stream schema
        StreamSchemaResponse schema = ApiRequestExecutor.execute(client,
                StandardEndpoints.GET_STREAM_SCHEMA.getEndpoint(), 
                COMPLEX_STREAM_NAME);
        
        // Verify schema
        assertNotNull(schema);
        assertNotNull(schema.getProperties());
        
        // Should have properties for each field in the complex stream
        assertTrue(schema.getProperties().containsKey("USERID"), "Schema should have USERID field");
        assertTrue(schema.getProperties().containsKey("FIRST_NAME"), "Schema should have FIRST_NAME field");
        assertTrue(schema.getProperties().containsKey("NICKNAMES"), "Schema should have NICKNAMES field");
        assertTrue(schema.getProperties().containsKey("ADDRESS"), "Schema should have ADDRESS field");
        
        // Verify types
        assertEquals("bigint", schema.getProperties().get("USERID").getType());
        assertEquals("string", schema.getProperties().get("FIRST_NAME").getType());
        assertEquals("array", schema.getProperties().get("NICKNAMES").getType());
        assertEquals("struct", schema.getProperties().get("ADDRESS").getType());
        
        // Verify array items
        assertNotNull(schema.getProperties().get("NICKNAMES").getItems());
        assertEquals("string", schema.getProperties().get("NICKNAMES").getItems().getType());
        
        // Verify struct properties
        assertNotNull(schema.getProperties().get("ADDRESS").getProperties());
        assertTrue(schema.getProperties().get("ADDRESS").getProperties().containsKey("STREET_NAME"));
        assertTrue(schema.getProperties().get("ADDRESS").getProperties().containsKey("NUMBER"));
    }
    
    @Test
    void testDeleteStream() {
        // First create a stream
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        List<StreamField> fields = StreamFieldBuilder.createFields(idField, tempField);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/topic");
        options.put("FORMAT", "JSON");
        
        String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
        
        CreateStreamRequest request = new CreateStreamRequest();
        request.setSql(sql);
        
        // Create the stream
        ApiRequestExecutor.executeBody(client,
                StandardEndpoints.CREATE_STREAM.getEndpoint(), 
                request);
        
        // Verify the stream exists
        List<String> streamsBefore = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_STREAMS.getEndpoint());
        assertTrue(streamsBefore.contains(TEST_STREAM_NAME), "Stream should exist before deletion");
        
        // Delete the stream
        String deleteResponse = ApiRequestExecutor.execute(client,
                StandardEndpoints.DELETE_STREAM.getEndpoint(), 
                TEST_STREAM_NAME);
        
        assertNotNull(deleteResponse);
        
        // Verify the stream no longer exists
        List<String> streamsAfter = ApiRequestExecutor.execute(client,
                StandardEndpoints.LIST_STREAMS.getEndpoint());
        assertFalse(streamsAfter.contains(TEST_STREAM_NAME), "Stream should not exist after deletion");
    }
} 