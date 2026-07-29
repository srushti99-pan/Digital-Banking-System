package com.banking.system.service;

import com.banking.system.domain.*;
import com.banking.system.repository.AccountRepository;
import com.banking.system.repository.TransactionRepository;
import com.banking.system.repository.TransactionSpecifications;
import com.banking.system.web.dto.TransactionDto;
import com.banking.system.web.dto.TransactionRequest;
import com.banking.system.web.dto.TransactionSearchCriteria;
import com.banking.system.web.exception.AccountException;
import com.banking.system.web.exception.InsufficientFundsException;
import com.banking.system.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public TransactionDto deposit(TransactionRequest request) {
        Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Target account not found"));

        validateAccountActive(targetAccount);

        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTargetAccount(targetAccount);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription());
        
        Transaction saved = transactionRepository.save(transaction);

        // Notify Customer
        notificationService.sendNotification(
            targetAccount.getCustomer().getUser(),
            "Deposit Successful",
            String.format("A deposit of $%s was successfully credited to account %s.", 
                request.getAmount(), targetAccount.getAccountNumber())
        );

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public TransactionDto withdraw(TransactionRequest request) {
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        validateAccountActive(sourceAccount);

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(sourceAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setSourceAccount(sourceAccount);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        // Notify Customer
        notificationService.sendNotification(
            sourceAccount.getCustomer().getUser(),
            "Withdrawal Successful",
            String.format("A withdrawal of $%s was debited from account %s.", 
                request.getAmount(), sourceAccount.getAccountNumber())
        );

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public TransactionDto transfer(TransactionRequest request) {
        if (request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            throw new AccountException("Cannot transfer to the same account");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        Account targetAccount = accountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Target account not found"));

        validateAccountActive(sourceAccount);
        validateAccountActive(targetAccount);

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in the source account");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        // Notify Sender
        notificationService.sendNotification(
            sourceAccount.getCustomer().getUser(),
            "Transfer Sent",
            String.format("You sent $%s from account %s to account %s.", 
                request.getAmount(), sourceAccount.getAccountNumber(), targetAccount.getAccountNumber())
        );

        // Notify Receiver
        notificationService.sendNotification(
            targetAccount.getCustomer().getUser(),
            "Transfer Received",
            String.format("You received $%s into account %s from account %s.", 
                request.getAmount(), targetAccount.getAccountNumber(), sourceAccount.getAccountNumber())
        );

        return mapToDto(saved);
    }

    @Override
    public Page<TransactionDto> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(
                TransactionSpecifications.withCriteria(criteria), pageable);
        return transactions.map(this::mapToDto);
    }

    private void validateAccountActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountException(String.format("Account %s is %s and cannot accept transactions.", 
                account.getAccountNumber(), account.getStatus().name()));
        }
    }

    private TransactionDto mapToDto(Transaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setId(tx.getId());
        dto.setSourceAccountNumber(tx.getSourceAccount() != null ? tx.getSourceAccount().getAccountNumber() : null);
        dto.setTargetAccountNumber(tx.getTargetAccount() != null ? tx.getTargetAccount().getAccountNumber() : null);
        dto.setTransactionType(tx.getTransactionType().name());
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setDescription(tx.getDescription());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
