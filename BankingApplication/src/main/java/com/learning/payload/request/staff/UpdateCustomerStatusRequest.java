package com.learning.payload.request.staff;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerStatusRequest {
	@NotNull
	private Integer customerId;
	@NotNull
	@Enumerated(EnumType.STRING)
	private EnabledStatus status;

}
