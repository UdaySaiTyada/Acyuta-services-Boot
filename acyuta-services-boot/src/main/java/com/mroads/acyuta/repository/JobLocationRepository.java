/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2016 mRoads LLC. All Rights Reserved.
 *
 */
package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.JobLocation;

/**
 * 
 * @author SaiRameshGupta
 *
 */
@Repository
public interface JobLocationRepository extends JpaRepository<JobLocation, BigInteger>{
	
//	@Query("select l from JobLocation where l.jobOrderId = :jobOrderId and l.status=:status")
//	public List<JobLocation> findByJobOrderIdAndStatus(@Param("jobOrderId")BigInteger jobOrderId,@Param("status")String status); 
	@Transactional
	@Modifying
	@Query("update JobLocation p set p.status= :status,p.updatedDate= :updatedDate where p.mappingId= :mappingId ")
	public void  setStatus(@Param("mappingId") BigInteger mappingId,@Param("status") String status,@Param("updatedDate") Date updatedDate ); 
}
