package com.learning.service;

import java.util.Optional;

import com.learning.entity.Account;
import com.learning.entity.User;

public interface UserService {
	public User addUser(User user);
	public Optional<User> getUserById(Integer id);
	public void updateUser(User user);
}
