package com.learning.utils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.learning.entity.User;
import com.learning.entity.Role;
import com.learning.enums.ERole;
import com.learning.exception.EnumNotFoundException;
import com.learning.repo.RoleRepository;
import com.learning.service.UserService;

@Component
public class SetAdmin {
	private static final String DEFAULT_ADMIN_USERNAME = "admin@admin.com";
	private static final String DEFAULT_ADMIN_PASSWORD = "secret@123";
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	private void initializeDB() {
	
		initializeAdmin();
	}

	private void initializeAdmin() {
		if (!userService.existsByUsername(DEFAULT_ADMIN_USERNAME)) {
			User admin = new User();
			admin.setFullName("Administrator");
	
			admin.setUsername(DEFAULT_ADMIN_USERNAME);
			admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
			Role role = roleRepository.findByRoleName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> 
						new EnumNotFoundException("Customer role not in database."));
			Set<Role> roles = new HashSet<Role>();
			roles.add(role);
			admin.setRoles(roles);
		
			
		
			
			userService.addUser(admin);
		}
	}
}
