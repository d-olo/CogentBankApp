package com.learning.payload.response;

import java.util.Date;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class AccountResponse {
	private AccountType accountType;
	private Double accountBalance;
	private ApprovedStatus approvedStatus;
	private Integer accountNumber;
	private Date dateCreated;
	private Integer customerId;
	
}
