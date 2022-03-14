package com.learning.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer extends User {

	private String phone;
	
	private String pan;
	
	private String aadhar;
	
	private String secretQuestion;

	private String secretAnswer;
	
	private LocalDate createdDate;
	
	private EnabledStatus status = EnabledStatus.ENABLED;
	
	@OneToMany
	private Set<Account> accounts;
	
	@OneToMany
	private Set<Beneficiary> beneficiaries;
}
