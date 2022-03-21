package com.learning.service;

import java.util.List;
import java.util.Optional;

import com.learning.entity.User;
import com.learning.enums.ERole;

public interface UserService {
	public User addUser(User user);
	public Optional<User> getUserById(Integer id);
	public Optional<User> getUserByUsername(String username);
	public User updateUser(User user);
	public List<User> findAllByRoleName(ERole roleName);
	public List<User> getAllUsers();
}
