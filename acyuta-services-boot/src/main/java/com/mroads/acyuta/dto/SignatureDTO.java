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

import javax.persistence.Column;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;


/**
 * @author Mahi.K
 * 
 */

@Scope("prototype")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Component("SignatureDTO")
@SuppressWarnings({"squid:S1068","all"})
public class SignatureDTO {

	private BigInteger signatureId;
	private String jobTitle;
	private BigInteger candidateId;
	private String name;
	private BigInteger jobOrderId;
	private Date updatedDate;
	private BigInteger artilceId;
	private String artilceTitle;
	private BigInteger interviewId;
	private String signature;
	private String intials;
	private String ipAddress;
	private String isAgree;
	@Column(name = "EMAIL_COUNT_SEND")
	private int emailCountSend;
	private String  digitalSignature; // Stores the canvas digital sign value.
	private String  adaptedSignature; // Stores the user selected adaptive signature.
	private String  deviceInfo; // Stores the user device information.

}
