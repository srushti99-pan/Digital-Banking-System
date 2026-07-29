package com.banking.system.web.controller;

import com.banking.system.service.TransactionService;
import com.banking.system.web.dto.TransactionDto;
import com.banking.system.web.dto.TransactionRequest;
import com.banking.system.web.dto.TransactionSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<TransactionDto> deposit(@Valid @RequestBody TransactionRequest request) {
        TransactionDto dto = transactionService.deposit(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<TransactionDto> withdraw(@Valid @RequestBody TransactionRequest request) {
        TransactionDto dto = transactionService.withdraw(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<TransactionDto> transfer(@Valid @RequestBody TransactionRequest request) {
        TransactionDto dto = transactionService.transfer(request);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<Page<TransactionDto>> searchTransactions(
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        TransactionSearchCriteria criteria = new TransactionSearchCriteria();
        criteria.setTransactionType(transactionType);
        criteria.setStatus(status);
        criteria.setMinAmount(minAmount);
        criteria.setMaxAmount(maxAmount);
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        criteria.setAccountNumber(accountNumber);

        // Build Sort list
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null && sort.length > 0) {
            if (sort[0].contains(",")) {
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(Sort.Direction.fromString(_sort[1]), _sort[0]));
                }
            } else {
                orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        Page<TransactionDto> result = transactionService.searchTransactions(criteria, pageable);
        return ResponseEntity.ok(result);
    }
}
