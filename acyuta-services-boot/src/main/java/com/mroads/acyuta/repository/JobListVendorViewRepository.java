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

import com.mroads.acyuta.model.JobListVendorView;

@Repository
public interface JobListVendorViewRepository extends JpaRepository<JobListVendorView, BigInteger>{

	/*@Query("select p "
			+ "from JobListView p where (:jobId='All' or p.jobId= :jobId) "
			+ "and p.updatedDate BETWEEN :fDate and :tDate "
			+ "and (p.status in :statusList) "
			+ "and (:client='All' or p.clientName= :client) " 
			+ "and (:location = 'All' or p.location like '%'||:location||'%') "
			+ "and (:recruiter = 'All' or p.recruiter like '%'||:recruiter||'%') "
			+ "and p.organizationId= :organizationId " 
			+ "order by p.updatedDate DESC ")*/
	
	@Query("select p "
	+ "from JobListVendorView p where (:jobId='All' or p.jobId= :jobId) "
	+ "and  (p.updatedDate BETWEEN :fDate and :tDate) "
	+ "and (p.status in :statusList) "
	+ "and (:vendorId='All' or p.vendorId= :vendorId ) "
	+ "and (:location = 'All' or p.location like '%:'||:location||':%') "
	+ "and (:reportingManager = 'All' or p.hiringManager = :reportingManager) "
	+ "and (:hRManager = 'All' or p.recruiter like '%:'||:hRManager||':%') " // FIXME: add HR Manager to jobListData
	+ "and (:recruiter = 'All' or p.recruiter like '%:'||:recruiter||':%')"
	+ "and (:loginUserName = 'All' or p.recruiter like '%:'||:loginUserName||':%') "
	+ "and p.organizationId= :organizationId " 
	+ "order by p.activeDays ASC ")
	public Page<JobListVendorView> findVenderFilterList(
			@Param("fDate") Date fDate,
			@Param("tDate") Date tDate,
			@Param("jobId") String jobId,
			@Param("statusList") List<String> statusList,
			@Param("vendorId") String vendorId,
			@Param("location") String location,
			@Param("reportingManager") String reportingManager,
			@Param("hRManager") String hRManager,
			@Param("recruiter") String recruiter,
			@Param("loginUserName") String loginUserName,
			@Param("organizationId") BigInteger organizationId,
			Pageable pageable);
	
	
//	@Query("select p "
//	+ "from JobListVendorView p where (:jobId='All' or p.jobId= :jobId) "
//	+ "and  (p.updatedDate BETWEEN :fDate and :tDate) "
//	+ "and (p.status in :statusList) "
//	+ "and (:vendorId='All' or p.vendorId= :vendorId ) "
//	+ "and (:location = 'All' or p.location like '%:'||:location||':%') "
//	+ "and (:reportingManager = 'All' or p.hiringManager = :reportingManager) "
//	+ "and (:hRManager = 'All' or p.recruiter like '%:'||:hRManager||':%') " // FIXME: add HR Manager to jobListData
//	+ "and (:recruiter = 'All' or p.recruiter like '%:'||:recruiter||':%')"
//	+ "and (:loginUserName = 'All' or p.recruiter like '%:'||:loginUserName||':%') "
//	+ "and p.organizationId= :organizationId " 
//	+ "order by p.activeDays ASC ")
//	public Page<JobListVendorView> findAccountmanagerFilterList(
//			@Param("fDate") Date fDate,
//			@Param("tDate") Date tDate,
//			@Param("jobId") String jobId,
//			@Param("statusList") List<String> statusList,
//			@Param("vendorId") String vendorId,
//			@Param("location") String location,
//			@Param("reportingManager") String reportingManager,
//			@Param("hRManager") String hRManager,
//			@Param("recruiter") String recruiter,
//			@Param("loginUserName") String loginUserName,
//			@Param("organizationId") BigInteger organizationId,
//			Pageable pageable);
//


}
