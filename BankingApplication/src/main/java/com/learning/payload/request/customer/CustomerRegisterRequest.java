package com.learning.payload.request.customer;

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
public class CustomerRegisterRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String fullName;
	@NotBlank
	private String password;
	
}
