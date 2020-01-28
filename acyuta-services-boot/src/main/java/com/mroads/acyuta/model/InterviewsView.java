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
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Vinoth G R
 *
 */
@Entity
@Table(name = "InterviewsView")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class InterviewsView implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger interviewId;
	private BigInteger candidateId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private BigInteger jobOrderId;
	private String jobId;
	private String jobTitle;
	private String clientName;
	private String environmentalName;
	private Date interviewDate;
	private String timeSlot;
	private String fileURL;
	private String skypeId;
	private String candidateTitle;
	private String consultancyName;
	private String payRate;
	private String payType;
	private String skills;
	private String Recruiter;
	private String Interviewer;
	private String interviewMode;
	private BigInteger interviewerId;
	private Float score;
	private Float techSkill;
	private Float communicationSkills;
	private String  interviewComments;
	private Float oldScore;
	private Float oldTechSkill;
	private Float oldCommunicationSkills;
	private String  mliveInterviewComments;
	private String pannaInterviewComments;
	private BigInteger organizationId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;
	private String interviewPosition;
	private String mLiveInterviewerLink;
	private String mLivereportURL;
	private String pannareportURL;
	private String startTime;
	private String endTime;
	private String oldMLivereportURL;
	private String oldPannareportURL;
	private String timeZone;
 

}
