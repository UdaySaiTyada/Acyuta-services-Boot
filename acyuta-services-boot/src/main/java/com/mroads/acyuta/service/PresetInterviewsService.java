/*
*  Copyright (c) 2017 mRoads LLC. All Rights Reserved.
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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaConstants;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.AcyutaInterviewsDTO;
import com.mroads.acyuta.dto.AcyutaPannaInterviewsDTO;
import com.mroads.acyuta.dto.InterviewerSlotsDTO;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaInterviews;
import com.mroads.acyuta.model.AcyutaPannaInterviews;
import com.mroads.acyuta.model.AcyutamLiveInterviews;
import com.mroads.acyuta.model.InterviewPositions;
import com.mroads.acyuta.model.InterviewerSlot;
import com.mroads.acyuta.model.JobLocation;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.PannaSignature;
import com.mroads.acyuta.model.User;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaInterviewsRepository;
import com.mroads.acyuta.repository.AcyutamLiveInterviewsRepository;
import com.mroads.acyuta.repository.InterviewPositionsRepository;
import com.mroads.acyuta.repository.InterviewerSlotsRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.PresetInterviewsRepository;
import com.mroads.acyuta.repository.SignatureRepository;
import com.mroads.acyuta.repository.UserRepository;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;


/**
 * @author Mahi K
 */
@Service
@SuppressWarnings({ "squid:S2696", "squid:S2209" })
public class PresetInterviewsService{


	
	private static final Logger log = LoggerFactory.getLogger(PresetInterviewsService.class);
	
	
	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private VendorService vendorService;
	
	
	
	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;
	
	@Autowired
	private InterviewPositionsRepository interviewPositionsRepository;
	
	@Autowired 
	InterviewerSlotsRepository interviewerSlotsRepository;
	
	@Autowired 
	AcyutaInterviewsRepository acyutaInterviewsRepository;
	
	@Autowired
	PresetInterviewsRepository presetInterviewsRepository;
	
	@Autowired
	JobOrderRepository jobOrderRepository;
	
	@Autowired 
	AcyutamLiveInterviewsRepository acyutamLiveInterviewsRepository;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	private SignatureRepository signatureRepository;

	
	
	@Autowired
	private Environment environment;
	

	
	public static final Map<String, String> zoneMap;
	static {
		zoneMap = new HashMap<>();
		zoneMap.put("America/Chicago", "CST");
		zoneMap.put("UTC", "UTC");
		zoneMap.put("Asia/Calcutta", "IST");
		zoneMap.put("Asia/Kolkata", "IST");
		zoneMap.put("America/New_York", "EST");
		zoneMap.put("America/Los_Angeles", "CST");
		zoneMap.put("America/Denver", "MST");
	}
	

	/**
	 * @param candidateId
	 * @param restfulResponse
	 * @param request
	 */
	public  void savePresetInterviewDetails(String jobOrderId, String jobId, BigInteger candidateId, BigInteger organizationId, String restfulResponse) {

		AcyutaPannaInterviewsDTO presetInterviewsDTO = new AcyutaPannaInterviewsDTO();
		log.info("in savePresetInterviewDetails");
		try {
			JSONObject candidateInterviewDetails =  (JSONObject)new JSONObject(restfulResponse);
			
			log.info("candidateInterviewDetails : {}",candidateInterviewDetails);
			
			JSONObject candidateJson = (JSONObject) new JSONArray(candidateInterviewDetails.getString("candidateInterviewDetails")).get(0);
			log.info("candidateJson : {}",candidateJson);
			BigInteger recruiterId =new  BigInteger(candidateJson.getString("recruiterId"));

			BigInteger userInterviewId = BigInteger.valueOf(candidateJson.getInt("userInterviewId"));
			presetInterviewsDTO.setUserInterviewId(userInterviewId);
			presetInterviewsDTO.setCandidateId(candidateId);
			// NOTE: Need to understand why we need jobOrderId in acyuta interviews table.
			presetInterviewsDTO.setJobId(jobId);
			presetInterviewsDTO.setJobTitle(candidateJson.getString("positionTitle"));
			Date interviewDate = new Date(candidateJson.getString("expiryDate"));
			presetInterviewsDTO.setInterviewDate(interviewDate);

			presetInterviewsDTO.setExpiryDays(candidateJson.getInt("expiryDays"));
			presetInterviewsDTO.setInterviewPosition(AConstants.INTERVIEW_STATUS_REGISTERED.getValue());

			presetInterviewsDTO.setUpdatedBy(recruiterId);
			presetInterviewsDTO.setCreatedBy(recruiterId);
			// AcyutaUtil.setTimezone(request);
			presetInterviewsDTO.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			presetInterviewsDTO.setCreatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			presetInterviewsDTO.setOrganizationId(organizationId);
			
			
			AcyutaPannaInterviews presetInterviews = presetInterviewsRepository.save((AcyutaPannaInterviews) ObjectConverter.convert(presetInterviewsDTO, new AcyutaPannaInterviews()));
			//presetInterviewsDTO = (AcyutaPannaInterviewsDTO) ObjectConverter.convert(presetInterviews, new AcyutaPresetInterviewsDTO());
			
			log.info("saved Panna Preset Interview details. into AcyutaPresetInterviews Table");

			AcyutaInterviewsDTO acyutaInterviewsDto = new AcyutaInterviewsDTO();
			acyutaInterviewsDto.setCandidateId(candidateId);
			acyutaInterviewsDto.setInterviewDate(interviewDate);
			acyutaInterviewsDto.setInterviewStatus("PANNA_INTERVIEW_SCHEDULED");
			acyutaInterviewsDto.setInterviewMode("PANNA");
			acyutaInterviewsDto.setEnvironmentalName("PANNA");
			acyutaInterviewsDto.setInterviewPosition(AConstants.INTERVIEW_STATUS_REGISTERED.getValue());
			acyutaInterviewsDto.setJobId(jobId);
			acyutaInterviewsDto.setJobOrderId(new BigInteger(jobOrderId));
			acyutaInterviewsDto.setInterviewerSlotId(BigInteger.ZERO);
			acyutaInterviewsDto.setTechSkill(0f);
			acyutaInterviewsDto.setUserInterviewId(userInterviewId);
			acyutaInterviewsDto.setCommunicationSkills(0f);
			acyutaInterviewsDto.setPresetInterviewId(presetInterviews.getPresetInterviewId());
			acyutaInterviewsDto.setOrganizationId(organizationId);
			acyutaInterviewsDto.setCreatedBy(recruiterId);
			acyutaInterviewsDto.setUpdatedBy(recruiterId);
			acyutaInterviewsDto.setCreatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			acyutaInterviewsDto.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			
			AcyutaInterviews acyutaInterviews = new AcyutaInterviews();
			acyutaInterviews = acyutaInterviewsRepository.save((AcyutaInterviews) ObjectConverter.convert(acyutaInterviewsDto, acyutaInterviews));
			// acyutaInterviewsDto = (AcyutaInterviewsDTO) ObjectConverter.convert(acyutaInterviews, acyutaInterviewsDto);

			log.info("updated Panna Preset Interview details into Acyuta Interviews Table");

		} catch (Exception e) {
			log.error("Exception: saving PresetInterviewDetails ", e);
		}

	}

	/**
	 * @param orgName
	 * @param expiry
	 * @param candidateId
	 * @param pannaInterviewId
	 * @return String
	 */
	
	// schedulePannaInterviewREST calls the panna schedule service to schedule the panna interview for the candidate.
	public  String schedulePannaInterviewREST(JSONObject inputJSON) {

		String restfulResponse = "";
		try {

			log.info("inputJSON  for schedulePannaInterviewREST :{}",inputJSON);
			JSONArray candidatesJSONArray = new JSONArray();
			JSONObject candidates = new JSONObject();
			JSONObject scheduleFormDataJSON = new JSONObject();
			
		 final String serviceUrl = environment.getProperty("schedule.panna.host.url") + "/panna-services/schedule/schedulePannaInterview";
		 String callBackUrl =  environment.getProperty("env.host.service.url") + "/acyuta-services/callback/updatePannaInterviewStatus";
			
			int expiry = inputJSON.getInt("expiry");
			String jobOrderId = inputJSON.getString("jobOrderId");
			String jobId = inputJSON.getString("jobId");
			BigInteger candidateId = new BigInteger(inputJSON.getString("candidateId"));
			BigInteger interviewPositionId = new BigInteger(inputJSON.getString("interviewPositionId"));
			BigInteger organizationId = new BigInteger(inputJSON.getString("organizationId"));
			
			
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
			InterviewPositions interviewPosition =	interviewPositionsRepository.findByInterviewPositionId(interviewPositionId);

			scheduleFormDataJSON.put("interviewPositionId", interviewPosition.getInterviewPositionId());
			scheduleFormDataJSON.put("positionTitle", interviewPosition.getPositionTitle());
			scheduleFormDataJSON.put("expiryDays", expiry);
			scheduleFormDataJSON.put("recruiterId", candidate.getUpdatedBy().longValue());
			scheduleFormDataJSON.put("organizationId", organizationId);
			scheduleFormDataJSON.put("fromInterface", false);
			scheduleFormDataJSON.put("timeZone", "America/Chicago");
			scheduleFormDataJSON.put("callBackUrl", callBackUrl);

			candidates.put("firstName", candidate.getFirstName());
			candidates.put("lastName", candidate.getLastName());
			candidates.put("email", candidate.getEmailAddress());
			candidates.put("s3DownloadLink", candidate.getFileURL());
			candidates.put("s3HtmlLink", candidate.getResumeHtmlUrl());

			candidatesJSONArray.put(candidates);
			scheduleFormDataJSON.put("candidates", candidatesJSONArray);
			
		
			
			log.info("serviceUrl :{}",serviceUrl);
			log.info("scheduleFormDataJSON :{}",scheduleFormDataJSON);
			
			RestTemplate restTemplate = new RestTemplate();
			ParameterizedTypeReference<String> typeRef = new 	ParameterizedTypeReference<String>() {};
			ResponseEntity<String> responseEntity = restTemplate.exchange(serviceUrl,  HttpMethod.POST, new HttpEntity<>(scheduleFormDataJSON.toString()), typeRef);
			restfulResponse = responseEntity.getBody();
			log.info("Schedule Panna Interview Response details are :" + restfulResponse);
			savePresetInterviewDetails(jobOrderId,jobId, candidateId, candidate.getOrganizationId(), restfulResponse);

		} catch (Exception e) {
			log.error("Exception: In schedulePannaInterviewREST: ", e);
		}
		return restfulResponse;
	}
	
	
	public String scheduleMliveInterviewREST( JSONObject inputJSON) {
			
			String restfulResponse ="";
			
			log.info("scheduleMliveInterviewREST Input : {}",inputJSON );
			try {
				BigInteger candidateId = new BigInteger(inputJSON.getString("candidateId"));
				BigInteger recruiterId = new BigInteger(inputJSON.getString("recruiterId"));
				
				String jobId = inputJSON.getString("jobId");
				String jobOrderId = inputJSON.getString("jobOrderId");
				String interviewID = inputJSON.getString("interviewID");
				BigInteger interviewerSlotId = new BigInteger(inputJSON.getString("interviewerSlotId"));
				String interviewTitle =  inputJSON.getString("interviewTitle");
				String timeZone =  inputJSON.getString("timeZone");
				String organizationId =  ""+inputJSON.get("organizationId");
				
				// Here we are converting the timeZone display name to shot notation.
				// We get issue at EmailService with timeZone display name.
				timeZone = zoneMap.get(timeZone);
				
				AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
				InterviewerSlot timeSlotDTO = interviewerSlotsRepository.findByInterviewerSlotId(interviewerSlotId);
				
				
				BigInteger interviewerId = (timeSlotDTO.getInterviewerId() == null || timeSlotDTO.getInterviewerId() == BigInteger.ZERO) ?  BigInteger.ZERO : timeSlotDTO.getInterviewerId();
				log.info("scheduling mlive interview ...candidateId :" + candidateId);

				// FIXME: move the call back url from portlet to spring boot.
				String callBackUrl =  environment.getProperty("env.host.service.url") + "/acyuta-services/callback/updateLiveInterviewStatus";
				
				String serviceUrl =  environment.getProperty("schedule.panna.host.url") + "/panna-services/schedule/scheduleMlive";

				JSONObject scheduleMliveFormDataJSON = new JSONObject();
				scheduleMliveFormDataJSON.put("interviewID", interviewID);
				scheduleMliveFormDataJSON.put("interviewTitle", interviewTitle);
				scheduleMliveFormDataJSON.put("recruiterId", recruiterId);
				scheduleMliveFormDataJSON.put("scheduleDate", timeSlotDTO.getDate().trim() + " " + timeSlotDTO.getStartTime().trim());
				scheduleMliveFormDataJSON.put("timezone", timeZone.trim());
				scheduleMliveFormDataJSON.put("environment", AConstants.ACYUTA_ENVIRONMENT_NAME.getValue());
				scheduleMliveFormDataJSON.put("callBackUrl", callBackUrl);
				scheduleMliveFormDataJSON.put("scheduleType", "schedule");
				scheduleMliveFormDataJSON.put("resumeS3Link", candidate.getFileURL());
				scheduleMliveFormDataJSON.put("organizationId", organizationId);

				// Adding Candidate Details
				JSONObject candidateDetails = new JSONObject();
				candidateDetails.put("firstName", candidate.getFirstName());
				candidateDetails.put("lastName", candidate.getLastName());
				candidateDetails.put(AConstants.EMAIL.getValue(), candidate.getEmailAddress());
				candidateDetails.put("resumeS3Link", candidate.getFileURL());
				scheduleMliveFormDataJSON.put("candidateDetails", candidateDetails);
				
				// Adding Interviewer panel details
				User interviewerDetails = userRepository.getUserInformation(interviewerId);
				JSONArray interviewerJsonArray = new JSONArray();
				JSONObject interviewPanel = new JSONObject();
				interviewPanel.put("name", interviewerDetails.getFirstName()+" "+interviewerDetails.getLastName());
				interviewPanel.put(AConstants.EMAIL.getValue(), interviewerDetails.getEmailAddress());
				interviewerJsonArray.put(interviewPanel);
				scheduleMliveFormDataJSON.put("interviewPanel", interviewerJsonArray);

				
				RestTemplate restTemplate = new RestTemplate();
				ParameterizedTypeReference<String> typeRef = new 	ParameterizedTypeReference<String>() {};
				ResponseEntity<String> responseEntity = restTemplate.exchange(serviceUrl,  HttpMethod.POST, new HttpEntity<>(scheduleMliveFormDataJSON.toString()), typeRef);
				restfulResponse = responseEntity.getBody();
				log.info("Schedule mLive Interview Response : {}", restfulResponse);

				/* saving the resultDto details in to AcyutaMliveinterview table */
					// FIXME: 
				String timeWithZone= "";
				 saveMliveScheduleDataAcyutaMlive(candidateId, jobOrderId, jobId, interviewerSlotId, timeSlotDTO, restfulResponse, timeWithZone);
			} catch (Exception e) {
				log.error("Exception :Schedule mLive Interview", e);
			}
			return restfulResponse;
	}


	
	public void saveMliveScheduleDataAcyutaMlive(BigInteger candidateId,String jobOrderId, String jobId, BigInteger interviewerSlotId,
			InterviewerSlot timeSlotDTO, String resultMliveScheduleJsonData, String timeWithZone) {

		AcyutamLiveInterviews acyutaMliveIntervewDTO = new AcyutamLiveInterviews();
		try {
			JSONObject mliveScheduleJsonObject = new JSONObject(resultMliveScheduleJsonData);
			JSONObject mliveScheduleJson = mliveScheduleJsonObject.getJSONObject("candidateInteviewDetails");
			
			log.info("mliveScheduleJson :{}", mliveScheduleJson);

			BigInteger mliveScheduleId =BigInteger.valueOf(mliveScheduleJson.getInt(AConstants.MLIVE_SCHEDULE_ID.getValue()));
			BigInteger recruiterId =BigInteger.valueOf(mliveScheduleJson.getInt("recruiterId"));
			// BigInteger mliveScheduleId = mliveScheduleIdStr== null ? BigInteger.ZERO : new BigInteger(mliveScheduleIdStr);

			JSONArray mliveInterviewPanelsJSONArray = mliveScheduleJson.getJSONArray("mliveInterviewPanelsDTO");
			JSONObject mliveInterviewPanelsObject;

			if (BigInteger.ZERO != mliveScheduleId || null != mliveScheduleId) {

				/*
				 * save mlive interview details in to PannaAcyutaInterviews Table
				 */
				AcyutaInterviews acyutaInterviews = saveScreeningDetailsIntoPannaAcyutaInterviewsTable(jobOrderId, jobId, interviewerSlotId,recruiterId, candidateId, timeWithZone, timeSlotDTO);

			 	long acyutaInterviewsId = (null == acyutaInterviews.getInterviewId()) ? 0 : acyutaInterviews.getInterviewId().longValue();

				acyutaMliveIntervewDTO.setMLiveScheduleId(mliveScheduleId);
				acyutaMliveIntervewDTO.setSessionId(mliveScheduleJson.getString("sessionId"));
				acyutaMliveIntervewDTO.setAcyutaInterviewsId(BigInteger.valueOf(acyutaInterviewsId));

				for (int i = 0; i < mliveInterviewPanelsJSONArray.length(); i++) {
					mliveInterviewPanelsObject = mliveInterviewPanelsJSONArray.getJSONObject(i);
					acyutaMliveIntervewDTO.setMLiveInterviewerLink(mliveInterviewPanelsObject.getString("interviewerMliveLink"));
					acyutaMliveIntervewDTO.setMLiveFeedbackLink(mliveInterviewPanelsObject.getString("interviewerFeedbackLink"));
					log.info("getting the MlivePanelSerialno >>>" + mliveInterviewPanelsObject.get("mlivePanelSerialno") + "<<<");

				}

				acyutaMliveIntervewDTO.setMLivereportURL(mliveScheduleJson.getString("reportURL"));
				acyutaMliveIntervewDTO.setMLiveStatus(mliveScheduleJson.getString("status"));
				acyutaMliveIntervewDTO.setOrganizationId(BigInteger.valueOf(mliveScheduleJson.getInt("organizationId")));
				acyutaMliveIntervewDTO.setCreatedDate(acyutaInterviews.getCreatedDate());
				acyutaMliveIntervewDTO.setUpdatedDate(new Date());
				/*
				 * save the result information in to panna_Acyuta_mLive_Interviews table
				 */
				acyutaMliveIntervewDTO = acyutamLiveInterviewsRepository.save(acyutaMliveIntervewDTO);
				log.info("saved the details in to panna AcyutaMliveInterviews table >>" + acyutaMliveIntervewDTO);
				
			//	acyutaInterviewsRepository.updateMliveReportURL(acyutaMliveIntervewDTO.getMLivereportURL(),BigInteger.valueOf(acyutaInterviewsId));
				
				acyutaInterviewsRepository.updateMLiveInterviewerLink(acyutaMliveIntervewDTO.getMLiveInterviewerLink(),BigInteger.valueOf(acyutaInterviewsId));
				
				
				mliveScheduleJson.put(JobOrderConstants.USER_ID_STRING, mliveScheduleJson.getInt(JobOrderConstants.RECRUITER_ID_STRING)+"");
				mliveScheduleJson.put(JobOrderConstants.ORGANIZATION_ID_STRING, mliveScheduleJson.getInt(JobOrderConstants.ORGANIZATION_ID_STRING)+"");
				mliveScheduleJson.put(JobOrderConstants.STATUS_STRING, AConstants.SENT_SCHEDULE_mLIVE_INTERVIEW.getValue());
				mliveScheduleJson.put(JobOrderConstants.CANDIDATE_ID_STRING, candidateId+"");
				mliveScheduleJson.put(JobOrderConstants.JOB_ORDER_ID_STRING, jobOrderId+"");
				jobRequisitionService.updateCandidateStatus(mliveScheduleJson);
			}

			/* change interviewerSlot status Open to Reserved */

			if (interviewerSlotId.compareTo(BigInteger.ZERO) > 0) {
				InterviewerSlotsDTO resultDto = new InterviewerSlotsDTO();
				resultDto = (InterviewerSlotsDTO) ObjectConverter.convert(interviewerSlotsRepository.findByInterviewerSlotId(interviewerSlotId), resultDto);
				timeSlotDTO.setStatus(AConstants.INTERVIEWER_SLOT_RESERVED_STATUS.getValue());
				 interviewerSlotsRepository.save(timeSlotDTO);
			}
		} catch (Exception e) {
			log.error("Exception : in saveMliveScheduleDataInAcyutaMlive", e);
		}
	}
	
	// FIXME : reduce the number of parameters passed to method.
	// Use the same method to save SCREENING data also...
	private AcyutaInterviews saveScreeningDetailsIntoPannaAcyutaInterviewsTable(String jobOrderId, String jobId,
			BigInteger interviewerSlotId, BigInteger recruiterId, BigInteger candidateId, String timeWithZone, InterviewerSlot interviewerSlot) {
		
		AcyutaInterviews interviews = new AcyutaInterviews();
		 String environmentalName = "mLive";
		 
		interviews.setCandidateId(candidateId);
		interviews.setJobId(jobId);
		interviews.setJobOrderId(new BigInteger(jobOrderId));
		interviews.setInterviewDate(new Date(interviewerSlot.getDate()));
		//It represents Acyuta  status for candidate.
		interviews.setInterviewStatus("mLIVE_INTERVIEW_SCHEDULED");
		interviews.setTechSkill(0f);
		interviews.setCommunicationSkills(0f);
		interviews.setInterviewerSlotId(interviewerSlotId);
		interviews.setTimeSlot(interviewerSlot.getStartTime());
		 interviews.setInterviewMode(environmentalName);
		interviews.setEnvironmentalName(environmentalName);
		interviews.setOrganizationId(interviewerSlot.getOrganizationId());
		interviews.setCreatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
		//It represents Panna Interview status.
		interviews.setInterviewPosition(AConstants.INTERVIEW_STATUS_REGISTERED.getValue());
		interviews.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
		interviews.setCreatedBy(recruiterId);
		interviews.setUpdatedBy(recruiterId);

		interviews = acyutaInterviewsRepository.save(interviews);
		log.info("after save interview details are : \n\n" + interviews);

		return interviews;
	}

	public List<JSONObject> getInterviewSlots(BigInteger organizationId, String dateOfInterview) throws JSONException {
		
		List<InterviewerSlot> interviewerSlotsListDto = interviewerSlotsRepository.findByDateAndOrganization(dateOfInterview, organizationId);
		 Map<Long, String> interviewersList = getinterviewersList(organizationId);
		List<JSONObject> interviewersSlotsList = new ArrayList<>();
		for (InterviewerSlot dto : interviewerSlotsListDto) {
			JSONObject object = new JSONObject();
			long id = ((dto.getInterviewerSlotId() == null) || (dto.getInterviewerSlotId().longValue() == 0)) ? 0 : dto.getInterviewerSlotId().longValue();
			long interviewerId = ((dto.getInterviewerId() == null) || (dto.getInterviewerId().longValue() == 0)) ? 0 : dto.getInterviewerId().longValue();
			object.put("interviewerSlotId", id);
			object.put("interviewerSlot", dto.getStartTime() + " To " + dto.getEndTime() + " :: " + interviewersList.get(interviewerId));
			interviewersSlotsList.add(object);
		}
		return interviewersSlotsList;
	}
	
	
	public Map<Long, String> getinterviewersList(BigInteger organizationId) {

		HashMap<Long, String> interviewersMap = new HashMap<>();
		ArrayList<String> interviewerRolesList = new ArrayList<>();
		interviewerRolesList.add(AConstants.ROLE_ATS_INTERVIEWER.getValue());
		try {
			List<User> interviewsDTOList = userRepository.getUsersListByRoleNameAndOrgId(AcyutaConstants.getListOfUsersRoleAsInterviewer(), organizationId);
			for (User interviewer : interviewsDTOList) {
				String fullName = StringUtils.isBlank(interviewer.getFirstName()) ? "" : interviewer.getFirstName()
						+ (StringUtils.isBlank(interviewer.getLastName()) ? "" : " " + interviewer.getLastName());
				interviewersMap.put(interviewer.getUserId().longValue(), fullName);
			}
		} catch (Exception e) {
			log.error("Exception : while getting the interviewers List ", e);
			return new HashMap<>();
		}
		return interviewersMap;
	}
	
	public void saveSignature(JSONObject input) {
		log.info("Inside saveSignature.");
		PannaSignature signature = new PannaSignature();
		try {
			
			BigInteger jobOrderId = new BigInteger(input.getString("jobOrderId"));
			BigInteger candidateId = new BigInteger(input.getString("candidateId"));
			String mailTemplate = input.getString(Constants.MAIL_TEMPLATE);
			BigInteger artilceId = AcyutaUtils.templateFormsMap.get(mailTemplate);
			int mailCountBasedOnJobOrderId = 1;
			
			// NOTE: if we all ready sent the signature template, we are updating the same for the job.
			PannaSignature signatureModel = signatureRepository.findByCandidateIdAndJobOrderIdAndArtilceId(candidateId,jobOrderId, artilceId);
			
			if(null != signatureModel && null != signatureModel.getArtilceId()) {
				  signature =signatureModel;
				  mailCountBasedOnJobOrderId = signatureModel.getEmailCountSend()+mailCountBasedOnJobOrderId +1;
			}
			
			String jobTitle = input.getString("jobTitle");
			String interviewId = input.getString("interviewId");
			String candidateName = input.getString("candidateName");
			String deviceInfo = input.has("deviceInfo")? input.getString("deviceInfo"):"";

			signature.setJobTitle(jobTitle);
			signature.setJobOrderId(jobOrderId);
			signature.setCandidateId(candidateId);
			signature.setInterviewId(new BigInteger(interviewId));
			signature.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			signature.setArtilceId(AcyutaUtils.templateFormsMap.get(mailTemplate));
			signature.setArtilceTitle(mailTemplate);
			signature.setIntials("");
			signature.setDeviceInfo(deviceInfo);
			signature.setName(candidateName);
			signature.setEmailCountSend(mailCountBasedOnJobOrderId);
			signature.setIsAgree(null);
			log.info("signature ArtilceId : {}",signature.getArtilceId());
			log.debug("signature model : {}",signature);
			signatureRepository.save(signature);
			
		} catch (JSONException e) {
			log.error("Exception in saveSignature: ", e);
		}
	}

	public String scheduleNextRoundInterview(JSONObject inputJSON) {
		String response = "";
		try {
			log.info("inputJSON  for scheduleNextRoundInterview :{}",inputJSON);
			
			BigInteger jobOrderId = new BigInteger(inputJSON.getString("jobOrderId"));
			// BigInteger candidateId = new BigInteger(inputJSON.getString("candidateId"));
			BigInteger vendorId = new BigInteger(inputJSON.getString("vendorId"));
			
			BigInteger userId = new BigInteger(inputJSON.getString("userId"));
			

			String interviewDate = inputJSON.getString("nextRoundDate");
			String interviewTime = inputJSON.getString("nextRoundTime");
			
			String interviewDateTime = interviewDate+" "+interviewTime;
					
			String status = inputJSON.getString("status");
			
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			
			UserDTO vendor = jobRequisitionService.getUserInformation(vendorId); 
			UserDTO TechnicalManager = jobRequisitionService.getUserInformation(jobOrder.getCreatedBy()); 
			
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			String mailTemplate =  status.equals("SCHEDULED INTERVIEW ROUND 1") ?Constants.VMS_SCHEDULE_ROUND_1: Constants.VMS_SCHEDULE_ROUND_2;

			// Notification mail to Vendor
			log.info("Notification mail to Vendor:{}",vendor);
			userNotificationMail(userId, vendor, jobOrder, mailSubject, mailTemplate,interviewDateTime);
			
			// Notification mail to TechnicalManager
			log.info("Notification mail to TechnicalManager:{}",TechnicalManager);
			userNotificationMail(userId, TechnicalManager, jobOrder, mailSubject, mailTemplate,interviewDateTime);
			
			jobRequisitionService.updateCandidateStatus(inputJSON);
			
			response = "success";
			
		}catch(Exception e) {
			
		}
		return response;
	}
	
	
	
	//NOTE: reduce the code complexity.
	public void userNotificationMail(BigInteger recruiterId, UserDTO candidate,JobOrder jobOrder, String mailSubject,String mailTemplate,String interviewDateTime ) {

		log.info("Inside vendorNotificationMail.");
		String logInUserEmailAddress="";
		String logInUserFullName="";
		 List<String> toAddressList = new ArrayList<>();
		UserDTO recruiter=new UserDTO(); 
		String locationString="";

		try {
			toAddressList.add(candidate.getEmailAddress());
			recruiter=jobRequisitionService.getRecruitersInfo(recruiterId);
			logInUserEmailAddress=recruiter.getEmailAddress();		
			logInUserFullName= StringUtils.isBlank(recruiter.getFirstName()) ? "" : recruiter.getFirstName()
					+ (StringUtils.isBlank(recruiter.getLastName()) ? "" : " " + recruiter.getLastName());

			AuthenticationConfiguration auth=new AuthenticationConfiguration();
			auth.setFromUser(logInUserEmailAddress);
			EmailParticipant emailParticipant = new EmailParticipant();
			emailParticipant.setTo(toAddressList);
			List<String> ccList = new ArrayList<>();
			// NOTE: If we want CC copy to mail sender...
			//	ccList.add(logInUserEmailAddress);
			emailParticipant.setCc(ccList);
			

			emailParticipant.setOrganizationId(jobOrder.getOrganizationId().toString());
			emailParticipant.setOrgTeamName(logInUserFullName);
			emailParticipant.setReplyTo(logInUserEmailAddress);
			emailParticipant.setEnvironment(environment.getProperty(Constants.ENVIRONMENT));

			MessageData messageData = new MessageData();
			
			if(mailTemplate==null) {
				messageData.setTemplate("NO_TEMPLATE");
				messageData.setDynamicTemplateText(" ");
			}else {
				messageData.setTemplate(mailTemplate);
			}
			
			messageData.setHeaderTemplate("NO_TEMPLATE");
			messageData.setFooterTemplate("NO_TEMPLATE");
			messageData.setSubject(mailSubject);

			String userName =candidate.getFirstName()+" "+candidate.getLastName();
					
			JobOrder job=jobOrderRepository.findByJobOrderId(jobOrder.getJobOrderId());
			log.debug("jobList obtained---{}",job);
			messageData.setDynamicValue("[$JOBID$]",job.getJobId());
	
			
			messageData.setDynamicValue("[$INTERVIEW_DATE_TIME$]",interviewDateTime);
			messageData.setDynamicValue("[$CANDIDATE_NAME$]",userName);
			messageData.setDynamicValue("[$JOBTITLE$]",job.getJobTitle());
			messageData.setDynamicValue("[$CLIENT_NAME$]",job.getClientName());
			messageData.setDynamicValue("[$JOB_PAY_RATE$]",job.getPayRate());
			messageData.setDynamicValue("[$JOBTYPE$]",job.getJobType());
			String duration ="";
			if(null!=job.getProjectStartDate() && !job.getProjectStartDate().isEmpty()) {
				duration = job.getProjectStartDate()+"-"+job.getProjectEndDate();
			}
			messageData.setDynamicValue("[$JOBDURATION$]",duration);
			messageData.setDynamicValue("[$RECRUITER_TITLE$]","");
			messageData.setDynamicValue("[$RECRUITER_PHONE$]",recruiter.getPhoneNumber()!=null?recruiter.getPhoneNumber().trim():"");
			messageData.setDynamicValue("[$RECRUITER_EMAIL$]",logInUserEmailAddress);
			String envURL=environment.getProperty("view.profile.url");
			String viewJobURL = envURL+"/#/job-requisition?jobOrderId="+job.getJobOrderId();
			messageData.setDynamicValue("[$JOBDESCRIPTION_LINK$]", viewJobURL);
			List<JobLocation> location=job.getJobLocations();
			for(JobLocation jobLocation:location) {
				locationString+=jobLocation.getLocation()+" ";
			}
			log.info("jobList locationString---{}",locationString);
			messageData.setDynamicValue("[$JOBLOCATION$]",locationString);
			messageData.setDynamicValue("[$RECRUITER_FIRST_NAME$]",recruiter.getFirstName());
			messageData.setDynamicValue("[$RECRUITER_LAST_NAME$]",recruiter.getLastName());
			
			log.info("messageData {}",messageData); 
			EmailService.sendEmail(auth, emailParticipant, messageData);
		} catch (Exception e) {
			log.error("Exception in vendorNotificationMail: ",e);
		}
	}

	
}
