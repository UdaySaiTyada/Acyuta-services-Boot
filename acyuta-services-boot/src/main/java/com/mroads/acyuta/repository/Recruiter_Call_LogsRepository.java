package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.Recruiter_Call_Logs;

/**
 * @author praveen katakam
 *
 */
@Repository
public interface Recruiter_Call_LogsRepository extends JpaRepository<Recruiter_Call_Logs, BigInteger> {
	
		@Query("SELECT p FROM Recruiter_Call_Logs p where p.recruiterId =:recruiterId AND p.date between :startDate AND :endDate ")
		public List<Recruiter_Call_Logs> findByRecruiterId(@Param("recruiterId") BigInteger recruiterId,
				@Param("startDate") Date startDate,@Param("endDate") Date endDate,Pageable pageable);
		
		
		public List<Recruiter_Call_Logs> findByRecruiterId(BigInteger recruiterId);
		
		
}
