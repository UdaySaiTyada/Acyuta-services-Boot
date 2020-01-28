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
package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.User;

/**
 * 
 * @author vanisree
 */

public interface UserRepository extends JpaRepository<User,BigInteger> {
	@Query("SELECT user FROM User user where user.userId = :userId  group by user.userId ")
	public User getUserInformation(@Param("userId") BigInteger userId);


	@Query("SELECT user FROM" + " User user,  IN(user.roles) role, IN(user.organization) organization" 
			+ " where " 
			+ " user.status = 0"
			+ " and  role.name in (:roleNamesList) " + " and   organization.organizationId =:organizationId group by user.userId" )
	public List<User> getUsersListByRoleNameAndOrgId(@Param("roleNamesList") List<String> roleNamesList, @Param("organizationId") BigInteger organizationId);
 
	@Query("SELECT user FROM User user where user.emailAddress = :emailAddress")
	public User getUserByEmailAddress(@Param("emailAddress") String emailAddress);
	
	
	
	@Query("SELECT user FROM  User user,  IN(user.roles) role, IN(user.organization) organization" 
			+ " where  user.status = 0  and  role.name in (:roleNamesList) " 
			+ " and   organization.organizationId =:organizationId  and user.emailAddress=:emailAddress group by user.userId" )
	public List<User> validateNewVendor(
			@Param("roleNamesList") List<String> roleNamesList,
			@Param("organizationId") BigInteger organizationId,
			@Param("emailAddress") String emailAddress );
	
	
	
}
