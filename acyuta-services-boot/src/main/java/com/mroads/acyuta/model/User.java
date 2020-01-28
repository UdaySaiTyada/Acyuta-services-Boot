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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
/**
 * @author vanisree
 * 
 * */
@Data
@Entity
@Table(name = "User_")
public class User {
	@Id
	@GeneratedValue
	private BigInteger userId;
	
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String emailAddress;
	private int status;
	private int defaultUser;
	private Date loginDate;
	private String timeZoneId;
	private String jobTitle;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "Users_Roles", joinColumns = @JoinColumn(name = "userId", nullable = false), inverseJoinColumns = @JoinColumn(name = "roleId", nullable = false))
	private List<Roles> roles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "Users_Orgs", joinColumns = @JoinColumn(name = "userId", nullable = false), inverseJoinColumns = @JoinColumn(name = "organizationId", nullable = false))
	private List<Organization>organization;
}
