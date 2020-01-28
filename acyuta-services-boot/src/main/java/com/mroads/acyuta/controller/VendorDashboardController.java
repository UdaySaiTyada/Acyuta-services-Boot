package com.mroads.acyuta.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
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

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.EncryptDecryptUtil;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.JobCommentsDTO;
import com.mroads.acyuta.dto.VendorDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.service.JobRequisitionService;
import com.mroads.acyuta.service.PostJobService;
import com.mroads.acyuta.service.VendorService;

// InterviewsController provides service end points to load 
// current and completed interviews(panna, mLive) scheduled for the acyuta candidates.
@CrossOrigin
@RestController
@RequestMapping(path = "/vendor")
public class VendorDashboardController {
	private static final Logger logger = LoggerFactory.getLogger(VendorDashboardController.class);
	private static final ObjectMapper mapper = new ObjectMapper();


	@Autowired
	private VendorService vendorService;

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	private JobRequisitionService jobRequisitionService;

	@Autowired
	private PostJobService postJobService;

	@Autowired
	private Environment environment;
	
	@Autowired
	public AcyutaCandidateRepository acyutaCandidateRepository;

	@RequestMapping(value = "/fetchVendorsForOrganization", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<VendorDTO>> fetchVendors(@RequestBody String request) throws Exception {

		logger.info("Inside fetchVendorsForOrganization");
		try {
			JSONObject inputJson = new JSONObject(request);
			logger.info("inputJson : {}",inputJson);
			BigInteger orgId = BigInteger.valueOf(inputJson.getInt("organizationId"));
			List<VendorDTO> vendorList = new ArrayList<>();
			vendorList = vendorService.getAllVendorsForOrganizationId(orgId);
			return new ResponseEntity<>(vendorList, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in fetchVendorsForOrganization() {}", e);
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.valueOf(420));
		}
	}

	@RequestMapping(value = "/fetchUserVendorInfo", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<VendorDTO> fetchUserVendorInfo(@RequestBody String request) throws Exception {

		logger.info("Inside fetchUserVendorInfo");
		try {
			JSONObject inputJson = new JSONObject(request);
			logger.info("inputJson : {}",inputJson);
			BigInteger orgId = new BigInteger(inputJson.getString("organizationId"));
			BigInteger userId = new BigInteger(inputJson.getString("userId"));
			VendorDTO vendorDTO = vendorService.getUserVendorInfo(orgId, userId);
			return new ResponseEntity<>(vendorDTO, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in fetchUserVendorInfo() {}", e);
			return new ResponseEntity<>(null, HttpStatus.valueOf(420));
		}
	}

	
	@RequestMapping(value = "/fetchHrmForOrganization", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> fetchHrmForOrganization(@RequestBody String request) throws Exception {
		logger.info("Inside  fetchHrmForOrganization, request {}", request);

		try {
			List<JSONObject> vendorList = new ArrayList<>();
			BigInteger vendorRoleId = new BigInteger(environment.getProperty("hrm.role.id"));
			JSONObject inputJson = new JSONObject(request);
			if (inputJson.isNull("organizationId")) {
				throw new Exception("organizationId can't be null");
			}
			BigInteger orgId = BigInteger.valueOf(inputJson.getInt("organizationId"));
			vendorList = vendorService.getRoleUsers(vendorRoleId, orgId);

			return new ResponseEntity<>(vendorList.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in fetchHrmForOrganization() {}", e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
	}

	@RequestMapping(value = "/fetchTechSourcingManagers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> fetchTechSourcingManagers(@RequestBody String request) throws Exception {
		logger.info("Inside fetchTechSourcingManagers request {}", request);

		try {
			List<JSONObject> vendorList = new ArrayList<>();
			BigInteger hrRoleId = new BigInteger(environment.getProperty("hr.role.id"));
			JSONObject inputJson = new JSONObject(request);
			if (inputJson.isNull("organizationId")) {
				throw new Exception("organizationId can't be null");
			}
			BigInteger orgId = BigInteger.valueOf(inputJson.getInt("organizationId"));
			vendorList = vendorService.getRoleUsers(hrRoleId, orgId);
			return new ResponseEntity<>(vendorList.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in fetchTechSourcingManagers() {}", e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
	}

	@RequestMapping(value = "/fetchVPsForOrganization", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> fetchVPsForOrganization(@RequestBody String request) throws Exception {
		logger.info("Inside fetchVPsForOrganization request {}", request);

		try {
			List<JSONObject> vendorList = new ArrayList<>();
			BigInteger vendorRoleId = new BigInteger(environment.getProperty("vp.role.id"));
			JSONObject inputJson = new JSONObject(request);
			if (inputJson.isNull("organizationId")) {
				throw new Exception("organizationId can't be null");
			}
			BigInteger orgId = BigInteger.valueOf(inputJson.getInt("organizationId"));
			vendorList = vendorService.getRoleUsers(vendorRoleId, orgId);
			return new ResponseEntity<>(vendorList.toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in fetchVPsForOrganization() {}", e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
	}

	/**
	 * @param body
	 * @return
	 */
	@RequestMapping(path = "/publishVendorJob", method = RequestMethod.POST)
	public ResponseEntity<String> publishVendorJobs(@RequestBody String body) {
		logger.info("Inside publishVendorJobs ");
		try {
			ResponseEntity<String> response;

			JSONObject inputJson = new JSONObject(body);
			String scheduleType = inputJson.getString(Constants.JOB_SCHEDULE_TYPE_STRING);
			BigInteger recruiterId = new BigInteger(inputJson.getString(JobOrderConstants.RECRUITER_ID_STRING));
			
			logger.info("publishVendorJobs inputJson: {}", inputJson);
			
			BigInteger vendorId = BigInteger.ZERO;
			if (inputJson.getBoolean("addNewVendor")) {
				logger.info("New Tier2 vendor provided. ");
				vendorId = vendorService.addNewVendor(inputJson, recruiterId);
			}

			// change job status to ACTIVE.
			BigInteger jobOrderId = BigInteger.valueOf(inputJson.getInt("jobOrderId"));
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setStatus(Constants.JOB_ACTIVE_STATUS);
			jobOrder.setScheduleType(scheduleType);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.CST_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(recruiterId);
			
			jobOrderRepository.save(jobOrder);

			// If auto schedule link job to interview position... interviewPostion Id,  jobOrderId...
			if (Constants.AUTO_SCHEDULE_STRING.equalsIgnoreCase(scheduleType)) {
				logger.info("scheduleType :Auto Schedule");
				postJobService.updateInterviewPosition(body);
			}
			// publish to all selected vendor managers.
			vendorService.publishVendorJob(inputJson, vendorId);

			response = new ResponseEntity<String>("success", HttpStatus.OK);
			return response;
		} catch (Exception e) {
			logger.error("Exception in publishVendorJob Controller: {}", e);
			return new ResponseEntity<>("failure",HttpStatus.valueOf(420));
		}
	}

	@GetMapping(path = "/getDraftedJobs")
	public ResponseEntity<String> getDraftedJobs(HttpServletRequest request) {

		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		try {
			String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
			String clientName = request.getHeader(JobOrderConstants.CLIENT_NAME_STRING);
			logger.info("Inside getDraftedJobs using organizationId = >{}<", orgIdString);

			if (orgIdString == null) {
				logger.info("Invalid data entered.");
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}

			BigInteger organizationId = new BigInteger(orgIdString);
			String status = JobOrderConstants.JOB_DRAFTED_STATUS;
			JSONArray allActiveJobsArray = postJobService.getClientJobs(organizationId, clientName, status);
			 response = new ResponseEntity<String>(allActiveJobsArray.toString(), HttpStatus.OK);
			logger.info("{} drafted jobs are retrieved for organizationId={} and ClientName={}", allActiveJobsArray.length(), organizationId, clientName);
		} catch (Exception e) {
			logger.error("Exception in getDraftedJobs : ",e);
			response = new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}

		return response;
	}

	@RequestMapping(value = "/updateJobVendors", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> updateJobVendors(@RequestBody String request) throws Exception {
		logger.info("Inside updateJobVendors {}", request);

		try {
			JSONObject inputJson = new JSONObject(request);
			vendorService.updateJobVendors(inputJson);
			return new ResponseEntity<>("success", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception in updateJobVendors() {}", e);
			return new ResponseEntity<>("failure", HttpStatus.valueOf(420));
		}
	}

	@PutMapping(path = "/updateScheduleType")
	public ResponseEntity<String> updateJobScheduleStatus(@RequestBody String body) {

		logger.info("Inside updateScheduleType request.");
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

		try {
			JSONObject input = new JSONObject(body);
			String scheduleType = input.getString(Constants.JOB_SCHEDULE_TYPE_STRING);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger jobOrderId = new BigInteger(input.has("jobOrderId") ? input.getString("jobOrderId") : "0");
			BigInteger userId = new BigInteger(input.has(JobOrderConstants.USER_ID_STRING) ? input.getString(JobOrderConstants.USER_ID_STRING) : "0");

			Date updatedDate = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setScheduleType(scheduleType);
			jobOrder.setUpdatedBy(userId);
			jobOrder.setUpdatedDate(updatedDate);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			vendorService.sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			response = new ResponseEntity<String>("success", HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error occurred when updating  updateScheduleType ", e);
			response = new ResponseEntity<String>("Bad request obtained", HttpStatus.valueOf(420));
		}
		return response;
	}

	/*-----------***************------------>::START-::>VIEW/EDIT JOB SERVICES::<----------------*************----------*/

	@PutMapping(path = "/updateJobStatus")
	public ResponseEntity<String> updateJobStatus(@RequestBody String body) {
		logger.info(" Inside updateStatus : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateJobStatus(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateSkills")
	public ResponseEntity<String> updateSkills(@RequestBody String body) {
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateSkills(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PostMapping(path = "/updateComment")
	public ResponseEntity<String> updateComment(@RequestHeader("timeZone") String timeZone, @RequestBody JobCommentsDTO jobComment, @RequestHeader String userId,
			org.json.JSONArray vendorInfo) {

		logger.info("Comment obtained to update/create in db is {}", jobComment.toString());
		String commentStatus = jobRequisitionService.updateCommentInDB(jobComment, userId);
		JobOrder jobOrder = jobOrderRepository.findByJobOrderId(new BigInteger(jobComment.getJobOrderId()));

		String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
		vendorService.sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

		JSONArray jobComments = new JSONArray();
		jobComments = jobRequisitionService.getComments(jobComment.getJobOrderId(), timeZone);

		if (commentStatus.equals("Error")) {
			logger.info("Cannot update job comment.{}", jobComment.getJobOrderId());
			// Use Status 420 to specify that db is available but unable to update in db
			return new ResponseEntity<String>("Cannot create a new comment", HttpStatus.valueOf(420));
		}

		logger.info("Updated comment with commentId = {}", commentStatus);
		return new ResponseEntity<String>(jobComments.toString(), HttpStatus.OK);
	}

	@PutMapping(path = "/updateDescription")
	public ResponseEntity<String> updateJobDescription(@RequestBody String body) {
		logger.info("Inside updateDescription");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateJobDescription(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updatePayRate")
	public ResponseEntity<String> updatePayRate(@RequestBody String body) {
		logger.info(" Inside updatePayRate : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updatePayRate(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateJobType")
	public ResponseEntity<String> updateJobType(@RequestBody String body) {
		ResponseEntity<String> response;
		logger.info(" Inside updateJobType : ");
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateJobType(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PostMapping(path = "/updateJobTitle")
	public ResponseEntity<String> updateJobTitle(@RequestBody String body) {
		ResponseEntity<String> response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		logger.info(" Inside updateJobTitle : ");
		try {
			vendorService.updateJobTitle(body);
			String returnResponse = vendorService.updateJobType(body);
			if (returnResponse == "UPDATED")
				response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
			return response;

		} catch (Exception e) {
			logger.error("Error occurred when updating JobTitle", e);
			response = new ResponseEntity<String>("Error in updateJobTitle", HttpStatus.valueOf(420));
		}
		return response;

	}

	@PutMapping(path = "/updateDuration")
	public ResponseEntity<String> updateJobDuration(@RequestBody String body) {
		logger.info(" Inside updateDuration : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateJobDuration(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateLocations")
	public ResponseEntity<String> updateLocations(@RequestBody String body) {
		ResponseEntity<String> response;
		logger.info(" Inside updateLocations : ");
		String returnResponse = vendorService.updateLocations(body);
		response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		if (returnResponse == "ERROR") {
			response = new ResponseEntity<String>("NOT UPDATED SUCCESSFULLY", HttpStatus.valueOf(420));
		}
		return response;
	}

	/*-----------***************------------>::END-::>VIEW/EDIT JOB SERVICES::<----------------*************----------*/

	@RequestMapping(value = "/checkDuplicateResume")
	@ResponseBody
	public List<AcyutaCandidateDTO> checkDuplicateResume(HttpServletRequest request) throws JSONException {
		logger.info("In checkDuplicateResume service at Vendor level.");
		List<AcyutaCandidateDTO> dupResumes = null;
		String phoneNumber = request.getHeader("phoneNumber");
		String emailAddress = request.getHeader("emailAddress");
		String vendorIdStr = request.getHeader("vendorId");
		BigInteger vendorId = new BigInteger(vendorIdStr);
		String full_name = "";

		logger.info("checkDuplicateResume Input :>> phoneNumber:{}, emailAddress:{}, vendorId:{} ", phoneNumber, emailAddress, vendorId);

		try {
			dupResumes = vendorService.findByEmailAddressOrPhoneNumberAndVendorId(phoneNumber, emailAddress, vendorId);
			// FIXME:
			if (dupResumes.size() > 0) {
				AcyutaCandidateDTO candidate = dupResumes.get(0);
				full_name = candidate.getFirstName() + " " + candidate.getLastName();
			}
			logger.info("Duplicate resume count :>{}< ", dupResumes.size());

		} catch (Exception e) {
			logger.error("Exception in checkDuplicateResume service", e);
		}
		return dupResumes;
	}

	/*-----------***************------------>::START-::>BILLING INFO SERVICES::<----------------*************----------*/

	@PutMapping(path = "/updateDepartmentCode")
	public ResponseEntity<String> updateDepartmentCode(@RequestBody String body) {
		logger.info(" Inside updateDepartmentCode : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateDepartmentCode(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateBuOu")
	public ResponseEntity<String> updateBuOu(@RequestBody String body) {
		logger.info(" Inside updateBuOu : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateBuOu(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateAccountNumber")
	public ResponseEntity<String> updateAccountNumber(@RequestBody String body) {
		logger.info(" Inside updateAccountNumber : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateAccountNumber(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateInvoicingContact")
	public ResponseEntity<String> updateInvoicingContact(@RequestBody String body) {
		logger.info(" Inside updateInvoicingContact : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateInvoicingContact(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateHiringManager")
	public ResponseEntity<String> updateHiringManger(@RequestBody String body) {
		logger.info(" Inside updateHiringManger : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateHiringManger(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateVicePresident")
	public ResponseEntity<String> updateVicePresident(@RequestBody String body) {
		logger.info(" Inside updateVicePresident : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateVicePresident(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	@PutMapping(path = "/updateInterviewPosition")
	public ResponseEntity<String> updateInterviewPosition(@RequestBody String body) {
		logger.info(" Inside updateInterviewPosition : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateInterviewPosition(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updatePotentialConversion in jobOrder table
	 * 
	 * @param body
	 * @return 
	 */
	@PutMapping(path = "/updatePotentialConversion")
	public ResponseEntity<String> updatePotentialConversion(@RequestBody String body) {
		logger.info(" Inside updatePotentialConversion : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updatePotentialConversion(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateResourceType in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateResourceType")
	public ResponseEntity<String> updateResourceType(@RequestBody String body) {
		logger.info(" Inside updateResourceType : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateResourceType(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateEquipment in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateEquipment")
	public ResponseEntity<String> updateEquipment(@RequestBody String body) {
		logger.info(" Inside updateEquipment : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateEquipment(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateDepartmentName in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateDepartmentName")
	public ResponseEntity<String> updateDepartmentName(@RequestBody String body) {
		logger.info(" Inside updateDepartmentName : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateDepartmentName(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateMaxBillRate in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateMaxBillRate")
	public ResponseEntity<String> updateMaxBillRate(@RequestBody String body) {
		logger.info(" Inside updateMaxBillRate : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateMaxBillRate(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateBillRate in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateBillRate")
	public ResponseEntity<String> updateBillRate(@RequestBody String body) {
		logger.info(" Inside updateBillRate : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateBillRate(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}

	/**
	 * updateJobLevel in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateJobLevel")
	public ResponseEntity<String> updateJobLevel(@RequestBody String body) {
		logger.info(" Inside updateJobLevel : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateJobLevel(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}
	
	/**
	 * updating ProjectTargetDate in jobOrder tables
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updateProjectTargetDate")
	public ResponseEntity<String> updateProjectTargetDate(@RequestBody String body) {
		logger.info(" Inside updateProjectTargetDate : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updateProjectTargetDate(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}
	
	
	
	/**
	 * updating PotentialSalaryRange in jobOrder tables
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/updatePotentialSalaryRange")
	public ResponseEntity<String> updatePotentialSalaryRange(@RequestBody String body) {
		logger.info(" Inside updatePotentialSalaryRange : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		String returnResponse = vendorService.updatePotentialSalaryRange(body);
		if (returnResponse == "UPDATED")
			response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		return response;
	}
	

	/**
	 * updating PotentialSalaryRange in jobOrder tables
	 * @param body
	 * @return
	 */
	@PutMapping(path = "/changeCandidateStatus")
	public ResponseEntity<String> changeCandidateStatus(@RequestBody String body) {
		
		logger.info(" Inside changeCandidateStatus : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));
		
		try {
			// NOTE: If candidate status is HIRED send notification mail to accounts team and Tech Sourcing Manager..
			String returnResponse = vendorService.updateCandidateStatus(body);
			if (returnResponse == "UPDATED")
				response = new ResponseEntity<String>("UPDATED SUCCESSFULLY", HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Exception changeCandidateStatus : {}", e);
		}
		
		
		return response;
	}
	
	// viewFudingApproveProfile called when account manager clicks 
	// on candidate link from mail to funding approve request.
	@RequestMapping(value = "/viewFudingApproveProfile", method = RequestMethod.POST)
	public ResponseEntity<String> viewFudingApproveProfile(@RequestBody String body) {

		logger.info(" Inside viewFudingApproveProfile : ");
		ResponseEntity<String> response;
		response = new ResponseEntity<String>("ERROR OCCURED", HttpStatus.valueOf(420));

		BigInteger candidateId = null;
		
		try {
			JSONObject inputJson = new JSONObject(body);
			String profile = inputJson.getString("profile");
			logger.info("viewFudingApproveProfile inputJson: {}", inputJson);
			
			if (inputJson.has("isEncoded") && inputJson.getBoolean("isEncoded")) {
				String candidateIdStr = EncryptDecryptUtil.getDecryptKey(profile);
				candidateId = new BigInteger(candidateIdStr);
			} else {
			
				candidateId = new BigInteger(profile);
			}

			if (null != candidateId) {
				AcyutaCandidate candidate = acyutaCandidateRepository.findByCandidateId(candidateId);

				AcyutaCandidateDTO candidateDTO = mapper.convertValue(candidate, AcyutaCandidateDTO.class);
				String candidateJSON = mapper.writeValueAsString(candidateDTO);

				response = new ResponseEntity<String>(candidateJSON, HttpStatus.OK);
			}

		} catch (Exception e) {
			logger.error("Exception viewFudingApproveProfile : {}", e);
		}

		return response;
	}

}
