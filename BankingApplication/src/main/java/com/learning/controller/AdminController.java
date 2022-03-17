package com.learning.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.entity.Role;
import com.learning.entity.User;
import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;
import com.learning.exception.EnumNotFoundException;
import com.learning.exception.IdNotFoundException;
import com.learning.exception.UnauthorizedAccessException;
import com.learning.payload.request.AuthenticationRequest;
import com.learning.payload.request.admin.CreateStaffRequest;
import com.learning.payload.request.admin.SetStaffEnabledRequest;
import com.learning.payload.response.JwtResponse;
import com.learning.repo.RoleRepository;
import com.learning.security.jwt.JwtUtils;
import com.learning.security.service.UserDetailsImpl;
import com.learning.service.impl.UserServiceImpl;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserServiceImpl userService ;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> signin(@Valid @RequestBody AuthenticationRequest signinRequest) {
	
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = jwtUtils.generateToken(authentication);
		// get user data/ principal
	
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetailsImpl.getAuthorities().stream().map(e -> e.getAuthority())
				.collect(Collectors.toList());
		// return new token
		boolean isadmin = false;
		for( int i =0 ; i < roles.size() ; i++) {
			if (roles.get(i).equals(ERole.ROLE_ADMIN.name())) {
				isadmin= true;
			}
		}
		if(!isadmin) {
			throw new UnauthorizedAccessException("unauthorized access");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(new JwtResponse(jwt, userDetailsImpl.getId(), userDetailsImpl.getUsername(), jwt, roles));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/staff")
	public ResponseEntity<?> createStaff(@Valid @RequestBody CreateStaffRequest request) {
		
		User staff = new User();
		staff.setFullName(request.getStaffFullName());
		staff.setUsername(request.getStaffUserName());
		staff.setPassword(passwordEncoder.encode(request.getStaffPassword()));
		staff.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		Role staffRole = roleRepository.findByRoleName(ERole.ROLE_STAFF)
				.orElseThrow(() -> 
					new EnumNotFoundException("Staff role not in database."));
		Set<Role> roles = new HashSet<>();
		roles.add(staffRole);
		staff.setRoles(roles);
		userService.addUser(staff);
		return ResponseEntity.status(HttpStatus.OK).body("staff added");
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/staff")
	public ResponseEntity<?> getAllStaff() {
		List<User> staff = new ArrayList<>();
		staff = userService.findAllByRoleName(ERole.ROLE_STAFF);
		return ResponseEntity.status(HttpStatus.OK).body(staff);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/staff")
	public ResponseEntity<?> setStaffEnabled(@Valid @RequestBody SetStaffEnabledRequest request) {
		User staff = (User) userService.getUserById(request.getStaffId()).orElseThrow(
					()-> new IdNotFoundException("Staff status not changed")
				);

		Map<String, String> response = new LinkedHashMap<>();
		response.put("staffId", " : " + request.getStaffId());
		if(request.getEnabledStatus() == EnabledStatus.STATUS_ENABLED) {
			response.put("status", " : " + EnabledStatus.STATUS_DISABLED.name());
			staff.setEnabledStatus(EnabledStatus.STATUS_DISABLED);
		}	
		else {
			response.put("status", " : " + EnabledStatus.STATUS_ENABLED.name());
			staff.setEnabledStatus(EnabledStatus.STATUS_ENABLED);
		}
		
		userService.updateUser(staff);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
