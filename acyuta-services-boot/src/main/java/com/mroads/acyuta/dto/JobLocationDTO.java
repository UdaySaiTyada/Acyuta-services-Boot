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

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * 
 * @author SaiRameshGupta
 *
 */

@Data
@Table(name="panna_Acyuta_JobOrder_Locaation_Mapping")
public class JobLocationDTO {
	@Id
	@GeneratedValue
	private BigInteger mappingId;
	private BigInteger jobOrderId;
	private String location;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createdDate;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updatedDate;
	
	private String status;
}
