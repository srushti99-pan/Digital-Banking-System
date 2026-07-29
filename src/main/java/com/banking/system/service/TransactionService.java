package com.banking.system.service;

import com.banking.system.web.dto.TransactionDto;
import com.banking.system.web.dto.TransactionRequest;
import com.banking.system.web.dto.TransactionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionDto deposit(TransactionRequest request);
    TransactionDto withdraw(TransactionRequest request);
    TransactionDto transfer(TransactionRequest request);
    Page<TransactionDto> searchTransactions(TransactionSearchCriteria criteria, Pageable pageable);
}
