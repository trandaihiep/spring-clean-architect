Bạn là kiến trúc sư backend và generator code. Hãy tạo MỘT project backend duy nhất bằng Spring Boot 3.x (Java 21+). KHÔNG tạo frontend.

Mục tiêu

- Xác thực người dùng bằng Access Token Keycloak (OIDC).
- Dùng Spring Security Resource Server (OAuth2) để verify JWT qua issuer-uri/jwk-set-uri.
- RBAC qua roles từ realm_access và resource_access.
- Tích hợp UMA (Authorization Services) để xin decision resource/scope (fine-grained).
- Cung cấp Admin Gateway gọi Keycloak Admin REST API để quản trị user.
- Cấu trúc sạch, method security, test.

Phụ thuộc Maven (hoặc Gradle tương đương)

BẮT BUỘC (cốt lõi)
- spring-boot-starter-web                    # REST (Servlet)
- spring-boot-starter-security               # Spring Security
- spring-boot-starter-oauth2-resource-server # Resource Server (JWT/OIDC)
- spring-boot-starter-webflux                # WebClient (gọi Keycloak/UMA qua HTTP)
- spring-boot-starter-validation             # Jakarta Bean Validation (DTO)
- spring-boot-starter-actuator               # Health/metrics
- org.springdoc:springdoc-openapi-starter-webmvc-ui  # Swagger UI (Dev UX)
- org.mapstruct:mapstruct + mapstruct-processor      # Mapping DTO ↔ Entity
- spring-boot-starter-test, spring-security-test     # Testing cơ bản + security

TUỲ CHỌN (bật theo nhu cầu)
- spring-boot-starter-cache + com.github.ben-manes.caffeine:caffeine  # Cache
- org.flywaydb:flyway-core                                             # Migration DB
- spring-boot-starter-data-jpa + org.postgresql:postgresql             # Persistence JPA/PG
- com.h2database:h2 (test)                                             # DB in-memory cho test
- org.wiremock:wiremock-standalone (test)                              # Mock HTTP / UMA
- io.github.resilience4j:resilience4j-spring-boot3                     # Retry/Circuit breaker
- com.tngtech.archunit:archunit-junit5 (test)                          # Kiểm tra rule kiến trúc

Tests

- Unit test:
  - JwtGrantedAuthoritiesConverter (mapping realm_access/resource_access → ROLE_*).
  - UmaDecisionService (mock WebClient bằng WireMock).
  - Use case/service trong application layer (không phụ thuộc web).
- Web adapter:
  - @WebMvcTest cho Controller với MockMvc (401/403/200).
- Security:
  - spring-security-test: dựng SecurityContext với Jwt giả để test @PreAuthorize.
- Kiến trúc:
  - ArchUnit: domain không import spring.*, interfaces không phụ thuộc infrastructure.
- Integration (nhẹ):
  - SpringBootTest với cấu hình test profile + H2 (nếu bật JPA).

Quality & tiện ích

- Checkstyle/Spotless (tuỳ chọn).
- OpenAPI (springdoc) cho endpoints /admin và /orders (tuỳ chọn).
- README.md:
  - Cách đặt ENV & cấu hình issuer-uri
  - Cách chạy: mvn spring-boot:run
  - Cách map route -> resource#scope cho UMA.


Best Practices & Kiến trúc sạch

- Áp dụng mô hình **Hexagonal Architecture (Ports & Adapters)** để tách biệt domain logic với infrastructure.
- Domain-Driven Design (DDD) cho các module quan trọng: chia package thành domain, application, infrastructure.
- Sử dụng **DTO/DAO** rõ ràng; tránh lẫn lộn entity với payload API.
- Áp dụng **SOLID principles** và Clean Code conventions (tên biến/hàm rõ ràng, class ngắn gọn).
- Cấu hình logging tập trung với SLF4J/Logback; không dùng `System.out`.
- Tách biệt config theo profiles (`application-dev.yml`, `application-prod.yml`).
- Áp dụng `@ConfigurationProperties` thay vì hardcode config.
- Dùng **constructor injection** thay cho `@Autowired` field injection.
- Bổ sung layer Service/Repository rõ ràng; Controller chỉ xử lý HTTP.
- Thực hiện exception handling tập trung với `@ControllerAdvice`.
- Tích hợp `MapStruct` cho mapping DTO ↔ Entity.
- Áp dụng **caching (Spring Cache)** khi cần để tăng hiệu năng.
- Bổ sung `@PreAuthorize` cho method-level security thay vì logic phân quyền trong controller.
- Tài liệu kiến trúc bằng ADR (Architecture Decision Records) để quản lý các quyết định.

Kiến trúc module & packages (Hexagonal)

- packages gợi ý:
  com.example.demo
  ├─ boot/                 # DemoApplication, Bootstrap config
  ├─ common/               # error, Result, utils, exception mappers
  ├─ domain/               # Model thuần + Port (Repository/Service interfaces) + policy
  │  ├─ model/
  │  ├─ port/              # e.g., OrderRepository, UmaDecisionPort
  │  └─ service/           # business rules/use cases (hoặc đặt tại application nếu muốn)
  ├─ application/          # orchestration, transaction boundary, use cases
  │  └─ usecase/
  ├─ infrastructure/
  │  ├─ persistence/       # JPA entities + Spring Data impl (adapter Repo)
  │  ├─ security/          # Resource Server, JwtAuthConverter, MethodSecurity
  │  ├─ http/              # WebClient/Keycloak Admin/UMA adapter
  │  └─ config/            # @ConfigurationProperties, cache, mapper, flyway
  └─ interfaces/           # Web adapter (controllers), DTO, MapStruct mappers

Security Config (bắt buộc)

- Dùng issuer-uri để tự khám phá JWK:
  spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_ISSUER_URI}
  # ví dụ: http://localhost:8080/realms/demo
- Viết JwtGrantedAuthoritiesConverter:
  - Lấy roles từ realm_access.roles và resource_access["<clientId>"].roles
  - Chuẩn hoá prefix ROLE_ trước khi map vào GrantedAuthority
- Bật @EnableMethodSecurity và dùng @PreAuthorize ở application layer (gần với rule domain).

UMA Notes (Authorization Services)

- Tách Port/Adapter:
  - Port: UmaDecisionPort { boolean isAllowed(subject, resource, scope); }
  - Adapter: UmaDecisionClient (WebClient) gọi:
    POST {issuer}/protocol/openid-connect/token
    grant_type=urn:ietf:params:oauth:grant-type:uma-ticket
    audience=<clientId>&permission=<resource>#<scope>
  - Trả kết quả decision → application layer.
- Test bằng WireMock: mock 200 allow / 403 deny.

Yêu cầu output

- Sinh full source code theo cấu trúc Hexagonal ở trên; kèm Maven/Gradle (Java 21 LTS).
- Có ví dụ controller/service/test chạy được:
  - /api/public, /api/admin/** (ROLE_admin), /api/me
- Thêm converter roles → authorities, bật method security.
- Có tối thiểu:
  - MapStruct mapper, Validation cho DTO, ControllerAdvice (error JSON thống nhất).
  - Actuator (health/info/metrics) và bảo vệ endpoint nhạy cảm.
  - OpenAPI (springdoc) hiển thị tài liệu endpoints.
- Không dựng Keycloak trong docker; chỉ mô tả .env và endpoint để kết nối.
- Thêm 01–02 ADR mẫu (Markdown) giải thích: 
  - Vì sao không dùng keycloak-spring-boot-starter (deprecated adapters).
  - Lý do chọn Resource Server OIDC + WebClient cho UMA thay vì SDK.
