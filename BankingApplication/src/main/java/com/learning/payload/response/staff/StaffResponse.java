package com.learning.payload.response.staff;

import com.learning.enums.EnabledStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StaffResponse {
	private Integer staffId;
	private String staffUserName;
	private String staffName;
	private EnabledStatus status;
}
