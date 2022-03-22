package com.learning.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.learning.enums.ApprovedStatus;
import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;
import com.learning.enums.TransactionType;
import com.learning.exception.AccountDisabledException;
import com.learning.exception.NoDataFoundException;
import com.learning.payload.request.AuthenticationRequest;
import com.learning.payload.request.staff.ApproveAccountRequest;
import com.learning.payload.request.staff.ApproveBeneficiaryRequest;
import com.learning.payload.request.staff.TransferRequest;
import com.learning.payload.request.staff.UpdateCustomerStatusRequest;
import com.learning.payload.response.JsonMessageResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.TransferResponse;
import com.learning.payload.response.customer.AccountStatementResponse;
import com.learning.payload.response.customer.CustomerById;
import com.learning.payload.response.staff.ApproveAccountResponse;
import com.learning.payload.response.staff.GetAllCustomersResponse;
import com.learning.payload.response.staff.UnapprovedAccountResponse;
import com.learning.payload.response.staff.UnapprovedBeneficiaryResponse;
import com.learning.repo.AccountRepository;
import com.learning.repo.BeneficiaryRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.UserService;

@RestController
@RequestMapping("/staff")
@CrossOrigin(origins = "http://localhost:4200")
/**
 * Handler for the staff API.
 * @author Dionel Olo
 * @since Mar 10, 2022
 */
public class StaffController {
	
	@Autowired
	// Manages authentication.
	AuthenticationManager authenticationManager;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	BeneficiaryRepository beneficiaryRepository;
	
	@Autowired
	// Utilities for JSON web tokens.
	JwtUtils jwtUtils;
	
	//TODO necessary service access
	
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
		
		// If the user is not a staff member, return a 403 error.
		if(!roles.contains("ROLE_STAFF"))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		// Builds a response from the JWT.
		return ResponseEntity.ok(new JwtResponse(
				jwt, 
				userDetailsImpl.getId(), 
				userDetailsImpl.getUsername(), 
				userDetailsImpl.getFullName(), userDetailsImpl.getStatus(),
				roles));
	}
	
	@GetMapping(value = "/account/{accountNum}")
	public ResponseEntity<?> getAccountByAccNum
		(@PathVariable("accountNum") Integer accountNum) {
		Account account = accountService
				.findByAccountId(accountNum)
				.orElseThrow(() -> new NoDataFoundException("Sorry, Account Not Found"));
		
		AccountStatementResponse accountResponse = new AccountStatementResponse();
		accountResponse.setAccountNumber(account.getAccountId());
		accountResponse.setCustomerName(userService
				.getUserById(account.getAccountOwner().getId())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found")).getFullName());
		accountResponse.setAccountBalance(account.getAccountBalance());
		accountResponse.setTransactions(account.getTransactions());
		return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
	}
	
	@GetMapping("/beneficiary")
	public ResponseEntity<?> getUnapprovedBeneficiaries() {
		List<Beneficiary> beneficiaries = beneficiaryRepository.findAll();
		List<UnapprovedBeneficiaryResponse> unapprovedLists = new ArrayList<UnapprovedBeneficiaryResponse>();
		for (Beneficiary beneficiary : beneficiaries) {
			if(beneficiary.getApprovedStatus() == ApprovedStatus.STATUS_NOT_APPROVED) {
				UnapprovedBeneficiaryResponse unapprovedList = new UnapprovedBeneficiaryResponse();
				unapprovedList.setFromCustomer(beneficiary.getMainUser().getId());
				unapprovedList.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
				unapprovedList.setDateAdded(beneficiary.getBeneficiaryAddedDate());
				unapprovedList.setIsApproved(beneficiary.getApprovedStatus());
				
				unapprovedLists.add(unapprovedList);
			}
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(unapprovedLists);
	}
	
	@PutMapping("/beneficiary")
	public ResponseEntity<?> approveBeneficiary
		(@Valid @RequestBody ApproveBeneficiaryRequest request) {
		User customer = userService.getUserById(request.getCustomerId())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Beneficiary Not Approved"));
		UnapprovedBeneficiaryResponse beneficiaryResponse = new UnapprovedBeneficiaryResponse();
		for (Beneficiary beneficiary : customer.getBeneficiaries()) {
			if (beneficiary.getAccountNumber() == request.getBeneficiaryAccountNumber()) {
				beneficiary.setApprovedStatus(request.getIsApproved());
				beneficiaryRepository.save(beneficiary);
				
				beneficiaryResponse.setFromCustomer(beneficiary.getBeneficiaryId());
				beneficiaryResponse.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
				beneficiaryResponse.setDateAdded(beneficiary.getBeneficiaryAddedDate());
				beneficiaryResponse.setIsApproved(beneficiary.getApprovedStatus());
			}
		}
		userService.updateUser(customer);
		
		return ResponseEntity.status(HttpStatus.OK).body(beneficiaryResponse);
	}
	
	@GetMapping("/accounts/approve")
	public ResponseEntity<?> getUnapprovedAccounts() {
		List<Account> accounts	= accountRepository.findAll();
		List<UnapprovedAccountResponse> unapprovedList = new ArrayList<UnapprovedAccountResponse>();
		for (Account account : accounts) {
			if(account.getApprovedStatus() == ApprovedStatus.STATUS_NOT_APPROVED) {
				UnapprovedAccountResponse unapprovedAccount = new UnapprovedAccountResponse();
				unapprovedAccount.setAccountType(account.getAccountType().toString());
				unapprovedAccount.setCustomerName(userService
						.getUserById(account.getAccountOwner().getId())
						.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found"))
						.getFullName());
				unapprovedAccount.setAccountNumber(account.getAccountId());
				unapprovedAccount.setDateCreated(account.getDateCreated());
				unapprovedAccount.setApproved(account.getApprovedStatus());
				
				unapprovedList.add(unapprovedAccount);
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(unapprovedList);
	}
	
	@PutMapping("/accounts/approve")
	public ResponseEntity<?> approveAccount
		(@Valid @RequestBody ApproveAccountRequest request) {
		Account account = accountRepository.getById(request.getAccountNumber());
		account.setApprovedStatus(request.getApproved());
		if(request.getApproved() == ApprovedStatus.STATUS_APPROVED) {
			account.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		}
		accountRepository.save(account);
		ApproveAccountResponse accountResponse = new ApproveAccountResponse();
		accountResponse.setAccType(account.getAccountType().toString());
		accountResponse.setCustomerName(account.getAccountOwner().getFullName());
		accountResponse.setAccNum(account.getAccountId());
		accountResponse.setDateCreated(account.getDateCreated());
		accountResponse.setApproved(account.getApprovedStatus());
		accountResponse.setStaffUsername(request.getStaffUsername());
	
		return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
	}
	
	@GetMapping("/customer")
	public ResponseEntity<?> getAllCustomers() {
		List<User> customers = userService.getAllUsers();
		List<GetAllCustomersResponse> customersResponse = new ArrayList<>();
		
		customers.forEach(e -> {
			boolean isCustomer = false;
			for(Role role: e.getRoles()) {
				if (role.getRoleName() == ERole.ROLE_CUSTOMER) {
					isCustomer = true;
					break;
				}
			}
			
			if(isCustomer) {
				GetAllCustomersResponse customerInfo = new GetAllCustomersResponse();
				customerInfo.setCustomerId(e.getId());
				customerInfo.setCustomerName(e.getFullName());
				customerInfo.setStatus(e.getEnabledStatus());
				customersResponse.add(customerInfo);
			}
		});
		

		return ResponseEntity.status(HttpStatus.OK).body(customersResponse);
	}
	
	@PutMapping("/customer")
	public ResponseEntity<?> updateCustomerStatus
		(@Valid @RequestBody UpdateCustomerStatusRequest request) {
		User customer = userService.getUserById(request.getCustomerId())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Status Not Changed"));
		customer.setEnabledStatus(request.getStatus());
		userService.updateUser(customer);
		
		JsonMessageResponse response = new JsonMessageResponse("Customer status changed.");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<?> getCustomerById(@PathVariable Integer customerId) {
		Account customer = accountRepository.findById(customerId)
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found"));
		CustomerById customerResponse = new CustomerById();
		customerResponse.setCustomerId(customer.getAccountOwner().getId());
		customerResponse.setCustomerName(customer.getAccountOwner().getFullName());
		customerResponse.setStatus(customer.getEnabledStatus());
		customerResponse.setCreated(customer.getDateCreated());
		
		return ResponseEntity.status(HttpStatus.OK).body(customerResponse);
	}
	
	@PutMapping("/transfer")
	public ResponseEntity<?> transfer
	(@Valid @RequestBody TransferRequest transferRequest) {
		Account fromAccount = accountService.findByAccountId(transferRequest.getFromAccNumber())
				.orElseThrow(() -> new NoDataFoundException("Sending account number not valid"));
		Account toAccount = accountService.findByAccountId(transferRequest.getToAccNumber())
				.orElseThrow(() -> new NoDataFoundException("Receiving account number not valid"));
		
		if(fromAccount.getEnabledStatus() == EnabledStatus.STATUS_DISABLED) {
			throw new AccountDisabledException(
					"Account " + fromAccount.getAccountId() + 
					" is disabled. Please contact a staff member.");
		}
		
		if(toAccount.getEnabledStatus() == EnabledStatus.STATUS_DISABLED) {
			throw new AccountDisabledException(
					"Account " + toAccount.getAccountId() +
					" is disabled. Please contact a staff member.");
		}
		
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - transferRequest.getAmount());
		toAccount.setAccountBalance(toAccount.getAccountBalance() + transferRequest.getAmount());
		
		LocalDateTime now = LocalDateTime.now(); 
		
		Transaction fromTransaction = new Transaction();
		fromTransaction.setReference(transferRequest.getReason());
		fromTransaction.setDate(Date.valueOf(now.toLocalDate()));
		fromTransaction.setAccount(fromAccount);
		fromTransaction.setAmount(-transferRequest.getAmount());
		fromTransaction.setTransactionType(TransactionType.DEBIT);
		
		fromAccount.getTransactions().add(fromTransaction);
		
		Transaction toTransaction = new Transaction();
		toTransaction.setReference(transferRequest.getReason());
		toTransaction.setDate(Date.valueOf(now.toLocalDate()));
		toTransaction.setAccount(toAccount);
		toTransaction.setAmount(transferRequest.getAmount());
		toTransaction.setTransactionType(TransactionType.DEBIT);
		
		toAccount.getTransactions().add(toTransaction);
		
		TransferResponse transferResponse = new TransferResponse();
		transferResponse.setFromAccNumber(fromAccount.getAccountId());
		transferResponse.setToAccNumber(toAccount.getAccountId());
		transferResponse.setAmount(transferRequest.getAmount());
		transferResponse.setReason(transferRequest.getReason());
		transferResponse.setBy(transferRequest.getBy());		
		accountService.updateAccount(fromAccount);
		accountService.updateAccount(toAccount);
		
		return ResponseEntity.status(HttpStatus.OK).body(transferResponse);
	}
	
	

	
}
