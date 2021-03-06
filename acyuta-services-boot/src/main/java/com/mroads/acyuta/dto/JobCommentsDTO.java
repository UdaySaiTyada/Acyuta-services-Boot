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
import java.math.BigInteger;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author p vani sree
 *
 */
@Data
@Component
public class JobCommentsDTO {

	private BigInteger commentId;
	private String	commentDescription; 
	private String jobOrderId;
	@DateTimeFormat(pattern="yyyy-MM-dd HH-mm-ss")
	private Date createdDate; 
	@DateTimeFormat(pattern="yyyy-MM-dd HH-mm-ss")
	private Date updatedDate;  
	private BigInteger createdBy; 
	private BigInteger updatedBy;
	private BigInteger organizationId; 
	private String recruiter;
	private int  active;

}
