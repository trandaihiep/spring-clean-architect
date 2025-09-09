package com.example.demo.application.usecase;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminUseCase {

    @PreAuthorize("hasRole('admin')")
    public String ping() {
        return "admin-ok";
    }
}

