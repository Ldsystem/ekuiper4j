package cn.brk2outside.ekuiper4j.sdk.api;

import cn.brk2outside.ekuiper4j.dto.request.CreateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.request.UpdateStreamRequest;
import cn.brk2outside.ekuiper4j.dto.response.StreamSchemaResponse;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.model.stream.Stream;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * API for managing eKuiper streams
 */
@RequiredArgsConstructor
public class StreamAPI {

    private final HttpClient client;

    /**
     * Create a new stream
     *
     * @param request the stream creation request
     * @return success message
     */
    public String createStream(CreateStreamRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.CREATE_STREAM.getEndpoint(), request);
    }

    /**
     * List all streams
     *
     * @return list of stream names
     */
    public List<String> listStreams() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.LIST_STREAMS.getEndpoint());
    }

    /**
     * Get details of a specific stream
     *
     * @param streamName the name of the stream
     * @return stream details
     */
    public Stream getStreamDetails(String streamName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_STREAM_DETAILS.getEndpoint(), streamName);
    }

    /**
     * Get schema of a specific stream
     *
     * @param streamName the name of the stream
     * @return stream schema
     */
    public StreamSchemaResponse getStreamSchema(String streamName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_STREAM_SCHEMA.getEndpoint(), streamName);
    }

    /**
     * Update an existing stream
     *
     * @param streamName the name of the stream to update
     * @param request the update request
     * @return success message
     */
    public String updateStream(String streamName, UpdateStreamRequest request) {
        return ApiRequestExecutor.executeBody(client, StandardEndpoints.UPDATE_STREAM.getEndpoint(), request, streamName);
    }

    /**
     * Delete a stream
     *
     * @param streamName the name of the stream to delete
     * @return success message
     */
    public String deleteStream(String streamName) {
        return ApiRequestExecutor.execute(client, StandardEndpoints.DELETE_STREAM.getEndpoint(), streamName);
    }
} 