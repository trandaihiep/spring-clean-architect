package com.example.demo.infrastructure.security;

import com.example.demo.infrastructure.config.AppSecurityProperties;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtRoleConverterTest {

    @Test
    void mapsRealmAndResourceRolesToAuthorities() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "123");
        claims.put("realm_access", Map.of("roles", List.of("user", "admin")));
        claims.put("resource_access", Map.of("demo-client", Map.of("roles", List.of("writer"))));

        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), Map.of("alg", "none"), claims);

        AppSecurityProperties props = new AppSecurityProperties();
        props.setClientId("demo-client");
        JwtRoleConverter converter = new JwtRoleConverter(props);

        var authorities = converter.convert(jwt);
        assertThat(authorities).extracting("authority").containsExactlyInAnyOrder("ROLE_user", "ROLE_admin", "ROLE_writer");
    }
}

