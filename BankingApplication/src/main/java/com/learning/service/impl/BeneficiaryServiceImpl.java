package com.learning.service.impl;

import org.springframework.stereotype.Service;

import com.learning.entity.Beneficiary;
import com.learning.repo.BeneficiaryRepository;
import com.learning.service.BeneficiaryService;

@Service
public class BeneficiaryServiceImpl implements BeneficiaryService {

	private BeneficiaryRepository beneficiaryRepository;
	
	@Override
	public void addBeneficiary(Beneficiary beneficiary) {
		beneficiaryRepository.save(beneficiary);

	}

	@Override
	public Beneficiary findBeneficiaryById(Integer beneficiaryId) {
		return beneficiaryRepository.getById(beneficiaryId);
	}

	@Override
	public void deleteBeneficiary(Integer beneficiaryId) {
		beneficiaryRepository.deleteById(beneficiaryId);
	}

	@Override
	public boolean existsById(Integer beneficiaryId) {
		return beneficiaryRepository.existsById(beneficiaryId);
	}

}
