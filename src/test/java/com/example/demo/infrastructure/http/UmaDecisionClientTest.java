package com.example.demo.infrastructure.http;

import com.example.demo.infrastructure.config.AppSecurityProperties;
import com.example.demo.infrastructure.config.UmaProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

public class UmaDecisionClientTest {

    private WireMockServer wm;

    @BeforeEach
    void setup() {
        wm = new WireMockServer(0);
        wm.start();
        WireMock.configureFor("localhost", wm.port());
    }

    @AfterEach
    void tearDown() {
        wm.stop();
    }

    @Test
    void returnsTrueOn2xx() {
        String issuer = "http://localhost:" + wm.port() + "/realms/demo";
        wm.stubFor(WireMock.post(WireMock.urlEqualTo("/realms/demo/protocol/openid-connect/token"))
                .willReturn(WireMock.aResponse().withStatus(200)));

        AppSecurityProperties sec = new AppSecurityProperties();
        sec.setClientId("demo-client");
        sec.setClientSecret("secret");
        UmaProperties uma = new UmaProperties();
        uma.setAudience("demo-client");

        UmaDecisionClient client = new UmaDecisionClient(WebClient.builder(), issuer, sec, uma);
        boolean allowed = client.isAllowed("subjectToken", "res1", "read");
        assertThat(allowed).isTrue();
    }

    @Test
    void returnsFalseOnError() {
        String issuer = "http://localhost:" + wm.port() + "/realms/demo";
        wm.stubFor(WireMock.post(WireMock.urlEqualTo("/realms/demo/protocol/openid-connect/token"))
                .willReturn(WireMock.aResponse().withStatus(403)));

        AppSecurityProperties sec = new AppSecurityProperties();
        sec.setClientId("demo-client");
        sec.setClientSecret("secret");
        UmaProperties uma = new UmaProperties();
        uma.setAudience("demo-client");

        UmaDecisionClient client = new UmaDecisionClient(WebClient.builder(), issuer, sec, uma);
        boolean allowed = client.isAllowed("subjectToken", "res1", "read");
        assertThat(allowed).isFalse();
    }
}

