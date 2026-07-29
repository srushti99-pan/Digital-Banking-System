package com.banking.system.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionSearchCriteria {
    private String transactionType;
    private String status;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String accountNumber; // matches source or target
}
