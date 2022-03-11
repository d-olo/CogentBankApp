package com.learning.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.security.access.prepost.PreAuthorize;
=======
>>>>>>> master
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.DeleteMapping;
=======
>>>>>>> master
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.entity.Account;
import com.learning.entity.Beneficiary;
import com.learning.entity.Role;
import com.learning.entity.Transaction;
import com.learning.entity.User;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;
import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;
<<<<<<< HEAD
import com.learning.payload.request.AccountRequest;
import com.learning.payload.request.BeneficiaryRequest;
import com.learning.payload.request.LoginRequest;
import com.learning.payload.request.RegisterRequest;
import com.learning.payload.request.TransferRequest;
import com.learning.payload.response.AccountByIdResponse;
import com.learning.payload.response.AccountListResponse;
import com.learning.payload.response.AccountResponse;
import com.learning.payload.response.BeneficiaryListResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.RegisterResponse;
=======
import com.learning.exception.EnumNotFoundException;
import com.learning.payload.request.customer.AuthenticationRequest;
import com.learning.payload.request.customer.CustomerRegisterRequest;
import com.learning.payload.response.CustomerRegisterResponse;
import com.learning.payload.response.JwtResponse;
>>>>>>> master
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
<<<<<<< HEAD
import com.learning.service.BeneficiaryService;
import com.learning.service.UserService;

@RestController
@RequestMapping("/api/customer")
=======
import com.learning.service.UserService;

@RestController
@RequestMapping("/customer")
/**
 * Handler for the customer API.
 * @author Dionel Olo, Oliver Pagalanan
 * @since Mar 8, 2022
 */
>>>>>>> master
public class CustomerController {
	
	@Autowired
	// Manages database operations for the user.
	private UserService userService;
	@Autowired
	private AccountService accountService;
	@Autowired
	// Manages database operations for a user's accounts.
	private AccountService accountService;
	
	@Autowired
	// Manages access to the role repository.
	private RoleRepository roleRepository;
	@Autowired
	private BeneficiaryService beneficiaryService;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	JwtUtils jwtUtils;
	
<<<<<<< HEAD
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		User user = new User();
		Set<Role> roles = new HashSet<>();
		Role role = roleRepository.findByRoleName(ERole.ROLE_CUSTOMER).orElseThrow(()->new RuntimeException("Role error"));
		roles.add(role);
		
		user.setUsername(registerRequest.getUsername());
		user.setFullName(registerRequest.getFullName());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
=======
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
>>>>>>> master
		user.setRoles(roles);
		
		// Initialization of empty fields.
		user.setAccounts(new HashSet<Account>());
		user.setEnabledStatus(EnabledStatus.STATUS_DISABLED);
		user.setBeneficiaries(new HashSet<Beneficiary>());
		
		
		User regUser = userService.addUser(user);
		
		RegisterResponse registerResponse = new RegisterResponse();
		registerResponse.setId(regUser.getId());
		registerResponse.setUsername(regUser.getUsername());
		registerResponse.setFullName(regUser.getFullName());
		registerResponse.setPassword(regUser.getPassword());
		
		return ResponseEntity.status(201).body(registerResponse);
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
	
	/** INCOMPLETE **/
	@PostMapping("/{id}/account")
	public ResponseEntity<?> addAccount(@PathVariable Integer id, AccountRequest accountRequest) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		Account account = new Account();
		
		switch (accountRequest.getAccountType().name()) {
		case "savings":
			account.setAccountType(accountService.findByAccountType(AccountType.ACCOUNT_SAVINGS)
			.orElseThrow(() -> new RuntimeException("Account Type error")));
			break;
		case "checking":
			account.setAccountType(accountService.findByAccountType(AccountType.ACCOUNT_CHECKING)
			.orElseThrow(() -> new RuntimeException("Account Type error")));
			break;

		default:
			break;
		}
		
		//account.setAccountNumber(????);
		account.setAccountBalance(accountRequest.getAccountBalance());
		account.setApprovedStatus(accountRequest.getApprovedStatus());
		account.setAccountOwner(user);
		account.setDateCreated(Date.valueOf(LocalDate.now()));
		account.setTransactions(new HashSet<Transaction>());
		
		accountService.addAccount(account);
		
		user.getAccounts().add(account);
		userService.updateUser(user);	// not sure if correct way
		
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setAccountType(account.getAccountType());
		accountResponse.setAccountBalance(account.getAccountBalance());
		accountResponse.setDateCreated(account.getDateCreated());
		accountResponse.setCustomerId(account.getAccountOwner().getId());
		
		return ResponseEntity.status(200).body(accountResponse);
	}
	
	@PutMapping("/{id}/account/{accountNo}")
	@PreAuthorize("hasRole('STAFF')")
	public ResponseEntity<?> approveAccount(@PathVariable("id") Integer id, @PathVariable("accountNo") Integer accountNumber) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		Account account = null;
		
		for(Account a : user.getAccounts()) {
			if(a.getAccountNumber() == accountNumber) {
				account = accountService.findByAccountNumber(a.getAccountId()).orElseThrow(()-> new RuntimeException("Account not found"));
				break;
			}
		}
		
		account.setApprovedStatus(ApprovedStatus.STATUS_APPROVED);
		accountService.approveAccount(account);
		return ResponseEntity.status(200).body(account);
		
	}
	
	@GetMapping("/{id}/account")
	public ResponseEntity<?> getAllAccounts(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		Set<Account> accounts = new HashSet<>();
		
		user.getAccounts().forEach(e-> {
			accounts.add(e);
		});
		
		Set<AccountListResponse> response = new HashSet<>();
		
		accounts.forEach(e-> {
			AccountListResponse accountList = new AccountListResponse();
			accountList.setAccountNumber(e.getAccountNumber());
			accountList.setAccountType(e.getAccountType());
			accountList.setAccountBalance(e.getAccountBalance());
			accountList.setApprovedStatus(e.getApprovedStatus());
			response.add(accountList);
		});
		
		return ResponseEntity.ok(response);
	
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		return ResponseEntity.status(200).body(user);
		
	}
	
<<<<<<< HEAD
	
	/** INCOMPLETE **/
	@PutMapping("{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, RegisterRequest registerRequest) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		user.setUsername(registerRequest.getUsername());
		user.setFullName(registerRequest.getFullName());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		
		/* Needs more fields */	
			
		userService.updateUser(user);
		
		return ResponseEntity.status(200).build();
		
	}
	
	@GetMapping("/{id}/account/{accountId}")
	public ResponseEntity<?> getAccountById(@PathVariable("id") Integer id, @PathVariable("accountId") Integer accountId) {
		User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		Account account = null;
		
		for(Account a : user.getAccounts()) {
			if(a.getAccountId() == accountId) {
				account = accountService.findByAccountId(accountId);
				break;
			}
		}
		
		AccountByIdResponse response = new AccountByIdResponse();
		
		response.setAccountNumber(account.getAccountNumber());
		response.setAccountType(account.getAccountType().name());
		response.setAccountBalance(account.getAccountBalance());
		response.setEnabledStatus(account.getEnabledStatus());
		
		Set<Transaction> transactions = new HashSet<>();
		
		account.getTransactions().forEach(e->{
			transactions.add(e);
		});
		
		response.setTransactions(transactions);
		
		return ResponseEntity.status(200).body(response);
		
	}
	
	@PostMapping("{id}/beneficiary")
	public ResponseEntity<?> addBeneficiary(@PathVariable("id") Integer id, BeneficiaryRequest beneficiaryRequest) {
		User user = userService.getUserById(id).orElseThrow(()-> new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		Beneficiary beneficiary = new Beneficiary();
		
		beneficiary.setAccountNumber(beneficiaryRequest.getAccountNumber());
		beneficiary.setAccountType(beneficiaryRequest.getAccountType());
		beneficiary.setApprovedStatus(beneficiaryRequest.getApprovedStatus());
		beneficiary.setMainUser(user);
		
		user.getBeneficiaries().add(beneficiary);
		
		userService.updateUser(user);
		
		beneficiaryService.addBeneficiary(beneficiary);
		
		return ResponseEntity.status(200).body("Beneficiary with Account Number: "+ beneficiaryRequest.getAccountNumber() + " added");
	
	}
	
	@GetMapping("{id}/beneficiary")
	public ResponseEntity<?> getBeneficiaries(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()-> new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		Set<Beneficiary> beneficiaries = new HashSet<>();
		
		user.getBeneficiaries().forEach(e-> {
			beneficiaries.add(e);
		});
		
		Set<BeneficiaryListResponse> response = null;
		
		if(beneficiaries != null) {
		
			response = new HashSet<>();
			
			beneficiaries.forEach(e-> {
				BeneficiaryListResponse beneficiaryList = new BeneficiaryListResponse();
				beneficiaryList.setBeneficiaryAccountNumber(e.getAccountNumber());
				beneficiaryList.setBeneficiaryName(e.getMainUser().getFullName());
				beneficiaryList.setActiveStatus(e.getActiveStatus());
			});
		} else {
			response = Collections.emptySet();
		}
		
		return ResponseEntity.status(200).body(response);
	}
	
	@DeleteMapping("{id}/beneficiary/{beneficiaryId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> deleteBeneficiary(@PathVariable("id") Integer id, @PathVariable("beneficiaryId") Integer beneficiaryId) {
		
		if(beneficiaryService.existsById(beneficiaryId)) {
			beneficiaryService.deleteBeneficiary(beneficiaryId);
			return ResponseEntity.status(200).body("Beneficiary deleted successfully");
		} else {
			throw new RuntimeException("Unable to delete beneficiary");
		}
	
	}
	
	/** INCOMPLETE **/
	@PutMapping("/transfer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> transferAmount(TransferRequest transferRequest) {
		Account toAccount = accountService.findByAccountNumber(transferRequest.getToAccNumber()).get();
		Account fromAccount = accountService.findByAccountNumber(transferRequest.getFromAccNumber()).get();
		
		toAccount.setAccountBalance(toAccount.getAccountBalance() + transferRequest.getAmount());
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferRequest.getAmount());
		
		/* STILL NEED ACTION FOR "REASON" AND "BY" */
		
		accountService.addAccount(fromAccount);	// not sure if correct
		accountService.addAccount(toAccount);	// not sure if correct
		
		return ResponseEntity.status(200).build();
	}
	
	
=======
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
>>>>>>> master
}
