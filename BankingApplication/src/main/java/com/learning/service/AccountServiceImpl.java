package com.learning.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.entity.Account;
import com.learning.enums.AccountType;
import com.learning.repo.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public Account addAccount(Account account) {
		return accountRepository.save(account);
	}

	@Override
	public Account approveAccount(Account account) {
		return accountRepository.save(account);
	}
	
	@Override
	public Account findByAccountId(Integer accountId) {
		return accountRepository.getById(accountId);
	}
	
	@Override
	public Optional<Account> findByAccountNumber(Integer accountNo) {
		return accountRepository.findByAccountNumber(accountNo);
	}
	
	@Override
	public Optional<AccountType> findByAccountType(AccountType accountType) {
		return accountRepository.findByAccountType(accountType);
	}

	

	
	

}
