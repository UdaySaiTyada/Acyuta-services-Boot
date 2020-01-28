/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2015 mRoads LLC. All Rights Reserved.
 *
 */
package com.mroads.acyuta.dto;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

/**
 *@author vanisree 
 *
 */
@Data
@SuppressWarnings({"squid:S1068","all"})
public class UserDTO {
	private BigInteger userId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String emailAddress;
	private int status;
	private int defaultUser;
	private String jobTitle;

	private List<RolesDTO> roles;
	private List<OrganizationDTO> organization;
}
