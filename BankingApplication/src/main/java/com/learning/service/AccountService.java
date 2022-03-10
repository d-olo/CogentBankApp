package com.learning.service;

import java.util.Optional;

import com.learning.entity.Account;
import com.learning.enums.AccountType;

public interface AccountService {
	public Account addAccount(Account account);
	public Account approveAccount(Account account);
	public Account findByAccountId(Integer accountId);
	public Optional<Account> findByAccountNumber(Integer accountNo);
	public Optional<AccountType> findByAccountType(AccountType accountType);
}
