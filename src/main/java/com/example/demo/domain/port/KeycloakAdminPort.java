package com.example.demo.domain.port;

import java.util.List;
import java.util.Map;

public interface KeycloakAdminPort {
    List<Map<String, Object>> listUsers(int first, int max);
}

