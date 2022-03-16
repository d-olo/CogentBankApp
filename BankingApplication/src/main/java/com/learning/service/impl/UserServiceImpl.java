package com.learning.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learning.entity.Role;
import com.learning.entity.User;
import com.learning.enums.ERole;
import com.learning.repo.UserRepository;
import com.learning.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User addUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	@Override
	public Optional<User> getUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<User> findAllByRoleName(ERole roleName) {
		List<User> allUsers = userRepository.findAll();
		List<User> usersOfRole = new ArrayList<User>();
		
		for(User user: allUsers) {
			for(Role role : user.getRoles()) {
				if(role.getRoleName() == roleName) {
					usersOfRole.add(user);
				}
			}
		}
		
		return usersOfRole;
	}

}
