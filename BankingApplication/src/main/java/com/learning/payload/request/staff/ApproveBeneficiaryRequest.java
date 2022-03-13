package com.learning.payload.request.staff;

import java.sql.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
@Validated
public class ApproveBeneficiaryRequest {
	  @NotNull
	  private Integer fromCustomer;
	  @NotNull
	  private Integer beneficiaryAcNo;
	  @NotNull
	  private Date beneficiaryAddedDate;
	  @NotBlank
	  private ApprovedStatus approvedStatus;

}
