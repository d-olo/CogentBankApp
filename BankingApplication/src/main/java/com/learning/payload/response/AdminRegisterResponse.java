package com.learning.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminRegisterResponse {
	private Integer id;
	private String username;
	private String password;
}
