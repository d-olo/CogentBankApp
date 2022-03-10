package com.learning.payload.response;

import java.util.Set;

import com.learning.entity.Transaction;
import com.learning.enums.EnabledStatus;

import lombok.Data;

@Data
public class AccountByIdResponse {
	private Integer accountNumber;
	private String accountType;
	private Double accountBalance;
	private EnabledStatus enabledStatus;
	private Set<Transaction> transactions;
	
}
