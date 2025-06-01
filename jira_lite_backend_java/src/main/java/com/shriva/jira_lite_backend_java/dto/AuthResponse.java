package com.shriva.jira_lite_backend_java.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    // Getters and setters
    private String token;
    private Long userId;
    private String role;

    public AuthResponse(String token, Long userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

}