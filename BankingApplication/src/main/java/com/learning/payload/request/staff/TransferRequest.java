package com.learning.payload.request.staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
	
	@NotNull
	private Integer fromAccNumber;
	@NotNull
	private Integer toAccNumber;
	@NotNull
	private Double amount;
	@NotBlank
	private String reason;
	@NotBlank
	private String by;
}
