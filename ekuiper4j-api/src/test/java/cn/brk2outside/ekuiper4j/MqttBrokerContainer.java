package cn.brk2outside.ekuiper4j;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * TestContainers implementation for a MQTT broker.
 * Uses the Eclipse Mosquitto image.
 */
@Getter
public class MqttBrokerContainer extends GenericContainer<MqttBrokerContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("eclipse-mosquitto:2.0.15");
    private static final int MQTT_PORT = 1883;
    private static final int MQTT_WEBSOCKET_PORT = 9001;

    private Path configDirectory;
    private Path configFile;

    public MqttBrokerContainer() {
        this(DEFAULT_IMAGE_NAME);
    }

    public MqttBrokerContainer(DockerImageName imageName) {
        super(imageName);

        // Expose the MQTT ports
        addExposedPort(MQTT_PORT);
        addExposedPort(MQTT_WEBSOCKET_PORT);

        // Wait until the container is ready
        waitingFor(Wait.forLogMessage(".*mosquitto version .* running.*", 1));
    }

    @Override
    public void start() {
        // Create config directory and file before starting the container
        createConfigDirectory();
        super.start();
    }

    private void createConfigDirectory() {
        try {
            // Clean up any existing config directory
            if (configDirectory != null) {
                cleanupConfigDirectory();
            }

            // Create a new temporary directory for Mosquitto configuration
            this.configDirectory = Files.createTempDirectory("mosquitto-config");
            this.configFile = configDirectory.resolve("mosquitto.conf");

            // Create Mosquitto configuration file
            String config = "listener 1883\n" +
                    "allow_anonymous true\n" +
                    "persistence false\n";
            Files.writeString(configFile, config, StandardOpenOption.CREATE);

            // Mount the configuration
            withCopyFileToContainer(
                    MountableFile.forHostPath(configDirectory.toString()),
                    "/mosquitto/config"
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary config directory", e);
        }
    }

    private void cleanupConfigDirectory() {
        try {
            if (configFile != null) {
                Files.deleteIfExists(configFile);
            }
            if (configDirectory != null) {
                Files.deleteIfExists(configDirectory);
            }
        } catch (IOException e) {
            // Log but ignore cleanup errors
            System.err.println("Failed to delete temporary config directory: " + e.getMessage());
        }
    }

    /**
     * Get the host where the MQTT broker is running.
     *
     * @return The host address
     */
    public String getMqttHost() {
        return getHost();
    }

    /**
     * Get the mapped port for MQTT broker.
     *
     * @return The mapped port
     */
    public int getMqttPort() {
        return getMappedPort(MQTT_PORT);
    }

    /**
     * Get the connection URL for the MQTT broker.
     *
     * @return The MQTT connection URL (tcp://host:port)
     */
    public String getMqttUrl() {
        return String.format("tcp://%s:%d", getMqttHost(), getMqttPort());
    }

    @Override
    public void stop() {
        super.stop();
        cleanupConfigDirectory();
    }
}