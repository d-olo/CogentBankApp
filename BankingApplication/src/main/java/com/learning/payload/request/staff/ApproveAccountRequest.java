package com.learning.payload.request.staff;

import java.sql.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
@Validated
public class ApproveAccountRequest {
	@NotBlank
	private String accType; 
	@NotBlank
    private String customerName; 
	@NotNull
    private Integer accNum; 
	@NotNull
    private Date dateCreated; 
	@NotNull
	@Enumerated(EnumType.STRING)
    private ApprovedStatus approved;
	@NotBlank
    private String staffUsername; 

}
