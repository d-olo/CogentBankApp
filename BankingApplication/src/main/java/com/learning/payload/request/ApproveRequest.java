package com.learning.payload.request;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
/*
 * Not yet implemented anywhere.
 */
public class ApproveRequest {
	private Integer accountNumber;
	private ApprovedStatus approvedStatus = ApprovedStatus.STATUS_APPROVED;
}
