package com.learning.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learning.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	public Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);

}
