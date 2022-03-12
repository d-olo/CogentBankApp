package com.learning.payload.response;

import lombok.Data;

@Data
public class GetCustomerResponse {
	private String username;
	private String fullName;
	private String phone;
	private String pan;
	private String aadhar;
}
