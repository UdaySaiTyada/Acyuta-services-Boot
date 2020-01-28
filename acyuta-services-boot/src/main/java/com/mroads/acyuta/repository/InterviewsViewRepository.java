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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.InterviewsView;

/**
 * @author Vinoth G R
 *
 */
@Repository
public interface InterviewsViewRepository extends JpaRepository<InterviewsView, BigInteger> {

	

	@Query("SELECT interview FROM InterviewsView interview  where  (interview.interviewDate BETWEEN :startDate AND :endDate )"
			+ " and (:clientName= null or  interview.clientName=:clientName)"
			+ " and (:interviewMode=null or interview.environmentalName=:interviewMode)"
			+ " and (:jobId= null or interview.jobId=:jobId )"
			+ " and (:searchText= null or ((concat_ws(' ',interview.firstName,interview.lastName,interview.jobId,interview.jobTitle,interview.Interviewer,interview.Recruiter,interview.skills) LIKE '%'||:searchText||'%' )))"
			+ " and ((interview.interviewerId is NULL) or (:interviewerId= null or  interview.interviewerId=:interviewerId) )"
			+ " and (:updatedBy= null or  interview.updatedBy=:updatedBy)"
			+ " and (interview.interviewPosition in (:interviewPositionList) OR interview.interviewPosition IS NULL )" + " and interview.organizationId=:organizationId")
	public Page<InterviewsView> getCurrentInterviews(
			@Param("interviewPositionList") List<String> interviewPositionList,
			@Param("clientName") String clientName,
			@Param("interviewMode") String interviewMode,
			@Param("jobId") String jobId,
			@Param("searchText") String searchText,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("updatedBy") BigInteger updatedBy,
			@Param("interviewerId") BigInteger interviewerId,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable
			);
	
	@Query("SELECT interview FROM InterviewsView interview " + " where " 
			+ " ((((interview.techSkill >= :startTechSkill  and interview.techSkill <= :endTechSkill"
			+ " and interview.communicationSkills >= :startOfCommRating and interview.communicationSkills <= :endOfCommRating)" 
			+ " and (interview.environmentalName != 'PANNA'))"
			+ " OR (interview.score >=:startOfPannaRating and interview.score <=:endOfPannaRating and interview.environmentalName = 'PANNA'))"
			+ " OR (interview.environmentalName = 'CLIENT'))"
			+ " and (interview.interviewDate BETWEEN :startDate AND :endDate)" 
			+ " and (:clientName= null or  interview.clientName=:clientName)"
			+ " and (:interviewMode=null or interview.environmentalName=:interviewMode)"
			+ " and (:jobId= null or interview.jobId=:jobId )"
			+ " and (:searchText= null or ((concat_ws(' ',interview.firstName,interview.lastName,interview.jobId,interview.jobTitle,interview.Interviewer,interview.Recruiter,interview.skills) LIKE '%'||:searchText||'%' )))"
			+ " and ((interview.interviewerId is NULL) or (:interviewerId = null or interview.interviewerId=:interviewerId))"
			+ " and ( :updatedBy = null or interview.updatedBy=:updatedBy )" 
			+ " and (interview.interviewPosition in (:interviewPositionList) )" + " and interview.organizationId=:organizationId")
	public Page<InterviewsView> getCompletedInterviews(
			
			@Param("interviewPositionList") List<String> interviewPositionList,
			@Param("startTechSkill") float startTechSkill,
			@Param("endTechSkill") float endTechSkill,
			@Param("startOfCommRating") float startOfCommRating,
			@Param("endOfCommRating") float endOfCommRating,
			@Param("startOfPannaRating") float startOfPannaRating,
			@Param("endOfPannaRating") float endOfPannaRating,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("clientName") String clientName,
			@Param("interviewMode") String interviewMode,
			@Param("jobId") String jobId,
			@Param("searchText") String searchText,
			@Param("updatedBy") BigInteger updatedBy,
			@Param("interviewerId") BigInteger interviewerId,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable
			);
	
}
