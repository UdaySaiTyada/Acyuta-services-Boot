/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2015 mRoads LLC. All Rights Reserved.
 *
 */

package com.mroads.acyuta.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Scope("prototype")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "squid:S1068", "all" })
public class JobOrderMappingDTO implements Serializable{
	private BigInteger mappingId;
	private String jobId;
	private BigInteger candidateId;
	private BigInteger jobOrderId;
	private BigInteger organizationId;
	private Date updatedDate;
	private Date createdDate;
	private String status;
	private String clientName;
	private BigInteger updatedBy;
}