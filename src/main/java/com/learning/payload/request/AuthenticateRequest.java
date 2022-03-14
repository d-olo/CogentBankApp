package com.learning.payload.request;
//Use Case: POST /api/customer/authenticate
//Use Case: POST /api/staff/authenticate
//Use Case: POST /api/admin/authenticate
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
}
