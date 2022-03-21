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
public class UnapprovedAccountResponse {
	
	private String accountType;
	private String customerName;
	private Integer accountNumber;
	private Date	dateCreated;
	@Enumerated(EnumType.STRING)
	private ApprovedStatus approved;
	
}
