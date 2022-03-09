package com.learning.payload.response;

import java.util.List;

import lombok.Data;

@Data
/**
 * JSON response containing a web token, for validation purposes.
 * @author Oliver Pagalanan
 * @since Mar 9, 2022
 */
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Integer id;
	private String username;
	private String fullName;
	private List<String> roles;
	
	public JwtResponse(String accessToken, Integer id, String username, String fullName, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.fullName = fullName;
		this.roles = roles; 
	}
}
