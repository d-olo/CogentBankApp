package com.learning.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.entity.Account;
import com.learning.entity.Role;
import com.learning.entity.User;
import com.learning.enums.ERole;
import com.learning.payload.request.LoginRequest;
import com.learning.payload.request.RegisterRequest;
import com.learning.payload.response.JwtResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.UserService;

@RestController
@RequestMapping("/api/customer")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	JwtUtils jwtUtils;
	
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		User user = new User();
		Set<Role> roles = new HashSet<>();
		Role role = roleRepository.findByRoleName(ERole.ROLE_USER).orElseThrow(()->new RuntimeException("Role error"));
		roles.add(role);
		
		user.setUsername(registerRequest.getUsername());
		user.setFullName(registerRequest.getFullName());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRoles(roles);
		
		User regUser = userService.addUser(user);
		return ResponseEntity.status(201).body(regUser);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.
				authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateToken(authentication);
		
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();		
		
		List<String> roles = userDetailsImpl.getAuthorities().stream()
				.map(e-> e.getAuthority()).collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(jwt, userDetailsImpl.getId(), userDetailsImpl.getUsername(), userDetailsImpl.getFullName(), roles));
	
	}
	
	@PostMapping("/{id}/account")
	public ResponseEntity<?> addAccount(@PathVariable Integer id, Account account) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		account.setAccountOwner(user);
		
		accountService.addAccount(account);
		
		return ResponseEntity.status(200).body(account);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		return ResponseEntity.status(200).body(user);
		
	}
	
	public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, User newUser) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		if(user != null) {
			user.setFullName(newUser.getFullName());
			
			userService.updateUser(user);
		} else {
			throw new RuntimeException("Sorry, Customer with ID: " + id + " not found");
		}
		
		return ResponseEntity.status(200).build();
		
	}
}
