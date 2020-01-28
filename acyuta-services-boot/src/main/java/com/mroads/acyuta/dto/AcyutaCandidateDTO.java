/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2015 mRoads LLC. All Rights Reserved.
 *
 */

package com.mroads.acyuta.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mroads.acyuta.model.JobOrderMapping;

import lombok.Data;

@Scope("prototype")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@SuppressWarnings({ "squid:S1068", "all" })
public class AcyutaCandidateDTO implements Serializable{
	private BigInteger candidateId;
	private BigInteger organizationId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String candidateLocation;
	private String visaStatus;
	private BigInteger userJobId;
	private String experienceLevel;
	private String dateOfBirth;
	private String skypeId;
	private String reLocation;
	private BigInteger createdBy;
	private BigInteger updatedBy;
	
	private String consultancyName;
	private String consultancyContactPerson;
	private String consultancyContactDetails;
	private String consultancyAddress;
	
	private String phoneNumber;
	private String payRate;
	private String billRate;
	private String loadedRate;
	private String payType;
	private String candidateTitle;
	private String comments;
	private String candidateStatus;	
	private String resumeHtmlUrl;
	private String resumeViewURL;
	private Date updatedDate;
	private Date createdDate;
	private String jobId;
	private String questions;
	
	private String ssnNumber;
	private String fileURL;
	private String rtrURL;
	private Date toDAte;
	private Date fromDate;
	private String candidateStatusForReview;
	private ArrayList finalStatusList = new ArrayList();
	private String pannaAcyutaPropertiesCandidateTitle;
	private String pannaAcyutaPropertiesVisaStatus;
	private String linkedIn;
	private String status;
	private String rehire;
	private String formerEmployee;
	private String company;
	private String videoFileUrl;
	private Float score;
	private Float techSkill;
	private Float communicationSkills;
	private String interviewComments;
	private String pannaInterviewComments;
	private String pannaReportURL;
	private String mLiveReportURL;
	private String stripColor;
	private String emailStatus;
	private String socialLinks;
	private String resumeSource;
	private String encryptedMail;
	private String eduAndEmpDetails;
	
	private String availabilityToJoin;
	private String benefits;
	private String interviewAvailability;
	private String documents;
	
	private String subVendorName;
	private String subVendorContactPerson;
	private String subVendorContactDetails;
	private String subVendorAddress;
	
	private String otherVisaStatus;
	
	private String managerAvailability;
	private String managerNewSAvailabilitylots;
	private String profilePicture;
	
	private Boolean isFundingApproved;
	private String hiredNotes;
	private Boolean isBookMarked;
	
	private BigInteger vendorId;
	private List<JobOrderMapping> jobMappings= new ArrayList<JobOrderMapping>();
}