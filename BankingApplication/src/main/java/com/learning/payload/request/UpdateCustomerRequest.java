package com.learning.payload.request;

import lombok.Data;

@Data
public class UpdateCustomerRequest {
	private Integer id;
	private String fullName;
	private String phone;
	private String pan;
	private String aadhar;
	private String secretQuestion;
	private String secretAnswer;
	private String panImage;
	private String aadharImage;
}
