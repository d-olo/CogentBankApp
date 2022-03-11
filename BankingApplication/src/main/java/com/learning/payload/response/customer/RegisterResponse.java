package com.learning.payload.response.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * The response body for customer registration.
 * @author Dionel Olo
 * @since Mar 8, 2022
 */
public class RegisterResponse {
	private Integer id;
	private String username;
	private String fullName;
	private String password;
}
