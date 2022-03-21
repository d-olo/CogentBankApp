package com.learning.payload.response;

import lombok.Data;

@Data
public class JsonMessageResponse {
	
	public JsonMessageResponse(String message) {
		this.message = message;
	}

	private String message;
}
