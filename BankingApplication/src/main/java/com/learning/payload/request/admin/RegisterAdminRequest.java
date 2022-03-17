package com.learning.payload.request.admin;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
/**
 * The request body for customer registration.
 * @author Dionel Olo
 * @since Mar 8, 2022
 */
public class RegisterAdminRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	
}
