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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author SaiRameshGupta
 *
 */

//@Data
@Entity
@Table(name="panna_Acyuta_JobOrder_Location_Mapping")
public class JobLocation {
	@Id
	@GeneratedValue
	private BigInteger mappingId;

	private String location;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createdDate;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updatedDate;
	
	private String status;
	
	private BigInteger organizationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="jobOrderId")
	private JobOrder jobOrder;

	public BigInteger getMappingId() {
		return mappingId;
	}

	public void setMappingId(BigInteger mappingId) {
		this.mappingId = mappingId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public JobOrder getJobOrder() {
		return jobOrder;
	}

	public void setJobOrder(JobOrder jobOrder) {
		this.jobOrder = jobOrder;
	}

	@Override
	public String toString() {
		return "JobLocation [mappingId=" + mappingId + ", location=" + location + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + ", status=" + status + "]";
	}
	
	public BigInteger getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(BigInteger organizationId) {
		this.organizationId = organizationId;
	}
	
}
