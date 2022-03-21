package com.learning.payload.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCustomerRequest {
	//@NotBlank
	//private Integer id;
	@NotBlank
	private String fullName;
	@NotBlank
	private String phone;
	@NotBlank
	private String pan;
	@NotBlank
	private String aadhar;
	@NotBlank
	private String secretQuestion;
	@NotBlank
	private String secretAnswer;

}
