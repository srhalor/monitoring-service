package com.shdev.monitoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class for Monitoring Service.
 * JWT authentication is handled by custom filters from security-utilities.
 */
@SpringBootApplication(scanBasePackages = {"com.shdev.monitoringservice", "com.shdev.omsdatabase"})
@EnableJpaRepositories(basePackages = "com.shdev.omsdatabase.repository")
@EntityScan(basePackages = "com.shdev.omsdatabase.entity")
public class MonitoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringServiceApplication.class, args);
    }

}
