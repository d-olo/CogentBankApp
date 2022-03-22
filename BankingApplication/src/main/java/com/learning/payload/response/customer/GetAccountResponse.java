package com.learning.payload.response.customer;

import lombok.Data;

@Data
public class GetAccountResponse {
	private String accountType;
	private Double accountBalance;
	private String enableStatus;
	private Integer accountNumber;
}
