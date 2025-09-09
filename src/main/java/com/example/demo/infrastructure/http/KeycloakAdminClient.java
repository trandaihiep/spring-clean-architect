package com.example.demo.infrastructure.http;

import com.example.demo.domain.port.KeycloakAdminPort;
import com.example.demo.infrastructure.config.AppSecurityProperties;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class KeycloakAdminClient implements KeycloakAdminPort {

    private final WebClient webClient;
    private final String issuerUri;
    private final String adminBase;
    private final String clientId;
    private final String clientSecret;

    public KeycloakAdminClient(WebClient.Builder builder,
                               @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
                               AppSecurityProperties props) {
        this.webClient = builder.build();
        this.issuerUri = issuerUri;
        String realm = issuerUri.substring(issuerUri.lastIndexOf('/') + 1);
        this.adminBase = issuerUri.replace("/realms/" + realm, "/admin/realms/" + realm);
        this.clientId = props.getClientId();
        this.clientSecret = props.getClientSecret();
    }

    private String fetchAdminToken() {
        String tokenEndpoint = issuerUri + "/protocol/openid-connect/token";
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<?, ?> resp = webClient.post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(form)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        return resp == null ? null : String.valueOf(resp.get("access_token"));
    }

    @Override
    public List<Map<String, Object>> listUsers(int first, int max) {
        String token = fetchAdminToken();
        if (token == null) return List.of();

        return webClient.get()
            .uri(adminBase + "/users?first=" + first + "&max=" + max)
            .headers(h -> h.setBearerAuth(token))
            .retrieve()
            .bodyToFlux(Map.class)
            .cast((Class<Map<String, Object>>)(Class<?>)Map.class)
            .collectList()
            .onErrorReturn(List.of())
            .blockOptional()
            .orElse(List.of());
    }
}

