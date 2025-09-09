package com.example.demo.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDto {
    private String id;

    @NotBlank
    private String username;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

