package cn.brk2outside.ekuiper4j.sdk.api;


import cn.brk2outside.ekuiper4j.dto.response.KuiperInfo;
import cn.brk2outside.ekuiper4j.http.HttpClient;
import cn.brk2outside.ekuiper4j.sdk.endpoint.StandardEndpoints;
import cn.brk2outside.ekuiper4j.sdk.util.ApiRequestExecutor;
import lombok.RequiredArgsConstructor;

/**
 * API for eKuiper server information
 */
@RequiredArgsConstructor
public class OverviewAPI {

    private final HttpClient client;

    public KuiperInfo getServerInfo() {
        return ApiRequestExecutor.execute(client, StandardEndpoints.GET_SERVER_INFO.getEndpoint());
    }

    public void ping() {
        ApiRequestExecutor.execute(client, StandardEndpoints.PING.getEndpoint());
    }

}
