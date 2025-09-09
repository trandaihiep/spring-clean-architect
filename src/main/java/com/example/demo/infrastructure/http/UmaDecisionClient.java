package com.example.demo.infrastructure.http;

import com.example.demo.domain.port.UmaDecisionPort;
import com.example.demo.infrastructure.config.AppSecurityProperties;
import com.example.demo.infrastructure.config.UmaProperties;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UmaDecisionClient implements UmaDecisionPort {

    private final WebClient webClient;
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;
    private final String audience;

    public UmaDecisionClient(WebClient.Builder builder,
                             @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
                             AppSecurityProperties securityProps,
                             UmaProperties umaProps) {
        this.webClient = builder.build();
        this.tokenEndpoint = issuerUri.replace("/realms/", "/realms/") + "/protocol/openid-connect/token";
        this.clientId = securityProps.getClientId();
        this.clientSecret = securityProps.getClientSecret();
        this.audience = umaProps.getAudience();
    }

    @Override
    public boolean isAllowed(String subjectToken, String resource, String scope) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "urn:ietf:params:oauth:grant-type:uma-ticket");
        form.add("audience", audience);
        form.add("permission", resource + "#" + scope);
        form.add("response_mode", "decision");

        return webClient.post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(h -> {
                h.setBasicAuth(clientId, clientSecret);
                h.setBearerAuth(subjectToken);
            })
            .bodyValue(form)
            .retrieve()
            .toBodilessEntity()
            .map(resp -> resp.getStatusCode().is2xxSuccessful())
            .onErrorReturn(false)
            .blockOptional()
            .orElse(false);
    }
}

