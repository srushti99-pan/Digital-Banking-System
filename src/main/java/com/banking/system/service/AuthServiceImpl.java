package com.banking.system.service;

import com.banking.system.domain.Customer;
import com.banking.system.domain.Role;
import com.banking.system.domain.RoleType;
import com.banking.system.domain.User;
import com.banking.system.repository.CustomerRepository;
import com.banking.system.repository.RoleRepository;
import com.banking.system.repository.UserRepository;
import com.banking.system.security.JwtTokenProvider;
import com.banking.system.security.UserPrincipal;
import com.banking.system.web.dto.JwtResponse;
import com.banking.system.web.dto.LoginRequest;
import com.banking.system.web.dto.RegisterRequest;
import com.banking.system.web.dto.UserDto;
import com.banking.system.web.exception.AccountException;
import com.banking.system.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userPrincipal.getUsername(), roles);
    }

    @Override
    @Transactional
    public UserDto registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new AccountException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AccountException("Email Address already in use!");
        }

        // Creating user's account
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName(RoleType.ROLE_CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role Customer not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        User savedUser = userRepository.save(user);

        // Creating customer profile
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setFirstName(registerRequest.getFirstName());
        customer.setLastName(registerRequest.getLastName());
        customer.setPhoneNumber(registerRequest.getPhoneNumber());
        customer.setAddress(registerRequest.getAddress());
        customerRepository.save(customer);

        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setUsername(savedUser.getUsername());
        userDto.setEmail(savedUser.getEmail());
        userDto.setEnabled(savedUser.isEnabled());
        userDto.setRoles(savedUser.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));

        return userDto;
    }
}
