package com.banking.system.service;

import com.banking.system.domain.Account;
import com.banking.system.domain.AccountStatus;
import com.banking.system.domain.AccountType;
import com.banking.system.domain.Customer;
import com.banking.system.repository.AccountRepository;
import com.banking.system.repository.CustomerRepository;
import com.banking.system.web.dto.AccountCreateRequest;
import com.banking.system.web.dto.AccountDto;
import com.banking.system.web.exception.AccountException;
import com.banking.system.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public AccountDto createAccount(AccountCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        AccountType type;
        try {
            type = AccountType.valueOf(request.getAccountType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AccountException("Invalid account type. Allowed: CHECKING, SAVINGS, BUSINESS");
        }

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(type);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(request.getInitialBalance());
        account.setStatus(AccountStatus.ACTIVE);

        Account saved = accountRepository.save(account);
        return mapToDto(saved);
    }

    @Override
    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        return mapToDto(account);
    }

    @Override
    public List<AccountDto> getAccountsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountDto updateAccountStatus(String accountNumber, String statusStr) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        AccountStatus status;
        try {
            status = AccountStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AccountException("Invalid status. Allowed: ACTIVE, SUSPENDED, CLOSED");
        }

        account.setStatus(status);
        Account saved = accountRepository.save(account);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        accountRepository.delete(account);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            long num = (long) (Math.random() * 9000000000L) + 1000000000L;
            accountNumber = String.valueOf(num);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountDto mapToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setCustomerId(account.getCustomer().getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType().name());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus().name());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}
