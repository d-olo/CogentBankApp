package com.learning.payload.request.staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
public class UpdateCustomerStatusRequest {
	@NotNull
	private Integer customerId;
	@NotBlank
	private String status;
}
