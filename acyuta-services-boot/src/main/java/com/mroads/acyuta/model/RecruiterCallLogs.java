package com.mroads.acyuta.model;

import java.io.Serializable;
import java.math.BigInteger;

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
@Table(name = "Recruiter_Call_Logs")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class RecruiterCallLogs implements Serializable {
	
	@Id
	@GeneratedValue
	private BigInteger Id;
	private String extensionId; 
	private String calledNumber; 
	private String duration;
	private String date;   
	private String time;   
	private String callType;
	private BigInteger recruiterId;
	private String recruiterName;
	private String callRecordingLink;
	
}
