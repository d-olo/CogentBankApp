package com.learning.payload.request;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
public class ApproveRequest {
	private Integer accountNumber;
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_APPROVED;
}
