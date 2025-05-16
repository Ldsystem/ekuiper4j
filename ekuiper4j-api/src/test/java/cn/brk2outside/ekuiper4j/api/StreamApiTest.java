package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.request.UpdateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.StreamSchemaResponse;
import cn.brk2outside.ekuiper4j.model.stream.Stream;
import cn.brk2outside.ekuiper4j.model.stream.StreamField;
import cn.brk2outside.ekuiper4j.sdk.api.StreamAPI;
import cn.brk2outside.ekuiper4j.utils.StreamFieldBuilder;
import cn.brk2outside.ekuiper4j.utils.StreamSqlBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StreamAPI endpoints
 */
public class StreamApiTest extends BaseApiTest {

    private static final String TEST_STREAM_NAME = "test_stream_api";
    private StreamAPI streamAPI;

    @BeforeEach
    void setUpStreamApi() {
        streamAPI = new StreamAPI(client);
        // Clean up any existing test streams
        cleanupTestStream();
    }

    @AfterEach
    void tearDownStream() {
        // Clean up test streams
        cleanupTestStream();
    }

    private void cleanupTestStream() {
        try {
            streamAPI.deleteStream(TEST_STREAM_NAME);
        } catch (Exception e) {
            // Ignore errors if stream doesn't exist
        }
    }

    private CreateStreamRequest createTestStreamRequest() {
        // Create a stream with id and temperature fields
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        List<StreamField> fields = StreamFieldBuilder.createFields(idField, tempField);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/topic");
        options.put("FORMAT", "JSON");
        
        String sql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, fields, options);
        
        CreateStreamRequest request = new CreateStreamRequest();
        request.setSql(sql);
        return request;
    }

    @Test
    void testCreateAndListStreams() {
        // Create a stream
        CreateStreamRequest request = createTestStreamRequest();
        String response = streamAPI.createStream(request);
        
        // Verify the response
        assertNotNull(response);
        
        // List streams and verify our stream exists
        List<String> streams = streamAPI.listStreams();
        
        assertNotNull(streams);
        assertTrue(streams.contains(TEST_STREAM_NAME), "Created stream should be in the list");
    }

    @Test
    void testGetStreamDetails() {
        // First create a stream
        CreateStreamRequest request = createTestStreamRequest();
        streamAPI.createStream(request);
        
        // Get stream details
        Stream stream = streamAPI.getStreamDetails(TEST_STREAM_NAME);
        
        // Verify details
        assertNotNull(stream);
        assertEquals(TEST_STREAM_NAME, stream.getName());
        
        // Get stream schema
        StreamSchemaResponse schema = streamAPI.getStreamSchema(TEST_STREAM_NAME);
        
        // Verify schema
        assertNotNull(schema);
        assertNotNull(schema.getProperties());
        assertEquals(2, schema.getProperties().size());
    }

    @Test
    void testUpdateStream() {
        // First create a stream
        CreateStreamRequest createRequest = createTestStreamRequest();
        streamAPI.createStream(createRequest);
        
        // Update the stream - add a new field
        StreamField idField = StreamFieldBuilder.createBigintField("id");
        StreamField tempField = StreamFieldBuilder.createFloatField("temperature");
        StreamField humidityField = StreamFieldBuilder.createFloatField("humidity"); // New field
        List<StreamField> updatedFields = StreamFieldBuilder.createFields(idField, tempField, humidityField);
        
        Map<String, String> options = new HashMap<>();
        options.put("DATASOURCE", "test/updated_topic");
        options.put("FORMAT", "JSON");
        
        String updatedSql = StreamSqlBuilder.buildCreateStreamSql(TEST_STREAM_NAME, updatedFields, options);
        
        UpdateStreamRequest updateRequest = new UpdateStreamRequest();
        updateRequest.setSql(updatedSql);
        
        // Update the stream
        String updateResponse = streamAPI.updateStream(TEST_STREAM_NAME, updateRequest);
        
        assertNotNull(updateResponse);
        
        // Get stream schema and verify update
        StreamSchemaResponse schema = streamAPI.getStreamSchema(TEST_STREAM_NAME);
        
        // Verify updated schema
        assertNotNull(schema);
        assertNotNull(schema.getProperties());
        assertEquals(3, schema.getProperties().size(), "Schema should now have 3 fields");
    }

    @Test
    void testDeleteStream() {
        // First create a stream
        CreateStreamRequest request = createTestStreamRequest();
        streamAPI.createStream(request);
        
        // Verify the stream exists
        List<String> streamsBefore = streamAPI.listStreams();
        assertTrue(streamsBefore.contains(TEST_STREAM_NAME), "Stream should exist before deletion");
        
        // Delete the stream
        String deleteResponse = streamAPI.deleteStream(TEST_STREAM_NAME);
        
        assertNotNull(deleteResponse);
        
        // Verify the stream no longer exists
        List<String> streamsAfter = streamAPI.listStreams();
        assertFalse(streamsAfter.contains(TEST_STREAM_NAME), "Stream should not exist after deletion");
    }
} 