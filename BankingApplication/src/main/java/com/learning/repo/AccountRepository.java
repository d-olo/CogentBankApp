package com.learning.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learning.entity.Account;
import com.learning.enums.AccountType;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	public Optional<AccountType> findByAccountType(AccountType accountType);
	public Optional<Account> findByAccountNumber(Integer accountNo);
}
