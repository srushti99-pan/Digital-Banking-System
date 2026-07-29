package com.banking.system.service;

import com.banking.system.domain.Account;
import com.banking.system.domain.AccountStatus;
import com.banking.system.domain.AccountType;
import com.banking.system.domain.Customer;
import com.banking.system.repository.AccountRepository;
import com.banking.system.repository.CustomerRepository;
import com.banking.system.web.dto.AccountCreateRequest;
import com.banking.system.web.dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
    }

    @Test
    void testCreateAccount_Success() {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setCustomerId(1L);
        request.setAccountType("CHECKING");
        request.setInitialBalance(BigDecimal.valueOf(100.00));

        Account account = new Account();
        account.setId(10L);
        account.setCustomer(customer);
        account.setAccountType(AccountType.CHECKING);
        account.setAccountNumber("1234567890");
        account.setBalance(BigDecimal.valueOf(100.00));
        account.setStatus(AccountStatus.ACTIVE);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDto result = accountService.createAccount(request);

        assertNotNull(result);
        assertEquals("1234567890", result.getAccountNumber());
        assertEquals("CHECKING", result.getAccountType());
        assertEquals(BigDecimal.valueOf(100.00), result.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}
