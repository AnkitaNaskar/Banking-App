package net.javaguides.springboot.banking.service;

import java.util.List;

import net.javaguides.springboot.banking.dto.AccountDto;
import net.javaguides.springboot.banking.dto.TransactionDto;
import net.javaguides.springboot.banking.dto.TransferFundDto;

public interface AccountService {
	AccountDto createAccount(AccountDto account);
	AccountDto getAccountById(Long id);
    AccountDto deposit(Long id, double amount);
    AccountDto withdraw(Long id, double amount);
    List<AccountDto> getAllAccounts();
    void deleteAccount(Long id);
    void transferFunds(TransferFundDto transferFundDto);
    List<TransactionDto> getAccountTransactions(Long accountId);
}

