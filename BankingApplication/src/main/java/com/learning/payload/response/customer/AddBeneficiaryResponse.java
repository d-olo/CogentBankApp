package com.learning.payload.response.customer;

import com.learning.entity.Beneficiary;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddBeneficiaryResponse
 *
 * @author bryan
 * @date Mar 8, 2022-4:52:25 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBeneficiaryResponse {
	private Integer accountNumber;
	private AccountType accountType;
	private ApprovedStatus approved;
	
	
}
