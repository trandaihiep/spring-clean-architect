package com.example.demo.infrastructure.security;

import com.example.demo.infrastructure.config.AppSecurityProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Converts Keycloak realm_access and resource_access roles to ROLE_* authorities.
 */
public class JwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final String clientId;

    public JwtRoleConverter(AppSecurityProperties props) {
        this.clientId = Objects.requireNonNullElse(props.getClientId(), "");
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        // realm_access.roles
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object realmRoles = realmAccess.get("roles");
            if (realmRoles instanceof Collection<?> col) {
                col.forEach(r -> roles.add(String.valueOf(r)));
            }
        }

        // resource_access[clientId].roles
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && clientId != null && !clientId.isBlank()) {
            Object client = resourceAccess.get(clientId);
            if (client instanceof Map<?, ?> m) {
                Object clientRoles = m.get("roles");
                if (clientRoles instanceof Collection<?> col) {
                    col.forEach(r -> roles.add(String.valueOf(r)));
                }
            }
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .filter(s -> !s.isBlank())
                .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }
}

