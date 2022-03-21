package com.learning.payload.response.admin;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.learning.enums.EnabledStatus;

import lombok.Data;

@Data
public class GetStaffResponse {
	private Integer staffId;
	private String staffName;
	private String staffUsername;
	@Enumerated(EnumType.STRING)
	private EnabledStatus status;
}
