package com.mroads.acyuta.dto;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author Mahi K
 *
 */



@Entity
@Data
@SuppressWarnings({ "squid:S1068", "all" })
public class RecruiterActivitiesDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private BigInteger activityId;

	private BigInteger candidateId;
	private String candidateName;
	private BigInteger jobOrderId;
	private String activityType;
	private String activity;
	private String updatedBy;
	private String updatedDate;
	private BigInteger organizationId;

}
