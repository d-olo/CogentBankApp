package com.learning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.learning.controller.CustomerController;
import com.learning.enums.AccountType;
import com.learning.enums.ApprovedStatus;
import com.learning.payload.request.customer.AddAccountRequest;
import com.learning.payload.request.customer.RegisterCustomerRequest;
import com.learning.payload.response.customer.AddAccountResponse;
import com.learning.payload.response.customer.CustomerRegisterResponse;
import com.learning.payload.response.customer.GetAccountResponse;

@SpringBootTest
class CustomerControllerTests {
	
	@Autowired
	CustomerController customerController;
	
	@Test
	void testRegister() {
		RegisterCustomerRequest request = new RegisterCustomerRequest();
		request.setFullName("Renko Usami");
		request.setUsername("usamimi123");
		request.setPassword("pass1");
		
		CustomerRegisterResponse response = 
				(CustomerRegisterResponse) customerController.register(request)
					.getBody();
		
		assertNotNull(response);
		assertEquals(response.getFullName(), "Renko Usami");
		assertEquals(response.getUsername(), "usamimi123");
	}
	
	@Test
	void testCreateAccount() {
		AddAccountRequest request = new AddAccountRequest();
		request.setAccountBalance(200.00);
		request.setAccountType(AccountType.ACCOUNT_SAVINGS);
		
		AddAccountResponse response = 
				(AddAccountResponse) customerController.addAccount(1, request)
					.getBody();
		
		assertNotNull(response);
		assertEquals(AccountType.ACCOUNT_SAVINGS, response.getAccountType());
		assertEquals(200.0, response.getAccountBalance());
		assertEquals(ApprovedStatus.STATUS_NOT_APPROVED, response.getApprovedStatus());
	}
	
	@Test
	void testGetAllAccounts() {
		Set<GetAccountResponse> response = (Set<GetAccountResponse>) customerController.getAllAccounts(1)
				.getBody();
		assertNotNull(response);
	}

}
