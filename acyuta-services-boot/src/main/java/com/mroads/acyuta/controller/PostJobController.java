package com.mroads.acyuta.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.InterviewPositionsDTO;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.User;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.UserRepository;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.acyuta.service.PostJobService;

@CrossOrigin
@RestController
@RequestMapping(path = "/postJob")
public class PostJobController {

	private static final Logger logger = LoggerFactory.getLogger(VendorDashboardController.class);

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	private PostJobService postJobService;
	
	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	public UserRepository userRepository;


	@GetMapping(path = "/getInterviewPositions")
	public ResponseEntity<List<InterviewPositionsDTO>> getInterviewPositions(HttpServletRequest request) {

		ResponseEntity<List<InterviewPositionsDTO>> response;
		try {
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			logger.info("getInterviewPositions called with organizationId = >{}<", orgIdString);
			if (orgIdString == null)
				return new ResponseEntity<List<InterviewPositionsDTO>>(HttpStatus.BAD_REQUEST);

			BigInteger organizationId = new BigInteger(orgIdString);
			List<InterviewPositionsDTO> interviewPositions = postJobService.getInterviewPositions(organizationId);
			response = new ResponseEntity<List<InterviewPositionsDTO>>(interviewPositions, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in getInterviewPositions: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}

	@GetMapping(path = "/internalLink")
	public ResponseEntity<String> getInternalLink(HttpServletRequest request) {

		ResponseEntity<String> response;
		try {
			String jobOrderId = request.getHeader("jobOrderId");
			logger.info("Internal Link Service is called with jobOrderId = >{}<", jobOrderId);

			if (jobOrderId == null)
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

			String link = postJobService.getInternalLink(new BigInteger(jobOrderId));
			logger.info("Internal Link obtained for jobOrderId = {} is {}", jobOrderId, link);
			response = new ResponseEntity<String>(link, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in getInternalLink: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}

		return response;
	}

	@GetMapping(path = "/externalLink")
	public ResponseEntity<String> getExternalLink(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		try {
			String jobOrderId = request.getHeader("jobOrderId");
			logger.info("External Link Service is called with jobOrderId = >{}<", jobOrderId);

			if (jobOrderId == null)
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

			String link = postJobService.getExternalLink(new BigInteger(jobOrderId));
			logger.info("External Link obtained for jobOrderId = {} is {}", jobOrderId, link);
			response = new ResponseEntity<String>(link, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in getExternalLink: ",e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
		return response;
	}
	
	//FIXME need to check whether we are using this anywhere

	@PostMapping(path = "/sendMail")
	public ResponseEntity<String> sendMail(@RequestBody String body) {

		logger.info("Request to send a mail is obtained with body = {}", body);
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.valueOf(420));
		try {
			String emailStatus = postJobService.sendMail(body);
			JSONObject input=new JSONObject(body);
			response = new ResponseEntity<String>("CANNOT SEND MAIL", HttpStatus.valueOf(422));
			if (emailStatus.equals("MAIL SENT")) {
				response = new ResponseEntity<String>(emailStatus, HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.error("Exception in sendMail: ",e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
		return response;
	}

	@GetMapping(path = "/getClientActiveJobs")
	public ResponseEntity<String> getClientActiveJobs(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.valueOf(420));
		try {
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			String clientName = request.getHeader(JobOrderConstants.CLIENT_NAME_STRING);
			logger.info("Request obtained for get all active jobs using organizationId = >{}<", orgIdString);

			if (orgIdString == null) {
				logger.info("entered");
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			BigInteger organizationId = new BigInteger(orgIdString);
			String status = JobOrderConstants.JOB_ACTIVE_STATUS;
			JSONArray allActiveJobsArray = postJobService.getClientJobs(organizationId, clientName, status);
			response = new ResponseEntity<String>(allActiveJobsArray.toString(), HttpStatus.OK);
			logger.info("{} active jobs are retrieved for organizationId={} and ClientName={}", allActiveJobsArray.length(), organizationId, clientName);
		} catch (Exception e) {
			logger.error("Exception in getClientActiveJobs: ",e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
		return response;
	}

	// FIXME: To avoid the name space issue we added for add-resume extension.
	@GetMapping(path = "/getAllActiveJobsExt")
	public ResponseEntity<String> getAllActiveJobsExt(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.valueOf(420));
		try {
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			logger.info("Request obtained for get all active jobs using organizationId = >{}<", orgIdString);

			if (orgIdString == null) {
				logger.info("entered");
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			BigInteger organizationId = new BigInteger(orgIdString);
			JSONArray allActiveJobsArray = postJobService.getAllActiveJobs(organizationId);
			response = new ResponseEntity<String>(allActiveJobsArray.toString(), HttpStatus.OK);
			logger.info("{} active jobs are retrieved for organizationId={} and ClientName={}", allActiveJobsArray.length(), organizationId);
		} catch (Exception e) {
			logger.error("Exception in getAllActiveJobsExt: ",e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}

		return response;
	}

	@PutMapping(path = "/updateInterviewPositionId")
	public ResponseEntity<String> updateInterviewPosition(@RequestBody String body) {

		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.valueOf(420));
		try {
			response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
			String returnResponse = postJobService.updateInterviewPosition(body);
			if (returnResponse == "UPDATED")
				response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in updateInterviewPosition: ",e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
		return response;
	}

	@GetMapping(path = "/clientsandjobtitle")
	public ResponseEntity<String> jobIdTitle(HttpServletRequest request) {
	
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.valueOf(420));
		try {
			String organizationId = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			JSONObject clientIdTitle = jobRequisitionService.jobIdTitle(new BigInteger(organizationId));
			response = new ResponseEntity<String>(clientIdTitle.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in jobIdTitle: ",e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
		return response;
	}

	
	// postJobOnWebSite called when recruiter wants to post/un post a job on panna web site.
	// Once job is posted on web site any candidate can apply for the job from web site.
	@RequestMapping(value = "/postJobOnWebSite", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> postJobOnWebSite(@RequestBody String request) throws Exception {

		logger.info("Inside postJobOnWebSite");
		try {
			JSONObject inputJson = new JSONObject(request);

			logger.info("inputJson : {}",inputJson);
			BigInteger jobOrderId = BigInteger.valueOf(inputJson.getInt("jobOrderId"));
			Boolean postOnWebSite = inputJson.getBoolean("postOnWebSite");

			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setPostOnWebSite(postOnWebSite);
			jobOrderRepository.save(jobOrder);

			return new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in postJobOnWebSite() {}", e);
			return new ResponseEntity<>("failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@RequestMapping(value = "/validateNewVendor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<User>> validateNewVendor(@RequestBody String request) throws Exception {

		logger.info("inside validateNewVendor");
		try {
			JSONObject inputJson = new JSONObject(request);

			BigInteger orgId = new BigInteger(inputJson.getString("organizationId"));
			String emailAddress = inputJson.getString("emailAddress");
			
			logger.info("emailAddress: {}, orgId: {} ",emailAddress,orgId);
			List<String> roles = new ArrayList<>();
			roles.add("VendorManager");

			List<User> vendors = userRepository.validateNewVendor(roles, orgId, emailAddress);
			
			return new ResponseEntity<>(vendors, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in validateNewVendor() {}", e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}
