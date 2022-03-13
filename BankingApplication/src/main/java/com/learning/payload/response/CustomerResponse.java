package com.learning.payload.response;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
	
	@NotBlank
	private String username;
	@NotBlank
	private String fullname;
	// Added roles for test and future purpose
	@NotEmpty
	private Set<String> roles;
}
