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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Vinoth G R
 *
 */

@Scope("prototype")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "all", "squid:S1068", "serial" })
@Data
public class MliveInterviewPanelDTO implements Serializable {

private BigInteger mliveInterviewPanelId;
private BigInteger mliveScheduleId;
private String sessionId;
private String emailAddress;
private String interviewerName;
private Float communicationRating;
private Float technicalRating;
private String comments;
private String interviewerMliveLink;
private String interviewerFeedbackLink;
private String status;
private boolean isFinalStatus;
private BigInteger rating;
private BigInteger mlivePanelSerialno;

}


