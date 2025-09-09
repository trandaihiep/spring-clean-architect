package com.example.demo.interfaces.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.infrastructure.security.SecurityConfig;
import com.example.demo.infrastructure.security.MethodSecurityConfig;
import com.example.demo.infrastructure.config.AppSecurityProperties;

@WebMvcTest(controllers = PublicController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class, AppSecurityProperties.class})
class PublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpointIsAccessible() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/public"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
    }
}

