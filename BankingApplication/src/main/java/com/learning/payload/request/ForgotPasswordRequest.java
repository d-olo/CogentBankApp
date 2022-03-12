package com.learning.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String secretQuestion;
	@NotBlank
	private String secretAnswer;
}
