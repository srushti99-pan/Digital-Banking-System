package com.banking.system.service;

import com.banking.system.domain.*;
import com.banking.system.repository.AccountRepository;
import com.banking.system.repository.TransactionRepository;
import com.banking.system.web.dto.TransactionDto;
import com.banking.system.web.dto.TransactionRequest;
import com.banking.system.web.exception.InsufficientFundsException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account sourceAccount;
    private Account targetAccount;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("john@example.com");

        Customer customer = new Customer();
        customer.setUser(user);

        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setAccountNumber("ACC111");
        sourceAccount.setBalance(BigDecimal.valueOf(500.00));
        sourceAccount.setStatus(AccountStatus.ACTIVE);
        sourceAccount.setCustomer(customer);

        targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setAccountNumber("ACC222");
        targetAccount.setBalance(BigDecimal.valueOf(200.00));
        targetAccount.setStatus(AccountStatus.ACTIVE);
        targetAccount.setCustomer(customer);
    }

    @Test
    void testTransfer_Success() {
        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountNumber("ACC111");
        request.setTargetAccountNumber("ACC222");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setDescription("Dinner Split");

        Transaction tx = new Transaction();
        tx.setId(5L);
        tx.setSourceAccount(sourceAccount);
        tx.setTargetAccount(targetAccount);
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setAmount(BigDecimal.valueOf(100.00));
        tx.setStatus(TransactionStatus.COMPLETED);

        when(accountRepository.findByAccountNumber("ACC111")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC222")).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(tx);

        TransactionDto result = transactionService.transfer(request);

        assertNotNull(result);
        assertEquals("TRANSFER", result.getTransactionType());
        assertEquals(BigDecimal.valueOf(100.00), result.getAmount());
        assertEquals(BigDecimal.valueOf(400.00), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(300.00), targetAccount.getBalance());

        verify(accountRepository, times(1)).save(sourceAccount);
        verify(accountRepository, times(1)).save(targetAccount);
        verify(notificationService, times(2)).sendNotification(any(User.class), anyString(), anyString());
    }

    @Test
    void testTransfer_InsufficientFunds() {
        TransactionRequest request = new TransactionRequest();
        request.setSourceAccountNumber("ACC111");
        request.setTargetAccountNumber("ACC222");
        request.setAmount(BigDecimal.valueOf(600.00)); // balance is 500

        when(accountRepository.findByAccountNumber("ACC111")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumber("ACC222")).thenReturn(Optional.of(targetAccount));

        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(request));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
