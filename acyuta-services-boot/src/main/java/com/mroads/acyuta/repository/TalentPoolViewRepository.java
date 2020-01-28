package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.TalentPoolView;

@Repository
public interface TalentPoolViewRepository extends JpaRepository<TalentPoolView, BigInteger>{

	
	
	@Query("select p from TalentPoolView p where "
	+ "(p.updatedDate BETWEEN :fDate and :tDate) "
	+ "and (:candidateTitle='All' or p.candidateTitle=:candidateTitle) "
	+ "and (:email ='All' or p.emailAddress=:email)"
	+ "and (:consultancyName ='All' or p.consultancyName=:consultancyName)"
	+ "and (:client = 'All' or p.clientName= :client) " 
	+ "and (:jobTitle ='All' or p.jobTitle=:jobTitle)"
	+ "and (:location = 'All' or p.location like '%:'||:location||':%') "
	+ "and (:recruiterName = 'All' or p.recruiterName like '%:'||:recruiterName||':%')"
	+ "and (:recruiterId='All' or p.updatedBy=:recruiterId)"
	+ "and (:status='All' or p.candidateStatus= :status)"
	+ "and (p.techSkill >= :startTechRating  and p.techSkill <= :endTechRating)"
	+ "and (p.communicationSkills >= :startCommRating and p.communicationSkills <= :endCommRating)"
	+ "and (p.score >=:startPannaScore and p.score <=:endPannaScore)"
	+ "and p.organizationId= :organizationId ")
	public Page<TalentPoolView> findfilteredlist(
			@Param("fDate") Date fDate,
			@Param("tDate") Date tDate,
			@Param("candidateTitle") String candidateTitle,
			@Param("email") String email,
			@Param("consultancyName") String consultancyName,
			@Param("client") String client,
			@Param("jobTitle") String jobTitle,
			@Param("location") String location,
			@Param("recruiterName") String recruiterName,
			@Param("recruiterId") String recruiterId,
			@Param("status") String status,
			@Param("startTechRating") float startTechRating,
			@Param("endTechRating") float endTechRating,
			@Param("startCommRating") float startCommRating,
			@Param("endCommRating") float endCommRating,
			@Param("startPannaScore") float startPannaScore,
			@Param("endPannaScore") float endPannaScore,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable);
}

