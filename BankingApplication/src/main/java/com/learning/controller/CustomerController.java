package com.learning.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.learning.entity.Account;
import com.learning.entity.Beneficiary;
import com.learning.entity.Role;
import com.learning.entity.Transaction;
import com.learning.entity.User;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;
import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;
import com.learning.exception.DataMismatchException;
import com.learning.exception.EnumNotFoundException;
import com.learning.exception.IdNotFoundException;
import com.learning.exception.NoDataFoundException;
import com.learning.exception.OperationFailedException;
import com.learning.payload.request.AuthenticationRequest;
import com.learning.payload.request.ForgotPasswordRequest;
import com.learning.payload.request.UpdateCustomerRequest;
import com.learning.payload.request.customer.AddAccountRequest;
import com.learning.payload.request.customer.AddBeneficiaryRequest;
import com.learning.payload.request.customer.RegisterCustomerRequest;
import com.learning.payload.request.customer.TransferRequest;
import com.learning.payload.response.GetCustomerResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.TransferResponse;
import com.learning.payload.response.customer.AccountByIdResponse;
import com.learning.payload.response.customer.AddAccountResponse;
import com.learning.payload.response.customer.BeneficiaryListResponse;
import com.learning.payload.response.customer.CustomerRegisterResponse;
import com.learning.payload.response.customer.GetAccountResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.BeneficiaryService;
import com.learning.service.UserService;
import com.learning.utils.FileUploadUtil;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:4200")
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
	// Manages database operations for beneficiaries
	private BeneficiaryService beneficiaryService;
	@Autowired
	// Manages authentication
	AuthenticationManager authenticationManager;
	@Autowired
	// Encodes passwords for database storage.
	PasswordEncoder passwordEncoder;
	@Autowired
	// Utilities for JSON web tokens
	JwtUtils jwtUtils;
	
	
	/**
	 * Registers a new customer.
	 * @param request A request entity containing customer information.
	 * @return An HTTP response containing the customer added to the database.
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register(
			@Valid @RequestBody RegisterCustomerRequest request) {
		
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
		
		CustomerRegisterResponse registerResponse = new CustomerRegisterResponse();
		registerResponse.setId(regUser.getId());
		registerResponse.setUsername(regUser.getUsername());
		registerResponse.setFullName(regUser.getFullName());
		registerResponse.setPassword(regUser.getPassword());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
	}
	
	
	/**
	 * Given a username and password, authenticates a user.
	 * @param loginRequest
	 * @return An HTTP response containing a JSON web token.
	 */
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate
		(@Valid @RequestBody AuthenticationRequest authRequest) {
		
		// Gets authentication from the username and password.
		Authentication authentication = authenticationManager.
				authenticate(new UsernamePasswordAuthenticationToken
						(authRequest.getUsername(), authRequest.getPassword()));
		
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
	
	/**
	 * Creates a new account.
	 * @param id Internal ID of the customer to create an account for.
	 * @param accountRequest The details of the account to be created
	 * @return An HTTP response containing the created account.
	 */
	@Transactional
	@PostMapping("/{id}/account")
	public ResponseEntity<?> addAccount(
			@Valid @PathVariable Integer id, AddAccountRequest accountRequest) {
		User user = userService.getUserById(id).orElseThrow(
				()->new IdNotFoundException(
						"Sorry, Customer with ID: " + id + " not found.")
				);
		
		Account account = new Account();
		
		switch (accountRequest.getAccountType().name()) {
		case "ACCOUNT_SAVINGS":
			account.setAccountType(AccountType.ACCOUNT_SAVINGS);
			break;
		case "ACCOUNT_CHECKING":
			account.setAccountType(AccountType.ACCOUNT_CHECKING);
			break;
		default:
			break;
		}
		
		account.setAccountBalance(accountRequest.getAccountBalance());
		
		// All accounts are not approved on creation.
		account.setApprovedStatus(ApprovedStatus.STATUS_NOT_APPROVED);
		account.setAccountOwner(user);
		account.setDateCreated(Date.valueOf(LocalDate.now()));
		account.setTransactions(new HashSet<Transaction>());
		account.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		Account createdAccount = accountService.addAccount(account);
		
		// Build the HTTP response.
		AddAccountResponse accountResponse = new AddAccountResponse();
		accountResponse.setAccountType(createdAccount.getAccountType());
		accountResponse.setAccountBalance(createdAccount.getAccountBalance());
		accountResponse.setDateCreated(createdAccount.getDateCreated());
		accountResponse.setCustomerId(createdAccount.getAccountOwner().getId());
		accountResponse.setApprovedStatus(createdAccount.getApprovedStatus());
		
		return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
	}
	
	/** COMPLETED **/
	@PutMapping("/{id}/account/{accountNo}")
	@PreAuthorize("hasRole('STAFF')")
	/**
	 * A method for staff members to approve a customer's account.
	 * @param id The customer whose ID to search.
	 * @param accountNumber The account whose ID to approve.
	 * @return An HTTP response containing the approved account.
	 */
	public ResponseEntity<?> approveAccount(
			@PathVariable("id") Integer id, 
			@PathVariable("accountNo") Integer accountNumber) {
		
		User user = userService.getUserById(id).orElseThrow(
				()->new RuntimeException("Sorry, Customer with ID: " + id + " not found")
				);
		Account account = null;
		
		for(Account a : user.getAccounts()) {
			if(a.getAccountId() == accountNumber) {
				account = accountService.findByAccountId(
						a.getAccountId()).orElseThrow(
								()-> new RuntimeException("Account not found"));
				break;
			}
		}
		account.setApprovedStatus(ApprovedStatus.STATUS_APPROVED);
		accountService.approveAccount(account);
		return ResponseEntity.status(HttpStatus.OK).body(account);
	}
	
	/** COMPLETED **/
	@GetMapping("/{id}/account")
	/**
	 * Gets all accounts from a certain user.
	 * @param id The user whose accounts to check.
	 * @return An HTTP response containing the list of accounts.
	 */
	public ResponseEntity<?> getAllAccounts(@PathVariable("id") Integer id) {
		
		User user = userService.getUserById(id).orElseThrow(
				()->new NoDataFoundException("data not available")
				);
		
		// Retrieve the user's accounts.
		Set<Account> accounts = user.getAccounts();
		Set<GetAccountResponse> response = new HashSet<GetAccountResponse>();
		
		accounts.forEach(e-> {
			GetAccountResponse accountList = new GetAccountResponse();
			accountList.setAccountType(e.getAccountType());
			accountList.setAccountBalance(e.getAccountBalance());
			accountList.setApprovedStatus(e.getApprovedStatus());
			response.add(accountList);
		});
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	
	}
	
	/** COMPLETED **/
	@GetMapping("/{id}")
	/**
	 * Gets a user by ID.
	 * @param id ID to search.
	 * @return HTTP response containing the user.
	 */
	public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(
				()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found")
				);
		
		GetCustomerResponse response = new GetCustomerResponse();
		
		response.setUsername(user.getUsername());
		response.setFullName(user.getFullName());
		response.setPhone(user.getPhone());
		response.setPan(user.getPan());
		response.setAadhar(user.getAadhar());
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
		
	}
	

	/** NEEDS REVIEW **/
	@PutMapping("{id}")
	public ResponseEntity<?> updateUser(@Valid @PathVariable("id") Integer id, UpdateCustomerRequest updateCustomerRequest, 
			@RequestParam("image") MultipartFile multipartFilePan, @RequestParam("image") MultipartFile multipartFileAadhar) 
	throws IOException {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
		
		user.setFullName(updateCustomerRequest.getFullName());
		user.setPhone(updateCustomerRequest.getPhone());
		user.setPan(updateCustomerRequest.getPan());
		user.setAadhar(updateCustomerRequest.getAadhar());
		user.setSecretQuestion(updateCustomerRequest.getSecretQuestion());
		user.setSecretAnswer(updateCustomerRequest.getSecretAnswer());
		
		/* Not sure if correct way to accept image files from input */
		
		String panImage = StringUtils.cleanPath(multipartFilePan.getOriginalFilename());
		String aadharImage = StringUtils.cleanPath(multipartFileAadhar.getOriginalFilename());
		
		user.setPanImage(panImage);	
		user.setAadharImage(aadharImage);	
			
		User updatedUser = userService.updateUser(user);
		
		String uploadDir = "customer-files/" + updatedUser.getId();
		 
        FileUploadUtil.saveFile(uploadDir, updatedUser.getPanImage(), multipartFilePan);
        FileUploadUtil.saveFile(uploadDir, updatedUser.getAadharImage(), multipartFileAadhar);
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	
	/** NEEDS REVIEW **/
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
				account = accountService.findByAccountId(accountId).orElseThrow(
						()-> new RuntimeException("Account not found"));
				break;
			}
		}
		
		AccountByIdResponse response = new AccountByIdResponse();
		
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
	
	/** NEEDS REVIEW **/
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
		beneficiary.setBeneficiaryAddedDate(Date.valueOf(LocalDate.now()));
		
		beneficiaryService.addBeneficiary(beneficiary);
		
		return ResponseEntity.status(HttpStatus.OK).body(
				"Beneficiary with Account Number: "+ beneficiaryRequest.getAccountNumber() + " added");
	
	}
	
	/** COMPLETED **/
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
	
	/** COMPLETED **/
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
			throw new NoDataFoundException("Unable to delete beneficiary");
		}
	
	}
	
	/** NEEDS REVIEW **/
	@PutMapping("/transfer")
	@PreAuthorize("hasRole('CUSTOMER')")
	/**
	 * Transfers from one account to another.
	 * @param transferRequest Data of the transfer request.
	 * @return HTTP response confirming successful transfer.
	 */
	public ResponseEntity<?> transferAmount(TransferRequest transferRequest) {
		Account toAccount = accountService.findByAccountId(transferRequest.getToAccNumber())
				.orElseThrow(
						()-> new RuntimeException("Account not found")
				);
		Account fromAccount = accountService.findByAccountId(transferRequest.getFromAccNumber())
				.orElseThrow(
						()-> new RuntimeException("Account not found")
				);
		
		toAccount.setAccountBalance(toAccount.getAccountBalance() + transferRequest.getAmount());
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferRequest.getAmount());
		
		TransferResponse transferResponse = new TransferResponse();
		transferResponse.setAmount(transferRequest.getAmount());
		transferResponse.setReason(transferRequest.getReason());
		transferResponse.setBy(transferRequest.getBy());	
		
		accountService.updateAccount(fromAccount);
		accountService.updateAccount(toAccount);	
		
		return ResponseEntity.status(HttpStatus.OK).body(transferResponse);
	}
	
	/** NEEDS REVIEW **/
	@GetMapping("{username}/forgot/question/answer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> forgotPassword(@Valid @PathVariable("username") String username, ForgotPasswordRequest forgotPasswordRequest) {
		User user = userService.getUserByUsername(username).get();
		
		if(user.getUsername().equals(forgotPasswordRequest.getUsername())
			&& user.getSecretQuestion().equals(forgotPasswordRequest.getSecretQuestion())
			&& user.getSecretAnswer().equals(forgotPasswordRequest.getSecretAnswer())) {
			
			return ResponseEntity.status(HttpStatus.OK).body("Details validated");
			
			
		} else {
			throw new DataMismatchException("Sorry your secret details are not matching");
		}
		
	}
	
	
	/** NEEDS REVIEW **/
	@PutMapping("/{username}/forgot")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> newPassword(@Valid @PathVariable("username") String username, AuthenticationRequest authRequest) {
		User user = userService.getUserByUsername(username).get();
		
		if(user != null && !user.getPassword().equals(authRequest.getPassword())) {
			user.setPassword(authRequest.getPassword());
		
			userService.updateUser(user);
		
			return ResponseEntity.status(HttpStatus.OK).body("New password updated");
		} else {
			throw new OperationFailedException("Sorry password not updated");
		}
		
	}
}
