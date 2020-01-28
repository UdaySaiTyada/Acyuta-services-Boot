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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaConstants;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.AcyutaInterviewsDTO;
import com.mroads.acyuta.dto.JobCommentsDTO;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.dto.VendorDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaTemplate;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.service.AcyutaPropertiesService;
import com.mroads.acyuta.service.CommonServices;
import com.mroads.acyuta.service.InterviewsService;
import com.mroads.acyuta.service.JobListService;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.acyuta.service.PresetInterviewsService;
import com.mroads.acyuta.service.VendorService;

/*
 * @author SaiRameshGupta, Mahi K
 * 
 * */
@CrossOrigin
@RestController
@RequestMapping(path = "/jobs")
public class JobRequisitionController 
{
	private static final Logger log = LoggerFactory.getLogger(JobRequisitionController.class);

	@Autowired
	private JobRequisitionService jobRequisitionService;

	@Autowired
	private JobListService jobListService;

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	AcyutaPropertiesService acyutaPropertiesService;

	@Autowired
	PresetInterviewsService scheduleInterviewsService;
	
	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;

	@Autowired
	InterviewsService interviewService;
	
	@Autowired
	VendorService vendorService;
	
	@Autowired
	CommonServices commonServices;
	
	

	@Autowired
	private Environment environment;

	private static final String MAIL_TEMPLATE = "mailTemplate";

	public static List<String> finalStatusList = new ArrayList<String>();
	public static List<String> inintialStatusList = new ArrayList<String>();

	// After start of the application we load the all the templates
	// where organization Ids are configured in application properties.
	@PostConstruct
	public void init() 
	{
		  commonServices.getActiveMailTemplates();
		// Here we are loading the different statues in to list.
		// We use this list to sort the applied candidates for job in view job page.

		String inintialStatusListString = environment.getProperty("initial.status.list");
		if (null != inintialStatusListString) 
		{
			String list[] = inintialStatusListString.split(",");
			for (String status : list) 
			{
				inintialStatusList.add(status.trim());
			}
		}

		String finalStatusListString = environment.getProperty("final.status.list");
		if (null != finalStatusListString) 
		{
			String list[] = finalStatusListString.split(",");
			for (String status : list) 
			{
				finalStatusList.add(status.trim());
			}
		}
		log.info("finalStatusList: {}", finalStatusList);
		log.info("inintialStatusList: {}", inintialStatusList);
	}



	/*-----------***************------------>::CREATE JOB SERVICES::<----------------*************----------*/

	@PostMapping("/saveJob")
	public ResponseEntity<String> createJob(@RequestBody String body)
	{
		log.info("Inside createJob controller:");
		JSONObject input;
		ResponseEntity<String> response = new ResponseEntity<String>("failure", HttpStatus.BAD_REQUEST);
		try 
		{
			input = new JSONObject(body);
			JSONArray vendorInfo = new JSONArray();
			
			JobOrder jobOrder = jobRequisitionService.saveJobInJobOrder(body);
			
			response = new ResponseEntity<String>(jobOrder.toString(), HttpStatus.CREATED);
		
			//If Job created by D3NonTech Manager publish the job to Tier1 vendors
			if(input.has(Constants.IS_D3_NonTech_MANAGER) && input.getBoolean(Constants.IS_D3_NonTech_MANAGER))
			{ 	
				log.info("isD3NonTechManager : true., So publishing the job to Tier1 vendors.");
				//Get the tier1 vendors for the organization.
				List<VendorDTO>list = vendorService.getTier1vendors(jobOrder.getOrganizationId());
				for (VendorDTO vendorDTO : list) 
				{
					vendorInfo.put(vendorDTO.getVendorId().toString());
				}
				log.info("D3NonTechManager vendorInfo : {}",vendorInfo);
				input.put(Constants.ORGANIZATIONID_STRING, jobOrder.getOrganizationId().toString());
				input.put(Constants.JOB_ORDER_ID_STR, jobOrder.getJobOrderId().toString());
				input.put(Constants.RECRUITER_ID_STRING, jobOrder.getCreatedBy().toString());
				input.put(Constants.VENDOR_INFO, vendorInfo);
				vendorService.publishVendorJob(input, BigInteger.ZERO);
			}
			return response;
		} 
		catch (Exception e)
		{
			log.error("Exception in createJob Controller: ", e);
			return response;
		}
	}

	@GetMapping(path = "/checkJobId")
	public ResponseEntity<String> checkJobId(HttpServletRequest request) 
	{
		String jobIdString = request.getHeader(JobOrderConstants.JOB_ID_STRING);
		String organizationIdStr = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		log.info("Checking JobId duplicates for jobId = >{}<, organizationId = >{}<", jobIdString, organizationIdStr);
		if (StringUtils.isBlank(jobIdString) || StringUtils.isBlank(organizationIdStr))
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		
		BigInteger organizationId = new BigInteger(organizationIdStr);
		JSONObject jobStatus = jobRequisitionService.getDistinctJobId(jobIdString,organizationId);
		
		ResponseEntity<String> response;
		response = new ResponseEntity<String>(jobStatus.toString(), HttpStatus.OK);

		return response;
	}

	@GetMapping(path = "/getMatchingResumes")
	public ResponseEntity<String> getMatchingResumes(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			if (!StringUtils.isBlank(request.getHeader("skills"))) {
				JSONArray skillsArray = new JSONArray(request.getHeader("skills"));
				JSONArray locationsArray = new JSONArray(request.getHeader("locations"));

				String serachName = request.getHeader("name");
				BigInteger organizationId = new BigInteger(request.getHeader("organizationId"));

				log.info("quick serach called for candidate:{}, skills:{}, locations:{}, organization:{}", serachName.toLowerCase(), skillsArray.toString(),
						locationsArray, organizationId);
				String responseString = jobRequisitionService.filterResumeFromResumeApplication(skillsArray, locationsArray, serachName, organizationId);

				if (responseString.length() == 0) {
					log.info("Cannot fetch resumes for skills {}", skillsArray.toString());
					log.debug("No response is obtained from resume service response = >{}<", responseString);
					response = new ResponseEntity<String>(HttpStatus.valueOf(420));
					return response;
				}
				response = new ResponseEntity<String>(responseString, HttpStatus.OK);
				return response;
			}
		} catch (JSONException e) {
			log.error("Error occurred when getMatchingResumes: ", e);
		}
		return response;
	}

	/*-----------***************------------>::END-::>CREATE JOB SERVICES::<----------------*************----------*/

	/*-----------***************------------>::JOB LIST SERVICES::<----------------*************----------*/

	@RequestMapping(path = "/getMoreJobs", method = RequestMethod.GET)
	public ResponseEntity<String> getMoreJobs(HttpServletRequest request) {

		JSONObject jsonResponse = jobListService.getJobsForJobListTable(request);
		ResponseEntity<String> response = new ResponseEntity<String>(jsonResponse.toString(), HttpStatus.OK);
		return response;
	}

	@PostMapping(path = "/filters")
	public ResponseEntity<String> getfilterlist(@RequestBody String body) {
		ResponseEntity<String> response;
		log.info("Inside the filters");
		try {
			JSONObject jsonResponse = jobListService.filters(body);
			response = new ResponseEntity<String>(jsonResponse.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred when using filters", e);
			response = new ResponseEntity<String>("Error in filters", HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	@PostMapping(path = "/updateJobTitle")
	public ResponseEntity<String> updateJobTitle(@RequestBody String body) {
		ResponseEntity<String> response;
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString("jobOrderId"));
			String jobTitle = input.getString(JobOrderConstants.JOB_TITLE_STRING);

			jobRequisitionService.updateJobTitle(jobOrderId, jobTitle);

			response = new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred when using filters", e);
			response = new ResponseEntity<String>("Error in filters", HttpStatus.BAD_REQUEST);
		}
		return response;

	}

	// This end-point is used to update status of list of selected jobs in JOB LIST
	// page
	@PutMapping(path = "/updateJobStatus")
	public ResponseEntity<String> updateJobStatus(@RequestBody String body) {
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			JSONObject input = new JSONObject(body);
			String status = input.getString(JobOrderConstants.STATUS_STRING);
			JSONArray jobsArray = input.getJSONArray(Constants.JOBS);
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			String organizationId = input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);

			log.info("Request to updateStatus obtained with status = >{}<, No of Jobs to be updated = {}", status, jobsArray.length());
			log.debug("Request to updateStatus obtained with status = {}, jobsArray = {}", status, jobsArray.toString());

			status = status.toUpperCase();
			List<BigInteger> jobs = new ArrayList<>();

			for (int i = 0; i < jobsArray.length(); i++) {
				jobs.add(new BigInteger(jobsArray.getString(i)));
			}

			log.info("Calling setJobStatus service to set status as = {}", status);
			Integer noOfJobsUpdated = jobListService.setJobStatus(jobs, status, userId, organizationId);

			if (noOfJobsUpdated >= 1) {
				log.info("{} jobs updated successfully with status = {}", noOfJobsUpdated, status);
				response = new ResponseEntity<String>("Statuses updated successfully", HttpStatus.OK);
			} else {
				log.error("Cannot update status for jobs");
				response = new ResponseEntity<String>("Unable to update statuses", HttpStatus.valueOf(421));
			}
			// }
		} catch (JSONException e) {
			log.error("Error occurred when updating job status ", e);
			response = new ResponseEntity<String>("Bad request obtained", HttpStatus.BAD_REQUEST);
		}

		return response;
	}

	/*-----------***************------------>::END-::>JOB LIST SERVICES::<----------------*************----------*/

	/*-----------***************------------>::VIEW/EDIT JOB SERVICES::<----------------*************----------*/

	@GetMapping(path = "/editJob")
	public ResponseEntity<String> editJob(HttpServletRequest request) {

		String jobOrderId = request.getHeader(JobOrderConstants.JOB_ORDER_ID_STRING);
		String timeZone = request.getHeader(Constants.TIME_ZONE_STRING);
		log.info("Request obtained to view job using jobOrderId = >{}<", jobOrderId);
		if (jobOrderId == null)
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		ResponseEntity<String> response;
		try {

			JSONObject job = jobRequisitionService.getJobByJobOrderId(new BigInteger(jobOrderId), timeZone);

			JSONArray jobComments = jobRequisitionService.getComments(jobOrderId, timeZone);
			job.put("jobComments", jobComments);
			
			response = new ResponseEntity<String>(job.toString(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("No job found with jobOrderId = {}", jobOrderId);
			log.error(" Exception is ", e);
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	/////////////////////////////////////////// UPDATE
	/////////////////////////////////////////// SERVICES////////////////////////////////////////////////////

	@PutMapping(path = "/updateSkills")
	public ResponseEntity<String> updateSkills(@RequestBody String body) {
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updateSkills(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PostMapping(path = "/updateComment")
	public ResponseEntity<String> updateComment(@RequestHeader("timeZone") String timeZone, @RequestBody JobCommentsDTO jobComment, @RequestHeader String userId) {

		log.info("Comment obtained to update/create in db is {}", jobComment.toString());
		String commentStatus = jobRequisitionService.updateCommentInDB(jobComment, userId);
		JSONArray jobComments = new JSONArray();
		jobComments = jobRequisitionService.getComments(jobComment.getJobOrderId(), timeZone);

		if (commentStatus.equals("Error")) {
			log.info("Cannot update job comment.{}", jobComment.getJobOrderId());
			// Use Status 420 to specify that db is available but unable to update in db
			return new ResponseEntity<String>("Cannot create a new comment", HttpStatus.valueOf(420));
		}

		log.info("Updated comment with commentId = {}", commentStatus);
		return new ResponseEntity<String>(jobComments.toString(), HttpStatus.OK);
	}

	@PutMapping(path = "/updateDescription")
	public ResponseEntity<String> updateJobDescription(@RequestBody String body) {
		log.info("Inside updateDescription");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updateJobDescription(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updatePayRate")
	public ResponseEntity<String> updatePayRate(@RequestBody String body) {
		log.info(" Inside updatePayRate : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updatePayRate(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateJobType")
	public ResponseEntity<String> updateJobType(@RequestBody String body) {
		ResponseEntity<String> response;
		log.info(" Inside updateJobType : ");
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updateJobType(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateStatus")
	public ResponseEntity<String> updateStatus(@RequestBody String body) {
		log.info(" Inside updateStatus : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updateStatus(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateDuration")
	public ResponseEntity<String> updateJobDuration(@RequestBody String body) {
		log.info(" Inside updateDuration : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = jobRequisitionService.updateJobDuration(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateLocations")
	public ResponseEntity<String> updateLocations(@RequestBody String body) {
		ResponseEntity<String> response;
		log.info(" Inside updateLocations : ");
		String returnResponse = jobRequisitionService.updateLocations(body);
		response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		if (returnResponse == "ERROR") {
			response = new ResponseEntity<String>("NOT UPDATED SUCCESSFULLY", HttpStatus.valueOf(420));
		}
		return response;
	}

	@PutMapping(path = "/updateRecruites")
	public ResponseEntity<String> updateRecruiters(@RequestBody String body) {
		ResponseEntity<String> response;
		log.info(" Inside updateRecruites : ");
		String returnResponse = jobRequisitionService.updateRecruiters(body);
		response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		if (returnResponse == "ERROR") {
			response = new ResponseEntity<String>("NOT UPDATED SUCCESSFULLY", HttpStatus.valueOf(420));
		}
		return response;
	}

	/*-----------***************------------>::END-::>VIEW/EDIT JOB SERVICES::<----------------*************----------*/

	// Add Candidate Service
	/**
	 * @param body
	 * @return
	 * @throws JSONException
	 */

	@RequestMapping(value = "/addCandidate")
	@ResponseBody
	public String addCandidate(@RequestBody String body) throws JSONException {
		log.info("In addCandidate service");
		JSONObject output = new JSONObject();

		try {

			jobRequisitionService.addCandidate(body);
			output.put("Message", "Success");
			output.put("Status", "200");
		} catch (Exception e) {
			output.put("Message", "Failure");
			output.put("Status", "500");
			log.error("Exception in addCandidate ", e);
		}
		return output.toString();
	}

	@RequestMapping(value = "/checkDuplicateResume")
	@ResponseBody
	public List<AcyutaCandidateDTO> checkDuplicateResume(HttpServletRequest request) throws JSONException {
		log.info("In checkDuplicateResume service");
		List<AcyutaCandidateDTO> dupResumes = null;
		String phoneNumber = request.getHeader("phoneNumber");
		String emailAddress = request.getHeader("emailAddress");
		String orgIdStr = request.getHeader("organizationId");
		BigInteger organizationId = new BigInteger(orgIdStr);
		String full_name = "";

		log.info("phoneNumber:{}, emailAddress:{}, organizationId:{} ", phoneNumber, emailAddress, organizationId);

		try {
			dupResumes = jobRequisitionService.findByEmailAddressOrPhoneNumberAndOrganizationId(phoneNumber, emailAddress, organizationId);

			if (dupResumes.size() > 0) {
				AcyutaCandidateDTO candidate = dupResumes.get(0);
				full_name = candidate.getFirstName() + " " + candidate.getLastName();
			}
			log.info("Check duplicate resume called for candidate:>{}< with emailAddress:>{}< and count:>{}< ", full_name.toLowerCase(), emailAddress, dupResumes.size());

		} catch (Exception e) {
			log.error("Exception in checkDuplicateResume service", e);
		}
		return dupResumes;
	}

	@RequestMapping(value = "/editCandidate")
	@ResponseBody
	public AcyutaCandidateDTO editCandidate(@RequestBody String body) throws JSONException {
		log.info("Inside editCandidate ");
		JSONObject output = new JSONObject();
		AcyutaCandidateDTO editCandidate = null;
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data " + input);
			editCandidate = jobRequisitionService.editCandidateByCandidateId(input);
			log.info("In editCandidate service ===> editCandidate: " + editCandidate);
		} catch (Exception e) {
			log.error("Exception in editCandidate service", e);
		}
		return editCandidate;
	}

	@RequestMapping(value = "/applyJob")
	@ResponseBody
	public ResponseEntity<String> applyJob(@RequestBody String body) throws JSONException {
		ResponseEntity<String> response;
		try {
			JSONObject input = new JSONObject(body);
			log.info("candidateInfo :{} ", input);
			JSONObject returnResponse = jobRequisitionService.applyJob(input);
			response = new ResponseEntity<String>(returnResponse.toString(), HttpStatus.OK);
			log.info("Input Json data:{} ", input);
		} catch (Exception e) {
			log.error("Exception in applyJob ", e);
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	@RequestMapping(value = "/removeFromJob")
	@ResponseBody
	public ResponseEntity<String> removeCandidateFromJob(@RequestBody String body) throws JSONException {
		ResponseEntity<String> response;
		log.info(" Inside removeCandidateFromJob :{} ", body);
		try {
			JSONObject input = new JSONObject(body);
			log.info("removeFromJob candidateInfo :{} ", input);
			JSONObject returnResponse = jobRequisitionService.removeCandidateFromJob(input);
			response = new ResponseEntity<String>(returnResponse.toString(), HttpStatus.OK);
			log.info("Input Json data:{} ", input);
		} catch (Exception e) {
			log.error("Exception in applyJob ", e);
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return response;
	}

	// getCandidatesForJob return the all the candidate applied for job.
	@RequestMapping(value = "/getCandidatesForJob")
	@ResponseBody
	public List<AcyutaCandidateDTO> getCandidates(@RequestBody String body) throws JSONException {
		log.info("Inside getCandidatesForJob controller");
		List<AcyutaCandidateDTO> getCandidates = null;
		try {
			JSONObject input = new JSONObject(body);
			log.info("getCandidatesForJob Input Json data: {}", input);
			getCandidates = jobRequisitionService.getCandidatesForJob(input);
			log.info("candidates are linked for the job are : >{}<", getCandidates.size());
		} catch (Exception e) {
			log.error("Exception in getCandidatesForJob service", e);
		}
		return getCandidates;
	}

	// getMailTemplate return the mail template content and subject
	// which we sent to candidate..
	@RequestMapping(value = "/getMailTemplate")
	@ResponseBody
	public String getMailTemplate(HttpServletRequest request) {
		String mailContent = "";
		String mailSubject = "";
		JSONObject mailTemplateJSON = new JSONObject();
		try {

			String templateName = request.getHeader("mailTemplate");
			String jobOrderIdStr = request.getHeader("jobOrderId");
			log.info("templateName:{} jobOrder Id: {}",templateName,  jobOrderIdStr);
			BigInteger jobOrderId = new BigInteger(jobOrderIdStr);

			String candidateIdStr = request.getHeader("candidateId");
			BigInteger candidateId = new BigInteger(candidateIdStr);
			String organizationIdStr = request.getHeader("organizationId");
			BigInteger organizationId = new BigInteger(organizationIdStr);
			String userIdStr = request.getHeader("userId");
			BigInteger userId = new BigInteger(userIdStr);

			String interviewIdStr = request.getHeader("interviewId");
			BigInteger interviewId = StringUtils.isNotBlank(interviewIdStr) ? new BigInteger(interviewIdStr) : BigInteger.ZERO;

			String timeZone = request.getHeader("timeZone") == null ? "CST" : request.getHeader("timeZone");

			log.info("getMailTemplate template name: {}", templateName);
			log.info("jobOrderId: {}, candidateId: {}, organizationId: {}, userId: {}, timeZone: {}", jobOrderId, candidateId, organizationId, userId, timeZone);

			AcyutaTemplate template = null;
			if (AcyutaUtils.templatesMap.containsKey(templateName + "-" + organizationId.toString())) {
				log.info(" mail Template: {} exist for organization: {} ", templateName, organizationIdStr);
				template = AcyutaUtils.templatesMap.get(templateName + "-" + organizationId);
			} else {
				log.info(" loading the default template. ");
				String ZERO = "0";
				template = AcyutaUtils.templatesMap.get(templateName + "-" + ZERO);
			}

			mailSubject = template.getTemplateSubject();

			// NOTE: If we have all mail subjects updated in the DB, we no longer need
			// getMailSubject method.
			if (null == mailSubject || StringUtils.isEmpty(mailSubject)) {
				mailSubject = jobRequisitionService.getMailSubject(jobOrderId, templateName, timeZone);
			}

			AcyutaCandidateDTO candidate = jobRequisitionService.getCandidate(candidateId);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			UserDTO userdto = jobRequisitionService.getUserInformation(userId);

			AcyutaInterviewsDTO interviewsDto = interviewId.compareTo(BigInteger.ZERO) > 0 ? interviewService.findByInterviewId(interviewId) : null;

			Map<String, String> dynamicValuesMap = jobRequisitionService.getDynamicValues(jobOrder, candidate, interviewsDto, userdto, templateName, timeZone);
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
			mailContent = StringUtils.replaceEachRepeatedly(template.getContent(), placeHolders, values);
			mailSubject = StringUtils.replaceEachRepeatedly(mailSubject, placeHolders, values);

			//FIXME get timeSlot in hh:mm a format only
			Pattern pattern = Pattern.compile("((((0|)[1-9])|(1[0-2])):([0-5])(0|5)(\\s*|)(A|P|a|p)(M|m))");
			Matcher match = pattern.matcher(dynamicValuesMap.get("[$TIME_SLOT$]"));
			
			mailTemplateJSON.put("mailContent", mailContent);
			mailTemplateJSON.put("mailSubject", mailSubject);
			mailTemplateJSON.put("interviewDate", dynamicValuesMap.get("[$DATE$]"));
			mailTemplateJSON.put("interviewTime", match.find()?match.group(1):"");
			mailTemplateJSON.put("clientInterviewMode", dynamicValuesMap.get("[$CLIENT_INTERVIEW_MODE$]"));
			mailTemplateJSON.put("clientDurationTime", dynamicValuesMap.get("[$CLIENT_DURATION$]"));
			mailTemplateJSON.put("timeZone", "CST");
			mailTemplateJSON.put("typeOfInterview", dynamicValuesMap.get("[$MODE_OF_INTERVIEW$]"));
			mailTemplateJSON.put("clientInterviewLocation", dynamicValuesMap.get("[$CLIENT_INTERVIEW_LOCATION$]"));
			mailTemplateJSON.put("clientInterviewerName", dynamicValuesMap.get("[$CLIENT_INTERVIEWER_NAME$]"));
			
//			dynamicValues.put("[$TIME_SLOT$]", interviews.getTimeSlot());
		
			log.info("mailSubject slected job: {}", mailSubject);
			log.debug("mailBody: {} ", mailContent);

		} catch (Exception e) {
			log.error("Exception in getMailTemplate method: ", e);
		}
		return mailTemplateJSON.toString();
	}

	// Provides the ActiveStatusList which we perform on the Candidate.
	// like SENT_RIGHT_TO_REPRESENT, REJECTED, CANDIDATE_PROFILE_UPDATED...
	@RequestMapping(value = "/getActiveStatusList")
	@ResponseBody
	public List<String> getActiveStatusList(HttpServletRequest request) throws JSONException {
		List<String> statusList = null;
		log.info("Inside getActiveStatusList");
		try {

			String orgIdStr = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			BigInteger organizationId = new BigInteger(orgIdStr);

			statusList = acyutaPropertiesService.getActiveStatusList(AConstants.SYSTEM_STATUS.getValue(), organizationId);
		} catch (Exception e) {
			log.error(" exception in getActiveStatusList method", e);
		}
		return statusList;
	}

	// When user performs any action on mail .. we get notification..
	// Like open, bounce,clicked link...in mail
	@PostMapping("/updateEmailStatus")
	@ResponseBody
	public void updateEmailStatus(@RequestBody String response) throws JSONException {
		log.info("Inside updateEmailStatus");
		try {
			JSONObject inputJSON = new JSONObject(response);
			log.info("inputJSON : {}", inputJSON);
			
			Integer candidateId = inputJSON.getInt(JobOrderConstants.CANDIDATE_ID_STRING);
			String eventStatus = inputJSON.getString(JobOrderConstants.EVENT_STRING);
			
			jobRequisitionService.updateEmailStatus(BigInteger.valueOf(candidateId.intValue()), eventStatus);

		} catch (Exception e) {
			log.error(" exception in getActiveStatusList method", e);
		}
	}

	// FIXME: write better logic to get mailTemplatesList... based on organizations.
	// Provided service to load templates list from DB.
	@RequestMapping(value = "/getMailTemplatesList")
	@ResponseBody
	public List<String> getActiveMailTemplatesList(HttpServletRequest request) {

		log.info("Inside getMailTemplatesList.");

		List<String> mailTemplatesList = new ArrayList<String>();
		String organizationIdStr = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		BigInteger organizationId = new BigInteger(organizationIdStr);
		if (null != AcyutaUtils.mailTemplateListMap && null == AcyutaUtils.mailTemplateListMap.get(organizationId)) {
			log.info("Inside getMailTemplatesList not empty.");
			List<String> templateList = null;
			templateList = acyutaPropertiesService.getActiveMailTemplateList(AConstants.ACYUTA_MAIL_TEMPLATE.getValue(), organizationId);
			mailTemplatesList.addAll(templateList);
		} else {
			mailTemplatesList = AcyutaUtils.mailTemplateListMap.get(organizationId);
		}
		log.info("MailTemplatesList size : {}", mailTemplatesList.size());
		log.debug("MailTemplatesList list : {}", mailTemplatesList);
		return mailTemplatesList;
	}

	// FIXME: Move send mail logic to common method..

	// sendEMail service send the e-mails to the candidates on related jobs.
	// RTR, ON_VOICEMAIL, AUTHORIZATION_FORM...
	@RequestMapping(value = "/sendEMail")
	@ResponseBody
	public String sendEMail(@RequestBody String body) throws JSONException {

		log.info("inside sendEMail");
		JSONObject output = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			String mailTemplate = input.getString(Constants.MAIL_TEMPLATE);
		//fix me get client slot details 	
			log.info("selected mailTemplate : {}", mailTemplate);
			String appEnvironement = environment.getProperty(JobOrderConstants.ENVIRONMENT);
			String status = input.getString(JobOrderConstants.STATUS_STRING);

			AcyutaUtils.sendMail(input, appEnvironement);

			// After every mail send, Change the email status to empty..
			String candidateIdStr = input.getString("candidateId");
			BigInteger candidateId = new BigInteger(candidateIdStr);
			jobRequisitionService.updateEmailStatus(candidateId, "");

			 String userIdStr = input.getString("userId");
			 BigInteger recruiterId = new BigInteger(userIdStr);
			List<String> articleStatusSignatureConstants = AcyutaConstants.getArticleStatusSignatureConstants();
			if (articleStatusSignatureConstants.contains(mailTemplate)) {
				log.info("mailTemplate :{} is signature template.", mailTemplate);
				scheduleInterviewsService.saveSignature(input);
			}

			if (JobOrderConstants.SCHEDULE_CLIENT_PHONE_INTERVIEW.equalsIgnoreCase(mailTemplate) || 
					JobOrderConstants.SCHEDULE_CLIENT_FACE2FACE_INTERVIEW.equalsIgnoreCase(mailTemplate) ||
					JobOrderConstants.SCHEDULE_CLIENT_VIDEOCONFERENCE_INTERVIEW.equalsIgnoreCase(mailTemplate)) {
				jobRequisitionService.saveClinetInterview(input);
			}

			if("UPDATE_INTERVIEW".equals(mailTemplate) || "CANCEL_INTERVIEW".equals(mailTemplate) || "REMINDER_INTERVIEW".equals(mailTemplate)){
				log.info("update interview date time  called during the mail send.");
				interviewService.updateCandidateInterview(input);
			}
			
			//FIXME:
			if(input.has("addStatus") && "true".equals(input.getString("addStatus"))){
				log.info("addStatus called during the mail send.");
				AcyutaCandidate candidate=addCandidateRepository.findByCandidateId(new BigInteger(input.getString("candidateId")));
				String candidateStatus = mailTemplate;
				if (AcyutaConstants.getArticleStatusConstants().containsKey(mailTemplate)) {
					candidateStatus = AcyutaConstants.getArticleStatusConstants().get(mailTemplate);
				}
				candidate.setCandidateStatus(candidateStatus);
				addCandidateRepository.save(candidate);
				jobRequisitionService.saveCandidateStatus(candidate, new BigInteger(input.getString("jobOrderId")),recruiterId);
			}
			output.put("Message", "Success");
			output.put("Status", "200");

		} catch (JSONException e) {
			output.put("Message", "Failure");
			output.put("Status", "500");
			log.error("exception occured while sending mail{}", e);
		}
		return output.toString();
	}

	@GetMapping(path = "/getCandidateTitles")
	public ResponseEntity<List<String>> getCandidateTitles(HttpServletRequest request) {
		log.info("Inside getCandidateTitles");
		ResponseEntity<List<String>> response = new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
		if (AcyutaUtils.jobTitleList.isEmpty()) {

			AcyutaUtils.jobTitleList = acyutaPropertiesService.getjobTitleList(AConstants.TITLE.getValue());
			response = new ResponseEntity<List<String>>(AcyutaUtils.jobTitleList, HttpStatus.OK);

		} else {
			response = new ResponseEntity<List<String>>(AcyutaUtils.jobTitleList, HttpStatus.OK);
		}
		return response;
	}

	@PostMapping("/changeCandidateJobStatus")
	@ResponseBody
	public int changeCandidateJobStatus(@RequestBody String response) throws JSONException {
		log.info("Inside changeCandidateJobStatus ");
		int updateStatus = 0;
		try {
			JSONObject inputJSON = new JSONObject(response);
			updateStatus = jobRequisitionService.updateCandidateStatus(inputJSON);

			log.info("update candidate job status : {}", updateStatus);
		} catch (Exception e) {
			log.error(" exception in changeCandidateJobStatus method", e);
		}
		return updateStatus;
	}

	@PostMapping("/schedulePannaInterview")
	@ResponseBody
	public String schedulePannaInterview(@RequestBody String response) throws JSONException {
		log.info("Inside schedulePannaInterview ");
		String updateStatus = "";
		try {
			JSONObject inputJSON = new JSONObject(response);
			updateStatus = scheduleInterviewsService.schedulePannaInterviewREST(inputJSON);
		} catch (Exception e) {
			log.error("Exception in schedulePannaInterview method", e);
		}
		return updateStatus;
	}

	@PostMapping("/scheduleMliveInterview")
	@ResponseBody
	public String scheduleMliveInterview(@RequestBody String response) throws JSONException {
		log.info("Inside scheduleMliveInterview ");
		String updateStatus = "";
		try {
			JSONObject inputJSON = new JSONObject(response);
			updateStatus = scheduleInterviewsService.scheduleMliveInterviewREST(inputJSON);
		} catch (Exception e) {
			log.error("Exception in scheduleMliveInterview method", e);
		}
		return updateStatus;
	}

	@PostMapping("/scheduleNextRoundInterview")
	@ResponseBody
	public String scheduleNextRoundInterviewService(@RequestBody String req) throws JSONException {
		log.info("Inside scheduleNextRoundInterview ");
		JSONObject output = new JSONObject();
		try {
			JSONObject inputJSON = new JSONObject(req);
			String succuss = scheduleInterviewsService.scheduleNextRoundInterview(inputJSON);
			output.put("Message", succuss);
			output.put("status", 200);
		} catch (Exception e) {
			log.error("Exception in scheduleMliveInterview method", e);
			output.put("Message", "failure");
			output.put("status", 500);
		}
		return output.toString();
	}

	// Load the interview time slots to schedule mLive interviews
	@RequestMapping(value = "loadInterviewersTimeSlotList")
	public String loadInterviewersTimeSlotList(HttpServletRequest request) {
		log.info("Inside loadInterviewersTimeSlotList ");
		String resultData = "";
		try {
			String orgIdStr = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			BigInteger organizationId = new BigInteger(orgIdStr);
			String dateOfInterview = request.getHeader("dateOfInterview");

			log.info("loadInterviewersTimeSlotList inputData # organizationId: {}, " + "dateOfInterview: {}  ", organizationId, dateOfInterview);

			List<JSONObject> result = scheduleInterviewsService.getInterviewSlots(organizationId, dateOfInterview);

			resultData = result.toString();
			log.info("resultData :{} ", resultData);
		} catch (Exception e) {
			log.error("Exception in showInterviewersTimeSlotList method", e);
		}
		return resultData;
	}

}