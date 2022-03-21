package com.learning.payload.request.customer;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAccountRequest {
	@NotNull
	@JsonFormat(with = { Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, Feature.ACCEPT_CASE_INSENSITIVE_VALUES }) 
	private AccountType accountType;
	@NotNull
	private Double accountBalance;

	

	//@NotBlank
	//private String approvedStatus;

}
