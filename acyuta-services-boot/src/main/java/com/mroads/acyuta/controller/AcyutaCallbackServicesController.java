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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.AcyutaInterviewsDTO;
import com.mroads.acyuta.dto.AcyutamLiveInterviewsDTO;
import com.mroads.acyuta.dto.InterviewerSlotsDTO;
import com.mroads.acyuta.model.AcyutaInterviews;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaInterviewsRepository;
import com.mroads.acyuta.service.CandidateProfileService;
import com.mroads.acyuta.service.InterviewerSlotsService;
import com.mroads.acyuta.service.InterviewsService;

/*
 * 
 * */
@CrossOrigin
@RestController
@RequestMapping(path = "/callback")
public class AcyutaCallbackServicesController {
	private static final Logger log = LoggerFactory.getLogger(AcyutaCallbackServicesController.class);

	@Autowired
	private Environment environment;

	@Autowired
	private CandidateProfileService candidateProfileService;

	@Autowired
	private InterviewerSlotsService interviewerSlotsService;

	@Autowired
	private InterviewsService interviewsService;

	@Autowired
	private AcyutaCandidateRepository acyutaCandidateRepository;

	@Autowired
	private AcyutaInterviewsRepository acyutaInterviewsRepository;

	private static final String MAIL_TEMPLATE = "mailTemplate";

	public static List<String> finalStatusList = new ArrayList<String>();
	public static List<String> inintialStatusList = new ArrayList<String>();

	@RequestMapping(value = "/testing", method = RequestMethod.POST)
	@ResponseBody
	public String abcTest(@RequestBody String body) {
		log.info("Entered body is {}", body);
		return "method executed. body[" + body + "]";
	}

	// updateAcyutaInterviewRatings

	/**
	 * @param mliveInterviewPanelDTO
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@RequestMapping(value = "/updateLiveInterviewStatus", method = RequestMethod.POST)
	@ResponseBody
	public String updateLiveInterviewStatus(@RequestBody String body) throws IOException {
		String response;
		AcyutamLiveInterviewsDTO liveInterviewsDTO = new AcyutamLiveInterviewsDTO();
		try {
			JSONObject liveInterviewPanelInfo = new JSONObject(body);
			log.info("updateLiveInterviewStatus from Live Interview {} >> ", liveInterviewPanelInfo);

			BigInteger mliveScheduleId = new BigInteger(liveInterviewPanelInfo.getString("mliveScheduleId"));
			Float technicalRating = Float.valueOf(liveInterviewPanelInfo.getString("technicalRating"));
			Float communicationRating = Float.valueOf(liveInterviewPanelInfo.getString("communicationRating"));
			String status = liveInterviewPanelInfo.getString("status");
			String comments = liveInterviewPanelInfo.getString("comments");

			liveInterviewsDTO = interviewsService.getAcyutamLiveInterviewsDTOByMLiveScheduleId(mliveScheduleId);

			if (null != liveInterviewsDTO && null != liveInterviewsDTO.getMLiveScheduleId()) {
				AcyutaInterviewsDTO acyutaInterviewsDTO = interviewsService.findByInterviewId(liveInterviewsDTO.getAcyutaInterviewsId());

				acyutaInterviewsDTO.setTechSkill(technicalRating);
				acyutaInterviewsDTO.setCommunicationSkills(communicationRating);
				acyutaInterviewsDTO.setInterviewPosition(status);
				acyutaInterviewsDTO.setComments(comments);

				/* Ratings in AcyutaInterviews table */
				interviewsService.saveAcyutaInterviews(acyutaInterviewsDTO);
				InterviewerSlotsDTO slotDTO = interviewerSlotsService.getTimeSlotsByInterviewerSlotId(acyutaInterviewsDTO.getInterviewerSlotId());
				slotDTO.setStatus(status);
				/* save status in AcyutaInterviewerSlotsDTO table */
				interviewerSlotsService.saveInterviewerSlots(slotDTO);

				/* save status in AcyutaMliveInterviews table */
				liveInterviewsDTO.setMLiveStatus(status);

				/* calling the report URL from Panna. */
				String serviceUrl = environment.getProperty("panna.report.url");
				JSONObject scheduleFormDataJSON = new JSONObject();
				serviceUrl = serviceUrl + "/" + mliveScheduleId + "/MLIVE";
				log.info("serviceUrl : {}", serviceUrl);

				RestTemplate restTemplate = new RestTemplate();
				ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
				};
				ResponseEntity<String> responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(scheduleFormDataJSON.toString()), typeRef);
				String restfulResponse = responseEntity.getBody();
				JSONObject resp = new JSONObject(restfulResponse);
				log.info("mLive Report URL : {}", resp);
				String reportURL = resp.getString("reportURL");
				log.info("reportUrl with HostAddress : >>"+ reportURL + "<<");
				liveInterviewsDTO.setMLivereportURL(reportURL);
				String candidateStatus = "MLIVE_INTERVIEW_COMPLETED";
				liveInterviewsDTO = interviewsService.saveAcyutamLiveInterviews(liveInterviewsDTO);
				
				
		

				/*
				 * update mlive interview details in to AcyutaCandidate (panna_Acyuta_User)
				 * table
				 */
				JSONObject mLiveInterviewDetails = new JSONObject();
				mLiveInterviewDetails.put("reportURL", reportURL);
				mLiveInterviewDetails.put("technicalRating", technicalRating);
				mLiveInterviewDetails.put("communicationRating", communicationRating);
				mLiveInterviewDetails.put("candidateStatus", candidateStatus);
				mLiveInterviewDetails.put("comments", comments);
				mLiveInterviewDetails.put("mliveScheduleId", mliveScheduleId);
				mLiveInterviewDetails.put("candidateId", acyutaInterviewsDTO.getCandidateId());
				AcyutaCandidateDTO candidateDTO = candidateProfileService.updateMliveInterviewDetails(mLiveInterviewDetails);
				
	
				acyutaInterviewsRepository.updateMliveReportURL(reportURL,liveInterviewsDTO.getAcyutaInterviewsId());

				/*
				 * adding the candidate status in CandidateStatusTracking
				 * (panna_Acyuta_StatusTracking) table
				 */
				JSONObject statusInfo = new JSONObject();
				statusInfo.put("candidateId", candidateDTO.getCandidateId());
				statusInfo.put("candidateStatus", candidateStatus);
				statusInfo.put("organizationId", candidateDTO.getOrganizationId());
				statusInfo.put("recruiterId", candidateDTO.getUpdatedBy());
				statusInfo.put("jobOrderId", acyutaInterviewsDTO.getJobOrderId());

				candidateProfileService.addCandidateStatus(statusInfo);

			}
			response = "UPDATED SUCCESSFULLY";
		} catch (Exception e) {
			log.error("Exception : updateLiveInterviewStatus by using callBack url", e);
			response = "updateLiveInterviewStatus failure";
		}

		return response;
	}

	// updateAcyutaUser
	/**
	 * @param userData
	 * @param request
	 * @return
	 */
	@CrossOrigin
	@RequestMapping(value = "/updatePannaInterviewStatus", method = RequestMethod.POST)
	@ResponseBody
	public String updateAcyutaUser(@RequestBody String userData) {
		String response;

		try {

			JSONObject json = new JSONObject(userData);

			log.info("updatePannaInterviewStatus JSON :{}  ", json);
			if (json.has("score") && json.has("userInterviewId") && json.has("interviewStatus")) {

				String interviewStatus = json.getString("interviewStatus");
				
				String pannaInterviewComments=json.has("comments") ? json.getString("comments"):null;

				BigInteger userInterviewId = new BigInteger(json.getString("userInterviewId"));

				Float score = new Float(json.getString("score"));

				if ("DISQUALIFIED".equalsIgnoreCase(interviewStatus)) {
					score = 0f;
				}

				JSONObject scheduleFormDataJSON = new JSONObject();
				String serviceUrl = environment.getProperty("panna.report.url");
				serviceUrl = serviceUrl + "/" + userInterviewId + "/PANNA";
				RestTemplate restTemplate = new RestTemplate();
				log.info("serviceUrl :{} ", serviceUrl);
				ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
				};
				ResponseEntity<String> responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(scheduleFormDataJSON.toString()), typeRef);

				String restfulResponse = responseEntity.getBody();
				JSONObject resp = new JSONObject(restfulResponse);
				log.info("Panna Report URL : {}", resp);

				String reportURL = resp.getString("reportURL");
				String candidateStatus = ("PANNA_EVALUATION_COMPLETED".equals(interviewStatus)) ? "PANNA_INTERVIEW_COMPLETED" : interviewStatus;
				
				String interviewPosition="";
				switch(interviewStatus){
				case "PANNA_INTERVIEW_INPROGRESS": interviewPosition="INPROGRESS"; break;
				case "PANNA_EVALUATION_PENDIING": interviewPosition="EVALUATION"; break;
				case "PANNA_EVALUATION_COMPLETED": interviewPosition="COMPLETED"; break;
				case "PANNA_INTERVIEW_EXPIRED" : interviewPosition="EXPIRED"; break;
				default: interviewPosition=interviewStatus;
				}
				
				
				
				acyutaInterviewsRepository.updateInterviewScore(score, userInterviewId, interviewStatus,interviewPosition,reportURL);

				AcyutaInterviews user = acyutaInterviewsRepository.findByUserInterviewId(userInterviewId);

				log.debug("AcyutaInterviews : " + user);

				/*
				 * update panna interview details in to AcyutaCandidate (panna_Acyuta_User)
				 * table
				 */
				acyutaCandidateRepository.updatePannaInterviewDetails(reportURL, user.getCandidateId(), score, candidateStatus, user.getUserInterviewId(),pannaInterviewComments);

				JSONObject statusInfo = new JSONObject();
				statusInfo.put("candidateId", user.getCandidateId());
				statusInfo.put("candidateStatus", candidateStatus);
				statusInfo.put("organizationId", user.getOrganizationId());
				statusInfo.put("recruiterId", user.getUpdatedBy());
				statusInfo.put("jobOrderId", user.getJobOrderId());
				candidateProfileService.addCandidateStatus(statusInfo);

			}
			response = "UPDATED SUCCESSFULLY";
		} catch (Exception e) {

			log.error("Exception in updatePannaInterviewStatus:", e);
			response = "updatePannaInterviewStatus failure";

		}

		return response;

	}

}