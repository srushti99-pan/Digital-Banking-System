package com.banking.system.web.controller;

import com.banking.system.service.AccountService;
import com.banking.system.web.dto.AccountCreateRequest;
import com.banking.system.web.dto.AccountDto;
import com.banking.system.web.exception.AccountException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        AccountDto dto = accountService.createAccount(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<AccountDto> getAccountByNumber(@PathVariable String accountNumber) {
        AccountDto dto = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<List<AccountDto>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<AccountDto> dtos = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{accountNumber}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<AccountDto> updateAccountStatus(@PathVariable String accountNumber, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null) {
            throw new AccountException("Status is required");
        }
        AccountDto dto = accountService.updateAccountStatus(accountNumber, status);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
