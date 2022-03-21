package com.learning.payload.response.customer;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;
import com.learning.enums.EnabledStatus;

import lombok.Data;

@Data
public class GetAccountResponse {
	private AccountType accountType;
	private Double accountBalance;
	private EnabledStatus enableStatus;
	private Integer accountNumber;
}
