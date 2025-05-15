package cn.brk2outside.ekuiper4j.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base test class for eKuiper tests.
 * Sets up a container for eKuiper before all tests and shuts it down after all tests.
 */
@Testcontainers
public abstract class BaseEKuiperTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEKuiperTest.class);
    
    @Container
    protected static final EKuiperContainer EKUIPER = new EKuiperContainer()
            .withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix("ekuiper"));
    
    /**
     * Set up the eKuiper container once before all tests.
     */
    @BeforeAll
    public static void setUpAll() {
        if (!EKUIPER.isRunning()) {
            EKUIPER.start();
        }
        
        LOGGER.info("eKuiper container started at {}:{}", EKUIPER.getEkuiperHost(), EKUIPER.getEkuiperPort());
        LOGGER.info("Using management directory: {}", EKUIPER.getMgmtDirectory());
    }
    
    /**
     * Tear down the eKuiper container after all tests.
     */
    @AfterAll
    public static void tearDownAll() {
        if (EKUIPER.isRunning()) {
            EKUIPER.stop();
            LOGGER.info("eKuiper container stopped");
        }
    }
} 