package com.banking.system.web.controller;

import com.banking.system.service.AuthService;
import com.banking.system.web.dto.JwtResponse;
import com.banking.system.web.dto.LoginRequest;
import com.banking.system.web.dto.RegisterRequest;
import com.banking.system.web.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto userDto = authService.registerUser(registerRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }
}
