package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.AcyutaInterviewsView;


/**
 * 
 * @author Mahi K
 *
 */

// InterviewsRepository performs DB operations on panna_Acyuta_Interviews table.
@Repository
public interface InterviewsRepository extends  JpaRepository<AcyutaInterviewsView, BigInteger>{

	// void getCurrentInterviews();

	// clientName, interviewType, jobId
	
	@Query("SELECT interview FROM AcyutaInterviewsView interview  where  (interview.interviewDate BETWEEN :startDate AND :endDate )"
			+ " and (:updatedBy= null or  interview.updatedBy=:updatedBy)"
			+ " and (:clientName= '' or  interview.clientName=:clientName)"
			+ " and (:interviewMode='' or interview.interviewMode=:interviewMode)"
			+ " and (:jobId= '' or interview.jobId=:jobId )"
			+ " and ((interview.interviewerId is NULL) or (:interviewerId= null or  interview.interviewerId=:interviewerId) )"
			+ " and (interview.interviewPosition in (:interviewPositionList) OR interview.interviewPosition IS NULL )" + " and interview.organizationId=:organizationId")
	public List<AcyutaInterviewsView> getCurrentInterviews(
			@Param("interviewPositionList") List<String> interviewPositionList,
			@Param("clientName") String clientName,
			@Param("interviewMode") String interviewMode,
			@Param("jobId") String jobId,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("updatedBy") BigInteger updatedBy,
			@Param("interviewerId") BigInteger interviewerId,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable);
	
	
	
	@Query("SELECT interview FROM AcyutaInterviewsView interview " + " where " 
			+ " (((interview.techSkill >= :startTechSkill  and interview.techSkill <= :endTechSkill"
			+ " and interview.communicationSkills >= :startOfCommRating and interview.communicationSkills <= :endOfCommRating)" 
			+ " and (interview.environmentalName != 'PANNA'))"
			+ " OR (interview.score >=:startOfPannaRating and interview.score <=:endOfPannaRating and interview.environmentalName = 'PANNA'))"
			+ " and (interview.interviewDate BETWEEN :startDate AND :endDate)" 
			+ " and ((interview.interviewerId is NULL) or (:interviewerId = null or interview.interviewerId=:interviewerId))"
			+ " and ( :updatedBy = null or interview.updatedBy=:updatedBy )" 
			+ " and (interview.interviewPosition in (:interviewPositionList) )" + " and interview.organizationId=:organizationId")
	public List<AcyutaInterviewsView> getCompletedInterviews(
			
			@Param("interviewPositionList") List<String> interviewPositionList,
			@Param("startTechSkill") float startTechSkill,
			@Param("endTechSkill") float endTechSkill,
			@Param("startOfCommRating") float startOfCommRating,
			@Param("endOfCommRating") float endOfCommRating,
			@Param("startOfPannaRating") float startOfPannaRating,
			@Param("endOfPannaRating") float endOfPannaRating,
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("interviewerId") BigInteger interviewerId,
			@Param("updatedBy") BigInteger updatedBy,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable
			);
}
