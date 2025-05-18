package cn.brk2outside.ekuiper4j.api;

import cn.brk2outside.ekuiper4j.config.EKuiperClientProperties;
import cn.brk2outside.ekuiper4j.EKuiperContainer;
import cn.brk2outside.ekuiper4j.MqttBrokerContainer;
import cn.brk2outside.ekuiper4j.http.auth.JwtTokenManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base test class for eKuiper tests.
 * Sets up a container for eKuiper before all tests and shuts it down after all tests.
 */
@Testcontainers
public abstract class BaseEKuiperAuthTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEKuiperAuthTest.class);
    
    // Create a shared Docker network for the containers
    private static final Network NETWORK = Network.newNetwork();
    private static final Path eKuiperMgmtPath;
    
    // Start MQTT broker container first
    @Container
    protected static final MqttBrokerContainer MQTT_BROKER = new MqttBrokerContainer()
            .withNetwork(NETWORK)
            .withNetworkAliases("mqtt-broker")
            .withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("mqtt"));
    
    // Then start eKuiper container
    @Container
    protected static final EKuiperContainer EKUIPER;

    protected static final EKuiperClientProperties  properties;
    protected static final JwtTokenManager tokenManager;

    static {
        try {
            eKuiperMgmtPath = Files.createTempDirectory("ekuiper-mgmt");
            // Configure properties
            properties = new EKuiperClientProperties();

            // Configure JWT
            EKuiperClientProperties.JwtAuth jwtAuth = new EKuiperClientProperties.JwtAuth();
            jwtAuth.setEnabled(true);
            jwtAuth.setIssuer("test-client.pub");
            jwtAuth.setAudience("eKuiper");
            jwtAuth.setExpirationTimeSeconds(3600);
            jwtAuth.setEkuiperMgmtPath(eKuiperMgmtPath.toString());

            properties.setJwt(jwtAuth);

            // Create token manager and client
            tokenManager = new JwtTokenManager(properties);
            tokenManager.getToken();

            EKUIPER = new EKuiperContainer(eKuiperMgmtPath)
                    .withBasicAuth()
                    .withNetwork(NETWORK)
                    .withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("ekuiper"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary management directory", e);
        }
    }

    /**
     * Set up the containers once before all tests.
     */
    @BeforeAll
    public static void setUpAll() {
        // Start MQTT broker first
        if (!MQTT_BROKER.isRunning()) {
            MQTT_BROKER.start();
        }
        LOGGER.info("MQTT broker container started at {}:{}", MQTT_BROKER.getMqttHost(), MQTT_BROKER.getMqttPort());

        // Then start eKuiper
        if (!EKUIPER.isRunning()) {
            EKUIPER.start();
        }
        LOGGER.info("eKuiper container started at {}:{}", EKUIPER.getEkuiperHost(), EKUIPER.getEkuiperPort());
        LOGGER.info("Using management directory: {}", EKUIPER.getMgmtDirectory());
    }
    
    /**
     * Tear down the containers after all tests.
     */
    @AfterAll
    public static void tearDownAll() {
        if (EKUIPER.isRunning()) {
            EKUIPER.stop();
            LOGGER.info("eKuiper container stopped");
        }
        
        if (MQTT_BROKER.isRunning()) {
            MQTT_BROKER.stop();
            LOGGER.info("MQTT broker container stopped");
        }
    }
    
    /**
     * Get the MQTT broker URL for external connections (outside containers).
     * 
     * @return MQTT broker URL
     */
    public static String getMqttBrokerUrl() {
        return MQTT_BROKER.getMqttUrl();
    }
    
    /**
     * Get the MQTT broker URL for internal connections (from eKuiper container).
     * This uses the network alias that's accessible inside the Docker network.
     * 
     * @return MQTT broker URL for internal connections
     */
    public static String getMqttBrokerInternalUrl() {
        return "tcp://mqtt-broker:1883";
    }
} 