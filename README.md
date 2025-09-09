# Spring Clean Architecture (Keycloak Resource Server + UMA)

Single Spring Boot 3.5 (Java 21) backend implementing:

- OAuth2 Resource Server (JWT via issuer-uri)
- Keycloak roles mapping (realm_access + resource_access → ROLE_*)
- Method security with @PreAuthorize
- UMA decision client via WebClient
- Admin gateway calling Keycloak Admin REST API
- Hexagonal package layout, MapStruct, Validation, Actuator, OpenAPI

## Run

Prerequisites:

- Java 21, Maven 3.9+
- Keycloak realm configured; obtain issuer URI and a client (confidential) for admin/UMA

Env vars:

```
export KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/demo
export KEYCLOAK_CLIENT_ID=demo-client
export KEYCLOAK_CLIENT_SECRET=change-me
```

Start app:

```
mvn spring-boot:run
```

Endpoints:

- GET `/api/public` → 200 (no auth)
- GET `/api/me` → 200 with JWT; returns claims
- GET `/api/admin/ping` → ROLE_admin only
- Actuator: `/actuator/health`, `/actuator/info` (public)
- OpenAPI UI: `/swagger-ui/index.html`

## UMA mapping

Basic decision call uses:

```
POST {issuer}/protocol/openid-connect/token
grant_type=urn:ietf:params:oauth:grant-type:uma-ticket
audience=${KEYCLOAK_CLIENT_ID}
permission=<resource>#<scope>
response_mode=decision
```

Client credentials (Basic) come from `${KEYCLOAK_CLIENT_ID}/${KEYCLOAK_CLIENT_SECRET}`.

## Notes

- Spring Security extracts roles from `realm_access.roles` and `resource_access[clientId].roles` and prefixes with `ROLE_`.
- Prefer putting domain rules behind services/use cases, secured with `@PreAuthorize` near the rule.

## Tests

- Unit: role converter, UMA client (WireMock)
- Web: `@WebMvcTest` for public/admin endpoints
- Architecture: ArchUnit rule for domain isolation

