package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.JobListVendorView;
import com.mroads.acyuta.model.JobListView;
import com.mroads.acyuta.model.JobOrder;
/**
 * 
 * @author SaiRameshGupta,vanisree
 *
 */

@Repository
public interface JobOrderRepository extends JpaRepository<JobOrder,BigInteger>{
	
	/**
	 * @param jobOrder
	 */
	@SuppressWarnings("unchecked")
	public JobOrder save(JobOrder jobOrder);

	/**
	 * 
	 * @param organizationId
	 * @param pageRequest
	 * @return
	 */
	@Query("select p"
			+" from JobListView p where (p.organizationId= :organizationId)"
			+ "and (p.status in :statusList) order by p.jobOrderId DESC")
	public Page<JobListView> findByOrganizationId(
			@Param("organizationId") BigInteger organizationId,
			@Param("statusList") List<String> statusList,
			Pageable pageRequest);

	/**
	 * 
	 * @param status
	 * @param organizationId
	 * @return
	 */
//	public List<JobOrder> findByStatusAndOrganizationIdAndClientName(String status,BigInteger organizationId,String clientName );
	
	
	
	@Query("select p"
			+" from JobOrder p where (p.organizationId= :organizationId)"
			+ "and (p.status =:status) and (p.clientName =:clientName) order by p.jobOrderId DESC")
	public List<JobOrder> findJobsByStatus(
			@Param("status") String status,
			@Param("organizationId") BigInteger organizationId,
			@Param("clientName") String clientName
		);
	
	
	public List<JobOrder> findByStatusAndOrganizationId(String status,BigInteger organizationId);

	/**
	 * 
	 * @param fDate
	 * @param tDate
	 * @param jobId
	 * @param status
	 * @param client
	 * @param location
	 * @param recruiterFullName
	 * @param OrganizationId
	 * @return
	 */
/*	@Query(value="select j.jobOrderId,j.jobId,j.jobTitle,j.createdDate,j.`status`,jr.rec as recruiters,jl.loc as locations "
			+ "from panna_JobOrder j,"
			+ "(select b.jobOrderId,group_concat(b.recruiterId SEPARATOR '-') rec from panna_Acyuta_Recruiter_JobOrder_Mapping b group by b.jobOrderId) jr, "
			+ "(select c.jobOrderId,group_concat(c.location SEPARATOR '-') loc from panna_Acyuta_JobOrder_Location_Mapping c group by c.jobOrderId) jl "
			+ "where j.jobOrderId = jr.jobOrderId and jr.jobOrderId = jl.jobOrderId and j.jobOrderId = jl.jobOrderId "
			+ "and j.updatedDate BETWEEN :fDate and :tDate "
			+ "and (:jobId='All' or j.jobId= :jobId) "
			+ "and (j.status in :statusList) "
			+ "and (:client='All' or j.clientName= :client) "
			+ "and j.organizationId= :organizationId " 
			+ "and (:location = 'All' or jl.loc like '%'||:location||'%') "
			+ "and (:recruiterId = 'All' or jr.rec like '%'||:recruiterId||'%') "
			+ "order by j.updatedDate DESC \n#pageable\n ",
			nativeQuery = true)*/

	/*public Page<JobListFilterResult> findfilteredlist(
			@Param("fDate") Date fDate,
			@Param("tDate") Date tDate,
			@Param("jobId") String jobId,
			@Param("statusList") List<String> statusList,
			@Param("client") String client,
			@Param("organizationId") BigInteger OrganizationId,
			@Param("location") String location,
			@Param("recruiterId") String recruiterId,
			Pageable pageable);
*/
	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	public Long countByOrganizationId(BigInteger organizationId);
	
	/**
	 * 
	 * @param jobId
	 * @return
	 */
	public Long countByJobIdAndOrganizationId(String jobId,BigInteger organizationI);
	
	/**
	 * 
	 * @param jobOrderId
	 * @return
	 */
	public JobOrder findByJobOrderId(BigInteger jobOrderId); 
	
	/**
	 * 
	 * @param jobs
	 * @return 
	 */
	@Transactional
	@Modifying
	@Query("UPDATE JobOrder j set j.status=:status where j.jobOrderId IN :jobs")
	public Integer setStatusByJobOrderId(@Param("jobs") List<BigInteger> jobs,@Param("status") String status);
	
	public Long countByJobId(String jobId );
	
	@Query("select distinct p.jobId from JobOrder p where p.organizationId = :organizationId and p.jobId!='null'" )
	public List<String> findJobId(@Param("organizationId") BigInteger organizationId);
	
	@Query("select distinct p.jobTitle from JobOrder p where p.organizationId = :organizationId and p.jobTitle!='null'" )
	public List<String> findJobTitle(@Param("organizationId") BigInteger organizationId);
	
	@Query("select distinct p.clientName from JobOrder p where p.organizationId = :organizationId and p.clientName!=' ' "
			+ "and p.status = :status "
			+"order by p.jobOrderId DESC")
	public List<String> findClients(@Param("organizationId") BigInteger organizationId, @Param("status") String status);
	
	@Query("select distinct p.jobLocation from JobOrder p where p.organizationId = :organizationId and p.jobLocation!=' '")
	public List<String> findLocations(@Param("organizationId") BigInteger organizationId);
	
	
	@Query("select distinct p.jobTitle from JobOrder p where p.organizationId = :organizationId and p.clientName=:clientName "
			+ "and p.status='ACTIVE'" )
	public List<String> findByClientName(@Param("organizationId") BigInteger organizationId,
			@Param("clientName") String clientName);
	
	@Query("select p from JobListView p "
				+"where  p.organizationId = :organizationId "
				+"and (p.recruiter like '%:'||:loginUserName||':%')"
				+"and (p.status in :statusList) "
				+ "order by p.jobOrderId DESC")
	public Page<JobListView> findJobsForRecruiter(@Param("loginUserName") String loginUserName,
												@Param("organizationId") BigInteger organizationId,
												@Param("statusList") List<String> statusList,
												Pageable pageRequest);
	
//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.skills= :skills,p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId ")
//	public void setMoreSkills(@Param("jobOrderId")BigInteger jobOrderId,@Param("skills") String skills,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy );
	
//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.jobDescription= :jobDescription,p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId ")
//	public void setJobDescription(@Param("jobOrderId")BigInteger jobOrderId,@Param("jobDescription") String jobDescription,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy);
 
//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.payRate= :payRate, p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId ")
//	public void setPayRate(@Param("jobOrderId")BigInteger jobOrderId,@Param("payRate") String payRate,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy);
	
//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.jobType= :jobType,p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId ")
//	public void setJobType(@Param("jobOrderId")BigInteger jobOrderId,@Param("jobType") String jobType,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy);
//	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.status= :status,p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId")
	public void setJobStatus(@Param("jobOrderId")BigInteger jobOrderId,@Param("status") String status,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy);

//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.jobDuration= :jobDuration,p.updatedDate = :updatedDate, p.updatedBy = :updatedBy where p.jobOrderId= :jobOrderId ")
//	public void setJobDuration(@Param("jobOrderId")BigInteger jobOrderId,@Param("jobDuration") String jobDuration,@Param("updatedDate") Date updatedDate,@Param("updatedBy")BigInteger updatedBy);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.interviewPositionId= :interviewPositionId where p.jobOrderId= :jobOrderId ")
	public void setinterviewPositionId(@Param("jobOrderId")BigInteger jobOrderId,@Param("interviewPositionId") BigInteger interviewPositionId);
	
//	@Transactional
//	@Modifying
//	@Query("update JobOrder p set p.pcStartDate= :pcStartDate and p.pcEndDate= :pcEndDate  where p.jobOrderId= :jobOrderId ")
//	public void setPotentialConversion(
//			@Param("jobOrderId")BigInteger jobOrderId,
//			@Param("pcStartDate") String pcStartDate,
//			@Param("pcEndDate") String pcEndDate
//			);
//	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.resourceType= :resourceType where p.jobOrderId= :jobOrderId ")
	public void setResourceType(@Param("jobOrderId")BigInteger jobOrderId,@Param("resourceType") String resourceType);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.equipment= :equipment where p.jobOrderId= :jobOrderId ")
	public void setEquipment(@Param("jobOrderId")BigInteger jobOrderId,@Param("equipment") String equipment);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.departmentName= :departmentName where p.jobOrderId= :jobOrderId ")
	public void setDepartmentName(@Param("jobOrderId")BigInteger jobOrderId,@Param("departmentName") String departmentName);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.maxBillRate= :maxBillRate where p.jobOrderId= :jobOrderId ")
	public void setMaxBillRate(@Param("jobOrderId")BigInteger jobOrderId,@Param("maxBillRate") String maxBillRate);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.billRate= :billRate where p.jobOrderId= :jobOrderId ")
	public void setBillRate(@Param("jobOrderId")BigInteger jobOrderId,@Param("billRate") String billRate);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.jobLevel= :jobLevel where p.jobOrderId= :jobOrderId ")
	public void setJobLevel(@Param("jobOrderId")BigInteger jobOrderId,@Param("jobLevel") String jobLevel);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.projectTargetDate= :projectTargetDate where p.jobOrderId= :jobOrderId ")
	public void setProjectTargetDate(@Param("jobOrderId")BigInteger jobOrderId,@Param("projectTargetDate") String projectTargetDate);
	
	@Transactional
	@Modifying
	@Query("update JobOrder p set p.potentialSalaryRange= :potentialSalaryRange where p.jobOrderId= :jobOrderId ")
	public void setPotentialSalaryRange(@Param("jobOrderId")BigInteger jobOrderId,@Param("potentialSalaryRange") String potentialSalaryRange);

	
	@Query("select p"
			+" from JobListVendorView p where (p.organizationId= :organizationId) "
			+"and (p.vendorId = :vendorId)"
			+ "and (p.status in :statusList) order by p.jobOrderId DESC")
	public Page<JobListVendorView> findJobsForVendor(
			@Param("vendorId") String vendorId,
			@Param("organizationId") BigInteger organizationId,
			@Param("statusList") List<String> statusList,
			Pageable pageRequest);
	
	
	@Query("select p"
			+" from JobListView p where (p.organizationId= :organizationId) and (p.accountManagerId= :accountManagerId) "
			+ "and (p.status in :statusList) order by p.jobOrderId DESC")
	public Page<JobListView> findJobsForAccountManager(
			@Param("accountManagerId") BigInteger accountManagerId,
			@Param("organizationId") BigInteger organizationId,
			@Param("statusList") List<String> statusList,
			Pageable pageRequest);

	
	@Query("select p"
			+" from JobListView p where (p.organizationId= :organizationId) and (p.createdBy =:recruiterId)  "
			+ "and (p.status in :statusList)  order by p.jobOrderId DESC")
	public Page<JobListView> findJobsForisD3NonTechManager(
			@Param("recruiterId") BigInteger recruiterId,
			@Param("organizationId") BigInteger organizationId,
			@Param("statusList") List<String> statusList,
			Pageable pageRequest);
	
}
