package com.example.demo.interfaces.web;

import com.example.demo.application.usecase.AdminUseCase;
import com.example.demo.infrastructure.config.AppSecurityProperties;
import com.example.demo.domain.port.KeycloakAdminPort;
import com.example.demo.infrastructure.security.MethodSecurityConfig;
import com.example.demo.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import({SecurityConfig.class, MethodSecurityConfig.class, AppSecurityProperties.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUseCase adminUseCase;

    @MockBean
    private KeycloakAdminPort keycloakAdminPort;

    @Test
    void adminRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/ping"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRequiresRole() throws Exception {
        mockMvc.perform(get("/api/admin/ping")
                .with(SecurityMockMvcRequestPostProcessors.jwt()))
            .andExpect(status().isForbidden());
    }

    @Test
    void adminWithRoleAdminGetsOk() throws Exception {
        when(adminUseCase.ping()).thenReturn("admin-ok");
        mockMvc.perform(get("/api/admin/ping")
                .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(() -> "ROLE_admin")))
            .andExpect(status().isOk());
    }
}
