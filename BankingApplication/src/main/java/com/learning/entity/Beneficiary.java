package com.learning.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.learning.enums.AccountType;
import com.learning.enums.ActiveStatus;
import com.learning.enums.ApprovedStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Beneficiary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer beneficiaryId;
	private Integer accountNumber;
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_NOT_APPROVED;
	private ActiveStatus activeStatus = ActiveStatus.STATUS_ACTIVE;
	
	@ManyToOne
	private User mainUser;
}
