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

@Entity
@Table(name="vendorView")
@Data
public class JobListVendorView {
	@Id
	@GeneratedValue
	private BigInteger jobOrderId;
	private String vendorId;
	private String jobId;
	private String jobTitle;
	private String clientName;
	private String location;
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	private String recruiter;
	
	private int activeDays;
	private BigInteger organizationId;
	
	private Integer total;
	private Integer submitted;
	private String vendor;
	private String vicePresident;
	private String hiringManager;
	private String projectTargetDate;
	private String resourceType;
}
