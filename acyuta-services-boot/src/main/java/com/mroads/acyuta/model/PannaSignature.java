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

import java.io.Serializable;
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
 * @author Mahi K
 *
 */
@Entity
@Table(name = "panna_Signature")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class PannaSignature implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private BigInteger signatureId;
	private String jobTitle;
	private BigInteger candidateId;
	private String name;
	private BigInteger jobOrderId;
	private Date updatedDate;
	private BigInteger artilceId;
	private String artilceTitle;

	@Column(name = "interview_Id")
	private BigInteger interviewId;

	private String signature;
	private String intials;
	private String ipAddress;
	private String isAgree;

	@Column(name = "EMAIL_COUNT_SEND")
	private int emailCountSend;
	private String  digitalSignature; // Stores the canvas digital sign value.
	private String  adaptedSignature; // Stores the canvas digital sign value.
	private String  deviceInfo; // Stores the user device information.

}
