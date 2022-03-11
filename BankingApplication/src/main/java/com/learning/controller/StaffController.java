package com.learning.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.learning.payload.request.customer.AuthenticationRequest;
import com.learning.payload.request.staff.ApproveAccountRequest;
import com.learning.payload.request.staff.ApproveBeneficiaryRequest;
import com.learning.payload.request.staff.UpdateCustomerStatusRequest;
import com.learning.payload.response.JwtResponse;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;

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
	
	@GetMapping("/account/:{accountNum}")
	public ResponseEntity<?> getAccountByAccNum
		(@PathVariable("accountNum") Integer accountNum) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@GetMapping("/beneficiary")
	public ResponseEntity<?> getUnapprovedBeneficiaries() {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@PutMapping("/beneficiary")
	public ResponseEntity<?> approveBeneficiary
		(@Valid @RequestBody ApproveBeneficiaryRequest request) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@GetMapping("/accounts/approve")
	public ResponseEntity<?> getUnapprovedAccounts() {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@PutMapping("/accounts/approve")
	public ResponseEntity<?> approveAccount
		(@Valid @RequestBody ApproveAccountRequest request) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@GetMapping("/customer")
	public ResponseEntity<?> getAllCustomers() {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@PutMapping("/customer")
	public ResponseEntity<?> updateCustomerStatus
		(@Valid @RequestBody UpdateCustomerStatusRequest request) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@GetMapping("/customer/:{id}")
	public ResponseEntity<?> getCustomerById(@PathVariable Integer id) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	@PutMapping("/transfer")
	public ResponseEntity<?> transfer
	(@Valid @RequestBody UpdateCustomerStatusRequest request) {
		//TODO Placeholder method for framework purposes.
		return null;
	}
	
	

	
}
