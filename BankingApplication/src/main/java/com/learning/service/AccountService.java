package com.learning.service;

import java.util.List;
import java.util.Optional;

import com.learning.entity.Account;
import com.learning.entity.User;
import com.learning.enums.AccountType;

public interface AccountService {
	public Account addAccount(Account account);
	public Account approveAccount(Account account);
	public Optional<Account> findByAccountId(Integer accountId);
	public Optional<AccountType> findByAccountType(AccountType accountType);
	public void updateAccount(Account account);
}
