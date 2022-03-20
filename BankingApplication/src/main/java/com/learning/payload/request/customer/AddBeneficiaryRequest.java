package com.learning.payload.request.customer;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class AddBeneficiaryRequest {
	@NotNull
	private Integer accountNumber;
	@NotNull
	@JsonFormat(with = { Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, Feature.ACCEPT_CASE_INSENSITIVE_VALUES }) 
	private AccountType accountType;
	@NotNull
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_NOT_APPROVED;
}
