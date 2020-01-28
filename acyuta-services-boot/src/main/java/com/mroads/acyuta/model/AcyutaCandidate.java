/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2016 mRoads LLC. All Rights Reserved.
 *
 */

package com.mroads.acyuta.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Entity
@Table(name = "panna_Acyuta_User")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class AcyutaCandidate implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger candidateId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String emailAddress;
	private String dateOfBirth;
	private String candidateLocation;
	private String experienceLevel;
	private String company;
	private String candidateTitle;
	private String payRate;
	private String billRate;
	private String loadedRate;
	private String payType;
	private String visaStatus;
	private String ssnNumber;
	private String skypeId;
	private String fileURL;
	private String rtrURL;
	private String candidateStatus;
	private BigInteger organizationId;
	
	private String consultancyName;
	private String consultancyContactPerson;
	private String consultancyContactDetails;
	private String consultancyAddress;
	
	private String linkedIn;
	private String comments;
	private String questions;
	private String status;
	private String rehire;
	private String reLocation;
	private String formerEmployee;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;
	private String videoFileUrl;
	private String resumeHtmlUrl;
	private Float score;
	private Float techSkill;
	private Float communicationSkills;
	private String interviewComments;
	private String pannaInterviewComments;
	private String pannaReportURL;
	private String mLiveReportURL;
	private String emailStatus;
	private String socialLinks;
	private String resumeSource;
	private String eduAndEmpDetails;
	
	private String availabilityToJoin;
	private String benefits;
	private String interviewAvailability;
	private String managerAvailability;
	private String managerNewSAvailabilitylots;
	private String documents;
	
	private Boolean isFundingApproved;
	private String hiredNotes;
	
	// subVendor Details...
	
	private String subVendorName;
	private String subVendorContactPerson;
	private String subVendorContactDetails;
	private String subVendorAddress;
	
	private String otherVisaStatus;
	

	private String profilePicture;
	
	
	private BigInteger vendorId;
	
	private Boolean isBookMarked;

	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
	@JoinColumn(name="candidateId") 
	private List<JobOrderMapping> jobMappings= new ArrayList<JobOrderMapping>();
}