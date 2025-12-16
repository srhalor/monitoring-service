# Monitoring Service

A Spring Boot microservice for monitoring and health check operations.

## Overview

This microservice is part of the common-libraries ecosystem and provides monitoring capabilities using Spring Boot Actuator.

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.8
- **Maven**: 3.9+
- **Parent**: `com.shdev:libraries-parent:0.1.0`

## Dependencies

This service uses the following shared libraries:
- `com.shdev:security-utilities`
- `com.shdev:oms-db-utilities`

## Prerequisites

- JDK 21 or higher
- Maven 3.9+ (or use the included Maven Wrapper)
- Access to common-libraries parent POM (installed in local Maven repository or accessible via Nexus)

## Building the Project

### Using Maven Wrapper (Windows)

```cmd
mvnw.cmd clean install
```

### Using Maven Wrapper (Unix/Mac)

```bash
./mvnw clean install
```

### Using System Maven

```bash
mvn clean install
```

## Running the Application

### Using Maven

```cmd
mvnw.cmd spring-boot:run
```

### Using Java

```bash
java -jar target/monitoring-service-0.0.1-SNAPSHOT.jar
```

## Configuration

The application configuration is located in `src/main/resources/application.properties`.

## Actuator Endpoints

Spring Boot Actuator provides several endpoints for monitoring:

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

## Development

### Project Structure

```
monitoring-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/shdev/monitoringservice/
│   │   │       └── MonitoringServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/shdev/monitoringservice/
│               └── MonitoringServiceApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Testing

Run tests using:

```cmd
mvnw.cmd test
```

## Related Projects

- [common-libraries](https://github.com/yourusername/common-libraries) - Shared libraries and parent POM

## License

Internal project for ShDev organization.

## Contact

For questions or issues, please contact the development team.

---

*Last updated: December 16, 2025*

