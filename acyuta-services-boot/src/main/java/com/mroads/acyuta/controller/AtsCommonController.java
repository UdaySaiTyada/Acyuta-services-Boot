package com.mroads.acyuta.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.MDC;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.service.AcyutaPropertiesService;
import com.mroads.acyuta.service.CommonServices;
import com.mroads.acyuta.service.InterviewsService;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.acyuta.service.PresetInterviewsService;
import com.mroads.acyuta.service.TalentPoolService;
import com.mroads.acyuta.service.VendorService;

@CrossOrigin
@RestController
@RequestMapping(path = "/commonServices")
public class AtsCommonController {

	private static final Logger log = LoggerFactory.getLogger(AtsCommonController.class);
	
	
	@Autowired
	private JobRequisitionService jobRequisitionService;

	@Autowired
	AcyutaPropertiesService acyutaPropertiesService;

	@Autowired
	PresetInterviewsService scheduleInterviewsService;

	@Autowired
	InterviewsService interviewService;
	
	@Autowired
	VendorService vendorService;
	
	@Autowired
	CommonServices commonServices;
	 
	@Autowired
	TalentPoolService talentPoolService;
	
	@Autowired
	private Environment environment;
	
	@GetMapping(path = "/getJobIds")
	public ResponseEntity<List<String>> getJobIds(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetJobIds OnLoad service called with organizationId = >{}<", organizationId);

			if (organizationId == null)
				return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);

			List<String> jobIds = jobRequisitionService.getJobIds(new BigInteger(organizationId));
			log.info("{} distinct JobIds retrieved", jobIds.size());
			log.debug("Distinct JobIds obtained are {}", jobIds);
			response = new ResponseEntity<List<String>>(jobIds, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getJobIds: ",e);
			response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}

	@GetMapping(path = "/getClients")
	public ResponseEntity<List<String>> getClients(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String userId = request.getHeader(JobOrderConstants.USER_ID_STRING);
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetClients OnLoad service called with organizationId = >{}<, userId = >{}< ", organizationId, userId);
			if (userId == null || organizationId == null)
				return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
			List<String> clients = jobRequisitionService.getClients(new BigInteger(organizationId));
			log.info("{} clients retrieved for organizationId = {}, userId = {}", clients.size(), organizationId, userId);
			log.debug("{} Clients retreived for organizationId = {}, userId = {} and clients = {}", organizationId, userId, clients.size(), clients);
			response = new ResponseEntity<List<String>>(clients, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getClients: ",e);
			response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}

	@GetMapping(path = "/AtsTestService")
	public String test(HttpServletRequest request) {
		
		String response = "Panna ATS up and running";
	    log.info("Inside test:{}",response);
		
	    return response;
	}

	
	@GetMapping(path = "/recruiters")
	public ResponseEntity<String> getRecruiterNames(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetRecruitersNames OnLoad service called with organizationId = >{}<", orgIdString);

			if (orgIdString == null)
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			BigInteger organizationId = new BigInteger(orgIdString);
			JSONArray recruitersArray = jobRequisitionService.getRecruiters(organizationId);
			response = new ResponseEntity<String>(recruitersArray.toString(), HttpStatus.OK);
			if (recruitersArray.length() == 0) {
				log.error("Recruiters list contains no elments");
				response = new ResponseEntity<String>(recruitersArray.toString(), HttpStatus.valueOf(420));
			}
			log.info("{} recruiters details are retreived from db for organizationId = >{}<", recruitersArray.length(), orgIdString);
		} catch (Exception e) {
			log.error("Exception in getRecruiterNames: ",e);
			response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}

	@GetMapping(path = "/locations")
	public ResponseEntity<List<String>> getLocations() {
		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			log.info("Inside getLocations controller");
			List<String> locations = jobRequisitionService.getLocation();
			response = new ResponseEntity<List<String>>(locations, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getLocations: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}

	@GetMapping(path = "/getAcyutaClients")
	public ResponseEntity<List<String>> getAcyutaClients(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			log.info("getAcyutaClients service called.");
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			BigInteger orgId = new BigInteger(orgIdString);
			List<String> clients = new ArrayList<>();
			response = null;

			if (AcyutaUtils.acyutaCleintsMap.containsKey(orgId)) {
				clients = AcyutaUtils.acyutaCleintsMap.get(orgId);
				response = new ResponseEntity<List<String>>(clients, HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Exception in getAcyutaClients: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}

	@GetMapping(path = "/skills")
	public ResponseEntity<List<String>> getSkills(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String userId = request.getHeader(JobOrderConstants.USER_ID_STRING);
			MDC.put("userId", userId);

			log.info("Inside GetSkills controller.");
			List<String> skills = jobRequisitionService.getSkills();
			response = new ResponseEntity<List<String>>(skills, HttpStatus.OK);
			if (skills.isEmpty()) {
				// db is not available or values are not avialable in db(Use status 420)
				log.error("Skills list is obtained with no items");
				response = new ResponseEntity<>(skills, HttpStatus.valueOf(420));
			}
			log.debug("{} skills are obtained from db", skills.size());
		} catch (Exception e) {
			log.error("Exception in getSkills: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}
	// Provided service to load templates, when we made changes DB.
	@RequestMapping(value = "/loadMailTemplates")
	@ResponseBody
	public String getActiveMailTemplates() {
	
		return commonServices.getActiveMailTemplates();
	}
	
	

	@CrossOrigin
	@RequestMapping(value = "/postLogs", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void postLogsToInstance(@RequestBody String jsondata) throws JSONException {
		try {
			JSONObject json = new JSONObject(jsondata);
			MDC.put("userId", json.has("userId")?json.get("userId"):"");
			String message = "Browser Log: "+json.getString("message");
			try {
				switch (json.getString("type")) {
				case "info":
					log.info(message);
					break;
				case "trace":
					log.trace(message);
					break;
				case "warn":
					log.warn(message);
					break;
				case "error":
					log.error(message);
					break;
				case "debug":
					log.debug(message);
					break;
				default:
					log.error("incorrect log format", jsondata);
					break;
				}
			} finally {
				MDC.remove("userId");
			}
		}catch (JSONException e) {
			log.error("Exception due to bad JSON request in postLogs", e);
		}catch (Exception e) {
			log.error("Exception in postLogs for message : " + jsondata, e);
		}
	}
	

	@GetMapping(path = "/getConsultancyName")
	public ResponseEntity<List<String>> getConsultancyName(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String userId = request.getHeader(JobOrderConstants.USER_ID_STRING);
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetConsultancyName OnLoad service called with organizationId = >{}<, userId = >{}< ", organizationId, userId);
			if (userId == null || organizationId == null)
				return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
			List<String> consultancyName = talentPoolService.getConsultancyName(new BigInteger(organizationId));
			log.info("{} ConsultancyName retrieved for organizationId = {}, userId = {}", consultancyName.size(), organizationId, userId);
			log.debug("{} ConsultancyName retreived for organizationId = {}, userId = {} and ConsultancyName = {}", organizationId, userId, consultancyName.size(), consultancyName);
			response = new ResponseEntity<List<String>>(consultancyName, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getConsultancyName: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}
	
	@GetMapping(path = "/getCandidateStatus")
	public ResponseEntity<List<String>> getCandidateStatus(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String userId = request.getHeader(JobOrderConstants.USER_ID_STRING);
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetCandidateStatus OnLoad service called with organizationId = >{}<, userId = >{}< ", organizationId, userId);
			if (userId == null || organizationId == null)
				return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
			List<String> candidateStatus = talentPoolService.getCandidateStatus(new BigInteger(organizationId));
			log.info("{} CandidateStatus retrieved for organizationId = {}, userId = {}", candidateStatus.size(), organizationId, userId);
			log.debug("{} CandidateStatus retreived for organizationId = {}, userId = {} and CandidateStatus = {}", organizationId, userId, candidateStatus.size(), candidateStatus);
			response = new ResponseEntity<List<String>>(candidateStatus, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getCandidateStatus: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}

	// FIXME combine both getJobIds & getJobTitle into one method 
	@GetMapping(path = "/getJobTitle")
	public ResponseEntity<List<String>> getJobTitle(HttpServletRequest request) {

		ResponseEntity<List<String>> response = new ResponseEntity<>(null, HttpStatus.valueOf(420));
		try {
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			log.info("GetJobIds OnLoad service called with organizationId = >{}<", organizationId);

			if (organizationId == null)
				return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);

			List<String> jobIds = jobRequisitionService.getJobTitle(new BigInteger(organizationId));
			log.info("{} distinct JobIds retrieved", jobIds.size());
			log.debug("Distinct JobIds obtained are {}", jobIds);
			response = new ResponseEntity<List<String>>(jobIds, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception in getJobTitle: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}
}
