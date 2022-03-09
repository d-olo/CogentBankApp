package com.learning.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.learning.enums.ERole;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
/**
 * Database entity which stores roles, in order to enable
 * many-to-many association with users.
 * @author Dionel Olo
 * @since March 7, 2022
 */
public class Role {

	@Id
	private Integer roleId;
	@Enumerated(EnumType.STRING)
	private ERole roleName;
}
