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
public class InterviewerSlotsDTO {
	private BigInteger interviewerSlotId;
	private BigInteger interviewerId;
	private String date;
	private String startTime;
	private String endTime;
	private BigInteger organizationId;
	private Date createdDate;
	private Date updatedDate;
	private BigInteger createdBy;
	private BigInteger updatedBy;
	private String status;
	// private UserLiferayDTO userLiferay;

}
