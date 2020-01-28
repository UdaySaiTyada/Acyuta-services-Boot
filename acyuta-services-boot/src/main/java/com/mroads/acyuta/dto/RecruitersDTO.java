package com.mroads.acyuta.dto;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class RecruitersDTO {
	@Id
	private BigInteger mappingId ; 
	private BigInteger jobOrderId;  
	private BigInteger organizationId; 
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	@Temporal(TemporalType.DATE)
	private Date createdDate;	 
	private String status; 
	private String clientName ;
	private BigInteger recruiterId;
	private BigInteger vendorId; 

	
}
