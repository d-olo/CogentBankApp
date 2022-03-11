package com.learning.payload.response.customer;

import com.learning.enums.ActiveStatus;

import lombok.Data;

@Data
public class BeneficiaryListResponse {
	private Integer beneficiaryAccountNumber;
	private String beneficiaryName;
	private ActiveStatus activeStatus;
}
