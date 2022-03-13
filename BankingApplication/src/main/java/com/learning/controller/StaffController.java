package com.learning.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.learning.entity.Account;
import com.learning.entity.Beneficiary;
import com.learning.entity.Transfer;
import com.learning.entity.User;
import com.learning.enums.ApprovedStatus;
import com.learning.enums.BeneficiaryStatus;
import com.learning.exception.NoDataFoundException;
import com.learning.payload.request.customer.AuthenticationRequest;
import com.learning.payload.request.staff.ApproveAccountRequest;
import com.learning.payload.request.staff.ApproveBeneficiaryRequest;
import com.learning.payload.request.staff.TransferRequest;
import com.learning.payload.request.staff.UpdateCustomerStatusRequest;
import com.learning.payload.response.CustomerResponse;
import com.learning.payload.response.JwtResponse;
import com.learning.payload.response.customer.AccountApproved;
import com.learning.payload.response.customer.AccountByIdResponse;
import com.learning.payload.response.customer.AccountStatementResponse;
import com.learning.payload.response.customer.CustomerById;
import com.learning.payload.response.staff.BeneficiaryApproved;
import com.learning.repo.AccountRepository;
import com.learning.repo.BeneficiaryRepository;
import com.learning.repo.TransferRepository;
import com.learning.repo.UserRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.AccountService;
import com.learning.service.StaffService;

@RestController
@RequestMapping("/staff")
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
	UserRepository userRepository;
	
	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	BeneficiaryRepository beneficiaryRepository;
	
	@Autowired
	TransferRepository transferRepository;
	
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
		
		// Builds a response from the JWT.
		return ResponseEntity.ok(new JwtResponse(
				jwt, 
				userDetailsImpl.getId(), 
				userDetailsImpl.getUsername(), 
				userDetailsImpl.getFullName(), 
				roles));
	
	}
	
	@GetMapping(value = "/account/{accountNum}")
	public ResponseEntity<?> getAccountByAccNum
		(@PathVariable("accountNum") Integer accountNum) {
		Account account = accountService
				.findByAccountNumber(accountNum)
				.orElseThrow(() -> new NoDataFoundException("Sorry, Account Not Found"));
		
		AccountStatementResponse accountResponse = new AccountStatementResponse();
		accountResponse.setAccountNumber(account.getAccountNumber());
//		accountResponse.setCustomerName(account.getAccountOwner().getFullName());
		accountResponse.setCustomerName(userRepository
				.findById(account.getAccountOwner().getId())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found")).getFullName());
		accountResponse.setAccountBalance(account.getAccountBalance());
		accountResponse.setTransactions(account.getTransactions());
		return ResponseEntity.status(200).body(accountResponse);
	}
	
	@GetMapping("/beneficiary")
	public ResponseEntity<?> getUnapprovedBeneficiaries() {
		List<Beneficiary> beneficiaries = beneficiaryRepository.findAllByApprovedStatus(BeneficiaryStatus.STATUS_NOT_APPROVED);
		List<BeneficiaryApproved> unapprovedLists = new ArrayList<>();
		for (Beneficiary beneficiary : beneficiaries) {
			BeneficiaryApproved unapprovedList = new BeneficiaryApproved();
			unapprovedList.setBeneficiaryId(beneficiary.getBeneficiaryId());
			unapprovedList.setBeneficiaryAcNo(beneficiary.getAccountNumber());
			unapprovedList.setBeneficiaryAddedDate(beneficiary.getBeneficiaryAddedDate());
			unapprovedList.setApprovedStatus(beneficiary.getApprovedStatus());
			
			unapprovedLists.add(unapprovedList);
		}
		
		return ResponseEntity.status(200).body(unapprovedLists);
	}
	
	@PutMapping("/beneficiary")
	public ResponseEntity<?> approveBeneficiary
		(@Valid @RequestBody ApproveBeneficiaryRequest request) {
		User customer = userRepository.findById(request.getFromCustomer())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Beneficiary Not Approved"));
		BeneficiaryApproved beneficiaryResponse = new BeneficiaryApproved();
		for (Beneficiary beneficiary : customer.getBeneficiaries()) {
			if (beneficiary.getAccountNumber() == request.getBeneficiaryAcNo()) {
				beneficiary.setApprovedStatus(request.getApprovedStatus());

				beneficiaryResponse.setBeneficiaryId(beneficiary.getBeneficiaryId());
				beneficiaryResponse.setBeneficiaryAcNo(beneficiary.getAccountNumber());
				beneficiaryResponse.setBeneficiaryAddedDate(beneficiary.getBeneficiaryAddedDate());
				beneficiaryResponse.setApprovedStatus(beneficiary.getApprovedStatus());
			}
		}
		userRepository.save(customer);
		
		return ResponseEntity.status(200).body(beneficiaryResponse);
	}
	
	@GetMapping("/accounts/approve")
	public ResponseEntity<?> getUnapprovedAccounts() {
		List<Account> accounts	= accountRepository
				.findAllByApprovedStatus(ApprovedStatus.STATUS_NOT_APPROVED);
		List<AccountApproved> unapprovedLists = new ArrayList<>();
		for (Account account : accounts) {
			AccountApproved unapprovedList = new AccountApproved();
			unapprovedList.setAccountType(account.getAccountType().toString());
//			unapprovedList.setCustomerName(account.getAccountOwner().getFullName());
			unapprovedList.setCustomerName(userRepository
					.findById(account.getAccountOwner().getId())
					.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found"))
					.getFullName());
			unapprovedList.setAccountNumber(account.getAccountNumber());
			unapprovedList.setDateCreated(account.getDateCreated());
			unapprovedList.setApprovedStatus(account.getApprovedStatus());
			
			unapprovedLists.add(unapprovedList);
		}
		return ResponseEntity.status(200).body(unapprovedLists);
	}
	
	@PutMapping("/accounts/approve")
	public ResponseEntity<?> approveAccount
		(@Valid @RequestBody ApproveAccountRequest request) {
		Account account = accountRepository.getById(request.getAccNum());
		account.setApprovedStatus(request.getApproved());
		accountRepository.save(account);
		ApproveAccountRequest accountRespone = new ApproveAccountRequest();
		accountRespone.setAccType(account.getAccountType().toString());
		accountRespone.setCustomerName(account.getAccountOwner().getFullName());
//		accountRespone.setCustomerName(userRepository
//				.findById(account.getAccountOwner().getId())
//				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found"))
//				.getFullName());
		accountRespone.setAccNum(account.getAccountNumber());
		accountRespone.setDateCreated(account.getDateCreated());
		accountRespone.setApproved(account.getApprovedStatus());
		accountRespone.setStaffUsername(account.getAccountOwner().getUsername());
	
		return ResponseEntity.status(200).body(accountRespone);
	}
	
	@GetMapping("/customer")
	public ResponseEntity<?> getAllCustomers() {
		List<Account> customers = accountRepository.findAll();
		List<CustomerById> customersResponse = new ArrayList<>();
		for(Account customer : customers) {
			CustomerById customerInfo = new CustomerById();
			customerInfo.setCustomerId(customer.getAccountOwner().getId());
			customerInfo.setCustomerName(customer.getAccountOwner().getFullName());
			customerInfo.setStatus(customer.getEnabledStatus());
			customersResponse.add(customerInfo);
		}
		return ResponseEntity.status(200).body(customersResponse);
	}
	
	@PutMapping("/customer")
	public ResponseEntity<?> updateCustomerStatus
		(@Valid @RequestBody UpdateCustomerStatusRequest request) {
		Account customer = accountRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Status Not Changed"));
		customer.setEnabledStatus(request.getStatus());
		accountRepository.save(customer);
		return ResponseEntity.status(200).body("Customer Status Changed");
	}
	
	@GetMapping("/customer/:{id}")
	public ResponseEntity<?> getCustomerById(@PathVariable Integer customerId) {
		Account customer = accountRepository.findById(customerId)
				.orElseThrow(() -> new NoDataFoundException("Sorry, Customer Not Found"));
		CustomerById customerResponse = new CustomerById();
		customerResponse.setCustomerId(customer.getAccountOwner().getId());
		customerResponse.setCustomerName(customer.getAccountOwner().getFullName());
		customerResponse.setStatus(customer.getEnabledStatus());
		customerResponse.setCreated(customer.getDateCreated());
		
		return ResponseEntity.status(200).body(customerResponse);
	}
	
	@PutMapping("/transfer")
	public ResponseEntity<?> transfer
	(@Valid @RequestBody TransferRequest request) {
		Account fromAccount = accountService.findByAccountNumber(request.getFromAccNumber())
				.orElseThrow(() -> new NoDataFoundException("From Account Number Not Vaild"));
		Account toAccount = accountService.findByAccountNumber(request.getToAccNumber())
				.orElseThrow(() -> new NoDataFoundException("To Account Number Not Vaild"));
		
		fromAccount.setAccountBalance(fromAccount.getAccountBalance() - request.getAmount());
		toAccount.setAccountBalance(toAccount.getAccountBalance() + request.getAmount());
		
		Transfer transferRequest = new Transfer();
		transferRequest.setFromAccNumber(fromAccount.getAccountNumber());
		transferRequest.setToAccNumber(toAccount.getAccountNumber());
		transferRequest.setAmount(request.getAmount());
		transferRequest.setReason(request.getReason());
		transferRequest.setRole(request.getRole());
		
		accountService.updateAccount(fromAccount);
		accountService.updateAccount(toAccount);
		
		transferRepository.save(transferRequest);

		return ResponseEntity.status(200).body("Transfer Complete");
	}
	
	

	
}
