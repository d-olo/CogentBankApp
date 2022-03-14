package com.learning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
	private Integer toAccNumber;
	private Integer fromAccNumber;
	private Double amount;
	private String reason;
	private String by;
}
