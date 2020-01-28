package com.mroads.acyuta.controller;

import java.math.BigInteger;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.service.TalentPoolService;


/**
 * @author Vinoth GR
 *This controller is used to navigate for TalentPool page from ATS
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/talentPool")
public class TalentPoolController {

	private static final Logger log = LoggerFactory.getLogger(TalentPoolController.class);

	@Autowired
	private TalentPoolService talentPoolService;

	
	// Service used to load default data for Talent Pool page
	@PostMapping(value = "/loadTalentPool")
	public ResponseEntity<String> talentPoolLoadData(@RequestBody String body) throws JSONException {
		
		log.info("Inside talentPoolLoadData controller");
		JSONObject getCandidateList = null;
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			JSONObject input = new JSONObject(body);
			log.info("loading default data for TalentPool: {}", input);
			getCandidateList = talentPoolService.getCandidateListForRecruiter(input);
			response = new ResponseEntity<String>(getCandidateList.toString(), HttpStatus.OK);
			log.info("Total no. of candidates: >{}<", getCandidateList.get("totalElements"));
		} catch (Exception e) {
			log.error("Exception in talentPoolLoadData service", e);
		}
		return response;
	}
	
	// Service used to update book mark for particular candidate
	@PostMapping(value = "/updateBookMark")
	public String updateBookMark(@RequestBody String request) throws JSONException {
		JSONObject responseJSON = new JSONObject();
		//FIXME: use status code with message...
		String response = "failure";
		log.info("Inside updateBookMark.");
		try {
			JSONObject inputJSON = new JSONObject(request);
			log.info("InputData :  {} ", inputJSON);
			response = talentPoolService.updateBookMarkField(inputJSON);
			responseJSON.put("message", response);
		} catch (Exception e) {
			log.error("Exception updateBookMark : ", e);
			responseJSON.put("message", response);
		}
		return responseJSON.toString();
	}
	
	@PostMapping(path = "/talentPoolFilters")
	public ResponseEntity<String> talentPoolFilters(@RequestBody String body) {
		ResponseEntity<String> response;
		log.info("Inside the talentPoolFilters");
		try {
			JSONObject jsonResponse = talentPoolService.talentPoolFilters(body);
			// Causes null pointer exception...
			response = new ResponseEntity<String>(jsonResponse.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred when using talentPoolFilters", e);
			response = new ResponseEntity<String>("Error in talentPoolFilters", HttpStatus.BAD_REQUEST);
		}
		return response;
	}
	
	@GetMapping(path = "/getJobListForClient")
	public ResponseEntity<List<String>> getJobListForClient(HttpServletRequest request) {
		//FIXME: handle error case...
		String clientName = request.getHeader(JobOrderConstants.CLIENT_NAME_STRING);
		String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		log.info("GetJobListForClient service called with organizationId = >{}<, clientName = >{}< ", organizationId, clientName);
		if (clientName == null || organizationId == null)
			return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
		List<String> clientList = talentPoolService.getClientList(new BigInteger(organizationId),clientName);
		log.info("{} CandidateStatus retrieved for organizationId = {}, clientName = {}", clientList.size(), organizationId, clientName);
		log.debug("{} CandidateStatus retreived for organizationId = {}, clientName = {} and CandidateStatus = {}", organizationId, clientName,  clientList);
		ResponseEntity<List<String>> response = new ResponseEntity<List<String>>(clientList, HttpStatus.OK);

		return response;
	}
}
