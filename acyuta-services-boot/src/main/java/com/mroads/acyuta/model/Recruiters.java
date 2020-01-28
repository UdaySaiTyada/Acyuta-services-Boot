package com.mroads.acyuta.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(name="panna_Acyuta_Recruiter_JobOrder_Mapping")
public class Recruiters {
	
	@Id
	@GeneratedValue
	private BigInteger mappingId ; 
	private BigInteger jobOrderId;  
	private BigInteger organizationId; 
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;	 
	private String status; 
	private String clientName ;
	private BigInteger recruiterId; 
	private BigInteger vendorId; 
	
	
	/*@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="jobOrderId",updatable=false,insertable=false)
	private JobOrder jobOrder;*/

}
