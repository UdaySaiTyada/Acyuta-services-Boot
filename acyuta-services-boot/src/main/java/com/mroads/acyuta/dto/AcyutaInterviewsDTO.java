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
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Mahi K
 *
 */
@Scope("prototype")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S1068","all"})
public class AcyutaInterviewsDTO {

	private BigInteger interviewId;
	private BigInteger interviewerId;
	private BigInteger candidateId;
	private BigInteger jobOrderId;
	private String jobId;
	private BigInteger interviewerSlotId;
	private Date interviewDate;
	private String timeSlot;
	private String duration;
	private String interviewStatus;
	private String interviewPosition;
	private String mLiveInterviewerLink;
	private String environmentalName;
	private String clientInterviewerName;
	private String clientInterviewLocation;
	private Float techSkill;
	private Float score;
	private BigInteger userInterviewId;
	private Float communicationSkills;
	private String comments;
	private String interviewMode;
	private BigInteger companyId;
	private BigInteger presetInterviewId;
	private BigInteger organizationId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;
	private String timeZone;
}
