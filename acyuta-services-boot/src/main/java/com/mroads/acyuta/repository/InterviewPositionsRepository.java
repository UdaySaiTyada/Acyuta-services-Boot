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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.InterviewPositions;


/**
 * 
 * @author Sreekanth
 *
 */

@Repository
public interface InterviewPositionsRepository extends JpaRepository<InterviewPositions, BigInteger>{

	@Query("SELECT p FROM InterviewPositions p WHERE  p.positionStatus = :positionStatus and p.organizationId=:organizationId")
	public List<InterviewPositions> findByOrganizationIdAndPositionStatus(@Param("positionStatus") String positionStatus, @Param("organizationId") BigInteger organizationId);
	
	
	
	public InterviewPositions findByInterviewPositionId(BigInteger organizationId);

}	
