package com.learning.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.learning.enums.AccountType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	
	private Integer id;
	private Date date;
	private String reference;
	private Long amount;
	private Account account;
	private AccountType accountType;

}
