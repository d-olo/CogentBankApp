package com.learning.payload.request.staff;

import java.sql.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.learning.enums.ApprovedStatus;

import lombok.Data;

@Data
@Validated
public class ApproveAccountRequest {
	@NotBlank
	private String accType; 
    private String customerName; 
    private Integer accNum; 
    private Date dateCreated; 
    private ApprovedStatus approved; 
    private String staffUsername; 

}
