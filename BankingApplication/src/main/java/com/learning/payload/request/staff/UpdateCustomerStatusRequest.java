package com.learning.payload.request.staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ERole;
import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Validate
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerStatusRequest {
	@NotNull
	private Integer customerId;
	@NotBlank
	private EnabledStatus status;

}
