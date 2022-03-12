package com.learning.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
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
import com.learning.entity.Transfer;
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
import com.learning.payload.request.ForgotPasswordRequest;
import com.learning.payload.request.UpdateCustomerRequest;
import com.learning.payload.request.customer.AddAccountRequest;
import com.learning.payload.request.customer.AddBeneficiaryRequest;
import com.learning.payload.request.customer.AuthenticationRequest;
import com.learning.payload.request.customer.CustomerRegisterRequest;
import com.learning.payload.request.customer.TransferRequest;
import com.learning.payload.response.AccountByIdResponse;
import com.learning.payload.response.AccountListResponse;
import com.learning.payload.response.AccountResponse;
import com.learning.payload.response.BeneficiaryListResponse;
import com.learning.payload.response.GetCustomerResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.RegisterResponse;
import com.learning.repo.RoleRepository;
import com.learning.repo.TransferRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.BeneficiaryService;
import com.learning.service.UserService;
import com.learning.utils.FileUploadUtil;

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
	// Manages database operations for beneficiaries
	private BeneficiaryService beneficiaryService;
	@Autowired
	// Manages access to the transfer repository
	private TransferRepository transferRepository;
	@Autowired
	// Manages authentication
	AuthenticationManager authenticationManager;
	@Autowired
	// Encodes passwords for database storage.
	PasswordEncoder passwordEncoder;
	@Autowired
	// Utilities for JSON web tokens
	JwtUtils jwtUtils;
	
	
	@PostMapping("/register")
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
	/**
	 * Given a username and password, authenticates a user.
	 * @param loginRequest
	 * @return An HTTP response containing a JSON web token.
	 */
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
	
	/** NEEDS REVIEW **/
	@PostMapping("/{id}/account")
	public ResponseEntity<?> addAccount(@Valid @PathVariable Integer id, AddAccountRequest accountRequest) {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
		Account account = new Account();
		
		switch (accountRequest.getAccountType().name()) {
		case "savings":
			account.setAccountType(accountService.findByAccountType(AccountType.ACCOUNT_SAVINGS)
			.orElseThrow(() -> new EnumNotFoundException("Account Type error")));
			break;
		case "checking":
			account.setAccountType(accountService.findByAccountType(AccountType.ACCOUNT_CHECKING)
			.orElseThrow(() -> new EnumNotFoundException("Account Type error")));
			break;

		default:
			break;
		}
		
		// Generate random number to set as Acct. No
		Random ran = new Random();
     
		account.setAccountNumber(ran.nextInt(9999) + 1000);
		account.setAccountBalance(accountRequest.getAccountBalance());
		account.setApprovedStatus(accountRequest.getApprovedStatus());
		account.setAccountOwner(user);
		account.setDateCreated(Date.valueOf(LocalDate.now()));
		account.setTransactions(new HashSet<Transaction>());
		
		accountService.addAccount(account);
		
		user.getAccounts().add(account);
		userService.updateUser(user);	// not sure if correct way or necessary
		
		AccountResponse accountResponse = new AccountResponse();
		accountResponse.setAccountType(account.getAccountType());
		accountResponse.setAccountBalance(account.getAccountBalance());
		accountResponse.setDateCreated(account.getDateCreated());
		accountResponse.setCustomerId(account.getAccountOwner().getId());
		
		return ResponseEntity.status(200).body(accountResponse);
	}
	
	/** COMPLETED **/
	@PutMapping("/{id}/account/{accountNo}")
	@PreAuthorize("hasRole('STAFF')")
	public ResponseEntity<?> approveAccount(@PathVariable("id") Integer id, @PathVariable("accountNo") Integer accountNumber) {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
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
	
	/** COMPLETED **/
	@GetMapping("/{id}/account")
	public ResponseEntity<?> getAllAccounts(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
		
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
	
	/** COMPLETED **/
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
		
		GetCustomerResponse response = new GetCustomerResponse();
		
		response.setUsername(user.getUsername());
		response.setFullName(user.getFullName());
		response.setPhone(user.getPhone());
		response.setPan(user.getPan());
		response.setAadhar(user.getAadhar());
		
		return ResponseEntity.status(200).body(response);
		
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
		
		return ResponseEntity.status(200).build();
		
	}
	
	/** NEEDS REVIEW **/
	@GetMapping("/{id}/account/{accountId}")
	public ResponseEntity<?> getAccountById(@PathVariable("id") Integer id, @PathVariable("accountId") Integer accountId) {
		User user = userService.getUserById(id).orElseThrow(()->new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
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
	
	/** NEEDS REVIEW **/
	@PostMapping("{id}/beneficiary")
	public ResponseEntity<?> addBeneficiary(@Valid @PathVariable("id") Integer id, AddBeneficiaryRequest beneficiaryRequest) {
		User user = userService.getUserById(id).orElseThrow(()-> new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
		
		Beneficiary beneficiary = new Beneficiary();
		
		beneficiary.setAccountNumber(beneficiaryRequest.getAccountNumber());
		beneficiary.setAccountType(beneficiaryRequest.getAccountType());
		beneficiary.setApprovedStatus(beneficiaryRequest.getApprovedStatus());
		beneficiary.setMainUser(user);
		
		user.getBeneficiaries().add(beneficiary);
		
		userService.updateUser(user);	// not sure if correct way or necessary
		
		beneficiaryService.addBeneficiary(beneficiary);
		
		return ResponseEntity.status(200).body("Beneficiary with Account Number: "+ beneficiaryRequest.getAccountNumber() + " added");
	
	}
	
	/** COMPLETED **/
	@GetMapping("{id}/beneficiary")
	public ResponseEntity<?> getBeneficiaries(@PathVariable("id") Integer id) {
		User user = userService.getUserById(id).orElseThrow(()-> new IdNotFoundException("Sorry, Customer with ID: " + id + " not found"));
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
	
	/** COMPLETED **/
	@DeleteMapping("{id}/beneficiary/{beneficiaryId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> deleteBeneficiary(@PathVariable("id") Integer id, @PathVariable("beneficiaryId") Integer beneficiaryId) {
		
		if(beneficiaryService.existsById(beneficiaryId)) {
			beneficiaryService.deleteBeneficiary(beneficiaryId);
			return ResponseEntity.status(200).body("Beneficiary deleted successfully");
		} else {
			throw new NoDataFoundException("Unable to delete beneficiary");
		}
	
	}
	
	/** NEEDS REVIEW **/
	@PutMapping("/transfer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> transferAmount(@Valid TransferRequest transferRequest) {
		Account toAccount = accountService.findByAccountNumber(transferRequest.getToAccNumber()).get();
		Account fromAccount = accountService.findByAccountNumber(transferRequest.getFromAccNumber()).get();
		
		toAccount.setAccountBalance(toAccount.getAccountBalance() + transferRequest.getAmount());
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferRequest.getAmount());
		
		Transfer transferRecord = new Transfer();
		transferRecord.setFromAccNumber(fromAccount.getAccountNumber());
		transferRecord.setToAccNumber(toAccount.getAccountNumber());
		transferRecord.setAmount(transferRequest.getAmount());
		transferRecord.setReason(transferRequest.getReason());
		transferRecord.setBy(userService.getUserByUsername(transferRequest.getBy()).get());	// not sure if relationship needed
														// between Transfer and Customer
		
		accountService.updateAccount(fromAccount);	// not sure if correct
		accountService.updateAccount(toAccount);	// not sure if correct
		
		transferRepository.save(transferRecord);
		
		return ResponseEntity.status(200).build();
	}
	
	/** NEEDS REVIEW **/
	@GetMapping("{username}/forgot/question/answer")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<?> forgotPassword(@Valid @PathVariable("username") String username, ForgotPasswordRequest forgotPasswordRequest) {
		User user = userService.getUserByUsername(username).get();
		
		if(user.getUsername().equals(forgotPasswordRequest.getUsername())
			&& user.getSecretQuestion().equals(forgotPasswordRequest.getSecretQuestion())
			&& user.getSecretAnswer().equals(forgotPasswordRequest.getSecretAnswer())) {
			
			return ResponseEntity.status(200).body("Details validated");
			
			
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
		
			return ResponseEntity.status(200).body("New password updated");
		} else {
			throw new OperationFailedException("Sorry password not updated");
		}
		
	}
	


	



}
