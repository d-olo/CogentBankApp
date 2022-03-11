package com.learning.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * The database entity which stores user data.
 * @author Dionel Olo
 * @since March 7, 2022
 */
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer id;
	private String username;
	private String fullName;
	private String password;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles",
	joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;
	
	@OneToMany(mappedBy = "accountOwner",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	private Set<Account> accounts;
	
	@Enumerated(EnumType.STRING)
	private EnabledStatus enabledStatus;
	
	@OneToMany(mappedBy = "mainUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Beneficiary> beneficiaries;
	
	private String phone;
	private String pan;
	private String aadhar;
	private String secretQuestion;
	private String secretAnswer;
	private String panImage;
	private String aadharImage;
	
}
