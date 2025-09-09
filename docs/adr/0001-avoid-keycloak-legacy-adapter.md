# ADR-0001: Avoid keycloak-spring-boot-starter (legacy adapters)

Status: accepted

Context:

The legacy Keycloak Spring Security adapters are deprecated and not aligned with Spring Security 6 / Boot 3.

Decision:

Use Spring Security OAuth2 Resource Server for JWT validation (via issuer-uri) and integrate Keycloak features via standards (OIDC/OAuth2) and plain HTTP clients where needed.

Consequences:

- Works with stock Spring Security model (Authentication, Jwt)
- Simpler upgrades with Spring Boot
- No adapter lock-in; we own UMA/Admin flows via WebClient

