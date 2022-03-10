package com.learning.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;
import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
/**
 * Database entity which stores account data.
 * @author Dionel Olo
 * @since March 7, 2022
 */
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer accountId;
	private Integer accountNumber;
	private Double accountBalance;
	
	private EnabledStatus enabledStatus;
	private ApprovedStatus approvedStatus;
	
	@ManyToOne
	private User accountOwner;
	@Enumerated(EnumType.STRING)
	private AccountType accountType;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateCreated;
	
	@OneToMany(mappedBy = "account",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	private Set<Transaction> transactions;
	
	
	
}
