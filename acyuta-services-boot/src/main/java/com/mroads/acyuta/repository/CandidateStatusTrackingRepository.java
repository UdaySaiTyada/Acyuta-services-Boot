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

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.CandidateStatusTracking;

/**
 * @author Mahi K
 *
 */
//@Repository("CandidateStatusTrackingRepository")
@Scope("prototype")
public interface CandidateStatusTrackingRepository extends JpaRepository<CandidateStatusTracking, BigInteger>{


	
	@Query("SELECT p FROM CandidateStatusTracking p WHERE  p.jobOrderId=:jobOrderId and p.candidateId=:candidateId and p.updatedBy=:updatedBy")
	public CandidateStatusTracking getCandidateStatusTracking(
			@Param("jobOrderId") BigInteger jobOrderId,
			@Param("candidateId") BigInteger candidateId,
			@Param("updatedBy") BigInteger updatedBy
			
			);
	
	
}
