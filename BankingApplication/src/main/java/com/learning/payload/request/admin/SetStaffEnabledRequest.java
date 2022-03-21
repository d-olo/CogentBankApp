package com.learning.payload.request.admin;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.EnabledStatus;

import lombok.Data;

@Data
@Validated
public class SetStaffEnabledRequest {
	@NotNull
	Integer staffId;
	@NotNull
	@Enumerated(EnumType.STRING)
	EnabledStatus status;
}
