package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.VmsRoles;


public interface VmsRolesRepository extends JpaRepository<VmsRoles,BigInteger> {
	
	
	public List<VmsRoles> findByRoleIdAndOrganizationId(
			@Param("roleId") BigInteger roleId,
			@Param("organizationId") BigInteger organizationId
			);

	
	public List<VmsRoles> findByVendorIdAndOrganizationId(
			@Param("vendorId") BigInteger vendorId,
			@Param("organizationId") BigInteger organizationId
			);

	public  VmsRoles findByUserIdAndOrganizationId(
			@Param("userId") BigInteger userId,
			@Param("organizationId") BigInteger organizationId
			);

	
	public  VmsRoles findByUserIdAndRoleId(
			@Param("userId") BigInteger userId,
			@Param("roleId") BigInteger roleId
			);

	
}