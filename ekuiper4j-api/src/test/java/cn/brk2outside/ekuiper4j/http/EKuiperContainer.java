package cn.brk2outside.ekuiper4j.http;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

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
        this(DEFAULT_IMAGE_NAME);
    }
    
    public EKuiperContainer(DockerImageName imageName) {
        super(imageName);
        
        addExposedPort(DEFAULT_PORT);
        waitingFor(Wait.forHttp("/").forPort(DEFAULT_PORT));
        
        // Create a temporary directory for management files
        try {
            this.mgmtDirectory = Files.createTempDirectory("ekuiper-mgmt");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary management directory", e);
        }
        
        // Mount the mgmt directory to the container
        withFileSystemBind(
                mgmtDirectory.toString(),
                "/opt/ekuiper/etc/mgmt"
        );
    }
    
    /**
     * Get the host where eKuiper is running.
     *
     * @return The host address
     */
    public String getEkuiperHost() {
        return getContainerIpAddress();
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