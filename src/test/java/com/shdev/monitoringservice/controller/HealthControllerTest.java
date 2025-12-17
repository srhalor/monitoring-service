package com.shdev.monitoringservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for HealthController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("HealthController Tests")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test: GET /api/health returns health status
     * Given: A request to the /api/health endpoint with authenticated user and required headers
     * When: The request is performed
     * Then: Returns HTTP 200 OK with a JSON body containing the health status
     */
    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /api/health returns health status")
    void testGetHealth() throws Exception {
        mockMvc.perform(get("/api/health")
                        .header("Atradius-Origin-Service", "test-service")
                        .header("Atradius-Origin-Application", "test-application")
                        .header("Atradius-Origin-User", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

}
