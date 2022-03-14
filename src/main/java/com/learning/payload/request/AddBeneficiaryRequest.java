package com.learning.payload.request;
//Use Case POST /api/customer/:customerID/beneficiary
import com.learning.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBeneficiaryRequest {
	private long accountNumber;
	private AccountType accountType;
	private String approved = "no";
}
