package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.VendorJobMapping;

/**
 * @author Mahi K
 *
 */
@Repository
public interface VendorJobMappingRepository extends JpaRepository<VendorJobMapping, BigInteger> {
		
	
	
	public List<VendorJobMapping> findJobOrderIdAndByOrganizationId(
			@Param("jobOrderId") BigInteger jobOrderId,
			@Param("organizationId") BigInteger organizationId
			);
	
	public  VendorJobMapping findByVendorIdAndOrganizationIdAndJobOrderId(
			@Param("vendorId") BigInteger vendorId,
			@Param("organizationId") BigInteger organizationId,
			@Param("jobOrderId") BigInteger jobOrderId
			);

	public List<VendorJobMapping> findByJobOrderIdAndStatus(
			@Param("jobOrderId") BigInteger jobOrderId,
			@Param("status") String status
			);
}
