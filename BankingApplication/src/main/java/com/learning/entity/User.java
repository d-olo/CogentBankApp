package com.learning.entity;

import java.sql.Date;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.learning.enums.EnabledStatus;
import com.learning.enums.ERole;

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
	private Set<ERole> roles;
	
	@OneToMany(mappedBy = "accountOwner",
			cascade = CascadeType.ALL,
			fetch = FetchType.LAZY)
	private Set<Account> accounts;
	
	@Enumerated(EnumType.STRING)
	private EnabledStatus enabledStatus;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateCreated;
	
}
