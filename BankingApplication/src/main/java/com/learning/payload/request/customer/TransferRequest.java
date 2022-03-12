package com.learning.payload.request.customer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TransferRequest {

	@NotNull
	private Integer toAccNumber;
	@NotNull
	private Integer fromAccNumber;
	@NotNull
	private Double amount;
	@NotNull
	private String reason;
	@NotBlank
	private String by;
}
