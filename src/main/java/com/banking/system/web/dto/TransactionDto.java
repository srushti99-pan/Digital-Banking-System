package com.banking.system.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDto {
    private Long id;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private String transactionType;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime createdAt;
}
