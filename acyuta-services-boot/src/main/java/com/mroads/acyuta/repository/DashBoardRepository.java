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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.DashBoard;
import com.mroads.acyuta.model.DashBoardRecruiters;


/**
 * 
 * @author Sreekanth
 *
 */

@Repository
public interface DashBoardRepository extends JpaRepository<DashBoard, BigInteger>{
	
	
//	@Query(value =" select  concat(b.firstName,' ',b.lastName) as recruiter, b.userId, b.loginDate as lastLogin, count(candidateId) addedResumes, "
//			+ " sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and d.propertyId !=6462 and date(d.updatedDate) >= CURDATE()) then 1 else 0 end) as submitted,  "
//			+ " sum(case when candidateStatus='PLACED WITH MROADS' then 1 else 0 end) closures, a.updatedDate, b.timeZoneId"
//			+ " from panna_Acyuta_User a, User_ b, Users_Orgs c  where  DATE(a.createdDate) = CURDATE( ) and b.userId=c.userId and c.organizationId=:organizationId and c.userId=a.updatedBy " 
//			+ " and b.userId not in (select userId from Users_Roles where roleId=1413314) and a.updatedBy=b.userId group by a.updatedBy", nativeQuery=true)
//	public List<Object> loadDashBoardData(
//			@Param("organizationId") BigInteger organizationId 
//			);

	 
		
		@Query(value =" select  a.userId, "
				+ " sum(IF(c.candidateStatus='SENT_ON_VOICEMAIL' and date(c.updatedDate) <= NOW() - INTERVAL 6 HOUR or c.candidateStatus='SENT JOB DESCRIPTION' and date(c.updatedDate) <= CURDATE(), 1, 0)) as Action "
				+ " from User_ a, Users_Orgs b, panna_Acyuta_User c where b.organizationId=:organizationId and b.organizationId=c.organizationId " 
				+ "and a.status!=5 and a.userId=b.userId and date(updatedDate) BETWEEN :startDate AND :endDate and a.userId=c.updatedBy group by a.userId ", nativeQuery=true)
		public List<Object> loadDashBoardDataActionRequired(
				@Param("organizationId") BigInteger organizationId,
				@Param("startDate") Date startDate ,
				@Param("endDate") Date endDate 
				);	
	
	
		

		
		
		@Query(value ="select d.userId, d.loginDate, d.timeZoneId, concat(d.firstName,' ',d.lastName) as recruiter, count(*) as addedResumes, "
				+ " sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and date(d.updatedDate) BETWEEN :startDate AND :endDate) then 1 else 0 end) as submitted,"
				+ " sum(case when b.candidateStatus='PLACED WITH MROADS' and(date(b.createdDate) BETWEEN :startDate AND :endDate ) then 1 else 0 end) closures "
				+ " from panna_Acyuta_User_JobOrder_Mapping a, panna_Acyuta_User b, panna_JobOrder c, User_ d "
				+ " where a.candidateId=b.candidateId and a.jobId=c.jobId and "
				+ " (DATE(a.createdDate) BETWEEN :startDate AND :endDate) and ((:clientName='' or c.clientName =:clientName ) and (:jobId='' or a.jobId =:jobId) ) and "
				+ " b.updatedBy=d.userId and c.organizationId=:organizationId  group by b.updatedBy ", nativeQuery=true)
		public List<Object> loadFilterData(
				@Param("organizationId") BigInteger organizationId ,
				@Param("startDate") Date startDate ,
				@Param("endDate") Date endDate ,
				@Param("jobId") String jobId,
				@Param("clientName") String clientName
				);
		
		
		// sum(case when b.candidateStatus='PLACED WITH MROADS' and(date(b.createdDate) BETWEEN '2018-06-01' AND '2018-06-22') then 1 else 0 end) closures
		
//		select b.updatedBy,concat(d.firstName,' ',d.lastName) as recruiter, count(*) as added,
//		sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and date(a.updatedDate) between '2018-06-01' and '2018-06-21') then 1 else 0 end) as submitted
//		from panna_Acyuta_User_JobOrder_Mapping a, panna_Acyuta_User b,panna_JobOrder c,User_ d
//		where a.candidateId=b.candidateId and a.jobId=c.jobId and  date(a.createdDate) between '2018-06-01' and '2018-06-21' and (c.clientName='Chronos Solutions' or c.clientName='') and (a.jobId='' or a.jobId='CH-61502' )
//		and b.updatedBy=d.userId group by b.updatedBy;
		
		// ---------------------------------
		
		@Query("select new com.mroads.acyuta.model.DashBoardRecruiters(concat(b.firstName,' ',b.lastName) as name, b.userId, b.loginDate, b.timeZoneId )"
				+ " from User b,UsersOrgs c  "
				+ " where  b.userId=c.userId and c.organizationId=:organizationId and b.status!=5 "
				+ "and b.userId  in (select userId from UsersRoles where roleId in (1413314)) group by b.userId")
		public List<DashBoardRecruiters> getRecruiters(
				@Param("organizationId") BigInteger organizationId 
				);	
	
		
		
		
		@Query(" select count(*) from JobOrder where status='ACTIVE' and organizationId =:organizationId and date(createdDate) BETWEEN :startDate AND :endDate")
		public Integer getActiveJobs(
				@Param("organizationId") BigInteger organizationId ,
				@Param("startDate") Date startDate ,
				@Param("endDate") Date endDate 
				);	
	
		@Query(value = " select  b.userId, sum( a.candidateId and(date(a.createdDate) BETWEEN :startDate AND :endDate)) added,"
				+ " sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and d.propertyId !=6462 and date(d.updatedDate) BETWEEN :startDate AND :endDate) then 1 else 0 end) as submitted, "
				+ " sum(case when candidateStatus='PLACED WITH MROADS' and(date(a.createdDate) BETWEEN :startDate AND :endDate) then 1 else 0 end) closures, MAX(a.updatedDate)"
				+ " from panna_Acyuta_User a,User_ b, Users_Orgs c "
				+ " where a.organizationId=:organizationId and a.updatedBy=b.userId and a.organizationId=c.organizationId and b.userId=c.userId "
				+ " and b.status!=5 and b.userId  in (select userId from Users_Roles where roleId in (1413314)) group by a.updatedBy",nativeQuery=true)
		public List<Object> getDashBoardStatistics(
				@Param("organizationId") BigInteger organizationId ,
				@Param("startDate") Date startDate ,
				@Param("endDate") Date endDate 
				);	
		
		
		
//		
//		@Query( value ="select concat(b.firstName,' ',b.lastName) as name, b.userId, b.loginDate as lastLogin, b.timeZoneId "
//				+ " from User_ b,Users_Orgs c  "
//				+ " where  b.userId=c.userId and c.organizationId=:organizationId and b.status!=5 "
//				+ "and b.userId  in (select userId from Users_Roles where roleId in (1413314)) group by b.userId",nativeQuery=true )
//		public List<Object> getRecruiters(
//				@Param("organizationId") BigInteger organizationId 
//				);	
		
//		select sum( a.candidateId and(date(a.createdDate) BETWEEN '2018-06-21' AND '2018-06-21')) added,
//		sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and d.propertyId !=6462 and date(d.updatedDate) BETWEEN '2018-06-21' AND '2018-06-21') then 1 else 0 end) as submitted,
//		sum(case when candidateStatus='PLACED WITH MROADS' and(date(a.createdDate) BETWEEN '2018-06-21' AND '2018-06-21') then 1 else 0 end) closures
//		from panna_Acyuta_User a,User_ b, Users_Orgs c
//		where a.organizationId=44932 and a.updatedBy=b.userId and a.organizationId=c.organizationId and b.userId=c.userId;
//	
		
		
		
//		select count(*) from panna_JobOrder where status='ACTIVE' and date(createdDate) between '2018-06-21' and '2018-06-21';
		
//		 and e.status ='ACTIVE'
//		select concat(b.firstName,' ',b.lastName) as recruiter, b.userId, b.loginDate as lastLogin,count(distinct a.candidateId),
//		sum(case when a.candidateId in (select candidateId from panna_Acyuta_StatusTracking d where d.propertyId=818 and d.propertyId !=6462 and date(d.updatedDate)between '2018-06-01' and '2018-06-22' ) then 1 else 0 end) as Submitted,
//		sum(case when candidateStatus='PLACED WITH MROADS' then 1 else 0 end) closures,a.updatedDate, b.timeZoneId 
//		from panna_Acyuta_User a,User_ b,Users_Orgs c,panna_Acyuta_User_JobOrder_Mapping d, panna_JobOrder e
//		where (DATE(a.createdDate) between '2018-06-01' and '2018-06-22') and ((e.jobId='' or e.jobId='') or (e.clientName='Hilton' or e.clientName=''))  
//		and b.userId=c.userId and c.organizationId=44932 and c.userId=a.updatedBy and a.candidateId=d.candidateId and d.jobId=e.jobId
//		and b.userId not in (select userId from Users_Roles where roleId=1413314)
//		and a.updatedBy=b.userId group by a.updatedBy;
		
		
		
//		select concat(b.firstName,' ',b.lastName) as recruiter, b.userId, b.loginDate as lastLogin, b.timeZoneId 
//		from User_ b,Users_Orgs c 
//		where  b.userId=c.userId and c.organizationId=44932 and b.status!=5
//		and b.userId  in (select userId from Users_Roles where roleId in (1413314))
//		group by b.userId;
	
	
}	
