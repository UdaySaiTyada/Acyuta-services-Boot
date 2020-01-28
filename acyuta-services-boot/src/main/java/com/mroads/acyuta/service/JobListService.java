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
package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.model.JobListVendorView;
import com.mroads.acyuta.model.JobListView;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.Recruiters;
import com.mroads.acyuta.repository.JobListVendorViewRepository;
import com.mroads.acyuta.repository.JobListViewRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.RecruitersRepository;

@Service
public class JobListService {
	private static final Logger log=LoggerFactory.getLogger(JobListService.class);
	
	@Autowired
	private JobOrderRepository jobOrderRepository;
	
	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private JobListViewRepository jobListViewRepository;
	
	@Autowired
	private RecruitersRepository recruiterRepository;
	
	
	@Autowired
	private JobListVendorViewRepository jobListVendorViewRepository;
	
	
	private static DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	
	/**
	 * 
	 * @param organizationId
	 * @param pageRequest
	 * @return
	 */
	public JSONObject getJobsForJobListTable(HttpServletRequest request) {
		
		// BigInteger organizationId, BigInteger recruiterId, PageRequest pageRequest,Boolean isRecruiterAdmin
		List<String> statusList = new ArrayList<>();
		statusList.add(JobOrderConstants.JOB_ACTIVE_STATUS);
		Page<JobListView> jobsPage = null;
		Page<JobListVendorView>  vendorJobsPage = null;
		JSONObject response=new JSONObject();
		
		int page = request.getIntHeader(Constants.PAGE_NUMBER);
		int size = request.getIntHeader(Constants.PAGE_SIZE);
		String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		String userIdString = request.getHeader(JobOrderConstants.USER_ID_STRING);
		Boolean isRecruiterAdmin = Boolean.valueOf(request.getHeader(Constants.IS_RECRUITER_ADMIN));
		log.info(
				"Request obtained for get jobsList default render page with " + "organizationId = >{}< ,page = >{}<, size = >{}<, "
						+ "recruiterId = >{}<, isRecruiterAdmin = >{}<", orgIdString, page, size, userIdString, isRecruiterAdmin);
		BigInteger organizationId = new BigInteger(orgIdString);
		BigInteger recruiterId = new BigInteger(userIdString);
		String vendorId = request.getHeader(JobOrderConstants.VENDOR_ID_STRING);
		log.info("vendorId : {}",vendorId);
		PageRequest pageRequest = new PageRequest(page, size);
		
		Boolean isVendorManger = Boolean.valueOf(request.getHeader(Constants.IS_VENDOR_MANAGER));
		Boolean isAccountManager = Boolean.valueOf(request.getHeader(Constants.IS_ACCOUNT_MANAGER));
		Boolean isD3NonTechManager = Boolean.valueOf(request.getHeader(Constants.IS_D3_NonTech_MANAGER));

		if(isAccountManager) {
			log.info("isAccountManager: true");
			log.info("isVendorManger statusList: {}, vendorId: {}, organizationId: {}",statusList,vendorId,organizationId);
			//NOTE: If logged in user is account Manager
			jobsPage = jobOrderRepository.findJobsForAccountManager(recruiterId, organizationId, statusList, pageRequest);
			response = processManagerJob(jobsPage);
			return response;
		}
		if(isD3NonTechManager) {
			log.info("isD3NonTechManager: true");
			log.info("isD3NonTechManager statusList: {}, vendorId: {}, organizationId: {}",statusList,vendorId,organizationId);
			jobsPage = jobOrderRepository.findJobsForisD3NonTechManager(recruiterId, organizationId, statusList, pageRequest);
			response = processManagerJob(jobsPage);
			return response;
		}
		
		if(isVendorManger) {
			log.info("isVendorManger: true");
			log.info("isVendorManger statusList: {}, vendorId: {}, organizationId: {}",statusList,vendorId,organizationId);
			vendorJobsPage = jobOrderRepository.findJobsForVendor(vendorId, organizationId, statusList, pageRequest);
			response = processVendorData(vendorJobsPage);
			return response;
		}
		else if(!isRecruiterAdmin) {
			log.info("isRecruiterAdmin: true");
			String loginUserName = jobRequisitionService.getRecruitersName(recruiterId);
			jobsPage = jobOrderRepository.findJobsForRecruiter(loginUserName, organizationId, statusList, pageRequest);
			response = processManagerJob(jobsPage);
			return response;
		}
		else {
			log.info("logged in user is RecruiterAdmin : ");
			jobsPage = jobOrderRepository.findByOrganizationId(organizationId, statusList, pageRequest);	
			response = processManagerJob(jobsPage);
			return response;
		}

	}
	
	
	
	private JSONObject processManagerJob(Page<JobListView> jobsPage) {
		log.info("processVendorData : called");
		JSONObject response=new JSONObject();
		JSONArray jobsArray=new JSONArray();
		Pattern pattern = Pattern.compile(":,:");
		try {
			for(JobListView job : jobsPage) {

				JSONObject jobJson=new JSONObject();
				jobJson.put(JobOrderConstants.JOB_ORDER_ID_STRING, job.getJobOrderId().toString());
				jobJson.put(JobOrderConstants.JOB_ID_STRING,job.getJobId());
				jobJson.put(JobOrderConstants.JOB_TITLE_STRING,job.getJobTitle());
				
				jobJson.put(JobOrderConstants.VICE_PRESIDENT_STRING,job.getVicePresident());
				jobJson.put(JobOrderConstants.HIRING_MANAGER_STRING,job.getHiringManager());
				

				String[] jobLocations = pattern.split(job.getLocation());
				jobLocations[0] = StringUtils.stripStart(jobLocations[0], ":");
				jobLocations[jobLocations.length - 1] = StringUtils.stripEnd(jobLocations[jobLocations.length - 1], ":");
				jobJson.put(JobOrderConstants.JOB_LOCATIONS_STRING,jobLocations);
				
				String[] jobRecruiters = pattern.split(job.getRecruiter());
				jobRecruiters[0] = StringUtils.stripStart(jobRecruiters[0], ":");
				jobRecruiters[jobRecruiters.length - 1] = StringUtils.stripEnd(jobRecruiters[jobRecruiters.length - 1], ":");
				jobJson.put(JobOrderConstants.RECRUITERS_STRING, jobRecruiters);
				
				String[] vendors =new String[0];
				if(null!=job.getVendor()) {
					vendors = pattern.split(job.getVendor());	
					vendors[0] = StringUtils.stripStart(vendors[0], ":");
					vendors[vendors.length - 1] = StringUtils.stripEnd(vendors[vendors.length - 1], ":");
				}
				jobJson.put(JobOrderConstants.VENDORS_STRING, vendors);
				jobJson.put(JobOrderConstants.ACTIVE_DAYS_STRING,job.getActiveDays());
				jobJson.put(JobOrderConstants.CLIENT_NAME_STRING, job.getClientName());
				jobJson.put(JobOrderConstants.STATUS_STRING, job.getStatus());
				
				jobJson.put(JobOrderConstants.PROJECT_TARGET_DATE, job.getProjectTargetDate());
				jobJson.put(JobOrderConstants.RESOURCE_TYPE, job.getResourceType());
				
				String formattedDate = dateFormat.format(job.getCreatedDate()).toString();
				jobJson.put(JobOrderConstants.CREATED_DATE_STRING, formattedDate);
				
				Integer total = job.getTotal();
				jobJson.put(JobOrderConstants.TOTAL_STRING, (total==null) ? 0 : total);
				
				Integer submitted = job.getSubmitted();
				jobJson.put(JobOrderConstants.SUBMITTED_STRING, (submitted==null) ? 0 : submitted);
				
				Integer fundingApprovalPending = job.getFundingApprovalPending();
				jobJson.put(JobOrderConstants.FUNDING_APPROVAL_PENDING, (fundingApprovalPending==null) ? 0 : fundingApprovalPending);
				
				
				jobsArray.put(jobJson);
			}
			log.info("Retrieved jobs count: >{}< ",jobsArray.length());
			response.put("jobs",jobsArray);
			long totalNoOfJobs = jobsPage.getTotalElements();
			response.put("totalNoOfJobs",totalNoOfJobs);
			log.debug("Jobs returned for are {}",response.toString());
			return response;
		}
		catch(Exception e) {
			log.error("Error occurred when getting jobs based on organizationId",e);
			return null;
		}
	 
	}
	
	
	private JSONObject processVendorData(Page<JobListVendorView> vendorJobsPage) {
		log.info("processVendorData : called");
		JSONObject response=new JSONObject();
		JSONArray jobsArray=new JSONArray();
		Pattern pattern = Pattern.compile(":,:");
		try {
			for(JobListVendorView job : vendorJobsPage) {

				JSONObject jobJson=new JSONObject();
				jobJson.put(JobOrderConstants.JOB_ORDER_ID_STRING, job.getJobOrderId().toString());
				jobJson.put(JobOrderConstants.JOB_ID_STRING,job.getJobId());
				jobJson.put(JobOrderConstants.JOB_TITLE_STRING,job.getJobTitle());
				
				jobJson.put(JobOrderConstants.VICE_PRESIDENT_STRING,job.getVicePresident());
				jobJson.put(JobOrderConstants.HIRING_MANAGER_STRING,job.getHiringManager());
				

				String[] jobLocations = pattern.split(job.getLocation());
				jobLocations[0] = StringUtils.stripStart(jobLocations[0], ":");
				jobLocations[jobLocations.length - 1] = StringUtils.stripEnd(jobLocations[jobLocations.length - 1], ":");
				jobJson.put(JobOrderConstants.JOB_LOCATIONS_STRING,jobLocations);
				
				String[] jobRecruiters = pattern.split(job.getRecruiter());
				jobRecruiters[0] = StringUtils.stripStart(jobRecruiters[0], ":");
				jobRecruiters[jobRecruiters.length - 1] = StringUtils.stripEnd(jobRecruiters[jobRecruiters.length - 1], ":");
				jobJson.put(JobOrderConstants.RECRUITERS_STRING, jobRecruiters);
				
				String[] vendors =new String[0];
				if(null!=job.getVendor()) {
					vendors = pattern.split(job.getVendor());	
					vendors[0] = StringUtils.stripStart(vendors[0], ":");
					vendors[vendors.length - 1] = StringUtils.stripEnd(vendors[vendors.length - 1], ":");
				}
				jobJson.put(JobOrderConstants.VENDORS_STRING, vendors);
				jobJson.put(JobOrderConstants.ACTIVE_DAYS_STRING,job.getActiveDays());
				jobJson.put(JobOrderConstants.CLIENT_NAME_STRING, job.getClientName());
				jobJson.put(JobOrderConstants.STATUS_STRING, job.getStatus());
				
				jobJson.put(JobOrderConstants.PROJECT_TARGET_DATE, job.getProjectTargetDate());
				jobJson.put(JobOrderConstants.RESOURCE_TYPE, job.getResourceType());
				
				String formattedDate = dateFormat.format(job.getCreatedDate()).toString();
				jobJson.put(JobOrderConstants.CREATED_DATE_STRING, formattedDate);
				
				Integer total = job.getTotal();
				jobJson.put(JobOrderConstants.TOTAL_STRING, (total==null) ? 0 : total);
				
				Integer submitted = job.getSubmitted();
				jobJson.put(JobOrderConstants.SUBMITTED_STRING, (submitted==null) ? 0 : submitted);
				
				jobsArray.put(jobJson);
			}
			log.info("Retrieved jobs count: >{}< ",jobsArray.length());
			response.put("jobs",jobsArray);
			long totalNoOfJobs = vendorJobsPage.getTotalElements();
			response.put("totalNoOfJobs",totalNoOfJobs);
			log.debug("Jobs returned for are {}",response.toString());
			return response;
		}
		catch(Exception e) {
			log.error("Error occurred when getting jobs based on organizationId",e);

			return null;
		}
	}

	/**
	 * 
	 * @param body
	 * @return JobOrder values
	 */
	public JSONObject filters(String body) {

		try {
			JSONObject input=new JSONObject(body);
			log.info("getting all the parameters for filtering");
			String startDate=input.getString(Constants.START_DATE_STRING);
			String endDate=input.getString(Constants.END_DATE_STRING);
			String jobId = input.getString(JobOrderConstants.JOB_ID_STRING);
			JSONArray status=input.getJSONArray(JobOrderConstants.STATUS_STRING);
			ArrayList<String> statusList = new ArrayList<String>();     
			if (status.length()!=0) { 
				for (int i=0;i<status.length();i++){ 
					statusList.add(status.getString(i));
				} 
			}
			else
			{
				statusList.add(JobOrderConstants.JOB_ACTIVE_STATUS);
				statusList.add(JobOrderConstants.JOB_DRAFTED_STATUS);
				statusList.add(JobOrderConstants.JOB_ARCHIVED_STATUS);
				statusList.add(JobOrderConstants.JOB_HALTED_STATUS);
				statusList.add(JobOrderConstants.JOB_DO_NOT_WORK_STATUS);
				statusList.add(JobOrderConstants.JOB_ON_HOLD_STATUS);
				
			}
			String client=input.getString(Constants.CLIENT_STRING);
			String location=(input.getString(Constants.LOCATION_STRING));
			String organizationId=input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);
			String jobType=input.has(JobOrderConstants.JOB_TYPE_STRING)? input.getString(JobOrderConstants.JOB_TYPE_STRING):"Assigned";
			BigInteger logInUserId = new BigInteger(input.getString(Constants.LOG_IN_USER_ID_STRING));
			String recruiterFullName = input.getString(Constants.RECRUITER_STRING);
			
			Boolean isVendorManger = input.has(Constants.IS_VENDOR_MANAGER)? input.getBoolean(Constants.IS_VENDOR_MANAGER):false;
			Boolean isAccountManager = input.has(Constants.IS_ACCOUNT_MANAGER)? input.getBoolean(Constants.IS_ACCOUNT_MANAGER):false;
			
			Boolean isHiringManager = input.has("isHiringManager")? input.getBoolean("isHiringManager"):false;

			
			String vendorId = input.has(Constants.VENDOR_ID_STR) ? input.getString(Constants.VENDOR_ID_STR) : "All";
			String hRManager =  input.has(Constants.HR_MANAGER_STR) ? input.getString(Constants.HR_MANAGER_STR) : "All";
			String reportingManager =  input.has(Constants.REPORTING_MANAGER_STR) ? input.getString(Constants.REPORTING_MANAGER_STR) : "All";
			
			//vendorId hRManager reportingManager
			
			String logInUserName=jobRequisitionService.getRecruitersName(logInUserId);
			logInUserName  = jobType.equals("Assigned")?logInUserName:"All";
			
			BigInteger orgId =new BigInteger(organizationId);
			log.info("convering into date format");
			Date fDate="Invalid Date".equals(startDate)?format.parse("01-01-1900 00:00:00"):format.parse(startDate + " 00:00:00");
			Date tDate="Invalid Date".equals(endDate)?format.parse("01-01-2900 23:59:59"):format.parse(endDate + " 23:59:59");

			//System.out.println(fDate+" "+tDate+" "+jobId+" "+client+" "+location+" "+status+" "+jobType+" "+orgId);
			Integer page=input.getInt(Constants.PAGE_NUMBER);
			Integer size=input.getInt(Constants.PAGE_SIZE);
			PageRequest pageRequest=new PageRequest(page,size);

			log.info("Filtering jobs using FromDate = {}, ToDate = {}, jobId={},statusList = {}"
					+", client = {}, location = {}, recruiterName = {}, loginUserName = {}, organizationId = {}, pageRequest = {}",
					fDate,tDate,jobId,statusList,client,location,recruiterFullName,logInUserName,orgId,pageRequest);

			
			log.info("Filtering jobs using vendorId = {}, hRManager = {}, reportingManager={}", vendorId,hRManager,reportingManager);
			
			//FIXME: During the TechSrc Manager apply filer with vendorId 
			// we are getting only that vendor job detail with individual submission count.
			if((isVendorManger || !vendorId.endsWith("All"))&& !isHiringManager) {
				log.info("isVendorManger : true");

				logInUserName =	isVendorManger? logInUserName:"All";
				
				log.info("logInUserName : {}", logInUserName);
				Page<JobListVendorView>  vendorJobsPage = null;
				vendorJobsPage = jobListVendorViewRepository.findVenderFilterList(fDate,tDate,jobId,statusList,vendorId,
						location, reportingManager, hRManager, recruiterFullName,logInUserName,orgId,pageRequest);
				JSONObject response = processVendorData(vendorJobsPage);
				return response;
				 
			}
			
			if(isAccountManager) {
				log.info("isAccountManager: true");

				logInUserName =	isVendorManger? logInUserName:"All";
				log.info("logInUserName : {}", logInUserName);
				Page<JobListView> jobList = jobListViewRepository.findAccountmanagerFilterList(fDate,tDate,jobId,statusList,client,
						location,vendorId, reportingManager, hRManager, recruiterFullName,logInUserId,orgId,pageRequest);
				JSONObject response = processJobListView(jobList); 
				return response;
				 
			}
			Page<JobListView> jobList = jobListViewRepository.findfilteredlist(fDate,tDate,jobId,statusList,client,
					location,vendorId, reportingManager, hRManager, recruiterFullName,logInUserName,orgId,pageRequest);

			JSONObject response= processJobListView(jobList); 
			return response;
		} catch (Exception e) {
			log.error("Error occurred when filtering jobs from db ",e);
		}

		return null;
	}
	
	
	JSONObject processJobListView(Page<JobListView> jobList){
	
		JSONArray jobsArray = new JSONArray();
		JSONObject job;
		JSONObject response=new JSONObject();
		Pattern pattern = Pattern.compile(":,:");
		for (JobListView jobs: jobList) {
			job = new JSONObject();
			job.put(JobOrderConstants.JOB_ORDER_ID_STRING, jobs.getJobOrderId());
			job.put(JobOrderConstants.JOB_ID_STRING, jobs.getJobId());
			job.put(JobOrderConstants.JOB_TITLE_STRING,jobs.getJobTitle());
			
			job.put(JobOrderConstants.VICE_PRESIDENT_STRING,jobs.getVicePresident());
			job.put(JobOrderConstants.HIRING_MANAGER_STRING,jobs.getHiringManager());
			
			String[] jobLocations = pattern.split(jobs.getLocation());
			jobLocations[0] = StringUtils.stripStart(jobLocations[0], ":");
			jobLocations[jobLocations.length - 1] = StringUtils.stripEnd(jobLocations[jobLocations.length - 1], ":");
			job.put(JobOrderConstants.JOB_LOCATIONS_STRING,jobLocations);
			
			String[] jobRecruiters = pattern.split(jobs.getRecruiter());
			jobRecruiters[0] = StringUtils.stripStart(jobRecruiters[0], ":");
			jobRecruiters[jobRecruiters.length - 1] = StringUtils.stripEnd(jobRecruiters[jobRecruiters.length - 1], ":");
			job.put(JobOrderConstants.RECRUITERS_STRING, jobRecruiters);
			
			
			String[] vendors =new String[0];
			if(null!=jobs.getVendor()) {
				vendors = pattern.split(jobs.getVendor());	
				vendors[0] = StringUtils.stripStart(vendors[0], ":");
				vendors[vendors.length - 1] = StringUtils.stripEnd(vendors[vendors.length - 1], ":");
			}
			job.put(JobOrderConstants.VENDORS_STRING, vendors);
			
			job.put(JobOrderConstants.ACTIVE_DAYS_STRING, jobs.getActiveDays());
			job.put(JobOrderConstants.CLIENT_NAME_STRING,jobs.getClientName());
			job.put(JobOrderConstants.STATUS_STRING, jobs.getStatus());
			
			String formattedDate = dateFormat.format(jobs.getCreatedDate()).toString();
			job.put(JobOrderConstants.CREATED_DATE_STRING, formattedDate);
			job.put(JobOrderConstants.RESOURCE_TYPE, jobs.getResourceType());
			job.put(JobOrderConstants.PROJECT_TARGET_DATE, jobs.getProjectTargetDate());
			
			
			Integer total = jobs.getTotal();
			job.put(JobOrderConstants.TOTAL_STRING,(total==null) ? 0 : total);

			Integer submitted = jobs.getSubmitted();
			job.put(JobOrderConstants.SUBMITTED_STRING, (submitted==null) ? 0 : submitted);

			jobsArray.put(job);
		}
		//log.info("Retrieved {} jobs using filters",jobsArray.length(),organizationId);
		response.put("jobs", jobsArray);
		long totalNoOfJobs = jobList.getTotalElements();
		log.info("Total jobs available based on filters are {}",totalNoOfJobs);
		response.put("totalNoOfJobs",jobList.getTotalElements());
	//	log.debug("Jobs returned based on filters are {} in page number--{} and with size--{}",response.toString(),page,size);
		
		return response;
		
	}
	

	/**
	 * 
	 * @param jobs
	 * @param status
	 * @return
	 */
	public Integer setJobStatus(List<BigInteger> jobs, String status, String userId, String organizationId) {
		
		log.debug("Setting JobStatus as {} for jobs = {} ", status, jobs);
		// returns the number of rows updated
		int updatedNumber = jobOrderRepository.setStatusByJobOrderId(jobs, status);
		String mailBody = "";
		String mailSubject;
		int applyTemplate = Constants.DO_NOT_APPLY_TEMPLATE;
		
		for (int i = 0; i < jobs.size(); i++) {
			BigInteger jobOrderId = jobs.get(i);
			log.info("{}", jobOrderId);
			JobOrder job = jobOrderRepository.findByJobOrderId(jobOrderId);
			log.info("JobOrder Id : {} ", job.getJobOrderId());
			List<Recruiters> recruiters = recruiterRepository.findByJobOrderIdAndStatus(jobOrderId,
					Constants.RECRUITER_MAPPING_ACTIVE_STATUS);

			List<String> toAddressList = new ArrayList<String>();

			if (status.equalsIgnoreCase(JobOrderConstants.JOB_ARCHIVED_STATUS)) {
				status = "closed/filled";
			}

			if (status.equalsIgnoreCase(JobOrderConstants.JOB_ACTIVE_STATUS)) {
				status = "modified";
				applyTemplate = Constants.APPLY_TEMPLATE;
			}
			mailSubject = "This is to inform you that " + job.getJobId() + "(" + job.getJobTitle() + ")" + " for "
					+ job.getClientName() + " has been " + status;

			if (status.equalsIgnoreCase(JobOrderConstants.JOB_DO_NOT_WORK_STATUS)) {
				mailSubject = "This is to inform you that do not work on " + job.getJobId() + "(" + job.getJobTitle()
						+ ")" + " for " + job.getClientName();
			}

			for (Recruiters recruiter : recruiters) {
				log.info("Recruiter Id: {}", recruiter.getRecruiterId());
				UserDTO userDTO = jobRequisitionService.getRecruitersInfo(recruiter.getRecruiterId());
				toAddressList.add(userDTO.getEmailAddress());
			}
			jobRequisitionService.sendRecruitersMail(new BigInteger(userId), toAddressList,  mailBody, job, mailSubject, Constants.JOB_ASSIGN_NOTIFICATION);
		}
		return updatedNumber;
	}
	
}
