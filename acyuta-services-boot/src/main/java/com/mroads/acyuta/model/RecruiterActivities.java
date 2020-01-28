package com.mroads.acyuta.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Mahi K
 *
 */



@Entity
@Table(name = "acyuta_recruiter_activities")
@Scope("prototype")
@Data
@SuppressWarnings({ "squid:S1068", "all" })
public class RecruiterActivities implements Serializable {

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
	private Date updatedDate;
	private BigInteger organizationId;

}
