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
@SuppressWarnings({"squid:S1068","all"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcyutaPannaInterviewsDTO {

	private BigInteger presetInterviewId;
	private BigInteger userInterviewId;
	private BigInteger candidateId;
	private String jobId;
	private String jobTitle;
	private String skillsSet;
	private Date interviewDate;
	private int expiryDays;

	private String interviewStatus;
	private String interviewPosition;

	private Float codingRating;
	private Float communicationRating;
	private Float problemSolvingRating;
	private Float technologyRating;
	private Float score;
	private String comments;

	private String pannaPresetReportUrl;

	private BigInteger organizationId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;

}
