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

import java.math.BigInteger;

import org.codehaus.jackson.map.ObjectMapper;
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

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.PannaSignature;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.SignatureRepository;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.acyuta.service.SignatureService;

/**
 * @author Mahi.K
 * 
 */ 

@CrossOrigin
@RestController
@RequestMapping(path = "/signature")
public class SignatureController {
	
	
	private static final Logger log = LoggerFactory.getLogger(SignatureController.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	
	@Autowired
	private SignatureService  signatureService;
	
	@Autowired
	private JobOrderRepository jobOrderRepository;
	
	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;
	
	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private SignatureRepository signatureRepository;
	
	
	
	// update signature called when candidate signs the digital signature form.
	@PostMapping("/updateSignature")
	public ResponseEntity<String> updateSignature(@RequestBody String request) {
		log.info("Inside the updateSignature");
		
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.BAD_REQUEST);
		
		try {
			signatureService.updateSignature(request);
			return new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
		 log.error("Exception in updateSignature : ",e);
		}
		return response;
	}

	// viewCandidateProfile service called when candidate clicks on the digital signature form.  
	@PostMapping(value = "/viewSignatureFrom")
	public ResponseEntity<String> viewCandidateProfile(@RequestBody String request) {
		
		JSONObject responseJOSN = new JSONObject();
		JSONObject signatureJSON =   new JSONObject();
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.BAD_REQUEST);
		
		try {
			JSONObject inputJson = new JSONObject(request);
			
			String candidateIdEncoded =  inputJson.getString("candidateIdEncoded");
			String jobOrderIdEncoded =  inputJson.getString("jobOrderIdEncoded");
			String articleId =  inputJson.getString("articleId");
			String timeZone =  inputJson.has("timeZone") ? inputJson.getString("timeZone"): "CST";
			
			log.info("candidateIdEncoded :{}, jobOrderIdEncoded: {}",candidateIdEncoded,jobOrderIdEncoded);
			
			String articleName = AcyutaUtils.templatesIdNameMap.get(articleId);
			log.info("articleId :{}, articleName: {}",articleId,articleName);
			
			BigInteger candidateId =  AcyutaUtils.getDecodedValue(candidateIdEncoded);
			BigInteger jobOrderId =  AcyutaUtils.getDecodedValue(jobOrderIdEncoded);
			
			log.info("After Decode candidateId :{}, jobOrderId: {}",candidateId,jobOrderId);
			
			JSONObject jobOrder = jobRequisitionService.getJobByJobOrderId(jobOrderId, timeZone);
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
	 
			JSONObject candidateJSON =   new JSONObject(mapper.writeValueAsString(candidate));	
			PannaSignature signatureModel = signatureRepository.findByCandidateIdAndJobOrderIdAndArtilceId(candidateId, jobOrderId, new BigInteger(articleId));
			
			if(null!=signatureModel) {
				  signatureJSON =   new JSONObject(mapper.writeValueAsString(signatureModel));	
			}
			responseJOSN.put("jobOrder", jobOrder);
			responseJOSN.put("candidate", candidateJSON);
			responseJOSN.put("articleName", articleName);
			responseJOSN.put("articleId", articleId);
			responseJOSN.put("signature", signatureJSON);
			
			response = new ResponseEntity<>(responseJOSN.toString(), HttpStatus.OK);
			
		} catch (Exception e) {
			 log.error("Exception in viewCandidateProfile: ",e);
		}  
		return response;
	} 

	// update signature called when candidate signs the digital signature form.
	@PostMapping("/pdfshift")
	public ResponseEntity<String> pdfshift(@RequestBody String request) {
		log.info("Inside the updateSignature");

		JSONObject inputJson = new JSONObject(request);
		
		String htmlFileURL = inputJson.getString("htmlFileURL");
		String pdfFile = inputJson.getString("pdfFile");
		
		org.codehaus.jettison.json.JSONObject builder = signatureService.generatePDF(htmlFileURL, pdfFile);
		  return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
	}
	
	
	// update signature called when candidate signs the digital signature form.
	@PostMapping("/rejectSignatureForm")
	public ResponseEntity<String> updateSignatureFeedback(@RequestBody String request) {
		log.info("Inside the updateSignature");
		
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.BAD_REQUEST);
		
		try {
			signatureService.updateSignatureFeedback(request);
			return new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
		 log.error("Exception in updateSignature : ",e);
		}
		return response;
	}
}