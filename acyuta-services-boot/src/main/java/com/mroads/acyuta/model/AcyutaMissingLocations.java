package com.mroads.acyuta.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Entity
@Table(name = "panna_Acyuta_missing_locations")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class AcyutaMissingLocations {
	
	@Id
	@GeneratedValue
	private BigInteger locationId;
	private String locations;
	private BigInteger jobOrderId;
	private BigInteger recruiterId;
	private BigInteger organizationId;
	private Date createdDate;
	

}
