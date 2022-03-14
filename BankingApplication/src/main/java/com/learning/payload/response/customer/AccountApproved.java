package com.learning.payload.response.customer;

import java.util.Date;

import com.learning.entity.User;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountApproved {
	
	private String accountType;
	private String customerName;
	private Integer accountNumber;
	private Date	dateCreated;
	private ApprovedStatus approvedStatus;
	
}
