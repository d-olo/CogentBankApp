package com.learning.payload.response.staff;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.learning.enums.EnabledStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetAllCustomersResponse {
	private Integer customerId; 
    private String customerName;
    @Enumerated(EnumType.STRING)
    private EnabledStatus status;
}
