package com.learning.service;

import com.learning.entity.Beneficiary;

public interface BeneficiaryService {
	public void addBeneficiary(Beneficiary beneficiary);
	public Beneficiary findBeneficiaryById(Integer beneficiaryId);
	public void deleteBeneficiary(Integer beneficiaryId);
	public boolean existsById(Integer id);
}
