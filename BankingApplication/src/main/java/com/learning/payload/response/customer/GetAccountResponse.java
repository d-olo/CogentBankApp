package com.learning.payload.response.customer;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class GetAccountResponse {
	private String accountType;
	private Double accountBalance;
	private ApprovedStatus approvedStatus;
	private Integer accountNumber;
}
