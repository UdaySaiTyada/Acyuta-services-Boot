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
 */
@Scope("prototype")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S1068","all"})
@Data
public class AcyutamLiveInterviewsDTO {

	private BigInteger acyutamLiveId;
	private BigInteger acyutaInterviewsId;

	private BigInteger mLiveScheduleId;
	private String mLiveInterviewerLink;
	private String mLiveFeedbackLink;
	private String mLiveStatus;
	private String sessionId;
	private BigInteger organizationId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private Date createdDate;
	private BigInteger createdBy;
	private BigInteger mlivePanelSerialno;

	private String mLivereportURL;

	
}
