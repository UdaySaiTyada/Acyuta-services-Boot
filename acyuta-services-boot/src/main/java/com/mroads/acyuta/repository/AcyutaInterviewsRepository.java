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
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mroads.acyuta.model.AcyutaInterviews;

/**
 * @author Mahi K
 *
 */
//@Repository("AcyutaInterviewsRepository")
@Scope("prototype")
@EnableJpaRepositories
public interface AcyutaInterviewsRepository extends JpaRepository<AcyutaInterviews, BigInteger> {

	/**
	 * @param interviewId
	 * @return
	 */
	public AcyutaInterviews findByInterviewId(BigInteger interviewId);
	public AcyutaInterviews findByUserInterviewId(BigInteger interviewId);

	/**
	 * @param interviewerSlotId
	 * @return
	 */
	public List<AcyutaInterviews> findByInterviewerSlotId(BigInteger interviewerSlotId);
	
	
	/**
	 * @param oldInterviewPosition
	 * @param newInterviewPosition
	 * @return count
	 */
	@Transactional 
	@Modifying	
	@Query("UPDATE AcyutaInterviews SET interviewPosition = :newInterviewPosition WHERE interviewPosition = :oldInterviewPosition AND interviewDate < (CURDATE()-1)")
	public int updateInterviewPositionStatus(@Param("oldInterviewPosition") String oldInterviewPosition,@Param("newInterviewPosition") String newInterviewPosition);

	
	
	@Transactional
	@Modifying
	@Query("UPDATE AcyutaInterviews SET score = :score , interviewStatus=:interviewStatus, interviewPosition=:interviewPosition,pannareportURL=:reportURL WHERE userInterviewId = :userInterviewId")
	public int updateInterviewScore(
			@Param("score") Float score,
			@Param("userInterviewId") BigInteger userInterviewId,
			@Param("interviewStatus") String interviewStatus,
			@Param("interviewPosition") String interviewPosition,
			@Param("reportURL") String reportURL
			);
	

	@Transactional
	@Modifying
	@Query("UPDATE AcyutaInterviews SET mLiveInterviewerLink = :mLiveInterviewerLink  WHERE interviewId = :interviewId")
	public int updateMLiveInterviewerLink(@Param("mLiveInterviewerLink") String mLiveInterviewerLink,
			@Param("interviewId") BigInteger interviewId);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE AcyutaInterviews SET mLivereportURL = :reportURL  WHERE interviewId = :interviewId")
	public int updateMliveReportURL(
			@Param("reportURL") String reportURL,
			@Param("interviewId") BigInteger interviewId);
	
	
	@Transactional 
	@Modifying	
	@Query("UPDATE AcyutaInterviews SET interviewStatus = :interviewStatus , interviewPosition=:interviewPosition WHERE interviewId = :interviewId")
	public int updateInterviewStatus(
			@Param("interviewStatus") String interviewStatus,
			@Param("interviewId") BigInteger interviewId,
			@Param("interviewPosition") String interviewPosition
			);
	
	
	@Transactional 
	@Modifying	
	@Query("UPDATE AcyutaInterviews SET comments =:comments WHERE interviewId = :interviewId")
	public int updateInterviewComments(@Param("comments") String comments,
			@Param("interviewId") BigInteger interviewId);

	@Transactional 
	@Modifying	
	@Query("UPDATE AcyutaInterviews SET interviewDate = :interviewDate, timeSlot =:timeSlot,interviewStatus = :interviewStatus,"
			+ "duration=:duration,clientInterviewerName=:clientInterviewerName,clientInterviewLocation=:clientInterviewLocation WHERE interviewId = :interviewId")
	public int updateInterview(@Param("interviewDate") Date interviewDate,
			@Param("timeSlot") String timeSlot,
			@Param("interviewStatus") String interviewStatus,
			@Param("duration") String duration,
			@Param("clientInterviewerName") String clientInterviewerName,
			@Param("clientInterviewLocation") String clientInterviewLocation,
			@Param("interviewId") BigInteger interviewId);
	
	@Transactional 
	@Modifying	
	@Query("UPDATE AcyutaInterviews SET interviewDate = :interviewDate, timeSlot =:timeSlot,interviewStatus = :interviewStatus, interviewPosition=:interviewPosition WHERE interviewId = :interviewId")
	public int cancelInterview(@Param("interviewDate") Date interviewDate,
			@Param("timeSlot") String timeSlot,
			@Param("interviewStatus") String interviewStatus,
			@Param("interviewPosition") String interviewPosition,
			@Param("interviewId") BigInteger interviewId);
	
}
