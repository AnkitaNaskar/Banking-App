package net.javaguides.springboot.banking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;


import org.springframework.stereotype.Service;
import net.javaguides.springboot.banking.dto.AccountDto;
import net.javaguides.springboot.banking.dto.TransactionDto;
import net.javaguides.springboot.banking.entity.Account;
import net.javaguides.springboot.banking.entity.Transaction;
import net.javaguides.springboot.banking.mapper.AccountMapper;
import net.javaguides.springboot.banking.repository.AccountRepository;
import net.javaguides.springboot.banking.repository.TransactionRepository;
import net.javaguides.springboot.banking.service.AccountService;
import net.javaguides.springboot.banking.dto.TransferFundDto;

import net.javaguides.springboot.banking.exception.AccountException;




@Service
public class AccountServiceImpl implements AccountService {

   
	private AccountRepository accountRepository;
    
    private TransactionRepository transactionRepository;

	private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
	private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
	private static final String TRANSACTION_TYPE_TRANSFER = "TRANSFER";

	
	public AccountServiceImpl(AccountRepository accountRepository, 
    		TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;

    }




	@Override
	public AccountDto createAccount(AccountDto accountDto) {
		Account account=AccountMapper.mapToAccount(accountDto);
		Account savedAccount =accountRepository.save(account);
		return AccountMapper.mapToAccountDto(savedAccount);
	}




	@Override
    public AccountDto getAccountById(Long id) {

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));
        return AccountMapper.mapToAccountDto(account);
    }
	
	 @Override
	    public AccountDto deposit(Long id, double amount) {

	        Account account = accountRepository
	                .findById(id)
	                .orElseThrow(() -> new AccountException("Account does not exists"));

	        double total = account.getBalance() + amount;
	        account.setBalance(total);
	        Account savedAccount = accountRepository.save(account);
	        
	        Transaction transaction = new Transaction();
	        transaction.setAccountId(id);
	        transaction.setAmount(amount);
	        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
	        transaction.setTimestamp(LocalDateTime.now());
	        transactionRepository.save(transaction);


	        return AccountMapper.mapToAccountDto(savedAccount);
	    }
	 
	    @Override
	    public AccountDto withdraw(Long id, double amount) {

	        Account account = accountRepository
	                .findById(id)
	                .orElseThrow(() -> new AccountException("Account does not exists"));

	        if(account.getBalance() < amount){
	            throw new RuntimeException("Insufficient amount");
	        }

	        double total = account.getBalance() - amount;
	        account.setBalance(total);
	        Account savedAccount = accountRepository.save(account);

	        
	        Transaction transaction = new Transaction();
	        transaction.setAccountId(id);
	        transaction.setAmount(amount);
	        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
	        transaction.setTimestamp(LocalDateTime.now());
	        transactionRepository.save(transaction);

	        return AccountMapper.mapToAccountDto(savedAccount);
	    }
	    
	    @Override
	    public List<AccountDto> getAllAccounts() {
	        List<Account> accounts = accountRepository.findAll();
	        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
	                .collect(Collectors.toList());
	    }

	    @Override
	    public void deleteAccount(Long id) {

	        Account account = accountRepository
	                .findById(id)
	                .orElseThrow(() -> new AccountException("Account does not exists"));

	        accountRepository.deleteById(id);
	    }
	    //transaction
	    @Override
	    public void transferFunds(TransferFundDto transferFundDto) {

	        // Retrieve the account from which we send the amount
	        Account fromAccount = accountRepository
	                .findById(transferFundDto.fromAccountId())
	                .orElseThrow(() -> new AccountException("Account does not exists"));

	        // Retrieve the account to which we send the amount
	        Account toAccount = accountRepository.findById(transferFundDto.toAccountId())
	                .orElseThrow(() -> new AccountException("Account does not exists"));

	        if(fromAccount.getBalance() < transferFundDto.amount()){
	            throw new RuntimeException("Insufficient Amount");
	        }
	        // Debit the amount from fromAccount object
	        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());

	        
	        // Debit the amount from fromAccount object
	        fromAccount.setBalance(fromAccount.getBalance() - transferFundDto.amount());

	        // Credit the amount to toAccount object
	        toAccount.setBalance(toAccount.getBalance() + transferFundDto.amount());

	        accountRepository.save(fromAccount);

	        accountRepository.save(toAccount);
	        
	        Transaction transaction = new Transaction();
	        transaction.setAccountId(transferFundDto.fromAccountId());
	        transaction.setAmount(transferFundDto.amount());
	        transaction.setTransactionType(TRANSACTION_TYPE_TRANSFER);
	        transaction.setTimestamp(LocalDateTime.now());
	        transactionRepository.save(transaction);
	    }
	    
	    @Override
		public List<TransactionDto> getAccountTransactions(Long accountId) {
	    	
	    	List<Transaction> transactions= transactionRepository
	    			.findByAccountIdOrderByTimestampDesc(accountId);
	    	
	    	return transactions.stream().map((transaction) -> convertEntityToDto(transaction))
	    	.collect(Collectors.toList());
	    	
		}

	    private TransactionDto convertEntityToDto(Transaction transaction) {
	    	return new TransactionDto(
	    			transaction.getId(),
	    			transaction.getAccountId(),
	    			transaction.getAmount(),
	    			transaction.getTransactionType(),
	    			transaction.getTimestamp()
	    		);
	    }
}
