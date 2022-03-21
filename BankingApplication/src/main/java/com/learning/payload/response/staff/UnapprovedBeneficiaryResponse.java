package com.learning.payload.response.staff;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnapprovedBeneficiaryResponse {
	
	private Integer fromCustomer;
	private Integer beneficiaryAccountNumber;
	private Date	dateAdded;
	@Enumerated(EnumType.STRING)
	private ApprovedStatus isApproved;
}
