package com.learning.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class ApproveRequest {
	@NotNull
	private Integer accountNumber;
	@NotBlank
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_APPROVED;
}
