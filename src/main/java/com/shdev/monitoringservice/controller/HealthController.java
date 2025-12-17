package com.shdev.monitoringservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the health status of the application.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    /**
     * Endpoint to retrieve the health status of the application.
     *
     * @return the health status as a HealthComponent
     */
    @GetMapping("/health")
    public HealthComponent getHealth() {
        log.info("Received request for health status");
        return healthEndpoint.health();
    }

}
