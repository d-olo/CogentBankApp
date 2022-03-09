package com.learning.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.learning.payload.request.CustomerRegisterRequest;
import com.learning.payload.response.CustomerRegisterResponse;
import com.learning.repository.RoleRepository;
import com.learning.service.UserService;

@RestController
@RequestMapping("/customer")
/**
 * Handler for the customer API.
 * @author Dionel Olo
 * @since Mar 8, 2022
 */
public class CustomerController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@PostMapping(value = "/register")
	/**
	 * Registers a new customer.
	 * @param request A request entity containing customer information.
	 * @return An HTTP response containing the customer added to the database.
	 */
	public ResponseEntity<?> register
		(@Valid @RequestBody CustomerRegisterRequest request) {
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setFullName(request.getFullName());
		user.setPassword(request.getPassword());
		
		Set<Role> roles = new HashSet<Role>();
		Role customerRole = roleRepository.findByRoleName(ERole.ROLE_CUSTOMER)
				.orElseThrow(() -> 
					new EnumNotFoundException("Customer role not in database."));
		roles.add(customerRole);
		user.setRoles(roles);
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
}
