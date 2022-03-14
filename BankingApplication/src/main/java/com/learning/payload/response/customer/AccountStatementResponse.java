package com.learning.payload.response.customer;

import java.util.List;
import java.util.Set;

import com.learning.entity.Transaction;

import lombok.Data;

@Data
public class AccountStatementResponse {
	private Integer accountNumber;
	private String customerName;
	private Double accountBalance;
//	private List<Transaction> transaction;  // Need to test
	private Set<Transaction> transactions; 
}
