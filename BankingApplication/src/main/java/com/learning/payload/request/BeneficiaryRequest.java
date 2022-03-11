package com.learning.payload.request;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class BeneficiaryRequest {
	private Integer accountNumber;
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_NOT_APPROVED;
}
