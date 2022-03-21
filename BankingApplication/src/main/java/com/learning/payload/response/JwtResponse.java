package com.learning.payload.response;

import java.util.List;

import com.learning.enums.EnabledStatus;

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
	private EnabledStatus status;
	private List<String> roles;
	
	public JwtResponse(String accessToken, Integer id, String username, String fullName, EnabledStatus status, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.fullName = fullName;
		this.status = status;
		this.roles = roles; 
	}
}
