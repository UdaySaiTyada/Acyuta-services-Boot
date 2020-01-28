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
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Entity
@Table(name = "Recruiter_Call_Logs")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class Recruiter_Call_Logs implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger id;
	private String extensionId;
	private String calledNumber;
	private Date date;
	private String time;
	private String duration;
	private String callType;
	

	private BigInteger recruiterId;
	private String recruiterName;
	private String callRecordingLink;
	
}