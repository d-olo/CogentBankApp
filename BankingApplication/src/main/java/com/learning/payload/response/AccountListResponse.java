package com.learning.payload.response;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class AccountListResponse {
	private AccountType accountType;
	private Double accountBalance;
	private ApprovedStatus approvedStatus;
	private Integer accountNumber;
}
