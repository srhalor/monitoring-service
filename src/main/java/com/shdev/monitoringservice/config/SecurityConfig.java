package com.shdev.monitoringservice.config;

import com.shdev.security.filter.JwtAuthenticationFilter;
import com.shdev.security.filter.OriginHeadersFilter;
import com.shdev.security.handler.CustomAccessDeniedHandler;
import com.shdev.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for monitoring-service.
 *
 * <p><b>Single Source of Truth for Security:</b>
 * This configuration defines ALL authentication and authorization rules.
 * Custom filters (JwtAuthenticationFilter, OriginHeadersFilter) only validate credentials
 * but do NOT make access decisions - Spring Security does that here.
 *
 * <p><b>Authentication Flow:</b>
 * 1. JwtAuthenticationFilter validates JWT tokens with security-service
 * 2. Extracts roles/authorities from userRole field ("ADMIN:USER")
 * 3. Sets Spring Security context with authenticated user and roles
 * 4. Spring Security checks the rules below to allow/deny access
 *
 * <p><b>Authorization is handled by:</b>
 * - URL-based rules (configured below) - e.g., .permitAll(), .hasRole()
 * - Method-level security annotations - e.g., @PreAuthorize("hasRole('ADMIN')")
 *
 * @author Shailesh Halor
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity()  // Enable @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OriginHeadersFilter originHeadersFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security for JWT-based authentication and role-based authorization");

        http
            // Disable CSRF (not needed for stateless JWT APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session (JWT-based, no session needed)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Custom exception handling
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint))

            // Add custom filters before Spring Security's authentication filters
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(originHeadersFilter, JwtAuthenticationFilter.class)

            // URL-based authorization rules
            .authorizeHttpRequests(authorize -> authorize
                // Actuator endpoints - public for health monitoring
                .requestMatchers("/actuator/**").permitAll()

                // Public API endpoints (if any)
                .requestMatchers("/api/public/**").permitAll()

                // Example role-based rules (uncomment and modify as needed):
                // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER")

                // All other /api/** endpoints - require authentication
                .requestMatchers("/api/**").authenticated()

                // Default - require authentication
                .anyRequest().authenticated()
            );

        log.info("Spring Security configured successfully");
        log.info("- JWT authentication via custom filter");
        log.info("- Role-based authorization enabled");
        log.info("- Method security enabled (@PreAuthorize)");

        return http.build();
    }
}

