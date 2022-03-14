package com.learning.payload.response.customer;

import java.util.Date;

import com.learning.enums.EnabledStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerById {

	private Integer customerId;
	private String customerName;
	private EnabledStatus status;
	private Date created;
}
