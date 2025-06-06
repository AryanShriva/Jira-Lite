package com.shriva.jira_lite_backend_java.service;

import com.shriva.jira_lite_backend_java.dto.UserDto;
import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    UserDto updateUserRole(Long userId, String newRole);

    List<UserDto> getAssignableUsers();
}