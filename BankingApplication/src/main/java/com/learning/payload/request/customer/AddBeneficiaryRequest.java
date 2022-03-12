package com.learning.payload.request.customer;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class AddBeneficiaryRequest {
	@NotNull
	private Integer accountNumber;
	@NotNull
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	@NotNull
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_NOT_APPROVED;
}
