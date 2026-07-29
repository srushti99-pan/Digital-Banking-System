package com.banking.system.service;

import com.banking.system.web.dto.AccountCreateRequest;
import com.banking.system.web.dto.AccountDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountCreateRequest request);
    AccountDto getAccountByNumber(String accountNumber);
    List<AccountDto> getAccountsByCustomerId(Long customerId);
    AccountDto updateAccountStatus(String accountNumber, String status);
    void deleteAccount(String accountNumber);
}
