package com.learning.payload.request.customer;


import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class ForgotPasswordRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String secretQuestion;
	@NotBlank
	private String secretAnswer;
}
