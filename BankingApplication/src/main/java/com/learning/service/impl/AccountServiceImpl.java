package com.learning.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.entity.Account;
import com.learning.entity.User;
import com.learning.enums.AccountType;
import com.learning.repo.AccountRepository;
import com.learning.service.AccountService;



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
	public Optional<Account> findByAccountId(Integer accountId) {
		return accountRepository.findById(accountId);
	}
	
	@Override
	public Optional<AccountType> findByAccountType(AccountType accountType) {
		return accountRepository.findByAccountType(accountType);
	}

	@Override
	public void updateAccount(Account account) {
		accountRepository.save(account);
		
	}

}
