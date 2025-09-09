package com.example.demo.interfaces.web;

import com.example.demo.application.usecase.AdminUseCase;
import com.example.demo.domain.port.KeycloakAdminPort;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUseCase adminUseCase;
    private final KeycloakAdminPort adminPort;

    public AdminController(AdminUseCase adminUseCase, KeycloakAdminPort adminPort) {
        this.adminUseCase = adminUseCase;
        this.adminPort = adminPort;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(adminUseCase.ping());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> users(@RequestParam(defaultValue = "0") int first,
                                                          @RequestParam(defaultValue = "10") int max) {
        return ResponseEntity.ok(adminPort.listUsers(first, max));
    }
}
