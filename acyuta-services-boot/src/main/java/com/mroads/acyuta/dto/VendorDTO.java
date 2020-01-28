package com.mroads.acyuta.dto;

import java.math.BigInteger;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.context.annotation.Scope;

import lombok.Data;

@Scope("prototype")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings({ "squid:S1068", "all" })
public class VendorDTO {

	private BigInteger vendorId;
	private String name;
	private Date createdDate;
	private Date updatedDate;
	private BigInteger updatedBy;
	private BigInteger createdBy;
	private BigInteger organizationId;
	private String tier;

}
