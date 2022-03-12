package com.learning.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {

}
