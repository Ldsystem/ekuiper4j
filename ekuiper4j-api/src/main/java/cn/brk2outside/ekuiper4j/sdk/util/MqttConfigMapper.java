package cn.brk2outside.ekuiper4j.sdk.util;

import cn.brk2outside.ekuiper4j.dto.request.MqttSourceConfigRequest;
import cn.brk2outside.ekuiper4j.dto.response.MqttSourceConfigResponse;
import cn.brk2outside.ekuiper4j.constants.MqttDecompressionType;
import cn.brk2outside.ekuiper4j.constants.MqttProtocolVersion;
import cn.brk2outside.ekuiper4j.model.MqttConnProps;
import cn.brk2outside.ekuiper4j.model.source.mqtt.MqttSourceConfig;

/**
 * Utility class for mapping between MQTT config DTOs and model objects.
 */
public final class MqttConfigMapper {

    private MqttConfigMapper() {
        // Utility class, do not instantiate
    }

    /**
     * Convert from request DTO to model.
     *
     * @param request the request DTO
     * @return the model
     */
    public static MqttSourceConfig toModel(MqttSourceConfigRequest request) {
        if (request == null) {
            return null;
        }

        // Create the common connection properties
        MqttConnProps connProps = MqttConnProps.builder()
                .connectionSelector(request.getConnectionSelector())
                .server(request.getServer())
                .username(request.getUsername())
                .password(request.getPassword())
                .protocolVersion(MqttProtocolVersion.fromString(request.getProtocolVersion()))
                .clientid(request.getClientid())
                .qos(request.getQos())
                .certificationPath(request.getCertificationPath())
                .privateKeyPath(request.getPrivateKeyPath())
                .rootCaPath(request.getRootCaPath())
                .insecureSkipVerify(request.getInsecureSkipVerify())
                .build();

        // Create the source-specific config with the common props
        return MqttSourceConfig.builder()
                .connProps(connProps)
                .kubeedgeVersion(request.getKubeedgeVersion())
                .kubeedgeModelFile(request.getKubeedgeModelFile())
                .decompression(MqttDecompressionType.fromString(request.getDecompression()))
                .build();
    }

    /**
     * Convert from model to response DTO.
     *
     * @param model the model
     * @return the response DTO
     */
    public static MqttSourceConfigResponse toResponse(MqttSourceConfig model) {
        if (model == null) {
            return null;
        }

        MqttConnProps connProps = model.getConnProps();
        if (connProps == null) {
            connProps = MqttConnProps.builder().build();
        }

        return MqttSourceConfigResponse.builder()
                .connectionSelector(connProps.getConnectionSelector())
                .server(connProps.getServer())
                .username(connProps.getUsername())
                .protocolVersion(connProps.getProtocolVersion() != null ? connProps.getProtocolVersion().getVersion() : null)
                .clientid(connProps.getClientid())
                .qos(connProps.getQos())
                .certificationPath(connProps.getCertificationPath())
                .privateKeyPath(connProps.getPrivateKeyPath())
                .rootCaPath(connProps.getRootCaPath())
                .insecureSkipVerify(connProps.getInsecureSkipVerify())
                .kubeedgeVersion(model.getKubeedgeVersion())
                .kubeedgeModelFile(model.getKubeedgeModelFile())
                .decompression(model.getDecompression() != null ? model.getDecompression().getType() : null)
                .build();
    }

    /**
     * Convert from model to response DTO with additional status information.
     *
     * @param model the model
     * @param status the connection status
     * @return the response DTO
     */
    public static MqttSourceConfigResponse toResponse(MqttSourceConfig model, String status) {
        MqttSourceConfigResponse response = toResponse(model);
        if (response != null) {
            response.setStatus(status);
        }
        return response;
    }
} 