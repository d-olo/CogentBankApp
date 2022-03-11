package com.learning.payload.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
	private String username;
	private String secretQuestion;
	private String secretAnswer;
}
