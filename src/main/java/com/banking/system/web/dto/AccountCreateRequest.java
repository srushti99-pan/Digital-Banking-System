package com.banking.system.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountCreateRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Account type is required (CHECKING, SAVINGS, BUSINESS)")
    private String accountType;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;
}
