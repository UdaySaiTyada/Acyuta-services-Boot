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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mroads.acyuta.model.InterviewerSlot;

/**
 * @author Mahi K
 *
 */
@Scope("prototype")
public interface InterviewerSlotsRepository extends JpaRepository<InterviewerSlot, BigInteger> {

	
	/**
	 * @param interviewerSlotId
	 * @return record based on slotId(primary Key)
	 */
	public InterviewerSlot findByInterviewerSlotId(@Param("interviewerSlotId") BigInteger interviewerSlotId);

	/**
	 * @param organizationId
	 * @return list of Records based on OrganizationId and status IS open or reserved
	 */
	@Query("SELECT p FROM InterviewerSlot p WHERE p.organizationId = ? AND (p.status='OPEN' OR p.status='RESERVED')")
	public List<InterviewerSlot> findByOrganizationAndStatus(@Param("p.organizationId") BigInteger organizationId);

	/**
	 * @param organizationId
	 * @param interviewerId
	 * @return list of records based organizationId and interviewerId
	 */
	@Query("SELECT p FROM InterviewerSlot p WHERE p.organizationId = ? AND p.interviewerId = ? AND (p.status='OPEN' OR p.status='RESERVED')")
	public List<InterviewerSlot> findByOrganizationAndStatusAndInterviewerId(@Param("p.organizationId") BigInteger organizationId, @Param("p.interviewerId") BigInteger interviewerId);

	/**
	 * @param organizationId
	 * @param interviewerId
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @return count of records with same slots
	 */
	@Query("SELECT COUNT(p) FROM InterviewerSlot p WHERE p.organizationId = ? AND p.interviewerId = ? AND p.date=? AND p.startTime= ? AND p.status != 'ARCHIVED')")
	public int findByInterviewIdAndDateAndTime(@Param("p.organizationId") BigInteger organizationId, @Param("p.interviewerId") BigInteger interviewerId, @Param("p.date") String date,
	        @Param("p.startTime") String startTime);

	/**
	 * @param date
	 * @param organizationId
	 * @return list of records based on particular date and organizatonId
	 */
	@Query("SELECT p FROM InterviewerSlot p WHERE p.date= ? AND p.organizationId= ? AND p.status='OPEN' ")
	public List<InterviewerSlot> findByDateAndOrganization(@Param("p.date") String date, @Param("p.organizationId") BigInteger organizationId);

	/**
	 * @param organizationId
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @return list of records based on date and start time and end time and organizationId
	 */
	@Query("SELECT p FROM InterviewerSlot p WHERE p.organizationId = ? AND p.date=? AND p.startTime= ? AND p.endTime= ? AND p.status='OPEN' ")
	public List<InterviewerSlot> findInterviewersListBasedOnDateAndTime(@Param("p.organizationId") BigInteger organizationId, @Param("p.date") String date, @Param("p.startTime") String startTime,
	        @Param("p.endTime") String endTime);

	/**
	 * @param interviewerId
	 * @param organizationId
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @return record based on interviewerId and organizationId and date and timeSlot
	 */
	@Query("SELECT p FROM InterviewerSlot p WHERE p.interviewerId = ? AND p.organizationId = ? AND p.date=? AND p.startTime= ? AND p.endTime= ?")
	public InterviewerSlot findInterviewersListBasedOnInterviewerIdDateAndTime(@Param("p.interviewerId") BigInteger interviewerId, @Param("p.organizationId") BigInteger organizationId,
	        @Param("p.date") String date, @Param("p.startTime") String startTime, @Param("p.endTime") String endTime);

	
	
	
	
	/**
	 * @param oldStatusList
	 * @param newStatus
	 * @return
	 */
	@Transactional 
	@Modifying	
	@Query("UPDATE InterviewerSlot SET status = :newStatus WHERE status IN(:oldStatusList) AND STR_TO_DATE(date, '%m/%d/%Y') < (CURDATE()-1)")
	public int updateSlotStatus(@Param("oldStatusList") List<String> oldStatusList, @Param("newStatus") String newStatus);
}
