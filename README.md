# eKuiper4j

[![Build and Test](https://github.com/Ldsystem/ekuiper4j/actions/workflows/build.yml/badge.svg)](https://github.com/Ldsystem/ekuiper4j/actions/workflows/build.yml)
[![Code Quality](https://github.com/Ldsystem/ekuiper4j/actions/workflows/code-quality.yml/badge.svg)](https://github.com/Ldsystem/ekuiper4j/actions/workflows/code-quality.yml)
[![Release](https://github.com/Ldsystem/ekuiper4j/actions/workflows/release.yml/badge.svg)](https://github.com/Ldsystem/ekuiper4j/actions/workflows/release.yml)
[![Maven Central](https://img.shields.io/maven-central/v/cn.brk2outside.ekuiper4j/ekuiper4j-api)](https://search.maven.org/artifact/cn.brk2outside.ekuiper4j/ekuiper4j-api)
[![Java Version](https://img.shields.io/badge/java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License](https://img.shields.io/github/license/Ldsystem/ekuiper4j)](https://github.com/Ldsystem/ekuiper4j/blob/main/LICENSE)

A Java client library for interacting with [LF Edge eKuiper](https://github.com/lf-edge/ekuiper), providing a type-safe and easy-to-use API for managing eKuiper resources.

## Features

- Full REST API coverage for eKuiper management
- Type-safe request/response objects
- JWT authentication support
- Automatic key management
- Comprehensive test coverage
- Spring Boot auto-configuration
- Docker-based integration testing

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- eKuiper 1.x

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>cn.brk2outside.ekuiper4j</groupId>
    <artifactId>ekuiper4j-api</artifactId>
    <version>0.1.2</version>
</dependency>
```

## Quick Start

### Basic Configuration

```java
@Configuration
public class EKuiperConfig {
    @Bean
    public EKuiperClient eKuiperClient() {
        EKuiperClientProperties properties = new EKuiperClientProperties();
        properties.setHost("localhost");
        properties.setPort(9081);
      
        // Optional: Configure JWT authentication
        EKuiperClientProperties.JwtAuth jwtAuth = new EKuiperClientProperties.JwtAuth();
        jwtAuth.setEnabled(true);
        jwtAuth.setIssuer("your-issuer");
        jwtAuth.setAudience("your-audience");
        properties.setJwt(jwtAuth);
      
        return new EKuiperClient(properties);
    }
}
```

### Using the Client

```java
@Service
public class YourService {
    private final EKuiperClient eKuiperClient;
  
    public YourService(EKuiperClient eKuiperClient) {
        this.eKuiperClient = eKuiperClient;
    }
  
    public void createRule() {
        CreateRuleRequest request = CreateRuleRequest.builder()
            .id("rule1")
            .sql("SELECT * FROM stream1")
            .actions(List.of(
                Map.of("mqtt", Map.of(
                    "server", "tcp://localhost:1883",
                    "topic", "result"
                ))
            ))
            .build();
          
        eKuiperClient.getRuleAPI().createRule(request);
    }
}
```

## API Documentation

The library provides the following main APIs:

### Rule Management

- Create, update, delete rules
- Start/stop/restart rules
- Get rule status and metrics
- Validate rules

### Stream Management

- Create and manage streams
- Get stream schemas
- Update stream configurations

### Connection Management

- Create and manage connections
- Test connection configurations
- Support for MQTT and other protocols

### Configuration Management

- Manage MQTT broker configurations
- Update system settings

For detailed API documentation, see [API Documentation](docs/document.json).

## Authentication

The library supports JWT authentication for secure communication with eKuiper. When enabled:

1. RSA key pair is automatically generated and managed
2. Public key is deployed to eKuiper's management directory
3. JWT tokens are automatically generated and included in requests
4. Private key is reused if available, with automatic public key regeneration

## Testing

The project includes comprehensive test coverage:

- Unit tests for all API components
- Integration tests using TestContainers
- Docker-based eKuiper instance for testing
- MQTT broker container for testing message flows

To run tests:

```bash
mvn test
```

## Development

### Project Structure

```
ekuiper4j/
├── ekuiper4j-api/           # Main API module
│   ├── src/
│   │   ├── main/java/      # Source code
│   │   └── test/java/      # Test code
│   └── pom.xml
├── docs/                    # Documentation
├── scripts/                 # Utility scripts
└── pom.xml                 # Root POM
```

### Building

```bash
mvn clean install
```

### Releasing

```bash
mvn release:prepare
mvn release:perform
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [LF Edge eKuiper](https://github.com/lf-edge/ekuiper) for the excellent edge computing platform
- [Spring Boot](https://spring.io/projects/spring-boot) for the framework
- [TestContainers](https://www.testcontainers.org/) for testing infrastructure
