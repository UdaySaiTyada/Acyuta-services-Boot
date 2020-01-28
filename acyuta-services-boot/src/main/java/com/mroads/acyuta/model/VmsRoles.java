package com.mroads.acyuta.model;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;


@Entity
@Table(name = "panna_VmsRoles")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class VmsRoles {
	@Id
	@GeneratedValue
	private BigInteger mappingId;
	private BigInteger userId;
	private BigInteger roleId;
	private BigInteger vendorId;
	private BigInteger organizationId;
	
}
