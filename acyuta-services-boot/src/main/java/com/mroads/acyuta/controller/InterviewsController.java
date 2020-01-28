package com.mroads.acyuta.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.service.CandidateProfileService;
import com.mroads.acyuta.service.InterviewsService;


/**
 * @author vinothgr
 *This controller is used to navigate for current & completed interviews from ATS
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/interviews")
public class InterviewsController {

	private static final Logger log = LoggerFactory.getLogger(InterviewsController.class);

	@Autowired
	private InterviewsService interviewsService;

	@Autowired
	CandidateProfileController candidateProfile;
	
	@Autowired
	AcyutaCandidateRepository acyutaCandidateRepository;
	
	@Autowired
	CandidateProfileService candidateProfileService;
	

	// Service used to load current Interviews
	@PostMapping(value = "/loadCurrentInterviews")
	public ResponseEntity<String> loadCurrentInterviews(@RequestBody String request) {

		log.info("Inside loadCurrentInterviews:");
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			JSONObject input = new JSONObject(request);
			log.info("InputData : {} ", input);
			JSONObject list = interviewsService.fetchCurrentInterviews(input);
			response = new ResponseEntity<String>(list.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception loadCurrentInterviews : ", e);
		}
		return response;
	}

	// Service used to load completed Interviews
	@PostMapping(value = "/loadCompletedInterviews")
	public ResponseEntity<String> loadCompletedInterviews(@RequestBody String request) {
	
		log.info("Inside loadCompletedInterviews.");
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			JSONObject input = new JSONObject(request);
			log.info("InputData : {} ", input);
			JSONObject list = interviewsService.fetchCompletedInterviews(input);
			response = new ResponseEntity<String>(list.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception loadCompletedInterviews : ", e);
		}
		return response;
	}

	// Service used to update Status for Candidate in Acyuta Interviews and Acyuta User
	@PostMapping(value = "/updateInterview")
	public String updateInterview(@RequestBody String request) throws JSONException {
		JSONObject responseJSON = new JSONObject();
		
		log.info("Inside updateInterview.");
		try {
			JSONObject inputJSON = new JSONObject(request);
			log.info("InputData :  {} ", inputJSON);
			interviewsService.updateCandidateInterview(inputJSON);
			candidateProfile.updateStatus(request);
			responseJSON.put("status", 200);
			responseJSON.put("message", "success");
		} catch (Exception e) {
			log.error("Exception updateInterview : ", e);
			responseJSON.put("status",500);
			responseJSON.put("message", "failure");
		}
		return responseJSON.toString();
	}
	// Service used to update comments and status in Acyuta Status/Comments tracking 
	@PostMapping(value = "/updateInterviewStatus")
	public String updateInterviewStatus(@RequestBody String request) throws JSONException {
		JSONObject responseJSON = new JSONObject();
		
		log.info("Inside updateInterviewStatus.");
		try {
			//FIXME: maintain josn jar s
			org.codehaus.jettison.json.JSONObject inputJSON = new org.codehaus.jettison.json.JSONObject(request);
			log.info("InputData  ##:   {} ", inputJSON);
			interviewsService.updateCandidateInterviewStatus(inputJSON.toString());
			candidateProfile.updateStatus(request);
			responseJSON.put("status", 200);
			responseJSON.put("message", "success");
		} catch (Exception e) {
			log.error("Exception updateInterviewStatus : ", e);
			responseJSON.put("status",500);
			responseJSON.put("message", "failure");
		}
		return responseJSON.toString();
	}
}
