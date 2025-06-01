package com.shriva.jira_lite_backend_java.controller;

import com.shriva.jira_lite_backend_java.dto.AuthRequest;
import com.shriva.jira_lite_backend_java.dto.AuthResponse;
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
import org.springframework.web.bind.annotation.*;

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

            String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
            logger.info("JWT generated for user: {}", user.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getRole().replace("ROLE_", "")));
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, null));
        } catch (Exception e) {
            logger.error("Login error for email: {}: {}", authRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(null, null, null));
        }
    }
}