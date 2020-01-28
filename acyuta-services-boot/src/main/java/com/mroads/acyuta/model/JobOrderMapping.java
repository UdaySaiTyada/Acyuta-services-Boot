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
/**
 * @author Sreekanth
 *
 */
@Entity
@Table(name = "panna_Acyuta_User_JobOrder_Mapping")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class JobOrderMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger mappingId;
	private String jobId;
	private BigInteger jobOrderId;
	private BigInteger candidateId;
	private BigInteger organizationId;
	private BigInteger vendorId;
	private Date updatedDate;
	private Date createdDate;
	private BigInteger updatedBy;
	private String status;
	private String clientName;
}