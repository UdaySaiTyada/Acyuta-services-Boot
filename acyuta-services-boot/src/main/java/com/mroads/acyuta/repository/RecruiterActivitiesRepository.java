package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.RecruiterActivities;

/**
 * @author Mahi K
 *
 */

@Scope("prototype")
@EnableJpaRepositories
public interface RecruiterActivitiesRepository extends JpaRepository<RecruiterActivities, BigInteger>{

	
	
	@Query("SELECT  activity FROM RecruiterActivities activity where candidateId = :candidateId order by updatedDate DESC")
	public List<RecruiterActivities> getRecruiterActivitiesByCandidateId(@Param("candidateId") BigInteger candidateId);
	
	@Query("SELECT  activity FROM RecruiterActivities activity where candidateId = :candidateId and jobOrderId = :jobOrderId order by jobOrderId, updatedDate DESC")
	public List<RecruiterActivities> getCandidateHistoryForJob(
			@Param("candidateId") BigInteger candidateId,
			@Param("jobOrderId") BigInteger jobOrderId
			);
}
