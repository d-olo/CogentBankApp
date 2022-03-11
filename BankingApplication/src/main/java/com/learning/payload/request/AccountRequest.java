package com.learning.payload.request;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	@NotNull
	private Double accountBalance;
	@NotBlank
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_NOT_APPROVED;
}
