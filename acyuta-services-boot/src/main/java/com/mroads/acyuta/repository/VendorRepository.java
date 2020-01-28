package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.Vendor;


public interface VendorRepository extends JpaRepository<Vendor,BigInteger> {
	
	
	public List<Vendor> findByOrganizationId(@Param("organizationId") BigInteger organizationId);

	public Vendor findByVendorId(@Param("vendorId") BigInteger vendorId);
	
	
	public List<Vendor> findByOrganizationIdAndTier(
			@Param("organizationId") BigInteger organizationId,
			@Param("tier") String tier	
			);


	
	
}