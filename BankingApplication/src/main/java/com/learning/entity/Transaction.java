package com.learning.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learning.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
/**
 * Database entity which stores an account's transactions.
 * @author Dionel Olo
 * @since March 7, 2022
 */
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private Date date;
	private String reference;
	private Double amount;
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@ManyToOne
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Account account;

	@JsonBackReference
	public Account getAccount() {
		return account;
	}

}
