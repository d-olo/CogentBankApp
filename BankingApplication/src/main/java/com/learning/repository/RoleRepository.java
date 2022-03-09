package com.learning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.entity.Role;
import com.learning.enums.ERole;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	
	Optional<Role> findByRoleName(ERole roleName);

}
