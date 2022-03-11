package com.learning.payload.request.customer;

import com.learning.entity.User;

import lombok.Data;

@Data
public class TransferRequest {
	private Integer toAccNumber;
	private Integer fromAccNumber;
	private Double amount;
	private String reason;
	private User by;
}
