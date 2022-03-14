package com.learning.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.learning.enums.ERole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Database entity which stores roles, in order to enable
 * many-to-many association with users.
 * @author Dionel Olo
 * @since March 7, 2022
 */
public class Role {

	@Id
	private Integer roleId;
	@NotNull  // must be not null value
	@Enumerated(EnumType.STRING)
	private ERole roleName;
}
