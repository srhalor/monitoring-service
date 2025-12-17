# Monitoring Service

A Spring Boot microservice demonstrating JWT-based authentication with role-based authorization.

## Overview

This microservice is part of the common-libraries ecosystem and provides:
- Health check monitoring via Spring Boot Actuator
- JWT-based authentication with security-service integration
- Role-based authorization using Spring Security
- Example of secure microservice architecture

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.8
- **Spring Security**: JWT-based authentication
- **Maven**: 3.9+
- **Parent**: `com.shdev:libraries-parent:0.1.0`

## Dependencies

### Shared Libraries
- `com.shdev:common-utilities` - Common utilities and constants
- `com.shdev:security-utilities` - JWT authentication filters

### Key Features from Dependencies
- JWT token validation with security-service
- Automatic role extraction from JWT tokens
- Spring Security context integration
- MDC logging for user tracking

---

## 🚀 Quick Start

### Prerequisites

1. **JDK 21** or higher
2. **Maven 3.9+** (or use included Maven Wrapper)
3. **security-service** running on port 8090 (for JWT validation)

### Build

```bash
# Windows
mvnw.cmd clean package

# Unix/Mac
./mvnw clean package
```

### Run

```bash
# Using Maven
mvnw.cmd spring-boot:run

# Using JAR
java -jar target/monitoring-service-0.0.1-SNAPSHOT.jar
```

### Access

- **Health Check**: http://localhost:8080/actuator/health (public, no auth)
- **API Health**: http://localhost:8080/api/health (requires JWT)

---

## 🔐 Security

This service uses JWT-based authentication with role-based authorization:
- JWT tokens validated with security-service
- Roles automatically extracted and integrated with Spring Security
- URL-based and method-level authorization supported

See [security-utilities](../common-libraries/security-utilities/README.md) for architecture details.


---

## 📋 How to Implement Role-Based Authorization

### Option 1: URL-Based Rules (SecurityConfig)

**File**: `src/main/java/com/shdev/monitoringservice/config/SecurityConfig.java`

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.authorizeHttpRequests(authorize -> authorize
        // Public endpoints
        .requestMatchers("/actuator/**").permitAll()
        .requestMatchers("/api/public/**").permitAll()
        
        // Role-based endpoints
        .requestMatchers("/api/admin/**").hasRole("ADMIN")
        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
        
        // Require authentication for all other /api/** endpoints
        .requestMatchers("/api/**").authenticated()
        
        // Default - deny all
        .anyRequest().authenticated()
    );
    return http.build();
}
```

### Option 2: Method-Level Security (Annotations)

**Enable in SecurityConfig** (already enabled):
```java
@EnableMethodSecurity  // ✅ Already enabled
```

**Use in Controllers**:

```java
@RestController
@RequestMapping("/api")
public class MyController {
    
    // ✅ Only ADMIN can access
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
    
    // ✅ ADMIN or MANAGER can access
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/reports")
    public Report createReport() {
        return reportService.create();
    }
    
    // ✅ Any authenticated user can access
    @GetMapping("/profile")
    public Profile getUserProfile(Authentication auth) {
        return profileService.get(auth.getName());
    }
    
    // ✅ Complex SpEL expression
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #userId == authentication.name)")
    @GetMapping("/user/{userId}/data")
    public Data getUserData(@PathVariable String userId) {
        return dataService.getData(userId);
    }
}
```

---

## 🎯 Authorization Patterns

For complete list of authorization patterns with `@PreAuthorize`, see [security-utilities Authorization Patterns](../common-libraries/security-utilities/README.md#authorization-patterns).

**Quick Examples**:
- Single role: `@PreAuthorize("hasRole('ADMIN')")`
- Multiple roles: `@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")`
- Own resource: `@PreAuthorize("#userId == authentication.name")`
- Complex: `@PreAuthorize("hasRole('ADMIN') or #id == authentication.name")`

---

## 🧪 Testing with JWT Tokens

### 1. Get JWT Token from security-service

```bash
curl -X POST 'http://localhost:8090/oauth2/rest/token' \
  -H 'X-OAUTH-IDENTITY-DOMAIN-NAME: DEV_JET_WebGateDomain' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Authorization: Basic REVWX09NU19PSURNV2ViR2F0ZUlEOmpnYWs4MjRmSGRLMzlnczhnYQ==' \
  -d 'grant_type=CLIENT_CREDENTIALS&scope=DEV_TokenPOC_RS.sharedcomponents'
```

**Response**:
```json
{
  "access_token": "eyJraWQi...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### 2. Test Public Endpoint (No Auth Required)

```bash
curl http://localhost:8080/actuator/health
```

**Expected**: ✅ 200 OK (no authentication needed)

### 3. Test Protected Endpoint (With JWT)

```bash
TOKEN="your_jwt_token_here"

curl http://localhost:8080/api/health \
  -H "Authorization: Bearer $TOKEN" \
  -H 'X-OAUTH-IDENTITY-DOMAIN-NAME: DEV_JET_WebGateDomain' \
  -H 'Atradius-Origin-Service: monitoring-ui' \
  -H 'Atradius-Origin-Application: monitoring-tool' \
  -H 'Atradius-Origin-User: Dev User'
```

**Expected**: ✅ 200 OK (authenticated)

### 4. Test Protected Endpoint (Without JWT)

```bash
curl http://localhost:8080/api/health
```

**Expected**: ❌ 401 Unauthorized

### 5. Test Role-Based Endpoint

```bash
# If your controller has @PreAuthorize("hasRole('ADMIN')")
curl http://localhost:8080/api/admin/data \
  -H "Authorization: Bearer $TOKEN"
```

**Expected**:
- ✅ 200 OK if token has ADMIN role
- ❌ 403 Forbidden if token doesn't have ADMIN role

---


## 🛠️ Configuration

### application.yml

```yaml
server:
  port: 8080

security:
  filter:
    token-validation-url: http://localhost:8090/oauth2/rest/token/info

logging:
  level:
    com.shdev.monitoringservice: DEBUG
```

See [security-utilities README](../common-libraries/security-utilities/README.md#configuration) for all security configuration options.

---

## 📁 Project Structure

```
monitoring-service/
├── src/
│   ├── main/
│   │   ├── java/com/shdev/monitoringservice/
│   │   │   ├── MonitoringServiceApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java          # Security configuration
│   │   │   └── controller/
│   │   │       └── HealthController.java        # Health check endpoint
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

---


---

## 🎓 Best Practices

### 1. Choose the Right Authorization Approach

**Use URL-based rules** when:
- ✅ Simple role checks for entire endpoint groups
- ✅ Securing entire URL patterns (`/api/admin/**`)

**Use method-level security** when:
- ✅ Complex authorization logic
- ✅ Parameter-based authorization
- ✅ Different endpoints in same URL path need different rules

### 2. Security Configuration Tips

```java
// ✅ DO: Use specific paths first, general paths last
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/**").authenticated()

// ❌ DON'T: General paths first (will match everything)
.requestMatchers("/api/**").authenticated()
.requestMatchers("/api/admin/**").hasRole("ADMIN")  // Never reached!
```

### 3. Testing Roles

```java
@Test
void testAdminEndpoint() {
    // Mock authentication with ADMIN role
    when(authentication.getAuthorities()).thenReturn(
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
    );
    
    // Test should pass
    ResponseEntity response = controller.adminEndpoint();
    assertEquals(200, response.getStatusCodeValue());
}
```

---

## 📖 Additional Documentation

- **Spring Security Implementation**: `SPRING_SECURITY_IMPLEMENTATION.md`
- **Role-Based Auth Quick Reference**: `ROLE_BASED_AUTH_QUICK_REF.md`
- **Security Config Cleanup**: `SECURITY_CONFIG_CLEANUP.md`

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized even with valid JWT

**Check**:
1. Is security-service running on port 8090?
2. Is token expired? (check `exp` claim)
3. Are required headers present? (`X-OAUTH-IDENTITY-DOMAIN-NAME`, `Authorization`)

### Issue: 403 Forbidden with valid JWT

**Check**:
1. Does JWT contain required roles? (decode token at jwt.io)
2. Check SecurityConfig rules match your endpoint path
3. Check `@PreAuthorize` annotation role spelling

### Issue: Public endpoint requires authentication

**Check**:
1. SecurityConfig has `.permitAll()` for the path
2. Path pattern matches correctly (e.g., `/actuator/**` vs `/actuator/*`)

---

## 📝 Testing

```bash
# Run all tests
mvnw.cmd test

# Run specific test class
mvnw.cmd test -Dtest=MonitoringServiceApplicationTests

# Run with coverage
mvnw.cmd clean test jacoco:report
```

---

## 📚 References

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [JWT.io](https://jwt.io) - Decode JWT tokens
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

---

## 🤝 Contributing

This is a template/example service. To create your own secure microservice:

1. Copy this project structure
2. Add your business logic in new controllers
3. Configure security rules in `SecurityConfig.java`
4. Add `@PreAuthorize` annotations as needed
5. Test with JWT tokens from security-service

---

## 📄 License

Part of the common-libraries ecosystem.

---

## 👥 Authors

- **Shailesh Halor** - Initial implementation

---

**Last Updated**: December 17, 2025


