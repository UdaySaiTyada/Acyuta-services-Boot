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

package com.mroads.acyuta.controller;
/*
 * @author Mahi.K
 * 
 * */

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroads.acyuta.common.EncryptDecryptUtil;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.JobOrderDTO;
import com.mroads.acyuta.dto.RecruiterActivitiesDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaTemplate;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.JobOrderMapping;
import com.mroads.acyuta.model.RecruiterActivities;
import com.mroads.acyuta.model.User;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaUserJobOrderMappingRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.RecruiterActivitiesRepository;
import com.mroads.acyuta.repository.UserRepository;
import com.mroads.acyuta.service.AcyutaPropertiesService;
import com.mroads.acyuta.service.CandidateProfileService;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;

@CrossOrigin
@RestController
@RequestMapping(path = "/candidateProfile")
public class CandidateProfileController {

	private static final Logger log = LoggerFactory.getLogger(CandidateProfileController.class);

	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private AcyutaUserJobOrderMappingRepository acyutaUserJobOrderMappingRepository;
	
	@Autowired
	private RecruiterActivitiesRepository recruiterActivitiesRepository;
	
	@Autowired
	private JobOrderRepository jobOrderRepository;
	
	@Autowired
	private CandidateProfileService candidateProfileService;
	
	@Autowired
	AcyutaPropertiesService acyutaPropertiesService;
	
	@Autowired
	public UserRepository userRepository;
	@Autowired
	public AcyutaCandidateRepository acyutaCandidateRepository;
	
	
	
	
	
	ObjectMapper objectMapper = new ObjectMapper();
	private static DateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm a");


	@PostMapping(value = "/viewCandidateProfile")
	public String viewCandidateProfile(@RequestBody String request) throws Exception {

		JSONObject inputJSON = new JSONObject(request);
		List<AcyutaCandidateDTO> matchedCandidates = new ArrayList<AcyutaCandidateDTO>();
		AcyutaCandidateDTO candidate = null;
		JobOrderDTO linkedJobInfo = null;

		log.info("Inside viewCandidateProfile.");
		log.info("inputJSON : {}", inputJSON);

		JSONObject profileInfo = new JSONObject();
		profileInfo.put("isLinkedForJob", false);
		String phoneEncoded = inputJSON.getString("phoneNumber");
		String emailEncoded = inputJSON.getString("emailAddress");
		String orgIdStr = inputJSON.getString("organizationId");
		String recruiterIdStr = inputJSON.getString("userId");
		BigInteger linkedJobOrderId = new BigInteger(inputJSON.getString("jobOrderId"));
		BigInteger organizationId = new BigInteger(orgIdStr);
		BigInteger recruiterId = new BigInteger(recruiterIdStr);

		User recruiter = userRepository.getUserInformation(recruiterId);

		List<JobOrderDTO> userLinedJobs = new ArrayList<JobOrderDTO>();

		String emailAddress = EncryptDecryptUtil.getDecryptKey(emailEncoded);
		String phoneNumber = EncryptDecryptUtil.getDecryptKey(phoneEncoded);

		log.info("After Decode, emailAddress :{}, phoneNumber :{}", emailAddress, phoneNumber);

		JobOrder linkedJobOrder = jobOrderRepository.findByJobOrderId(linkedJobOrderId);
		linkedJobInfo = (JobOrderDTO) ObjectConverter.convert(linkedJobOrder, new JobOrderDTO());

		matchedCandidates = jobRequisitionService.findByEmailAddressOrPhoneNumberAndOrganizationId(phoneNumber,
				emailAddress, organizationId);

		log.info("matchedCandidates count :{} ", matchedCandidates.size());

		if (null != matchedCandidates && !matchedCandidates.isEmpty()) {
			candidate = matchedCandidates.get(0);

			// get all linked jobs for the candidate..
			List<JobOrderMapping> userJobsMapping = candidate.getJobMappings();

			log.info("JobOrderMapping size :{}, for userId: {} ", userJobsMapping.size(), candidate.getCandidateId());

			for (JobOrderMapping job : userJobsMapping) {

				if ("ACTIVE".equalsIgnoreCase(job.getStatus())) {
					
					JobOrder jobInfo = jobOrderRepository.findByJobOrderId(job.getJobOrderId());
					if(jobInfo.getJobOrderId().compareTo(linkedJobInfo.getJobOrderId())==0) {
						profileInfo.put("isLinkedForJob", true);
					}
					JobOrderDTO jobDTO = (JobOrderDTO) ObjectConverter.convert(jobInfo, new JobOrderDTO());

					List<RecruiterActivities> jobsHistoryModels = recruiterActivitiesRepository
							.getCandidateHistoryForJob(candidate.getCandidateId(), jobInfo.getJobOrderId());

					log.info("recruiterActivities size :{}, for jobOrderId: {} ", jobsHistoryModels.size(),	jobInfo.getJobOrderId());

					for (RecruiterActivities model : jobsHistoryModels) {
						RecruiterActivitiesDTO jobHistoryDTO = (RecruiterActivitiesDTO) ObjectConverter.convert(model,
								new RecruiterActivitiesDTO());
						jobHistoryDTO.setUpdatedDate(format.format(model.getUpdatedDate()));
						jobDTO.getRecruiterActivities().add(jobHistoryDTO);
					}
					userLinedJobs.add(jobDTO);
					candidate.setJobId(jobInfo.getJobId());
				}
			}
		}

		AcyutaTemplate acyutaTemplate = acyutaPropertiesService.getActiveMailTemplateContent("PANNA_STANDARD_SIGNATURE",
				BigInteger.ZERO);

		String signatureContent = processSigDynamicValues(acyutaTemplate.getContent(), recruiter);
		profileInfo.put("emailAddress", emailAddress);
		profileInfo.put("linkedJobInfo", objectMapper.writeValueAsString(linkedJobInfo));
		profileInfo.put("pannaStandardSignature", signatureContent);
		profileInfo.put("candidateInfo", objectMapper.writeValueAsString(candidate));
		profileInfo.put("jobsHistory", objectMapper.writeValueAsString(userLinedJobs));

		return profileInfo.toString();
	}
	
	String processSigDynamicValues(String content, User recruiter){
		
		String signatureContent ="";
		
		try {
			Map<String, String> dynamicValuesMap = new HashMap<>();
			
			dynamicValuesMap.put("[$RECRUITER_FIRST_NAME$]", recruiter.getFirstName());
			dynamicValuesMap.put("[$RECRUITER_LAST_NAME$]", recruiter.getLastName());
			String recruiterName = recruiter.getFirstName()+" "+recruiter.getLastName();
			dynamicValuesMap.put("[$RECRUITER_NAME$]", recruiterName);
			
			dynamicValuesMap.put("[$RECRUITER_PHONE$]",recruiter.getPhoneNumber()!=null?recruiter.getPhoneNumber().trim():"");
			dynamicValuesMap.put("[$RECRUITER_EMAIL$]", recruiter.getEmailAddress());
			dynamicValuesMap.put("[$RECRUITER_TITLE$]", recruiter.getJobTitle());
			
			int dynamicValueSize = (dynamicValuesMap == null) ? 0 : dynamicValuesMap.size();
			String[] placeHolders = new String[dynamicValueSize];
			String[] values = new String[dynamicValueSize];


			if (dynamicValuesMap != null) {
				int i = 0;
				for (Map.Entry<String, String> entry : dynamicValuesMap.entrySet()) {
					placeHolders[i] = entry.getKey();
					values[i] = entry.getValue();
					i++;
				}
			}
			signatureContent = StringUtils.replaceEachRepeatedly(content, placeHolders, values);
			log.info("signatureContent : {}",signatureContent);
		} catch (Exception e) {
			log.error("Exception in processSigDynamicValues : ",e);
		}
		return signatureContent;
		
	}

	
	// Provided service to updateCandidateInfo.
	@PostMapping(value = "/updateCandidateInfo")
	@ResponseBody
	public ResponseEntity<String> updateCandidateInfo(@RequestBody String body) {
		log.info("Inside the updateCaniddateInfo.");
		ResponseEntity<String> response;
		String updateStatus ="failure";
		try {
			JSONObject caniddateInfoJSON = new JSONObject(body);
			log.info("caniddateInfoJSON {}:",caniddateInfoJSON);
			
			updateStatus = candidateProfileService.updateCandidateInfo(caniddateInfoJSON);
			response = new ResponseEntity<String>(updateStatus, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Exception in updateCaniddateInfo: ",e);
			response = new ResponseEntity<>(updateStatus, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}
	
	//FIXME: Remove duplicate method. Change url in the panna ATS and remove this method.
	@PostMapping(value = "/updateCaniddateInfo")
	@ResponseBody
	public ResponseEntity<String> updateCaniddateInfo(@RequestBody String body) {
		log.info("Inside the updateCaniddateInfo.");
		ResponseEntity<String> response;
		String updateStatus ="failure";
		try {
			JSONObject caniddateInfoJSON = new JSONObject(body);
			log.info("caniddateInfoJSON {}:",caniddateInfoJSON);
			
			updateStatus = candidateProfileService.updateCandidateInfo(caniddateInfoJSON);
			response = new ResponseEntity<String>(updateStatus, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Exception in updateCaniddateInfo: ",e);
			response = new ResponseEntity<>(updateStatus, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}
	
	//sendMail
	@PostMapping(value = "/sendMail")
	@ResponseBody
	public ResponseEntity<String> sendMail(@RequestBody String body) {
		log.info("Inside the sendMail.");
		ResponseEntity<String> response;
		String updateStatus ="failure";
		List<String> toAddressList=new ArrayList<String>();
		List<String> ccAddressList=new ArrayList<String>();
		List<String> bccAddressList=new ArrayList<String>();
		
		
		try {
	
			JSONObject sendMailInfo = new JSONObject(body);
			
			String logInUserFullName=sendMailInfo.getString(JobOrderConstants.RECRUITER_STRING);
			String candidateId = sendMailInfo.getString("candidateId");
			String emailAddress = sendMailInfo.getString("emailAddress");
			log.info("sendMailInfo {}:",sendMailInfo);
			
			 org.codehaus.jettison.json.JSONArray emailsTOList=sendMailInfo.getJSONArray("TOEmailsList");
			 for(int i=0;i<emailsTOList.length();i++) {
				 toAddressList.add(emailsTOList.getString(i));
			 }
			 org.codehaus.jettison.json.JSONArray emailsCCTagList=sendMailInfo.getJSONArray("CCEmailsList");
			 for(int i=0;i<emailsCCTagList.length();i++) {
				 ccAddressList.add(emailsCCTagList.getString(i));
			 }
			 //FIXME:  If send mail from AWS, it is not showing the mail Inbox.
			 // To solve it temporarily, add recruiter mail in CC.
			 ccAddressList.add(emailAddress);
			 bccAddressList.add(emailAddress);
			 BigInteger organizationId = new BigInteger(sendMailInfo.getString("organizationId"));
			 AuthenticationConfiguration auth=new AuthenticationConfiguration();
			 auth.setFromUser(emailAddress);
			 EmailParticipant emailParticipant = new EmailParticipant();
			 emailParticipant.setTo(toAddressList);
			 
			 emailParticipant.setApplicationSource("ACYUTA");
			 emailParticipant.setCandidateId(candidateId);
			 emailParticipant.setCc(ccAddressList);
			 emailParticipant.setBcc(bccAddressList);
			 emailParticipant.setOrganizationId(organizationId.toString());
			 emailParticipant.setOrgTeamName(logInUserFullName);
			 emailParticipant.setReplyTo(emailAddress);
			 emailParticipant.setEnvironment("ACYUTA");
			 
			 MessageData messageData = new MessageData();
			 messageData.setTemplate("NO_TEMPLATE");
			 messageData.setSubject(sendMailInfo.getString("mailSubject"));
			 messageData.setDynamicTemplateText(sendMailInfo.getString("mailBody"));
			 messageData.setHeaderTemplate("NO_TEMPLATE");
			 messageData.setFooterTemplate("NO_TEMPLATE");
			 
			 //sender mail-id  validity should be checked as exception cannot be caught here
			 log.info("auth : {}, emailParticipant : {}, messageData: {}", auth, emailParticipant, messageData);
			 EmailService.sendEmail(auth, emailParticipant, messageData);
			response = new ResponseEntity<String>(updateStatus, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Exception in sendMail: ",e);
			response = new ResponseEntity<>(updateStatus, HttpStatus.BAD_REQUEST);
		}
		
		return response;
	}
	
	@PostMapping(value = "/updateStatusComments")
	@ResponseBody
	public ResponseEntity<String> updateStatus(@RequestBody String body) {
		log.info("Inside the updateStatusComments.");
		ResponseEntity<String> response;
		List<RecruiterActivitiesDTO> recruiterActivitiesList= new ArrayList<>();
		try {
			JSONObject statusInfo = new JSONObject(body);
			log.info("Inside updateStatusComments: {}",statusInfo);
			BigInteger candidateId = new BigInteger(statusInfo.getString("candidateId"));
			BigInteger jobOrderId = new BigInteger(statusInfo.getString("jobOrderId"));			
			String candidateStatus = statusInfo.getString("candidateStatus");
			
			if(null!=candidateStatus && !candidateStatus.isEmpty()) {
				candidateProfileService.addCandidateStatus(statusInfo);
			}
			String comments = statusInfo.getString("comments");
			if(null!=comments && !comments.isEmpty()) {
				candidateProfileService.saveCandidateComments(statusInfo);
			}
//FIXME
			candidateProfileService.updateUser(statusInfo);
			 List<RecruiterActivities>   jobsHistoryModels = recruiterActivitiesRepository.getCandidateHistoryForJob(candidateId,jobOrderId);
			 
			 log.info("recruiterActivities size: {}", jobsHistoryModels.size());
			 for (RecruiterActivities model : jobsHistoryModels) {
				 RecruiterActivitiesDTO jobHistoryDTO=(RecruiterActivitiesDTO) ObjectConverter.convert(model,new RecruiterActivitiesDTO());
				 jobHistoryDTO.setUpdatedDate(format.format(model.getUpdatedDate()));
				 recruiterActivitiesList.add(jobHistoryDTO);
			}
			 
			response = new ResponseEntity<String>(objectMapper.writeValueAsString(recruiterActivitiesList), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Exception in updateStatusComments: ",e);
			response = new ResponseEntity<>("failure", HttpStatus.BAD_REQUEST);
		}
		return response;
	}
	

	@PutMapping(path = "/updateCandidateDocuments")
	public ResponseEntity<String> updateCandidateDocuments(@RequestBody String body) {
		log.info("Inside updateCandidateDocuments");
		
		ResponseEntity<String> response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		try {
			JSONObject input=new JSONObject(body);
			
			BigInteger candidateId = new BigInteger(input.getString("candidateId"));
			String documents = input.getString("documents");
			AcyutaCandidate candiate = acyutaCandidateRepository.findByCandidateId(candidateId);
			candiate.setDocuments(documents);
			acyutaCandidateRepository.save(candiate);

			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in updateCandidateDocuments: ",e);
		}
		return response;
	}
	
}