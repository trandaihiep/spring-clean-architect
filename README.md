# Spring Boot Clean Architecture with Keycloak Integration

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Keycloak](https://img.shields.io/badge/Keycloak-OIDC/OAuth2-orange.svg)](https://www.keycloak.org/)

Một dự án backend Spring Boot 3.x với kiến trúc sạch (Hexagonal Architecture), tích hợp Keycloak cho authentication và authorization, hỗ trợ UMA (User-Managed Access) cho fine-grained authorization.

## 🎯 Mục tiêu

- **Authentication**: Xác thực người dùng bằng Access Token Keycloak (OIDC)
- **Authorization**: RBAC qua roles từ `realm_access` và `resource_access`
- **Fine-grained Access Control**: Tích hợp UMA (Authorization Services) để kiểm soát resource/scope
- **Admin Gateway**: Cung cấp API để quản trị user qua Keycloak Admin REST API
- **Clean Architecture**: Áp dụng Hexagonal Architecture với Domain-Driven Design
- **Security**: Method-level security với `@PreAuthorize`
- **Testing**: Comprehensive test coverage với unit, integration và security tests

## 🏗️ Kiến trúc

Dự án áp dụng **Hexagonal Architecture (Ports & Adapters)** với cấu trúc package như sau:

```
com.example.demo
├─ boot/                 # DemoApplication, Bootstrap config
├─ common/               # Error handling, Result, utils, exception mappers
├─ domain/               # Pure domain models + Ports (interfaces) + business rules
│  ├─ model/            # Domain entities và value objects
│  ├─ port/             # Repository/Service interfaces (e.g., OrderRepository, UmaDecisionPort)
│  └─ service/          # Domain services và business rules
├─ application/          # Use cases, orchestration, transaction boundaries
│  └─ usecase/          # Application services
├─ infrastructure/       # External concerns adapters
│  ├─ persistence/      # JPA entities + Spring Data implementations
│  ├─ security/         # Resource Server, JWT converter, Method Security
│  ├─ http/             # WebClient for Keycloak Admin/UMA API calls
│  └─ config/           # Configuration classes, cache, mappers, flyway
└─ interfaces/           # Web adapters (controllers), DTOs, MapStruct mappers
```

## 🛠️ Công nghệ sử dụng

### Core Dependencies
- **Spring Boot 3.x** - Framework chính
- **Java 21+** - LTS version
- **Spring Security** - Authentication & Authorization
- **Spring OAuth2 Resource Server** - JWT validation
- **Spring WebFlux** - Reactive WebClient cho HTTP calls
- **Spring Validation** - Bean validation cho DTOs
- **Spring Actuator** - Health checks và metrics
- **SpringDoc OpenAPI** - API documentation
- **MapStruct** - DTO ↔ Entity mapping

### Optional Dependencies
- **Spring Cache + Caffeine** - Caching layer
- **Flyway** - Database migration
- **Spring Data JPA + PostgreSQL** - Persistence layer
- **H2 Database** - In-memory DB cho testing
- **WireMock** - HTTP mocking cho tests
- **Resilience4j** - Circuit breaker và retry patterns
- **ArchUnit** - Architecture testing

## 🚀 Cài đặt và Chạy

### Prerequisites
- Java 21+
- Maven 3.6+
- Keycloak Server (có thể chạy local hoặc remote)

### Environment Variables
Tạo file `.env` hoặc cấu hình environment variables:

```bash
# Keycloak Configuration
KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/demo
KEYCLOAK_ADMIN_URL=http://localhost:8080/admin/realms/demo
KEYCLOAK_CLIENT_ID=demo-client
KEYCLOAK_CLIENT_SECRET=your-client-secret

# Database (optional)
DB_URL=jdbc:postgresql://localhost:5432/demo
DB_USERNAME=demo
DB_PASSWORD=password

# Application
SERVER_PORT=8080
```

### Chạy ứng dụng

```bash
# Clone repository
git clone https://github.com/trandaihiep/spring-clean-architect.git
cd spring-clean-architect

# Chạy với Maven
mvn spring-boot:run

# Hoặc với Maven wrapper
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy tại `http://localhost:8080`

## ⚙️ Cấu hình

### Security Configuration
- **JWT Issuer URI**: Sử dụng `issuer-uri` để tự động khám phá JWK set
- **Role Mapping**: Converter tự động map roles từ `realm_access` và `resource_access` thành `ROLE_*`
- **Method Security**: Bật `@EnableMethodSecurity` cho `@PreAuthorize`

### UMA Integration
- **Port/Adapter Pattern**: Tách biệt domain logic với UMA implementation
- **WebClient**: Gọi Keycloak UMA endpoint để kiểm tra permissions
- **Resource-Scope Mapping**: Map API routes thành UMA resources và scopes

## 📋 API Endpoints

### Public Endpoints
- `GET /api/public` - Không yêu cầu authentication
- `GET /actuator/health` - Health check

### Protected Endpoints
- `GET /api/me` - Thông tin user hiện tại (yêu cầu authentication)
- `GET /api/admin/**` - Admin endpoints (yêu cầu `ROLE_admin`)

### Keycloak Admin Gateway
- `GET /api/admin/users` - List users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user

## 🧪 Testing

### Unit Tests
- **JwtGrantedAuthoritiesConverter**: Test mapping roles → authorities
- **UmaDecisionService**: Test UMA decisions với WireMock
- **Domain Services**: Test business logic không phụ thuộc infrastructure

### Integration Tests
- **Web Layer**: `@WebMvcTest` với MockMvc (401/403/200 responses)
- **Security**: Test `@PreAuthorize` với mock JWT
- **Architecture**: ArchUnit rules để đảm bảo clean architecture

### Test Coverage
```bash
# Chạy tất cả tests
mvn test

# Với coverage report
mvn test jacoco:report
```

## 📚 Documentation

### API Documentation
Truy cập Swagger UI tại: `http://localhost:8080/swagger-ui.html`

### Architecture Decision Records (ADR)

#### ADR 001: Không sử dụng keycloak-spring-boot-starter
**Context**: Keycloak cung cấp adapter cho Spring Boot nhưng đã deprecated.

**Decision**: Sử dụng Spring Security OAuth2 Resource Server thay vì deprecated adapters.

**Rationale**:
- Spring Security OAuth2 Resource Server là standard approach
- Tương thích tốt với Spring Boot 3.x
- Không phụ thuộc vào Keycloak-specific libraries
- Dễ maintain và upgrade

#### ADR 002: Resource Server OIDC + WebClient cho UMA
**Context**: Cần tích hợp UMA cho fine-grained authorization.

**Decision**: Sử dụng WebClient để gọi UMA endpoints thay vì SDK.

**Rationale**:
- WebClient là reactive và non-blocking
- Không cần thêm dependencies
- Linh hoạt trong việc customize requests
- Dễ test với WireMock
- Consistent với Spring ecosystem

## 🔧 Development

### Code Quality
- **Checkstyle/Spotless**: Code formatting (optional)
- **ArchUnit**: Architecture rules validation
- **Bean Validation**: DTO validation
- **Exception Handling**: Centralized với `@ControllerAdvice`

### Best Practices
- **SOLID Principles**: Clean code conventions
- **Constructor Injection**: Thay cho field injection
- **Configuration Properties**: Externalized configuration
- **Logging**: SLF4J/Logback thay cho System.out
- **Caching**: Spring Cache khi cần performance

## 🤝 Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

Nếu có câu hỏi hoặc cần hỗ trợ, vui lòng tạo issue trên GitHub hoặc liên hệ maintainer.

---

**Lưu ý**: Dự án này không bao gồm setup Keycloak server. Vui lòng tham khảo [Keycloak Documentation](https://www.keycloak.org/documentation) để setup và cấu hình realm phù hợp.

