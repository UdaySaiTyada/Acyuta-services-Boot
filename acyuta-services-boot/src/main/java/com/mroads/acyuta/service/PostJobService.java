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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.InterviewPositionsDTO;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.model.InterviewPositions;
import com.mroads.acyuta.model.JobLocation;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.repository.InterviewPositionsRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;

@Service
public class PostJobService {
	private static final Logger log=LoggerFactory.getLogger(PostJobService.class);

	@Autowired
	private InterviewPositionsRepository interviewPositionsRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private JobOrderRepository jobOrderRepository;


	/**
	 * 
	 * @param organizationId
	 * @return
	 */
	public List<InterviewPositionsDTO> getInterviewPositions(BigInteger organizationId) throws Exception{

		List<InterviewPositions> allActivePositions = interviewPositionsRepository.findByOrganizationIdAndPositionStatus(Constants.JOB_ACTIVE_STATUS, organizationId);
		List<InterviewPositionsDTO> positionsDTOList=new ArrayList<>();
		for(InterviewPositions position:allActivePositions) {
			positionsDTOList.add((InterviewPositionsDTO)ObjectConverter.convert(position, new InterviewPositionsDTO()));
		}
		log.info("Returning {} interview positions for organizationId = {}",positionsDTOList.size(),organizationId);
		log.debug("Interview Positions returning are {}",positionsDTOList);

		return positionsDTOList;
	}

	/**
	 * 
	 * @param jobOrderId
	 * @return String
	 */
	public String getInternalLink(BigInteger jobOrderId) {
		String encodedJobOrderId=AcyutaUtils.getEncodedValue(jobOrderId.toString());
		log.debug("Encoded JobOrderId for {} is {}",jobOrderId,encodedJobOrderId);
		String presentEnvironment=environment.getProperty(Constants.ENVIRONMENT);

		String link="https://"+presentEnvironment+".mroads.com/web/acyuta/apply-online?internal=true&jobId="+encodedJobOrderId;
		log.debug("internal link generated is {}",link);
		return link;
	}

	/**
	 * 
	 * @param jobOrderId
	 * @return String
	 */
	public String getExternalLink(BigInteger jobOrderId) {

		String encodedJobOrderId=AcyutaUtils.getEncodedValue(jobOrderId.toString());
		log.debug("Encoded JobOrderId for {} is {}",jobOrderId,encodedJobOrderId);
		String presentEnvironment=environment.getProperty(Constants.ENVIRONMENT);
		String link="https://"+presentEnvironment+".mroads.com/web/acyuta/apply-online?internal=false&jobId="+encodedJobOrderId;
		log.debug("External link generated is {}",link);

		return link;
	}

	/**
	 * 
	 * @param body
	 * @return String
	 */
	public String sendMail(String body) {
		String mailBody="";
		String mailSubject=" ";
		String logInUserFullName="";
		String logInUserEmailAddress="";
		JSONArray emailsTagList;
		String organizationId="";
		List<String> toAddressList=new ArrayList<String>();
		try {
			JSONObject input=new JSONObject(body);
			mailBody=input.getString(Constants.MAIL_BODY);
			mailSubject=input.getString(Constants.MAIL_SUBJECT);
			String userId=input.getString(JobOrderConstants.USER_ID_STRING);
			emailsTagList=input.getJSONArray(Constants.MAIL_LIST);
			organizationId=input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);
			for(int i=0;i<emailsTagList.length();i++) {
				toAddressList.add(emailsTagList.getString(i));
			}

			log.info("Input obtained to send a mail is Body = >{}<, "
					+"Subject = >{}<, UserId = {}, ToAddressList = {}"
					,mailBody,mailSubject,userId,toAddressList.toString());
			log.info("value--{}",userId);
			//logInUserId=new BigInteger(userId);
			UserDTO userDTO=jobRequisitionService.getRecruitersInfo(new BigInteger(userId));
			logInUserFullName= StringUtils.isBlank(userDTO.getFirstName()) ? "" : userDTO.getFirstName()
					+ (StringUtils.isBlank(userDTO.getLastName()) ? "" : " " + userDTO.getLastName());
			log.info("full name--{}",logInUserFullName);
			logInUserEmailAddress=userDTO.getEmailAddress();


			if(logInUserEmailAddress.length()==0) {
				return "MAIL NOT SENT";
			}

			AuthenticationConfiguration auth=new AuthenticationConfiguration();
			auth.setFromUser(logInUserEmailAddress);
			EmailParticipant emailParticipant = new EmailParticipant();
			
			emailParticipant.setTo(toAddressList);
			List<String> ccList = new ArrayList<>();
			ccList.add(logInUserEmailAddress);
			emailParticipant.setCc(ccList);
			emailParticipant.setOrganizationId(organizationId);
			emailParticipant.setOrgTeamName(logInUserFullName);
			emailParticipant.setReplyTo(logInUserEmailAddress);
			emailParticipant.setEnvironment(environment.getProperty(Constants.ENVIRONMENT));
			
			log.info("Email Participant(Details) are {}",emailParticipant);
			log.debug("Email Participant(Details) are {}",emailParticipant);
			
			MessageData messageData = new MessageData();
			messageData.setTemplate("NO_TEMPLATE");
			messageData.setSubject(mailSubject);
			messageData.setDynamicTemplateText(mailBody);
			log.info("Message Data(Details) are {}",messageData);
			log.debug("Message Data(Details) are {}",messageData);
			messageData.setHeaderTemplate("NO_TEMPLATE");
			messageData.setFooterTemplate("NO_TEMPLATE");
			//sender mail-id  validity should be checked as exception cannnot be caught here
			EmailService.sendEmail(auth, emailParticipant, messageData);
			
			
		} catch (Exception e) {
			log.error("exception occured while sending mail{}",e);
		}

		return "MAIL SENT";
	}



	/**
	 * 
	 * @param organizationId
	 * @param clientName
	 * @return
	 */
	public JSONArray getAllActiveJobs(BigInteger organizationId) throws Exception{

		List<JobOrder> allActiveJobs = jobOrderRepository.findByStatusAndOrganizationId(JobOrderConstants.JOB_ACTIVE_STATUS,organizationId);
		JSONArray allActiveJobsArray = new JSONArray();
		try {
			for(JobOrder job : allActiveJobs) {
				JSONObject jsonJob = new JSONObject();
				jsonJob.put(JobOrderConstants.JOB_ORDER_ID_STRING, job.getJobOrderId());
				jsonJob.put(JobOrderConstants.JOB_ID_STRING, job.getJobId());
				jsonJob.put(JobOrderConstants.JOB_TITLE_STRING, job.getJobTitle());
				jsonJob.put(JobOrderConstants.INTERVIEW_POSITION_ID_STRING,job.getInterviewPositionId());
				jsonJob.put(JobOrderConstants.CLIENT_NAME_STRING, job.getClientName());
				
				allActiveJobsArray.put(jsonJob);
			}
		}
		catch(Exception e) {
			log.error("Exception occurred when fetching all active jobs ",e);
			throw new Exception("Exception occurred when fetching all active jobs");
		}
		log.debug("{} active jobs returned for organizationId = {} and clientName= {}",allActiveJobsArray.length(),organizationId);
		log.debug("All active jobs found are {}",allActiveJobsArray.toString());

		return allActiveJobsArray;
	}
	
	
	/**
	 * 
	 * @param organizationId
	 * @param clientName
	 * @return
	 */
	public JSONArray getClientJobs(BigInteger organizationId, String clientName, String status) {

		List<JobOrder> allActiveJobs = jobOrderRepository.findJobsByStatus(status,organizationId,clientName);
		JSONArray allActiveJobsArray = new JSONArray();
		try {
			for(JobOrder job : allActiveJobs) {
				JSONObject jsonJob = new JSONObject();
				String jobLocation = ""; 
				List<JobLocation>  locations = job.getJobLocations();
				for (JobLocation location : locations) {
						jobLocation = jobLocation +location.getLocation() + " , ";	
				}
				if(!jobLocation.equalsIgnoreCase("")) {
					jobLocation = jobLocation.substring(0, (jobLocation.length()-2));
				}else {
					log.warn("Job locations are empty for the JobOrderId : {}",job.getJobOrderId() );
				}
				jsonJob.put(JobOrderConstants.JOB_ORDER_ID_STRING, job.getJobOrderId());
				jsonJob.put(JobOrderConstants.JOB_ID_STRING, job.getJobId());
				jsonJob.put(JobOrderConstants.JOB_TITLE_STRING, job.getJobTitle());
				jsonJob.put("createdBy", job.getCreatedBy());
				jsonJob.put(JobOrderConstants.INTERVIEW_POSITION_ID_STRING,job.getInterviewPositionId());
				jsonJob.put(JobOrderConstants.JOB_LOCATIONS_STRING, jobLocation);
				jsonJob.put(JobOrderConstants.JOB_POSTED_ON_WEBSITE, job.getPostOnWebSite());
				allActiveJobsArray.put(jsonJob);
			}
		}
		catch(JSONException e) {
			log.error("Exception occurred when fetching all active jobs ",e);
		}
		log.debug("{} drafted jobs returned for organizationId = {}",allActiveJobsArray.length());
		log.debug("All active jobs found are {}",allActiveJobsArray.toString());

		return allActiveJobsArray;
	}
	
	/**
	 * 
	 * @param body
	 * @return
	 */
	public String updateInterviewPosition(String body) {
		log.info("Inside the updateInterviewPosition during the publish job");
		try {
			JSONObject input=new JSONObject(body);
			BigInteger jobOrderId=new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			BigInteger interviewPosition=new BigInteger(input.getString(JobOrderConstants.INTERVIEW_POSITION_ID_STRING));
			log.debug("Updated interviewPosition: {} with jobOrderId: {}",interviewPosition,jobOrderId);
			jobOrderRepository.setinterviewPositionId(jobOrderId,interviewPosition);
			return "UPDATED";
		}
		catch(Exception e){
			log.error("error occured during updating interview position status. ",e);
			return "ERROR";
		}	
	}
}
