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

package com.mroads.acyuta.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
/**
 * @author Mahi K
 *
 */
@Component
@Scope("prototype")
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({"squid:S1068","all"})
@Data
public class JobVendorMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger mappingId;
	private BigInteger jobOrderId;
	private BigInteger vendorId;
	private BigInteger organizationId;
	private Date updatedDate;
	private Date createdDate;
	private BigInteger updatedBy;
	private BigInteger createdBy;
	private String status;
	
}