package com.shriva.jira_lite_backend_java.service.impl;

import com.shriva.jira_lite_backend_java.dto.UserDto;
import com.shriva.jira_lite_backend_java.entity.User;
import com.shriva.jira_lite_backend_java.repository.UserRepository;
import com.shriva.jira_lite_backend_java.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Check if user already exists by email
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // Assumes password is already hashed
        user.setRole(userDto.getRole());
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        return convertToDto(userOptional.get());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        User user = userOptional.get();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        // Password update might require additional logic (e.g., hashing)
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(userDto.getPassword());
        }
        user.setRole(userDto.getRole());

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUserRole(Long userId, String newRole) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public List<UserDto> getAssignableUsers() {
        List<String> assignableRoles = Arrays.asList("DEVELOPER", "TESTER");
        List<User> users = userRepository.findByRoleIn(assignableRoles);
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}