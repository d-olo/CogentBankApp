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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.learning.exception.EnumNotFoundException;
import com.learning.payload.request.customer.AddAccountRequest;
import com.learning.payload.request.customer.AuthenticationRequest;
import com.learning.payload.request.customer.AddBeneficiaryRequest;
import com.learning.payload.request.customer.CustomerRegisterRequest;
import com.learning.payload.request.customer.TransferRequest;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.customer.AccountByIdResponse;
import com.learning.payload.response.customer.AccountListResponse;
import com.learning.payload.response.customer.AccountResponse;
import com.learning.payload.response.customer.BeneficiaryListResponse;
import com.learning.payload.response.customer.RegisterResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.BeneficiaryService;
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
	private BeneficiaryService beneficiaryService;
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
	public ResponseEntity<?> register(
			@Valid @RequestBody CustomerRegisterRequest request) {
		
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
		user.setBeneficiaries(new HashSet<Beneficiary>());
		
		// Adding user to DB.
		User regUser = userService.addUser(user);
		
		RegisterResponse registerResponse = new RegisterResponse();
		registerResponse.setId(regUser.getId());
		registerResponse.setUsername(regUser.getUsername());
		registerResponse.setFullName(regUser.getFullName());
		registerResponse.setPassword(regUser.getPassword());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
	}
	
	@PostMapping("/authenticate")
	/** 
	 * Returns a JWT for customer login.
	 * @param loginRequest
	 * @return
	 */
	public ResponseEntity<?> authenticate(
			@Valid @RequestBody AuthenticationRequest loginRequest) {
		Authentication authentication = authenticationManager.
				authenticate(new UsernamePasswordAuthenticationToken
						(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateToken(authentication);
		
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();		
		
		List<String> roles = userDetailsImpl.getAuthorities().stream()
				.map(e-> e.getAuthority()).collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(
				jwt, 
				userDetailsImpl.getId(), 
				userDetailsImpl.getUsername(), 
				userDetailsImpl.getFullName(), 
				roles));
	
	}
	
	/** INCOMPLETE **/
	@PostMapping("/:{id}/account")
	/**
	 * Creates a new account.
	 * @param id Internal ID of the customer to create an account for.
	 * @param accountRequest The details of the account to be created
	 * @return An HTTP response containing the created account.
	 */
	public ResponseEntity<?> addAccount(
			@PathVariable Integer id, AddAccountRequest accountRequest) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Customer with ID: " + id + " not found"));
		Account account = new Account();
		
		switch (accountRequest.getAccountType().name()) {
		case "savings":
			account.setAccountType(
					accountService.findByAccountType(AccountType.ACCOUNT_SAVINGS)
			.orElseThrow(() -> new RuntimeException("Account Type error")));
			break;
		case "checking":
			account.setAccountType(
					accountService.findByAccountType(AccountType.ACCOUNT_CHECKING)
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
		
		return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
	}
	
	@PutMapping("/{id}/account/{accountNo}")
	@PreAuthorize("hasRole('STAFF')")
	/**
	 * A method for staff members to approve a certain account of a customer.
	 * @param id The customer whose ID to search.
	 * @param accountNumber The account whose ID to approve.
	 * @return An HTTP response containing the approved account.
	 */
	public ResponseEntity<?> approveAccount(
			@PathVariable("id") Integer id, @PathVariable("accountNo") Integer accountNumber) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		Account account = null;
		
		for(Account a : user.getAccounts()) {
			if(a.getAccountNumber() == accountNumber) {
				account = accountService.findByAccountNumber(
						a.getAccountId()).orElseThrow(()-> new RuntimeException("Account not found"));
				break;
			}
		}
		
		account.setApprovedStatus(ApprovedStatus.STATUS_APPROVED);
		accountService.approveAccount(account);
		return ResponseEntity.status(HttpStatus.OK).body(account);
		
	}
	
	@GetMapping("/{id}/account")
	/**
	 * Gets all accounts from a certain user.
	 * @param id The user whose accounts to check.
	 * @return An HTTP response containing the list of accounts.
	 */
	public ResponseEntity<?> getAllAccounts(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
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
	/**
	 * Gets a user by ID.
	 * @param id ID to search.
	 * @return HTTP response containing the user.
	 */
	public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		return ResponseEntity.status(HttpStatus.OK).body(user);
		
	}
	
	/** INCOMPLETE **/
	@PutMapping("/{id}")
	/**
	 * Updates the user of the given ID.
	 * @param id ID to search.
	 * @param registerRequest Updated customer data.
	 * @return HTTP response OK.
	 */
	public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, 
			CustomerRegisterRequest registerRequest) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		user.setUsername(registerRequest.getUsername());
		user.setFullName(registerRequest.getFullName());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		
		/* Needs more fields */	
			
		userService.updateUser(user);
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	
	/**
	 * Gets an account from a customer.
	 * @param id The customer to check.
	 * @param accountId The account to get.
	 * @return HTTP response containing the account data.
	 */
	@GetMapping("/{id}/account/{accountId}")
	public ResponseEntity<?> getAccountById(
			@PathVariable("id") Integer id, @PathVariable("accountId") Integer accountId) {
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
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
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
		
	}
	
	@PostMapping("{id}/beneficiary")
	/**
	 * Adds a beneficiary to a user.
	 * @param id The ID of the user to modify.
	 * @param beneficiaryRequest The data of the beneficiary to add.
	 * @return HTTP response confirming the beneficiary addition.
	 */
	public ResponseEntity<?> addBeneficiary(
			@PathVariable("id") Integer id, AddBeneficiaryRequest beneficiaryRequest) {
		User user = userService.getUserById(id).orElseThrow(
				()-> new RuntimeException("Sorry, Customer with ID: " + id + " not found"));
		
		Beneficiary beneficiary = new Beneficiary();
		
		beneficiary.setAccountNumber(beneficiaryRequest.getAccountNumber());
		beneficiary.setAccountType(beneficiaryRequest.getAccountType());
		beneficiary.setApprovedStatus(beneficiaryRequest.getApprovedStatus());
		beneficiary.setMainUser(user);
		
		user.getBeneficiaries().add(beneficiary);
		
		userService.updateUser(user);
		
		beneficiaryService.addBeneficiary(beneficiary);
		
		return ResponseEntity.status(HttpStatus.OK).body(
				"Beneficiary with Account Number: "+ beneficiaryRequest.getAccountNumber() + " added");
	
	}
	
	@GetMapping("{id}/beneficiary")
	/**
	 * Gets all beneficiaries of a user.
	 * @param id The user to check.
	 * @return HTTP response containing the list of beneficiaries.
	 */
	public ResponseEntity<?> getBeneficiaries(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id)
				.orElseThrow(()-> new RuntimeException(
						"Sorry, Customer with ID: " + id + " not found"));
		Set<Beneficiary> beneficiaries = new HashSet<>();
		
		user.getBeneficiaries().forEach(e-> {
			beneficiaries.add(e);
		});
		
		Set<BeneficiaryListResponse> response = null;
		
		if(beneficiaries.size() > 0) {
		
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
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping("{id}/beneficiary/{beneficiaryId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	/**
	 * Removes a beneficiary from a customer. 
	 * @param id The customer to search.
	 * @param beneficiaryId The beneficiary to remove.
	 * @return HTTP response confirming deletion.
	 */
	public ResponseEntity<?> deleteBeneficiary(@PathVariable("id") Integer id, @PathVariable("beneficiaryId") Integer beneficiaryId) {
		
		if(beneficiaryService.existsById(beneficiaryId)) {
			beneficiaryService.deleteBeneficiary(beneficiaryId);
			return ResponseEntity.status(HttpStatus.OK)
					.body("Beneficiary deleted successfully");
		} else {
			throw new RuntimeException("Unable to delete beneficiary");
		}
	
	}
	
	/** INCOMPLETE **/
	@PutMapping("/transfer")
	@PreAuthorize("hasRole('CUSTOMER')")
	/**
	 * Transfers from one account to another.
	 * @param transferRequest Data of the transfer request.
	 * @return HTTP response confirming successful transfer.
	 */
	public ResponseEntity<?> transferAmount(TransferRequest transferRequest) {
		Account toAccount = accountService.findByAccountNumber(transferRequest.getToAccNumber()).get();
		Account fromAccount = accountService.findByAccountNumber(transferRequest.getFromAccNumber()).get();
		
		toAccount.setAccountBalance(toAccount.getAccountBalance() + transferRequest.getAmount());
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferRequest.getAmount());
		
		/* STILL NEED ACTION FOR "REASON" AND "BY" */
		
		accountService.addAccount(fromAccount);	// not sure if correct
		accountService.addAccount(toAccount);	// not sure if correct
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
