package com.banking.system.service;

import com.banking.system.web.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    List<UserDto> getAllUsers();
    void deleteUser(Long id);
}
