package com.shriva.jira_lite_backend_java.entity;

public enum Role {
    DEVELOPER,
    TESTER,
    MANAGER,
    DESIGNER,
    ANALYST,
    ADMIN;

    public static Role fromString(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}