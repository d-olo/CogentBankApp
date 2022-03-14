package com.learning.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learning.entity.Beneficiary;
import com.learning.enums.BeneficiaryStatus;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Integer> {
	public List<Beneficiary> findAllByApprovedStatus(BeneficiaryStatus beneficiaryStatus);
}
