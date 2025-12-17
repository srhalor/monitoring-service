package com.shdev.monitoringservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for monitoring-service.
 *
 * <p>Authentication is handled by JwtAuthenticationFilter which:
 * - Validates JWT tokens with security-service
 * - Extracts roles/authorities from JWT payload
 * - Sets Spring Security context with authenticated user and roles
 *
 * <p>Authorization is handled by Spring Security using:
 * - URL-based rules (configured below)
 * - Method-level security annotations (@PreAuthorize, @Secured, etc.)
 *
 * @author Shailesh Halor
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()  // Enable @PreAuthorize, @PostAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security for JWT-based authentication and role-based authorization");

        http
            // Disable CSRF (not needed for stateless JWT APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session (JWT-based, no session needed)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // URL-based authorization rules
            .authorizeHttpRequests(authorize -> authorize
                // Public endpoints - no authentication required
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()

                // Admin endpoints - require ADMIN role
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Manager endpoints - require ADMIN or MANAGER role
                .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")

                // All other /api/** endpoints - require authentication (any role)
                .requestMatchers("/api/**").authenticated()

                // Everything else - require authentication
                .anyRequest().authenticated()
            );

        log.info("Spring Security configured successfully");
        log.info("- JWT authentication via custom filter");
        log.info("- Role-based authorization enabled");
        log.info("- Method security enabled (@PreAuthorize)");

        return http.build();
    }
}

