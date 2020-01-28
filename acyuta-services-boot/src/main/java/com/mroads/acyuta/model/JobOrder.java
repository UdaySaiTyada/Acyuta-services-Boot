/* 
* Copyright (c) 2017 mRoads LLC. All Rights Reserved.
* mailto:support@mroads.com
* This computer program is the confidential information and proprietary trade
* secret of mRoads LLC. Possessions and use of this program must conform
* strictly to the license agreement between the user and mRoads LLC,
* and receipt or possession does not convey any rights to divulge, reproduce,
* or allow others to use this program without specific written authorization
* of mRoads LLC.
*/
package com.mroads.acyuta.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * @author SaiRameshGupta
 *
 */
@Entity
@Table(name = "panna_JobOrder")
@Scope("prototype")
@DynamicUpdate(value = true)
@SuppressWarnings({ "squid:S1068", "all" })
@Data
public class JobOrder implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private BigInteger jobOrderId;
	private String jobId;
	private String jobTitle;
	private String clientName;
	private String jobDescription;
	private String jobLocation;
	private String jobType;
	private String jobDuration;
	private String status;
	private String payRate;
	@Column(name = "interviewType")
	private String assignedRecruiters;
	private BigInteger companyId;
	private BigInteger organizationId;
	private BigInteger interviewPositionId;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatedDate;

	private BigInteger createdBy;
	private BigInteger updatedBy;
	private String skills;
	private String scheduleType;

	private String vicePresident;
	private String hiringManager;
	private BigInteger hiringManagerId;

	private String equipment;
	private String timeSheetApproval;
	private String jobLevel;
	private String potentialConversion;

	private String projectStartDate;
	private String projectTargetDate;

	private String projectEndDate;
	private String resourceType;
	private String departmentName;

	private String departmentCode;
	private String buOu;
	private String accountNumber;
	private String invoicingContact;

	private String maxBillRate;
	private String billRate;
	private String potentialSalaryRange;
	private String pcStartDate;
	private String pcEndDate;
	private Boolean checkTBD;
	private Boolean postOnWebSite;

	private String resourceNotes;
	private String contractTerm;
	private String contractAmount;
	private String expenseType;
	private Boolean isBudgetedExpense;
	
	private BigInteger accountManagerId;
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="jobOrderId")
	private List<JobLocation> jobLocations = new ArrayList<>();

}