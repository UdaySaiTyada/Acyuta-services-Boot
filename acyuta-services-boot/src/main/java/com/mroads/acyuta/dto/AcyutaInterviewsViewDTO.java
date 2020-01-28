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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Mahi K
 *
 */


@Scope("prototype")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S1068","all"})
@Data
public class AcyutaInterviewsViewDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger interviewId;
	private BigInteger interviewerSlotId;
	private BigInteger pannaUserInterviewId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String skypeId;
	private String emailAddress;
	private String encryptedMail;
	private String clientName;
	private String consultancyName;
	private String payRate;
	private String payType;
	private String fileURL;
	private BigInteger jobOrderId;
	private String jobId;
	private String jobTitle;
	private String skills;
	private BigInteger interviewerId;
	private BigInteger candidateId;

	private String isAgree;
	private String mLiveInterviewerLink;
	private String mLivereportURL;
	private String mLiveStatus;

	private String interviewerName;
	private String timeSlot;
	private String startTime;
	private String endTime;
	private String createdUserRecruiterName;
	private String updatedrecRuiterName;
	private String interviewDate;
	private String interviewStatus;
	private String interviewMode;
	private String interviewPosition;
	private String environmentalName;
	private String candidateStatus;
	private Float techSkill;
	private Float communicationSkills;
	private Float technology;
	private Float problemSolving;
	private Float coding;
	private Float communication;
	private Float customScore;
	private Float score;
	private String comments;
	private BigInteger organizationId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;
 

}
