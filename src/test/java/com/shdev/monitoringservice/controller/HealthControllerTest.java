package com.shdev.monitoringservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for HealthController.
 */
@WebMvcTest(
        controllers = HealthController.class,
        excludeAutoConfiguration = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JpaRepositoriesAutoConfiguration.class
        }
)
@ComponentScan(
        basePackages = "com.shdev.monitoringservice.controller",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = HealthController.class
        ),
        useDefaultFilters = false
)
@ContextConfiguration(classes = HealthControllerTest.TestConfig.class)
@DisplayName("HealthController Tests")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Configuration
    static class TestConfig {
        @Bean
        public HealthEndpoint healthEndpoint() {
            return org.mockito.Mockito.mock(HealthEndpoint.class);
        }
    }

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
        // Mock the health endpoint to return a UP status
        HealthComponent healthComponent = Health.up().build();
        when(healthEndpoint.health()).thenReturn(healthComponent);

        mockMvc.perform(get("/api/health")
                        .header("Atradius-Origin-Service", "test-service")
                        .header("Atradius-Origin-Application", "test-application")
                        .header("Atradius-Origin-User", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

}
