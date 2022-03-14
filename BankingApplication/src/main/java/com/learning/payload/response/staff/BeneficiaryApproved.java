package com.learning.payload.response.staff;

import java.util.Date;

import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryApproved {
	
	private Integer beneficiaryId;
	private Integer beneficiaryAcNo;
	private Date	beneficiaryAddedDate;
	private ApprovedStatus approvedStatus;
}
