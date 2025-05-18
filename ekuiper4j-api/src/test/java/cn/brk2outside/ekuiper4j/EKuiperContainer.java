package cn.brk2outside.ekuiper4j;

import lombok.Getter;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TestContainers implementation for eKuiper.
 */
@Getter
public class EKuiperContainer extends GenericContainer<EKuiperContainer> {
    
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("krccr.ccs.tencentyun.com/arm64.brk2outside.cn/lfedge_ekuiper:2.1.3-full");
    private static final int DEFAULT_PORT = 9081;

    /**
     * -- GETTER --
     *  Get the local management directory path, which is mapped to
     *  the container's /opt/ekuiper/etc/mgmt directory.
     *
     * @return Path to the local management directory
     */
    private final Path mgmtDirectory;
    
    public EKuiperContainer() {
        this(DEFAULT_IMAGE_NAME, null);
    }

    public EKuiperContainer(Path mgmtDirectory) {
        this(DEFAULT_IMAGE_NAME, mgmtDirectory);
    }
    
    public EKuiperContainer(DockerImageName imageName, Path mgmtDirectory) {
        super(imageName);
        
        addExposedPort(DEFAULT_PORT);
        waitingFor(Wait.forHttp("/").forPort(DEFAULT_PORT));
        
        // Create a temporary directory for management files
        if (null != mgmtDirectory)
            this.mgmtDirectory = mgmtDirectory;
        else
            try {
                this.mgmtDirectory = Files.createTempDirectory("ekuiper-mgmt");
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temporary management directory", e);
            }
        
        // Mount the mgmt directory to the container
        withFileSystemBind(
                this.mgmtDirectory.toString(),
                "/kuiper/etc/mgmt",
                BindMode.READ_WRITE
        );
        
        // Add environment variables for MQTT broker connection
        withEnv("MQTT_HOST", "mqtt-broker");
        withEnv("MQTT_PORT", "1883");
    }
    
    public EKuiperContainer withBasicAuth() {
        withEnv("KUIPER__BASIC__AUTHENTICATION", "true");
        return this;
    }

    /**
     * Get the host where eKuiper is running.
     *
     * @return The host address
     */
    public String getEkuiperHost() {
        return getHost();
    }
    
    /**
     * Get the mapped port for eKuiper REST API.
     *
     * @return The mapped port
     */
    public int getEkuiperPort() {
        return getMappedPort(DEFAULT_PORT);
    }

    @Override
    public void stop() {
        super.stop();
        
        // Clean up the temporary directory
        try {
            Files.deleteIfExists(mgmtDirectory);
        } catch (IOException e) {
            // Log but ignore
            System.err.println("Failed to delete temporary management directory: " + e.getMessage());
        }
    }
} 