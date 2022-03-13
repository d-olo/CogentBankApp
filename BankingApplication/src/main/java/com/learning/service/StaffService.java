package com.learning.service;

import java.util.List;

import com.learning.entity.Account;
import com.learning.entity.User;

public interface StaffService {

	public List<User> getAccountByAccNum(long accountNum);
	public Account getAccountByAccNum(Long accountNum);
}
