package com.banking.system.service;

import com.banking.system.web.dto.JwtResponse;
import com.banking.system.web.dto.LoginRequest;
import com.banking.system.web.dto.RegisterRequest;
import com.banking.system.web.dto.UserDto;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    UserDto registerUser(RegisterRequest registerRequest);
}
