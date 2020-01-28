package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mroads.acyuta.model.Recruiters;

@Repository
public interface RecruitersRepository extends JpaRepository<Recruiters, BigInteger>{
	
	@SuppressWarnings("unchecked")
	public Recruiters save(Recruiters recruiters);

	@Query("select r from Recruiters r where r.jobOrderId = :jobOrderId and r.status = :status")
	public List<Recruiters> findByJobOrderIdAndStatus(@Param("jobOrderId")BigInteger jobOrderId,@Param("status")String status);

	public List<Recruiters> getByJobOrderId(BigInteger jobOrderId);
	
	public Recruiters getByJobOrderIdAndRecruiterId(BigInteger jobOrderId,BigInteger recruiterId);
	
	@Transactional
	@Modifying
	@Query("update Recruiters p set p.status= :status,p.updatedDate= :updatedDate where p.mappingId= :mappingId ")
	public void  setStatus(@Param("mappingId") BigInteger mappingId,@Param("status") String status,@Param("updatedDate") Date updatedDate ); 
}
