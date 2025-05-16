package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.dto.response.KuiperInfo;
import cn.brk2outside.ekuiper4j.sdk.api.OverviewAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for OverviewAPI endpoints
 */
public class OverviewApiTest extends BaseApiTest {

    private OverviewAPI overviewAPI;

    @BeforeEach
    void setUpOverviewApi() {
        overviewAPI = new OverviewAPI(client);
    }

    @Test
    void testGetServerInfo() {
        // Call the API to get server info
        KuiperInfo info = overviewAPI.getServerInfo();
        
        // Verify that the response is not null and has expected content
        assertNotNull(info, "Server info should not be null");
        assertNotNull(info.version(), "Version should not be null");
    }

    @Test
    void testPing() {
        // Call the ping API - should not throw exception
        overviewAPI.ping();
        // If we get here, the test passes (no exception was thrown)
    }
} 