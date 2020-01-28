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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Mahi L
 *
 */
@Entity
@Table(name = "panna_Acyuta_CommentsTracking")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class CandidateCommentsTracking {

	@Id
	@GeneratedValue
	@Column(name = "trackingId")
	private BigInteger trackingId;
	private BigInteger candidateId;
	private BigInteger  jobOrderId;
	private Date updatedDate;
	private BigInteger updatedBy;
	private BigInteger organizationId;
	private String comments;

}