package com.learning.payload.request.admin;
//Use Case POST /api/admin/staff

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class CreateStaffRequest {
	@NotBlank
	private String staffFullName;
	@NotBlank
	private String staffUserName;
	@NotBlank
	private String staffPassword;
}
