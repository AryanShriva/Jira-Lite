package com.shriva.jira_lite_backend_java.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String password;
    private String role;
    private LocalDateTime createdAt;

}