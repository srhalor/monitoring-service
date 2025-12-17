package com.shdev.monitoringservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Example REST controller demonstrating role-based security with Spring Security.
 *
 * <p>Shows different authorization approaches:
 * - URL-based authorization (configured in SecurityConfig)
 * - Method-level authorization using @PreAuthorize
 *
 * @author Shailesh Halor
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SecureController {

    /**
     * Public endpoint - accessible without authentication.
     * Configured as permitAll() in SecurityConfig.
     */
    @GetMapping("/public/info")
    public Map<String, Object> getPublicInfo() {
        log.info("Public endpoint accessed - no authentication required");
        return Map.of(
            "message", "This is a public endpoint",
            "timestamp", System.currentTimeMillis(),
            "accessible", "Anyone can access this"
        );
    }

    /**
     * Authenticated endpoint - requires any valid JWT token.
     * No specific role required.
     */
    @GetMapping("/user/profile")
    public Map<String, Object> getUserProfile(Authentication authentication) {
        log.info("User profile accessed by: {}", authentication.getName());

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", authentication.getName());
        profile.put("authenticated", authentication.isAuthenticated());
        profile.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return profile;
    }

    /**
     * Manager endpoint - requires ADMIN or MANAGER role.
     * Configured in SecurityConfig: .hasAnyRole("ADMIN", "MANAGER")
     */
    @GetMapping("/manager/dashboard")
    public Map<String, Object> getManagerDashboard(Authentication authentication) {
        log.info("Manager dashboard accessed by: {}", authentication.getName());
        return Map.of(
            "message", "Manager Dashboard",
            "user", authentication.getName(),
            "roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()),
            "data", "Manager-specific data"
        );
    }

    /**
     * Admin endpoint - requires ADMIN role.
     * Configured in SecurityConfig: .hasRole("ADMIN")
     */
    @GetMapping("/admin/users")
    public Map<String, Object> getAdminUsers(Authentication authentication) {
        log.info("Admin users endpoint accessed by: {}", authentication.getName());
        return Map.of(
            "message", "Admin Users List",
            "admin", authentication.getName(),
            "totalUsers", 150,
            "activeUsers", 120
        );
    }

    /**
     * Method-level security - requires ADMIN role.
     * Using @PreAuthorize annotation instead of SecurityConfig.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/sensitive")
    public Map<String, Object> getSensitiveReport(Authentication authentication) {
        log.info("Sensitive report accessed by ADMIN: {}", authentication.getName());
        return Map.of(
            "message", "Sensitive Report",
            "accessedBy", authentication.getName(),
            "reportType", "Financial Summary",
            "confidential", true
        );
    }

    /**
     * Complex authorization rule using SpEL expressions.
     * Requires ADMIN role OR (MANAGER role AND specific username).
     */
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #authentication.name == 'special-manager')")
    @GetMapping("/reports/advanced")
    public Map<String, Object> getAdvancedReport(Authentication authentication) {
        log.info("Advanced report accessed by: {}", authentication.getName());
        return Map.of(
            "message", "Advanced Report",
            "user", authentication.getName(),
            "accessLevel", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
        );
    }

    /**
     * Endpoint that checks if user has specific scope (OAuth2 style).
     * Example: SCOPE_read, SCOPE_write
     */
    @PreAuthorize("hasAuthority('SCOPE_read')")
    @GetMapping("/data/read")
    public Map<String, Object> readData(Authentication authentication) {
        log.info("Read data accessed with read scope by: {}", authentication.getName());
        return Map.of(
            "message", "Data retrieved successfully",
            "data", "Sample data content",
            "scope", "read"
        );
    }
}

