package com.shdev.monitoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application class for Monitoring Service.
 * <p>
 * This service provides monitoring capabilities with JWT-based authentication
 * and role-based authorization.
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.shdev.monitoringservice", "com.shdev.omsdatabase"})
@EnableJpaRepositories(basePackages = "com.shdev.omsdatabase.repository")
@EntityScan(basePackages = "com.shdev.omsdatabase.entity")
public class MonitoringServiceApplication {

    /**
     * Main entry point for the Monitoring Service application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MonitoringServiceApplication.class, args);
    }

}
