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

import com.mroads.acyuta.model.AcyutamLiveInterviews;

/**
 * @author Mahi K
 *
 */
//@Repository("AcyutamLiveInterviewsRepository")
@Scope("prototype")
public interface AcyutamLiveInterviewsRepository extends JpaRepository<AcyutamLiveInterviews, BigInteger> {

	/**
	 * @param acyutaInterviewsId
	 * @return AcyutamLiveInterviews
	 */
	@Query("SELECT p FROM AcyutamLiveInterviews p WHERE  p.acyutaInterviewsId=:acyutaInterviewsId")
	public AcyutamLiveInterviews findByAcyutaMliveInterviewsId(@Param("acyutaInterviewsId") BigInteger acyutaInterviewsId);
	
	/**
	 * @param mLiveScheduleId
	 * @return AcyutamLiveInterviews
	 */
	@Query("SELECT p FROM AcyutamLiveInterviews p WHERE  p.mLiveScheduleId=:mLiveScheduleId")
	public AcyutamLiveInterviews findByMLiveScheduleId(@Param("mLiveScheduleId") BigInteger mLiveScheduleId);

}
