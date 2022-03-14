package com.learning.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.learning.entity.Account;
import com.learning.entity.Customer;
import com.learning.exception.NoDataFoundException;
import com.learning.payload.request.AddBeneficiaryRequest;
import com.learning.payload.request.ApproveAccountRequest;
import com.learning.payload.request.AuthenticateRequest;
import com.learning.payload.request.CreateAccountRequest;
import com.learning.payload.request.RegisterRequest;
import com.learning.payload.request.TransferRequest;
import com.learning.payload.request.UpdatePasswordRequest;
import com.learning.payload.response.AccountCreationResponse;
import com.learning.payload.response.AccountDetailsResponse;
import com.learning.payload.response.AccountSummary;
import com.learning.payload.response.ApproveAccountResponse;
import com.learning.payload.response.BeneficiarySummary;
import com.learning.payload.response.GetCustomerResponse;
import com.learning.payload.response.RegisterUserResponse;
import com.learning.payload.response.UpdateCustomerResponse;
import com.learning.repo.CustomerRepo;
import com.learning.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepo repo;
	@Override
	public RegisterUserResponse registerCustomer(RegisterRequest request) {
		//create a customer from the request
		Customer customer = new Customer();
		customer.setFullname(request.getFullname());
		customer.setUsername(request.getUsername());
		customer.setPassword(request.getPassword()); //should encrypt password here.
		//customer creation date is now.
		customer.setCreatedDate(LocalDate.now());
		//save customer to DB
		Customer temp = repo.save(customer);
		//Create response from the DB returned customer.
		RegisterUserResponse response = new RegisterUserResponse();
		response.setFullname(temp.getFullname());
		response.setId(temp.getId());
		response.setPassword(temp.getPassword());//should be encrypted password.
		response.setUsername(temp.getUsername());
		return response;
	}

	@Override
	public String authenticate(AuthenticateRequest request) {
		Customer customer = repo.findByUsername(request.getUsername()).orElseThrow(()-> new NoDataFoundException("user not found"));
		return "JWT TOKEN HERE";
	}

	@Override
	public AccountCreationResponse addAccount(long customerID, CreateAccountRequest request) {
		Customer customer = repo.findById(customerID).orElseThrow(()-> new NoDataFoundException("user not found"));
		Set<Account> accounts = customer.getAccounts();
		Account newAccount = new Account();
		newAccount.setAccountBalance(request.getAccountBalance());
		newAccount.setAccountType(request.getAccountType());
		newAccount.setApproved(request.getApproved());
		newAccount.setDateOfCreation(LocalDate.now());
		accounts.add(newAccount);
		Customer updated = repo.save(customer);
		Set<Account> newAccounts = updated.getAccounts();
		newAccounts.removeAll(accounts);
		AccountCreationResponse response = new AccountCreationResponse();
		response.setAccountBalance(newAccount.getAccountBalance());
		return null;
	}

	@Override
	public ApproveAccountResponse approveAccount(long customerID, long accountNo, ApproveAccountRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountSummary> getCustomerAccounts(long customerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetCustomerResponse getCustomer(long customerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateCustomerResponse updateCustomer(long customerID, Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountDetailsResponse getCustomerAccount(long customerID, long accountID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addBeneficiary(long customerID, AddBeneficiaryRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BeneficiarySummary> getBeneficiaries(long customerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteBeneficiary(long customerID, long beneficiaryID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String transferFunds(TransferRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuestion(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String validateAnswer(String username, String answer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePassword(String username, UpdatePasswordRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
