package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mroads.acyuta.model.AcyutaCandidate;

/**
 * @author sameer/Sreekanth
 *
 */
@Repository
public interface AcyutaCandidateRepository extends JpaRepository<AcyutaCandidate, BigInteger> {
	
	
	@Query("SELECT p FROM AcyutaCandidate p where (p.emailAddress = :emailAddress or (('' != :phoneNumber) and (replace(replace(replace(replace(replace(p.phoneNumber,'(',''), ')',''),' ',''), '-',''),'+','') = :phoneNumber) )) and p.candidateId !=:candidateId  and p.organizationId=:organizationId and p.status = 'ACTIVE' ")
	public List<AcyutaCandidate> findByEmailAddressOrPhoneNumberCandidateIdAndOrganizationId (
								@Param("emailAddress") String emailAddress,
								@Param("phoneNumber") String phoneNumber,
								@Param("candidateId") BigInteger candidateId,
								@Param("organizationId") BigInteger organizationId
								);
	
	
	@Query("SELECT p FROM AcyutaCandidate p where (p.emailAddress = :emailAddress or (('' != :phoneNumber) and (replace(replace(replace(replace(replace(p.phoneNumber,'(',''), ')',''),' ',''), '-',''),'+','') = :phoneNumber) )) and p.candidateId !=:candidateId and p.status = 'ACTIVE' ")
	public List<AcyutaCandidate> findByEmailAddressOrPhoneNumberCandidateIdAndVendorId (
								@Param("emailAddress") String emailAddress,
								@Param("phoneNumber") String phoneNumber,
								@Param("candidateId") BigInteger candidateId 
								);

	
	
		@Query("SELECT p FROM AcyutaCandidate p where p.candidateId =:candidateId ")
		public AcyutaCandidate findByCandidateId(@Param("candidateId") BigInteger candidateId);
		
		@Query("SELECT p FROM AcyutaCandidate p where p.candidateId IN ("
				+ "SELECT q.candidateId FROM JobOrderMapping q where q.jobId=:jobId and q.status =:candidateJobStatus and q.organizationId=:organizationId"
				+ " and (:recruiterId='' or p.updatedBy=:recruiterId) and (:vendorId='' or p.vendorId=:vendorId) )"
				+ " order by p.updatedDate DESC")
		public List<AcyutaCandidate> findByCandidatesForJob(
				@Param("jobId") String jobId,
				@Param("organizationId") BigInteger organizationId,
				@Param("vendorId") String vendorId,
				@Param("recruiterId") String recruiterId,
				@Param("candidateJobStatus") String candidateJobStatus
				);

		
		@Query("SELECT p FROM AcyutaCandidate p where p.candidateId IN ("
				+ "SELECT q.candidateId FROM JobOrderMapping q where q.jobId=:jobId and q.status =:candidateJobStatus and q.organizationId=:organizationId"
				+ " and (p.candidateStatus=:candidateStatus) )"
				+ " order by p.updatedDate DESC")
		public List<AcyutaCandidate> findJobCandidatesByStatsu(
				@Param("jobId") String jobId,
				@Param("organizationId") BigInteger organizationId,
				@Param("candidateJobStatus") String candidateJobStatus,
				@Param("candidateStatus") String candidateStatus
				);

		

		@Query("SELECT p FROM AcyutaCandidate p where p.emailAddress =:emailAddress ")
		public List<AcyutaCandidate> findByEmailAddress(@Param("emailAddress") String emailAddress);
		
		

		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET candidateStatus = :candidateStatus, updatedDate=:updatedDate  WHERE candidateId = :candidateId")
		public int updateCandidateStatus(
				@Param("candidateId") BigInteger candidateId,
				@Param("candidateStatus") String candidateStatus,
				@Param("updatedDate") Date updatedDate
				);
	
		
		/**
		 * @param candidateId
		 * @param score
		 * @param interviewStatus
		 * @param pannaUserInterviewId
		 * @return
		 */
		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET candidateStatus = :interviewStatus, pannaReportURL = :reportURL, score=:score, pannaUserInterviewId =:pannaUserInterviewId, pannaInterviewComments=:pannaInterviewComments  WHERE candidateId = :candidateId")
		public int updatePannaInterviewDetails(
				@Param("reportURL") String reportURL,
				@Param("candidateId") BigInteger candidateId,
				@Param("score") Float score,
				@Param("interviewStatus") String interviewStatus,
				@Param("pannaUserInterviewId") BigInteger pannaUserInterviewId,
				@Param("pannaInterviewComments") String interviewComments
				);
		
		
		//updateLiveStatus
		/**
		 * @param techSkill
		 * @param communicationSkills
		 * @param candidateStatus
		 * @param interviewComments
		 * @param mliveScheduleId
		 * @param candidateId
		 * @return
		 */
		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET  techSkill = :techSkill, communicationSkills=:communicationSkills, candidateStatus=:candidateStatus, interviewComments=:interviewComments, mliveScheduleId =:mliveScheduleId  WHERE candidateId = :candidateId")
		public int updateLiveStatus(
				@Param("techSkill") Float techSkill,
				@Param("communicationSkills") Float communicationSkills,
				@Param("candidateStatus") String candidateStatus,
				@Param("interviewComments") String interviewComments,
				@Param("mliveScheduleId") BigInteger mliveScheduleId,
				@Param("candidateId") BigInteger candidateId
				);
		
		//update mlive report url
		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET  mLiveReportURL = :reportURL  WHERE candidateId = :candidateId")
		public int updateReportURL(
				@Param("reportURL") String reportURL,
				@Param("candidateId") BigInteger candidateId
				);
		
		//update mlive interview details 
		
		/**
		 * @Param reportURL,
		 * @param techSkill
		 * @param communicationSkills
		 * @param candidateStatus
		 * @param interviewComments
		 * @param mliveScheduleId
		 * @param candidateId
		 * @return
		 */
		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET mLiveReportURL = :reportURL,"
				+ " techSkill = :techSkill, "
				+ "communicationSkills=:communicationSkills, "
				+ "candidateStatus=:candidateStatus, "
				+ "interviewComments=:interviewComments, "
				+ "mliveScheduleId =:mliveScheduleId  WHERE candidateId = :candidateId")
		public int updateMliveInterviewDetails(
				@Param("reportURL") String reportURL,
				@Param("techSkill") Float techSkill,
				@Param("communicationSkills") Float communicationSkills,
				@Param("candidateStatus") String candidateStatus,
				@Param("interviewComments") String interviewComments,
				@Param("mliveScheduleId") BigInteger mliveScheduleId,
				@Param("candidateId") BigInteger candidateId
				);
		
		@Query("SELECT p FROM AcyutaCandidate p where p.organizationId =:organizationId ")
		public Page<AcyutaCandidate> getCandidateList(@Param("organizationId") BigInteger organizationId, Pageable pageable);
		
		
		@Query("SELECT p FROM AcyutaCandidate p where p.updatedBy=:recruiterId and  p.organizationId =:organizationId ")
		public Page<AcyutaCandidate> getCandidateListByRecruiter(@Param("recruiterId") BigInteger recruiterId,
				@Param("organizationId") BigInteger organizationId,Pageable pageable);
		
		@Transactional 
		@Modifying	
		@Query("UPDATE AcyutaCandidate SET  isBookMarked = :isBookMarked  WHERE candidateId = :candidateId")
		public int updateBookMarkField(
				@Param("isBookMarked") Boolean isBookMarked,
				@Param("candidateId") BigInteger candidateId
				);
		
		@Query("SELECT distinct consultancyName FROM AcyutaCandidate p where p.organizationId =:organizationId  and p.consultancyName !=''")
		public List<String> findConsultancyName(@Param("organizationId") BigInteger organizationId);
		
		
		@Query("SELECT distinct candidateStatus FROM AcyutaCandidate p where p.organizationId =:organizationId  and p.candidateStatus !=''")
		public List<String> findCandidateStatus(@Param("organizationId") BigInteger organizationId);
}
