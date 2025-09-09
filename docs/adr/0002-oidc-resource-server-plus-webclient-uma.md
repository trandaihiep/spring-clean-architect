# ADR-0002: Resource Server (OIDC) + WebClient for UMA

Status: accepted

Context:

We need fine-grained authorization (resource#scope). Keycloak UMA Authorization Services expose OAuth endpoints to request a decision.

Decision:

Use Spring Security Resource Server (OIDC JWT) for authentication and role mapping. For UMA decisions, use a lightweight WebClient adapter calling the token endpoint with `grant_type=uma-ticket` and `response_mode=decision`.

Consequences:

- Keeps authentication and coarse RBAC in Security layer
- Fine-grained checks can be invoked from application layer via an UmaDecisionPort
- No extra SDK dependency; easy to mock in tests with WireMock

