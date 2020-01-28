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
package com.mroads.acyuta.dto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * @author SaiRameshGupta
 *
 */
@Component
@Scope("prototype")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@SuppressWarnings({"squid:S1068","all"})
@Data
public class JobOrderDTO {

	private BigInteger organizationId;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date createdDate;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date updatedDate;
	
	private String jobComment;
	private BigInteger createdBy;
	private BigInteger updatedBy;
	private String skills;
	
	private BigInteger interviewPositionId;
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
	private String scheduleType;
	private String vicePresident;
	private String hiringManager;
	private BigInteger hiringManagerId;
	
	private String projectStartDate;
	private String projectTargetDate;
	private String projectEndDate;
	
	private String departmentCode;
	private String buOu;
	private String accountNumber;
	private String invoicingContact;
	
	private String billRate;
	private String maxBillRate;
	private String potentialSalaryRange;
	private String pcStartDate;
	private String pcEndDate;
	private Boolean checkTBD;
	
	
	private String equipment;
	private String timeSheetApproval;
	private String jobLevel;
	private String potentialConversion;
	private String resourceType;
	private String departmentName;
	private Boolean postOnWebSite;
	
	private String resourceNotes;
	private String contractTerm;
	private String contractAmount;
	private String expenseType;
	private Boolean isBudgetedExpense;
	
	private BigInteger accountManagerId;
	
	
	
	@Column(name = "interviewType")
	private String assignedRecruiters;
	
	private BigInteger companyId;
	private String recruiters;
	// In view profile we link all recruiter activities related to the each job.
	private List<RecruiterActivitiesDTO> recruiterActivities = new ArrayList<RecruiterActivitiesDTO>(); 
	
	public JobOrderDTO() {
		
	}
	private JobOrderDTO(BigInteger jobOrderId, String jobId, String jobDescription , String jobTitle, String jobLocation,
			String payRate, String jobDuration, String skills, String assignedRecruiters,  String jobType, String clientName
			) {
		this.jobOrderId = jobOrderId;
		this.jobId = jobId;
		this.jobDescription = jobDescription;
		this.jobTitle = jobTitle;
		this.jobLocation = jobLocation;
		this.payRate = payRate;
		this.jobDuration = jobDuration;
		this.skills = skills;
		this.assignedRecruiters = assignedRecruiters;
		this.jobType = jobType;
		this.clientName = clientName;
		
		
	}

	@Override
	public String toString() {
		return "JobOrderDTO [organizationId=" + organizationId + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", jobComment=" + jobComment + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", skills=" + skills + ", interviewPositionId=" + interviewPositionId + ", jobOrderId=" + jobOrderId
				+ ", jobId=" + jobId + ", jobTitle=" + jobTitle + ", clientName=" + clientName + ", jobDescription="
				+ jobDescription + ", jobLocation=" + jobLocation + ", jobType=" + jobType + ", jobDuration="
				+ jobDuration + ", status=" + status + ", payRate=" + payRate + ", assignedRecruiters="
				+ assignedRecruiters + ", companyId=" + companyId + "]";
	}
	
	
	}
