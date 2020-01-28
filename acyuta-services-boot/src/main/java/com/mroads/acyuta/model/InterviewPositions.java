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

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Sreekanth
 * 
 * */

@Entity
@Data
@Table(name="panna_InterviewPositions")
public class InterviewPositions {
@Id
@GeneratedValue
private BigInteger interviewPositionId;
private String interviewID;
private String positionTitle;
private String positionDescription;
private String corporation;
private String templateType;
private String positionStatus;
private String interviewTime;
private BigInteger organizationId;
private Date createdDate;
private Date updatedDate;
private BigInteger createdBy;
private BigInteger updatedBy;
	
}