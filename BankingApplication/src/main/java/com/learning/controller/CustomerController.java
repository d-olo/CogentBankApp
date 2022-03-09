package com.learning.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.learning.enums.EnabledStatus;
import com.learning.exception.EnumNotFoundException;
import com.learning.payload.request.AuthenticationRequest;
import com.learning.payload.request.CustomerRegisterRequest;
import com.learning.payload.response.CustomerRegisterResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.UserService;

@RestController
@RequestMapping("/customer")
/**
 * Handler for the customer API.
 * @author Dionel Olo, Oliver Pagalanan
 * @since Mar 8, 2022
 */
public class CustomerController {
	
	@Autowired
	// Manages database operations for the user.
	private UserService userService;
	
	@Autowired
	// Manages database operations for a user's accounts.
	private AccountService accountService;
	
	@Autowired
	// Manages access to the role repository.
	private RoleRepository roleRepository;
	
	@Autowired
	// Encodes passwords for database storage.	
	PasswordEncoder passwordEncoder;
	
	@Autowired
	// Manages authentication.
	AuthenticationManager authenticationManager;
	
	@Autowired
	// Utilities for JSON web tokens.
	JwtUtils jwtUtils;
	
	@PostMapping(value = "/register")
	/**
	 * Registers a new customer.
	 * @param request A request entity containing customer information.
	 * @return An HTTP response containing the customer added to the database.
	 */
	public ResponseEntity<?> register
		(@Valid @RequestBody CustomerRegisterRequest request) {
		
		// Creating new user.
		User user = new User();
		user.setUsername(request.getUsername());
		user.setFullName(request.getFullName());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		// Initialization of roles with Customer.
		Set<Role> roles = new HashSet<Role>();
		Role customerRole = roleRepository.findByRoleName(ERole.ROLE_CUSTOMER)
				.orElseThrow(() -> 
					new EnumNotFoundException("Customer role not in database."));
		roles.add(customerRole);
		user.setRoles(roles);
		
		// Initialization of empty fields.
		user.setAccounts(new HashSet<Account>());
		user.setEnabledStatus(EnabledStatus.STATUS_DISABLED);
		user.setDateCreated(Date.valueOf(LocalDate.now()));
		
		user = userService.addUser(user);
		CustomerRegisterResponse response = new CustomerRegisterResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setFullName(user.getFullName());
		response.setPassword(user.getPassword());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/authenticate")
	/**
	 * Given a username and password, authenticates a user.
	 * @param loginRequest
	 * @return An HTTP response containing a JSON web token.
	 */
	public ResponseEntity<?> authenticate
		(@Valid @RequestBody AuthenticationRequest loginRequest) {
		
		// Gets authentication from the username and password.
		Authentication authentication = authenticationManager.
				authenticate(new UsernamePasswordAuthenticationToken
						(loginRequest.getUsername(), loginRequest.getPassword()));
		
		// Generates the JWT from the authentication.
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateToken(authentication);
		
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();		
		
		List<String> roles = userDetailsImpl.getAuthorities().stream()
				.map(e-> e.getAuthority()).collect(Collectors.toList());
		
		// Builds a response from the JWT.
		return ResponseEntity.ok(new JwtResponse(
				jwt, 
				userDetailsImpl.getId(), 
				userDetailsImpl.getUsername(), 
				userDetailsImpl.getFullName(), 
				roles));
	
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
