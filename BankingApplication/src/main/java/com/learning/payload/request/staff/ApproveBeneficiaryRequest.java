package com.learning.payload.request.staff;

import java.sql.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
@Validated
public class ApproveBeneficiaryRequest {
	  @NotNull
	  private Integer customerId;
	  @NotNull
	  private Integer beneficiaryAccountNumber;
	  @NotNull
	  @Enumerated(EnumType.STRING)
	  private ApprovedStatus isApproved;

}
