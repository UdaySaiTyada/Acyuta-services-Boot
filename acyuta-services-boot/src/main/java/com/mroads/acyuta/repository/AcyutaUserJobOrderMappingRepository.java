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
package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.JobOrderMapping;

/**
 * @author Mahi K
 *
 */
@Scope("prototype")
public interface AcyutaUserJobOrderMappingRepository extends JpaRepository<JobOrderMapping, BigInteger> {

	
	/**
	 * @param candidateId
	 * @param organizationId
	 * @return
	 */
	@Query("select distinct p from JobOrderMapping p where  p.candidateId=:candidateId")
	public List<JobOrderMapping> getJobDetailsByCandidateId(@Param("candidateId") BigInteger candidateId);
	
	/**
	 * @param jobId
	 * @param candidateId
	 * @return
	 */
	@Query("select distinct p.candidateId from JobOrderMapping p where  p.jobId=:jobId and p.candidateId in :candidateId and p.status = 'ACTIVE'")
	public List<BigInteger> getCandidateIdsUnderJobId(@Param("jobId") String jobId,@Param("candidateId") BigInteger[] candidateId);
	
 
	@Query("select distinct p from JobOrderMapping p where  p.jobId=:jobId and p.candidateId = :candidateId and p.status = 'ACTIVE'")
	public List<JobOrderMapping> getCandidatesForJob(@Param("jobId") String jobId,@Param("candidateId") BigInteger candidateId);
	
	
	/**
	 * @param jobId
	 * @param candidateId
	 * @param organizationId
	 * @return
	 */
	@Query("select distinct p from JobOrderMapping p where p.jobId=:jobId and p.candidateId=:candidateId and p.organizationId=:organizationId and p.status = 'ACTIVE'")
	public JobOrderMapping getMappingDetails(@Param("jobId") String jobId,@Param("candidateId") BigInteger candidateId,@Param("organizationId") BigInteger organizationId);
	
	
}
