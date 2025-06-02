package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.dto.AuthRequest;
import com.shriva.jira_lite_backend_java.dto.AuthResponse;
import com.shriva.jira_lite_backend_java.entity.Role;
import com.shriva.jira_lite_backend_java.entity.User;
import com.shriva.jira_lite_backend_java.repository.UserRepository;
import com.shriva.jira_lite_backend_java.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("Attempting login for email: {}", authRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            logger.info("User found: {}", user.getUsername());

            // Temporarily bypass JWT generation
            String jwt = "temp-jwt-bypass-" + user.getEmail();
            logger.info("Bypassing JWT generation for user: {}", user.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getRole()));
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, null));
        } catch (Exception e) {
            logger.error("Login error for email: {}: {}", authRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(null, null, null));
        }
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationRequest registrationRequest) {
        User user = new User();
        user.setEmail(registrationRequest.getEmail());
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        // Validate and set role
        String requestedRole = registrationRequest.getRole() != null ? registrationRequest.getRole().toUpperCase() : "DEVELOPER";
        Role roleEnum = Role.fromString(requestedRole);
        if (roleEnum == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN role cannot be assigned during registration");
        }
        user.setRole(requestedRole);

        return ResponseEntity.ok(userRepository.save(user));
    }
}

class UserRegistrationRequest {
    private String email;
    private String username;
    private String password;
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}