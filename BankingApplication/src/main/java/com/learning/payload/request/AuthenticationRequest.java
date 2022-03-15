package com.learning.payload.request;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
/**
 * Parsed JSON request body for user login.
 * @author Dionel Olo
 * @since Mar 9, 2022
 */
public class AuthenticationRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String password;
}
