package com.mroads.acyuta.model;


import java.io.Serializable;
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
@Table(name = "panna_Vendor_")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class Vendor implements Serializable{

	@Id
	@GeneratedValue
	private BigInteger vendorId;
	private String name;
	private String status;
	private Date createdDate;
	private Date updatedDate;
	private BigInteger updatedBy;
	private BigInteger createdBy;
	private BigInteger organizationId;
	private String tier;

}
