package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.EncryptDecryptUtil;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.common.VendorConstatns;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.RecruitersDTO;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.dto.VendorDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.JobLocation;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.Recruiters;
import com.mroads.acyuta.model.User;
import com.mroads.acyuta.model.Vendor;
import com.mroads.acyuta.model.VendorJobMapping;
import com.mroads.acyuta.model.VmsRoles;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.JobLocationRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.RecruitersRepository;
import com.mroads.acyuta.repository.UserRepository;
import com.mroads.acyuta.repository.VendorJobMappingRepository;
import com.mroads.acyuta.repository.VendorRepository;
import com.mroads.acyuta.repository.VmsRolesRepository;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;


@Service
public class VendorService {

	private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	VendorRepository vendorRepository;

	@Autowired
	VmsRolesRepository vmsRolesRepository;

	@Autowired
	public UserRepository userRepository;

	@Autowired
	private JobRequisitionService jobRequisitionService;

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	private VendorJobMappingRepository vendorJobMappingRepository;

	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;

	@Autowired
	private Environment environment;

	@Autowired
	private JobLocationRepository jobLocationRepository;

	@Autowired
	private CommonServices commonServices;
	
	

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private static RestTemplate restTemplate;

	@Autowired(required = true)
	public synchronized void setRestTemplate(RestTemplate env1) {
		VendorService.restTemplate = env1;
	}

	@Autowired
	private RecruitersRepository recruitersRepository;

	public List<VendorDTO> getAllVendors() {

		try {
			List<VendorDTO> dtoList = new ArrayList<>();

			dtoList = convertFromModeltoDTO(vendorRepository.findAll());
			return dtoList;

		}

		catch (Exception e) {
			logger.error("Exception in getAllVendors", e);
			return new ArrayList<>();
		}
	}

	public List<VendorDTO> getAllVendorsForOrganizationId(BigInteger organizationId) {

		try {
			List<VendorDTO> dtoList = new ArrayList<>();
			dtoList = convertFromModeltoDTO(vendorRepository.findByOrganizationId(organizationId));
			logger.info("Vendor list size {}, for organizationId: {}", dtoList.size(), organizationId);
			logger.debug("Vendor details: {}", dtoList);
			return dtoList;
		} catch (Exception e) {
			logger.error("Exception in getAllVendorsForOrganizationId", e);
			return new ArrayList<>();
		}
	}
	
	public List<VendorDTO> getTier1vendors(BigInteger organizationId) {

		try {
			List<VendorDTO> dtoList = new ArrayList<>();
			dtoList = convertFromModeltoDTO(vendorRepository.findByOrganizationIdAndTier(organizationId,VendorConstatns.TIER1_VENDOR));
			logger.info("Vendor list size {}, for organizationId: {}", dtoList.size(), organizationId);
			logger.debug("Vendor details: {}", dtoList);
			return dtoList;
		} catch (Exception e) {
			logger.error("Exception in getAllVendorsForOrganizationId", e);
			return new ArrayList<>();
		}
	}
	

	public VendorDTO getUserVendorInfo(BigInteger organizationId, BigInteger userId) {

		VendorDTO vendorDTO = new VendorDTO();
		try {
			VmsRoles vmsRoles = null;
			String vendorRoleStr = environment.getProperty("vendor.role.id");
			BigInteger vendorRoleId = new BigInteger(vendorRoleStr);

			logger.info("userId: {}, organizationId: {}", userId, organizationId);
			vmsRoles = vmsRolesRepository.findByUserIdAndRoleId(userId, vendorRoleId);
			logger.debug("vmsRole: {}", vmsRoles);
			if(null!=vmsRoles) {
				Vendor vendor = vendorRepository.findByVendorId(vmsRoles.getVendorId());
				vendorDTO = mapper.convertValue(vendor, VendorDTO.class);	
			}

			logger.debug("Vendor details: {}", vendorDTO);
			return vendorDTO;
		} catch (Exception e) {
			logger.error("Exception in getUserVendorInfo", e);
			return vendorDTO;
		}
	}

	public List<JSONObject> getRoleUsers(BigInteger vendorRoleId, BigInteger organizationId) {

		try {
			List<VmsRoles> vmsRoles = new ArrayList<>();

			vmsRoles = vmsRolesRepository.findByRoleIdAndOrganizationId(vendorRoleId, organizationId);
			List<JSONObject> vendorUsers = new ArrayList<JSONObject>();
			for (VmsRoles vmsRole : vmsRoles) {
				JSONObject vendor = new JSONObject();
				User user = userRepository.getUserInformation(vmsRole.getUserId());

				if (null != user) {
					vendor.put(JobOrderConstants.USER_ID_STRING, vmsRole.getUserId());
					vendor.put("userName", user.getFirstName() + " " + user.getLastName());
					vendorUsers.add(vendor);
				} else {
					JSONObject responseJSON = getFireBaseUserInfo(vmsRole.getUserId(), organizationId);
					vendor.put(JobOrderConstants.USER_ID_STRING, vmsRole.getUserId());
					vendor.put("userName", responseJSON.getString("firstName") + " " + responseJSON.getString("lastName"));
					vendorUsers.add(vendor);
				}

			}
			logger.info("UserRoles size {}, UserRoles: {}", vendorUsers.size(),vendorUsers);
			return vendorUsers;
		}

		catch (Exception e) {
			logger.error("Exception in getRoleUsers", e);
			return new ArrayList<>();
		}
	}

	JSONObject getFireBaseUserInfo(BigInteger userId, BigInteger organizationId) {
		String responseBody = "";
		JSONObject responseJSON = new JSONObject();
		String url = environment.getProperty("firebase.user.info");
		try {
			logger.info("getFireBaseUserInfo.....");
			JSONObject inputJson = new JSONObject();
			inputJson.put(JobOrderConstants.USER_ID_STRING, userId.toString());
			inputJson.put("organizationId", organizationId.toString());
			logger.info("url: {}", url);
			logger.info("restTemplate: {}", restTemplate);
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(inputJson.toString()), String.class);
			logger.info("Inside responseEntity: {}", responseEntity.getBody());
			responseBody = responseEntity.getBody();
			responseJSON = new JSONObject(responseBody);

			logger.info("responseBody : {}", responseBody);
		} catch (Exception e) {
			logger.error("Error in validateMySqlUser {}", e);
		}
		return responseJSON;
	};

	public List<VmsRoles> getHiringMangers(BigInteger userId) {

		try {
			List<VmsRoles> dtoList = new ArrayList<>();

			BigInteger hrmRoleId = null;
			dtoList = vmsRolesRepository.findByRoleIdAndOrganizationId(hrmRoleId, userId);
			logger.info("Total no. of vendors {}", dtoList.size());
			return dtoList;
		} catch (Exception e) {
			logger.error("Exception in getHiringMangers", e);
			return new ArrayList<>();
		}
	}

	public List<VendorDTO> convertFromModeltoDTO(List<Vendor> vendorList) {
		List<VendorDTO> dtoList = new ArrayList<>();
		for (Vendor vendor : vendorList) {
			dtoList.add(mapper.convertValue(vendor, VendorDTO.class));
		}
		return dtoList;
	}

	public List<VmsRoles> getAssignedVendormanagers(BigInteger vendorId, BigInteger organizationId) {

		List<VmsRoles> vendorUsers = vmsRolesRepository.findByVendorIdAndOrganizationId(vendorId, organizationId);

		return vendorUsers;

	}

	public void assignVendorToJob(BigInteger vendorId, JobOrder jobOrder,  BigInteger recruiterId) {

		logger.info("Save recruiters service called for jobOrderId = >{}<, recruiterId = >{}<", jobOrder.getJobOrderId(), recruiterId);

		RecruitersDTO recruitersDTO = new RecruitersDTO();
		Recruiters recruitersRep = new Recruiters();
		recruitersDTO.setJobOrderId(jobOrder.getJobOrderId());
		recruitersDTO.setRecruiterId(recruiterId);
		recruitersDTO.setClientName(jobOrder.getClientName());
		Date date = AcyutaUtils.getTimeInZone(ZoneOffset.UTC);
		recruitersDTO.setUpdatedDate(date);
		recruitersDTO.setCreatedDate(date);
		recruitersDTO.setVendorId(vendorId);
		recruitersDTO.setOrganizationId(jobOrder.getOrganizationId());
		recruitersDTO.setStatus(Constants.JOB_ACTIVE_STATUS);
		recruitersRep = (Recruiters) ObjectConverter.convert(recruitersDTO, new Recruiters());
		Recruiters recruiters = recruitersRepository.save(recruitersRep);
		logger.debug("Saving recruiter recruiterId = >{}< with jobOrderId = >{}<, data = {}", recruiterId, jobOrder.getJobOrderId(), recruiters);

	}

	// When HR Manger publish a job/ D3 Manager creates a job, send assignment notification mail to
	// all assigned vendors and Hiring Manger.
	public void publishVendorJob(JSONObject inputJson, BigInteger newVendorId) throws Exception {
		logger.info("Inside publishVendorJob.");
		try {
			String addCandidateEmail = "";
			BigInteger organizationId = new BigInteger(inputJson.getString(JobOrderConstants.ORGANIZATION_ID_STRING));
			BigInteger recruiterId = new BigInteger(inputJson.getString(JobOrderConstants.RECRUITER_ID_STRING));
			BigInteger jobOrderId = BigInteger.valueOf(inputJson.getInt("jobOrderId"));

			org.json.JSONArray vendorInfo = inputJson.getJSONArray("vendorInfo");

			if (newVendorId.compareTo(BigInteger.ZERO) != 0) {
				vendorInfo.put(newVendorId);
			}
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);

			String mailSubject = "New Hilton Requirement RFS# " +jobOrder.getJobId()+" "+ jobOrder.getJobTitle();

			List<String> toAddressList = new ArrayList<String>();
			for (int i = 0; i < vendorInfo.length(); i++) {

				BigInteger vendorId = new BigInteger("" + vendorInfo.get(i));
				// For each vendor save job order mapping.
				saveVendorJobMapping(inputJson, vendorId);

				// get all assigned vendor managers for the organization...
				List<VmsRoles> vendorUsers = getAssignedVendormanagers(vendorId, organizationId);

				for (VmsRoles vmsRole : vendorUsers) {
					BigInteger vendorManagerId = vmsRole.getUserId();
					UserDTO userDTO = jobRequisitionService.getRecruitersInfo(vendorManagerId);
					assignVendorToJob(vendorId, jobOrder, vendorManagerId);
					vendorNotificationMail(recruiterId, userDTO, jobOrder, mailSubject, Constants.JOB_ASSIGN_NOTIFICATION, addCandidateEmail);
					logger.info("Job assign Notification mail sent to vendor manager :{}, with vendorId: {}", userDTO.getEmailAddress(),vendorId);
				}
			}
			// If the job has Hiring Manger, send the job publish notification mail.
			if (null != jobOrder.getHiringManagerId()) {
				toAddressList = new ArrayList<>();
				UserDTO user = jobRequisitionService.getUserInformation(jobOrder.getHiringManagerId());
				toAddressList.add(user.getEmailAddress());
				vendorNotificationMail(jobOrder.getCreatedBy(), user, jobOrder, mailSubject, Constants.JOB_ASSIGN_NOTIFICATION_HM, addCandidateEmail);
				logger.info("Job assign Notification mail sent to HiringManager :{}", user.getEmailAddress());
			}
		} catch (Exception e) {
			logger.error("Exception in publishVendorJob: ", e);
			throw new Exception("Exception in publishVendorJob:");
		}
	}

	private void saveVendorJobMapping(JSONObject inputJson, BigInteger vendorId) {
		try {
			Log.info("Inside saveVendorJobMapping: {}",vendorId);
			BigInteger organizationId = new BigInteger(inputJson.getString(JobOrderConstants.ORGANIZATION_ID_STRING));
			BigInteger recruiterId = new BigInteger(inputJson.getString(JobOrderConstants.RECRUITER_ID_STRING));
			BigInteger jobOrderId = BigInteger.valueOf(inputJson.getInt("jobOrderId"));

			VendorJobMapping vendorJobMapping = vendorJobMappingRepository.findByVendorIdAndOrganizationIdAndJobOrderId(vendorId, organizationId,jobOrderId);
			if(null!=vendorJobMapping) {
				vendorJobMapping.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
				vendorJobMapping.setStatus(Constants.JOB_ACTIVE_STATUS);
				vendorJobMapping.setUpdatedBy(recruiterId);
				vendorJobMappingRepository.save(vendorJobMapping);
				
			}else {
				logger.info("Inside saveVendorJobMapping, jobOrderId :{}, vendorId: {}", jobOrderId, vendorId);
				VendorJobMapping vendorJob = new VendorJobMapping();
				vendorJob.setJobOrderId(jobOrderId);
				vendorJob.setOrganizationId(organizationId);
				vendorJob.setVendorId(vendorId);
				vendorJob.setStatus(Constants.JOB_ACTIVE_STATUS);
				vendorJob.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
				vendorJob.setCreatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
				vendorJob.setCreatedBy(recruiterId);
				vendorJob.setUpdatedBy(recruiterId);
				vendorJobMappingRepository.save(vendorJob);
			}
			
			

		} catch (Exception e) {
			logger.error("Exception in save VendorJobMapping: ", e);
		}
	}

	public void updateJobVendors(JSONObject inputJson) throws Exception {

		Log.info("Inside updateJobVendors");
		org.json.JSONArray addedVendors = inputJson.getJSONArray("vendorInfo");
		org.json.JSONArray removedVendors = inputJson.getJSONArray("removedVendors");

		if (addedVendors.length() > 0) {
			for (int i = 0; i < addedVendors.length(); i++) {
				// we not create new vendor so we passing the BigInteger.ZERO
				publishVendorJob(inputJson, BigInteger.ZERO);
			}
		}
		if (removedVendors.length() > 0) {
			removeVendorFromJob(inputJson);
		}

	}

	void removeVendorFromJob(JSONObject inputJson) throws JSONException {
		String addCandidateEmail = "";
		logger.info("Inside removeVendorFromJob.");
		org.json.JSONArray removedVendors = inputJson.getJSONArray("removedVendors");

		String clientName = inputJson.getString("clientName");
		String jobTitle = inputJson.getString("jobTitle");
		BigInteger jobOrderId = BigInteger.valueOf(inputJson.getInt("jobOrderId"));
		JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);

		BigInteger userId = new BigInteger(inputJson.getString(JobOrderConstants.USER_ID_STRING));
		BigInteger organizationId = new BigInteger(inputJson.getString(JobOrderConstants.ORGANIZATION_ID_STRING));

		List<String> toAddressList = new ArrayList<String>();
		String mailSubject = "You have been unassigned to " + jobTitle + " for " + clientName;
		for (int i = 0; i < removedVendors.length(); i++) {

			logger.info("removedVendors .... {}", removedVendors.get(i));
			BigInteger vendorId = new BigInteger(removedVendors.getString(i));
			VendorJobMapping vendorJobMapping = vendorJobMappingRepository.findByVendorIdAndOrganizationIdAndJobOrderId(vendorId, organizationId,jobOrderId);
			vendorJobMapping.setStatus(Constants.JOB_HALTED_STATUS);
			vendorJobMappingRepository.save(vendorJobMapping);

			// NOTE: send mail to all assigned vendors about vendor removal.

			// get all assigned vendor managers for the organization...
			List<VmsRoles> vendorUsers = getAssignedVendormanagers(vendorId, organizationId);

			for (VmsRoles vmsRole : vendorUsers) {
				toAddressList = new ArrayList<>();
				BigInteger vendorManagerId = vmsRole.getUserId();
				UserDTO userDTO = jobRequisitionService.getRecruitersInfo(vendorManagerId);
				Recruiters recruiterJobMapping = recruitersRepository.getByJobOrderIdAndRecruiterId(jobOrderId, vendorManagerId);
				recruiterJobMapping.setStatus(Constants.JOB_ARCHIVED_STATUS);
				recruitersRepository.save(recruiterJobMapping);
				toAddressList.add(userDTO.getEmailAddress());
				vendorNotificationMail(userId, userDTO, jobOrder, mailSubject, Constants.ACYUTA_JOB_UNASSIGN_NOTIFICATION, addCandidateEmail);
			}
		}
	}

	public String updateJobStatus(String body) {

		String mailTempalte = "";
		String mailSubject = "";

		try {
			JSONObject input = new JSONObject(body);
			logger.info("updateStatus input JSON: {}", input);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");

			String status = input.getString(JobOrderConstants.STATUS_STRING);

			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			jobOrderRepository.setJobStatus(jobOrderId, status.toUpperCase(), date, updatedBy);
			jobOrderRepository.flush();

			if (status.equalsIgnoreCase(JobOrderConstants.JOB_ACTIVE_STATUS)) {
				mailSubject = "Requisition Active Notification for Requisition " + jobOrder.getJobId() + " (" + jobOrder.getJobTitle() + ")";
				mailTempalte = Constants.VMS_JOB_ACTIVE;
			}
			if (status.equalsIgnoreCase(JobOrderConstants.JOB_ARCHIVED_STATUS)) {
				mailSubject = "Requisition Closed Notification for Requisition " + jobOrder.getJobId() + " (" + jobOrder.getJobTitle() + ")";
				mailTempalte = Constants.VMS_JOB_CLOSED;
			}
			if (status.equalsIgnoreCase(JobOrderConstants.JOB_HALTED_STATUS) || status.equalsIgnoreCase(JobOrderConstants.JOB_ON_HOLD_STATUS)) {
				mailSubject = "Requisition On Hold  Notification for Requisition " + jobOrder.getJobId() + " (" + jobOrder.getJobTitle() + ")";
				mailTempalte = Constants.VMS_JOB_HALTED;
			}

			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, mailTempalte, mailSubject);

			logger.info("Updated status to >{}< with jobOrderId{}", status, jobOrderId);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating jobStatus", e);
			return "ERROR";

		}
	}

	public String updateJobDescription(String body) {
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String jobDescription = input.getString(JobOrderConstants.JOB_DESCRIPTION_STRING);
			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");

			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			jobOrder.setJobDescription(jobDescription);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(updatedBy);
			jobOrderRepository.save(jobOrder);
			jobOrderRepository.flush();
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating jobDescription", e);
			return "ERROR";

		}
	}

	public String updateSkills(String body) {
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			JSONArray skillsArray = input.getJSONArray(JobOrderConstants.SKILLS_STRING);
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");
			logger.info("Update Skills request called with jobOrderId =  >{}<, userId = >{}<, skills = >{}<", jobOrderId, updatedBy, skillsArray);
			String skills = "";
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);

			if (skillsArray.length() == 0) {
				// jobOrderRepository.setMoreSkills(jobOrderId, skills,date,updatedBy);
				jobOrder.setSkills(skills);
				jobOrder.setUpdatedDate(date);
				jobOrder.setUpdatedBy(updatedBy);
				jobOrderRepository.save(jobOrder);
				jobOrderRepository.flush();
				logger.info("Updated skills for jobOrderId--{} with skills--{}", jobOrderId, skills);
			} else {
				if (skillsArray.length() > 1) {
					skills = skillsArray.getString(0);
					for (int i = 1, size = skillsArray.length(); i < size; i++) {
						skills = skills + "," + skillsArray.getString(i);
					}
				} else
					skills = skillsArray.getString(0);

				jobOrder.setSkills(skills);
				jobOrder.setUpdatedDate(date);
				jobOrder.setUpdatedBy(updatedBy);
				jobOrderRepository.save(jobOrder);
				jobOrderRepository.flush();

				logger.info("updated Skills of jobOrderId--{} with skills--{}", jobOrderId, skills);
				String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
				sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

			}
			return "UPDATED";
		} catch (Exception e) {
			logger.info("error occured updateding skills", e);
			return "ERROR";
		}
	}

	public String updatePayRate(String body) {
		try {
			JSONObject input = new JSONObject(body);
			logger.info("updatePayRate input JSON: {}", input);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String payRate = input.getString(JobOrderConstants.PAY_RATE_STRING);
			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);

			jobOrder.setPayRate(payRate);
			jobOrder.setUpdatedDate(date);
			jobOrder.setUpdatedBy(updatedBy);
			jobOrderRepository.save(jobOrder);
			jobOrderRepository.flush();
			logger.info("Updated payrate to {} with jobOrderId: {}", payRate, jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating PayRate", e);
			return "ERROR";

		}
	}

	public String updateJobType(String body) {
		try {
			JSONObject input = new JSONObject(body);
			logger.info("updateJobType input JSON: {}", input);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String jobType = input.getString(JobOrderConstants.JOB_TYPE_STRING);
			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			jobOrder.setJobType(jobType);
			jobOrder.setUpdatedDate(date);
			jobOrder.setUpdatedBy(updatedBy);
			jobOrderRepository.save(jobOrder);
			jobOrderRepository.flush();
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating jobType", e);
			return "ERROR";

		}
	}

	public String updateJobDuration(String body) {
		try {
			JSONObject input = new JSONObject(body);
			logger.info("updateJobDuration input JSON: {}", input);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String projectStartDate = input.getString(JobOrderConstants.PROJECT_START_DATE);
			String projectEndDate = input.getString(JobOrderConstants.PROJECT_END_DATE);

			BigInteger updatedBy = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			jobOrder.setProjectStartDate(projectStartDate);
			jobOrder.setProjectEndDate(projectEndDate);
			jobOrder.setUpdatedDate(date);
			jobOrder.setUpdatedBy(updatedBy);
			jobOrderRepository.save(jobOrder);
			jobOrderRepository.flush();

			logger.info("Updated from project duration : {} to {} with jobOrderId: {}", projectStartDate, projectEndDate, jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);

			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating jobStatus", e);
			return "ERROR";

		}
	}

	public String updateLocations(String body) {
		try {
			JSONObject input = new JSONObject(body);
			logger.info("updateLocations input JSON: {}", input);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			JSONArray deleted = input.getJSONArray("deletedLocations");
			String userId = input.getString(JobOrderConstants.USER_ID_STRING);
			Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
			Boolean locationExist = false;
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			org.json.JSONArray vendorInfo = input.getJSONArray("vendorInfo");

			if (deleted.length() != 0) {
				for (int i = 0; i < deleted.length(); i++) {
					String location = deleted.getString(i);
					List<JobLocation> jobLocationList = jobOrder.getJobLocations();

					for (JobLocation jobLocation : jobLocationList) {
						locationExist = jobLocation.getLocation().equals(location);
						if (locationExist == true) {
							BigInteger mappingId = jobLocation.getMappingId();
							jobLocationRepository.setStatus(mappingId, Constants.LOCATION_MAPPING_INACTIVE_STATUS, date);
							logger.debug("Deleted Location{} with jobOrderId {}", location, jobOrderId);

							break;
						}

					}
				}
			}

			JSONArray added = input.getJSONArray("addedLocations");
			if (added.length() != 0) {
				locationExist = false;
				for (int i = 0; i < added.length(); i++) {
					String location = added.getString(i);
					List<JobLocation> jobLocationList = jobOrder.getJobLocations();
					for (JobLocation jobLocation : jobLocationList) {
						locationExist = jobLocation.getLocation().equals(location);
						if (locationExist == true) {
							BigInteger mappingId = jobLocation.getMappingId();
							jobLocationRepository.setStatus(mappingId, Constants.LOCATION_MAPPING_ACTIVE_STATUS, date);
							logger.debug("Added location {} with jobOrderId {}", location, jobOrderId);
							break;
						}
					}
					if (locationExist == false) {
						logger.info("Added location {} with jobOrderId {}", location, jobOrderId);
						JobOrder jobOrderRep = jobOrderRepository.findByJobOrderId(jobOrderId);

						JobLocation jobLocation = new JobLocation();
						jobLocation.setJobOrder(jobOrderRep);
						jobLocation.setLocation(location);
						jobLocation.setCreatedDate(date);
						jobLocation.setUpdatedDate(date);
						jobLocation.setStatus(Constants.LOCATION_MAPPING_ACTIVE_STATUS);
						jobLocationRepository.save(jobLocation);
						jobOrder.getJobLocations().add(jobLocation);
						jobOrderRepository.flush();
					}
				}
			}
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, new BigInteger(userId), jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating recruiters", e);
			return "ERROR";
		}
	}

	public String updateJobTitle(String body) {

		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString("jobOrderId"));
			String jobTitle = input.getString(JobOrderConstants.JOB_TITLE_STRING);

			JobOrder job = jobOrderRepository.findByJobOrderId(jobOrderId);
			job.setJobTitle(jobTitle);
			jobOrderRepository.save(job);
			jobOrderRepository.flush();
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateJobTitle: ", e);
			return "ERROR";
		}
	}

	// NOTE: reduce the code complexity.
	public void vendorNotificationMail(BigInteger recruiterId, UserDTO candidate,  JobOrder jobOrder, String mailSubject, String mailTemplate,
			String email) {

		logger.info("Inside vendorNotificationMail.");
		String logInUserEmailAddress = "";
		String logInUserFullName = "";
		List<String> toAddressList = new ArrayList<>();
		UserDTO recruiter = new UserDTO();
		String locationString = "";

		try {
			toAddressList.add(candidate.getEmailAddress());
			recruiter = jobRequisitionService.getRecruitersInfo(recruiterId);
			logInUserEmailAddress = recruiter.getEmailAddress();
			logInUserFullName = StringUtils.isBlank(recruiter.getFirstName()) ? ""
					: recruiter.getFirstName() + (StringUtils.isBlank(recruiter.getLastName()) ? "" : " " + recruiter.getLastName());

			AuthenticationConfiguration auth = new AuthenticationConfiguration();
			
			 auth.setFromUser(logInUserEmailAddress);
			// NOTE: If we doesn't set setFromUser all mails sent from noreply@panna.ai		
				auth.setFromUser("noreply@panna.ai");
				if(logInUserEmailAddress.indexOf("@mroads.com") !=-1 || logInUserEmailAddress.indexOf("@panna.ai") !=-1 ) {
					auth.setFromUser(logInUserEmailAddress);
				}
			 logger.info("For Other than Hilton organization we are sending from noreply@panna.ai. FromUser: {}",auth.getFromUser());
			 
			EmailParticipant emailParticipant = new EmailParticipant();
			emailParticipant.setTo(toAddressList);
			List<String> ccList = new ArrayList<>();
			// NOTE: If we want CC copy to mail sender...
			// ccList.add(logInUserEmailAddress);
			emailParticipant.setCc(ccList);

			emailParticipant.setOrganizationId(jobOrder.getOrganizationId().toString());
			emailParticipant.setOrgTeamName(logInUserFullName);
			emailParticipant.setReplyTo(logInUserEmailAddress);
			emailParticipant.setEnvironment(environment.getProperty(Constants.ENVIRONMENT));

			MessageData messageData = new MessageData();

			if (mailTemplate == null) {
				messageData.setTemplate("NO_TEMPLATE");
				messageData.setDynamicTemplateText(" ");
			} else {
				messageData.setTemplate(mailTemplate);
			}

			messageData.setHeaderTemplate("NO_TEMPLATE");
			messageData.setFooterTemplate("NO_TEMPLATE");
			messageData.setSubject(mailSubject);
			
			//FIXME: We are saving other vendor name as first and last name to avoid filter issue  in job list vendor page.
			logger.info("candidate : {}",candidate);
			String userName = StringUtils.capitalize(candidate.getFirstName());
			if(!candidate.getFirstName().equalsIgnoreCase(candidate.getLastName())) {
				  userName = StringUtils.capitalize(candidate.getFirstName()) + " " + StringUtils.capitalize(candidate.getLastName());
			}


			// JobOrder job = jobOrderRepository.findByJobOrderId(jobOrder);
			logger.debug("jobList obtained---{}", jobOrder);
			messageData.setDynamicValue("[$JOBID$]", validateFiedld(jobOrder.getJobId()));

			String envURL = environment.getProperty("view.profile.url");
			if (null != email && !email.isEmpty()) {
				String encryptedMail = EncryptDecryptUtil.getEncryptKey(email);
				String viewProfileURL = envURL + "/candidate-profile/#/::" + jobOrder.getJobOrderId() + "?"+jobOrder.getOrganizationId()+"/:" + encryptedMail;
				messageData.setDynamicValue("[$VIEW_PROFILE$]", validateFiedld(viewProfileURL));
				AcyutaCandidate acyutaCandidate = addCandidateRepository.findByEmailAddress(email).get(0);
				String name = acyutaCandidate.getFirstName() + " " + acyutaCandidate.getLastName();
				messageData.setDynamicValue("[$VMS_CANDIDATE$]", validateFiedld(name));
				messageData.setDynamicValue("[$STATUS$]", validateFiedld(acyutaCandidate.getCandidateStatus()));
				messageData.setDynamicValue("[$RESOURCE_AVAILABILITY$]", validateFiedld(acyutaCandidate.getManagerAvailability()));
				messageData.setDynamicValue("[$MANAGER_AVAILABILITY_NEW$]", validateFiedld(acyutaCandidate.getManagerNewSAvailabilitylots()));
				
				logger.info("ManagerAvailability : {}", acyutaCandidate.getManagerAvailability());

				messageData.setDynamicValue("[$MANAGER_AVAILABILITY$]", validateFiedld(acyutaCandidate.getManagerAvailability()));
				messageData.setDynamicValue("[$WITHDRAWN_BY$]", validateFiedld(logInUserFullName));
				String profileEncoded = EncryptDecryptUtil.getEncryptKey(acyutaCandidate.getCandidateId().toString());
				String fundingApproveProfile = envURL + "/#/requisitions?jobOrderId=" + jobOrder.getJobOrderId()+"&profile:"+profileEncoded;

				messageData.setDynamicValue("[$FUNDING_APPROVE_PROFILE$]", validateFiedld(fundingApproveProfile));
			}
			messageData.setDynamicValue("[$CANDIDATE_NAME$]", validateFiedld(userName));
			messageData.setDynamicValue("[$JOBTITLE$]", validateFiedld(jobOrder.getJobTitle()));
			messageData.setDynamicValue("[$JOBID$]", validateFiedld(jobOrder.getJobId()));
			messageData.setDynamicValue("[$CLIENT_NAME$]", validateFiedld(jobOrder.getClientName()));
			messageData.setDynamicValue("[$JOB_PAY_RATE$]", validateFiedld(jobOrder.getPayRate()));
			messageData.setDynamicValue("[$JOBTYPE$]", validateFiedld(jobOrder.getJobType()));
			messageData.setDynamicValue("[$PROJECT_TARGET_DATE$]", validateFiedld(jobOrder.getProjectTargetDate()));
			
			String duration = "";
			if (null != jobOrder.getProjectStartDate() && !jobOrder.getProjectStartDate().isEmpty()) {
				duration = jobOrder.getProjectStartDate() + "-" + jobOrder.getProjectEndDate();
			}
			messageData.setDynamicValue("[$JOBDURATION$]", validateFiedld(duration));
			messageData.setDynamicValue("[$RECRUITER_TITLE$]", "");
			String phoneNumber = recruiter.getPhoneNumber() != null ? recruiter.getPhoneNumber().trim() : " ";
			messageData.setDynamicValue("[$RECRUITER_PHONE$]", validateFiedld(phoneNumber));
			messageData.setDynamicValue("[$RECRUITER_EMAIL$]", validateFiedld(logInUserEmailAddress));
			String viewJobURL = envURL + "/#/requisitions?jobOrderId=" + jobOrder.getJobOrderId();
			messageData.setDynamicValue("[$JOBDESCRIPTION_LINK$]", validateFiedld(viewJobURL));
			List<JobLocation> location = jobOrder.getJobLocations();
			for (JobLocation jobLocation : location) {
				if(jobLocation.getStatus().equals(Constants.LOCATION_MAPPING_ACTIVE_STATUS)) {
					locationString += jobLocation.getLocation() + " ";	
				}
			}
			messageData.setDynamicValue("[$JOBLOCATION$]", validateFiedld(locationString));
			messageData.setDynamicValue("[$RECRUITER_FIRST_NAME$]", validateFiedld(recruiter.getFirstName()));
			messageData.setDynamicValue("[$RECRUITER_LAST_NAME$]", validateFiedld(recruiter.getLastName()));

			logger.debug("messageData {}", messageData);
			EmailService.sendEmail(auth, emailParticipant, messageData);
		} catch (Exception e) {
			logger.error("Exception in vendorNotificationMail: ", e);
		}
	}

	private String validateFiedld(String field) {
		String resp = " ";
		if (null != field && !field.isEmpty()) {
			resp = field;
		}
		return resp;
	}

	public void sendJobModificationVendorMail(org.json.JSONArray vendorInfo, BigInteger userId, JobOrder jobOrder, String mailTempalte, String mailSubject) {

		String addCandidateEmail = "";
		logger.info("Inside sendJobModificationVendorMail vendorInfo size: ", vendorInfo.length());
		try {
			for (int i = 0; i < vendorInfo.length(); i++) {
				BigInteger vendorId = new BigInteger("" + vendorInfo.get(i));

				// get all assigned vendor managers for the organization...
				List<VmsRoles> vendorUsers = getAssignedVendormanagers(vendorId, jobOrder.getOrganizationId());
				for (VmsRoles vmsRole : vendorUsers) {
					BigInteger vendorManagerId = vmsRole.getUserId();
					UserDTO userDTO = jobRequisitionService.getRecruitersInfo(vendorManagerId);
					if (null != userDTO) {
						// String mailSubject = "Alert: Job details have been modified for req " +
						// jobOrder.getJobId();
						vendorNotificationMail(userId, userDTO,jobOrder, mailSubject, mailTempalte,addCandidateEmail);
						logger.info("Job updattion mail sent to vendor: {}.", vendorId);
					} else {
						logger.warn("No assgined vendors found for vendorId: {}", vendorId);
					}
				}
				// send mail to each vendor wise..
			}
		} catch (Exception e) {
			logger.error("error occured during getAssifnedVendorEmails: ", e);
		}
	}

	public List<AcyutaCandidateDTO> findByEmailAddressOrPhoneNumberAndVendorId(String phoneNumber, String emailAddress, BigInteger vendorId) throws JSONException {

		// FIXME : Need to check during the edit do we required the candidateId
		// condition.
		BigInteger candidateId = BigInteger.ZERO;
		List<AcyutaCandidateDTO> candidateDtoList = new ArrayList<>();
		phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
		List<AcyutaCandidate> duplicateResumes = addCandidateRepository.findByEmailAddressOrPhoneNumberCandidateIdAndVendorId(emailAddress, phoneNumber, candidateId);
		if (CollectionUtils.isNotEmpty(duplicateResumes)) {
			for (AcyutaCandidate obj : duplicateResumes) {
				candidateDtoList.add((AcyutaCandidateDTO) ObjectConverter.convert(obj, new AcyutaCandidateDTO()));
			}
		}
		return candidateDtoList;
	}

	public String updateDepartmentCode(String body) {

		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String departmentCode = input.getString(JobOrderConstants.DEPARTMENT_CODE);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setDepartmentCode(departmentCode);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateDepartmentCode: ", e);
			return "ERROR";
		}
	}

	public String updateBuOu(String body) {
		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String buOu = input.getString(JobOrderConstants.BU_OU);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setBuOu(buOu);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateBuOu: ", e);
			return "ERROR";
		}
	}

	public String updateAccountNumber(String body) {
		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String accountNumber = input.getString(JobOrderConstants.ACCOUNT_NUMBER);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setAccountNumber(accountNumber);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateAccountNumber: ", e);
			return "ERROR";
		}
	}

	public String updateInvoicingContact(String body) {
		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String invoicingContact = input.getString(JobOrderConstants.INVOICING_CONTACT);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setInvoicingContact(invoicingContact);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateInvoicingContact: ", e);
			return "ERROR";
		}
	}

	public String updateHiringManger(String body) {
		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));

			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String hiringManger = input.getString(JobOrderConstants.HIRING_MANAGER_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setHiringManager(hiringManger);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateHiringManger: ", e);
			return "ERROR";
		}
	}

	public String updateVicePresident(String body) {
		try {
			JSONObject input = new JSONObject(body);
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			String vicePresident = input.getString(JobOrderConstants.VICE_PRESIDENT_STRING);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			jobOrder.setVicePresident(vicePresident);
			jobOrder.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			jobOrder.setUpdatedBy(userId);
			jobOrderRepository.save(jobOrder);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("Exception in updateVicePresident: ", e);
			return "ERROR";
		}
	}

	/**
	 * 
	 * @param body
	 * @return
	 */
	public String updateInterviewPosition(String body) {
		logger.info("Inside the updateInterviewPosition during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));
			BigInteger interviewPosition = new BigInteger(input.getString(JobOrderConstants.INTERVIEW_POSITION_ID_STRING));
			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));

			jobOrderRepository.setinterviewPositionId(jobOrderId, interviewPosition);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);

			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	// code

	/**
	 * updatePotentialConversion in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updatePotentialConversion(String body) {
		logger.info("Inside the updatePotentialConversion during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			
			String	pcEndDate = input.getString(JobOrderConstants.PC_ENDDATE);
			String 	pcStartDate = input.getString(JobOrderConstants.PC_STARTDATE);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			
			Boolean checkTBD =input.has("checkTBD")? input.getBoolean("checkTBD"):jobOrder.getCheckTBD();
			

			jobOrder.setPcStartDate(pcStartDate);
			jobOrder.setPcEndDate(pcEndDate);
			jobOrder.setCheckTBD(checkTBD);
			jobOrderRepository.save(jobOrder);
			
			// jobOrderRepository.setPotentialConversion(jobOrderId, pcStartDate, pcEndDate);
			// JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateResourceType in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateResourceType(String body) {
		logger.info("Inside the updateResourceType during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String resourceType = input.getString(JobOrderConstants.RESOURCE_TYPE_STRING);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setResourceType(jobOrderId, resourceType);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateEquipment in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateEquipment(String body) {
		logger.info("Inside the updateEquipment during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String equipment = input.getString(JobOrderConstants.EQUIPMENT);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setEquipment(jobOrderId, equipment);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateDepartmentName in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateDepartmentName(String body) {
		logger.info("Inside the updateDepartmentName during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String departmentName = input.getString(JobOrderConstants.DEPARTMENT_NAME);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setDepartmentName(jobOrderId, departmentName);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateMaxBillRate in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateMaxBillRate(String body) {
		logger.info("Inside the updateMaxBillRate during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String maxBillRate = input.getString(JobOrderConstants.MAX_BILL_RATE_STRING);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setMaxBillRate(jobOrderId, maxBillRate);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateBillRate in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateBillRate(String body) {
		logger.info("Inside the updateBillRate during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String billRate = input.getString(JobOrderConstants.BILL_RATE_STRING);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setBillRate(jobOrderId, billRate);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updateJobLevel in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateJobLevel(String body) {
		logger.info("Inside the updateJobLevel during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String jobLevel = input.getString(JobOrderConstants.JOB_LEVEL);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setJobLevel(jobOrderId, jobLevel);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}

	/**
	 * updating ProjectTargetDate in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	public String updateProjectTargetDate(String body) {
		logger.info("Inside the updateProjectTargetDate during the publish job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String projectTargetDate = input.getString(JobOrderConstants.PROJECT_TARGET_DATE);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setProjectTargetDate(jobOrderId, projectTargetDate);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Job details have been modified for req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating interview position status. ", e);
			return "ERROR";
		}
	}
	
	/**
	 * updating PotentialSalaryRange in jobOrder table
	 * 
	 * @param body
	 * @return
	 */
	
	public String updatePotentialSalaryRange(String body) {
		logger.info("Updating Salary Range for the Job");
		try {
			JSONObject input = new JSONObject(body);
			BigInteger jobOrderId = new BigInteger(input.getString(JobOrderConstants.JOB_ORDER_ID_STRING));

			String potentialSalaryRange = input.getString(JobOrderConstants.POTENTIAL_SALARY_RANGE);

			org.json.JSONArray vendorInfo = input.getJSONArray(JobOrderConstants.VENDOR_INFO_STRING);
			BigInteger userId = new BigInteger(input.getString(JobOrderConstants.USER_ID_STRING));
			jobOrderRepository.setPotentialSalaryRange(jobOrderId, potentialSalaryRange);
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String mailSubject = "Alert: Salary Details has changed for the req " + jobOrder.getJobId();
			sendJobModificationVendorMail(vendorInfo, userId, jobOrder, Constants.VMS_JOB_UPDATED, mailSubject);
			return "UPDATED";
		} catch (Exception e) {
			logger.error("error occured during updating salary details ", e);
			return "ERROR";
		}
	}

	/**
	 * adding new vendor in vendors table and add vendor as new user in User_ table
	 * using service url after the creation of vendor add vendor role info in
	 * vmsRoles table
	 * 
	 * @param inputJson
	 * @param userId
	 * @return userId
	 * @throws JSONException
	 * @throws ParseException
	 */
	public BigInteger addNewVendor(JSONObject inputJson, BigInteger userId) throws JSONException, Exception {
		
		Log.info("Inside addNewVendor : ");
		
		Vendor vendor = new Vendor();
		BigInteger roleId = new BigInteger(environment.getProperty("vendor.role.id"));
		BigInteger organizationId = new BigInteger(inputJson.getString(JobOrderConstants.ORGANIZATION_ID_STRING));
		vendor.setName(inputJson.getString("vendorName"));
		vendor.setCreatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
		vendor.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
		vendor.setCreatedBy(userId);
		vendor.setUpdatedBy(userId);
		vendor.setStatus(Constants.JOB_ACTIVE_STATUS);
		vendor.setTier(VendorConstatns.TIER2_VENDOR);
		vendor.setOrganizationId(organizationId);
		
		vendor = vendorRepository.save(vendor);
		
		BigInteger vendorId = vendor.getVendorId();
		String emailAddress = inputJson.getString("vendorEmail");

		// create user table using vendor mail id user id...
		JSONArray vendorRolesArray = new JSONArray();
		JSONObject vendorUser = new JSONObject();
		
		
		// FIXME: During the view filter we are using the user full name as column value.
		String fullName =inputJson.getString("vendorPOC").trim(); 
		int index = fullName.lastIndexOf(" ");
		String firstName = fullName;
		String lastName = fullName;
		if(index!=-1) {
		  firstName = fullName.substring(0, index);
		  lastName = fullName.substring(index+1); 
		  lastName = lastName.isEmpty() || lastName == null ?firstName:lastName;
		}
		vendorUser.put("firstName", firstName);
		vendorUser.put("screenName", firstName);
		vendorUser.put("lastName", lastName);
		
		vendorUser.put("jobTitle", "");
		vendorUser.put("phoneNumber", "");
		vendorUser.put("emailAddress", emailAddress);
		//vendorUser.put(JobOrderConstants.USER_ID_STRING, null);
		vendorUser.put("organizationId", organizationId);

		JSONObject vendorRole = new JSONObject();

		vendorRole.put("roleId", roleId);
		vendorRole.put("roleName", "VendorManager");
		vendorRole.put("displayName", "Vendor Manager");
		vendorRole.put("description", " ");
		vendorRole.put("selected", true);
		vendorRole.put("disabled", true);

		vendorRolesArray.put(vendorRole);
		vendorUser.put("userRoles", vendorRolesArray);

		String responseBody = "";
		JSONObject vendorUserResponseJSON = new JSONObject();
		String url = environment.getProperty("panna.update.user");
		logger.info("url : {}",url);
		logger.debug("vendorUser : {}",vendorUser);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(vendorUser.toString()), String.class);
		logger.info("Create Vendor responseEntity: {}", responseEntity.getBody());
		responseBody = responseEntity.getBody();
		vendorUserResponseJSON = new JSONObject(responseBody);
		// FIXME: vendorUserResponseJSON showing success/failure message we modify it to UserDto or userId
		User vendorUserObj = userRepository.getUserByEmailAddress(emailAddress);
		BigInteger vendorUser_userId = vendorUserObj.getUserId();
		logger.info("userId based on [emailAddress:{}] is >>{}", emailAddress, vendorUser_userId);
		VmsRoles vmsRoles = new VmsRoles();
		vmsRoles.setUserId(vendorUser_userId);
		vmsRoles.setRoleId(roleId);
		vmsRoles.setVendorId(vendor.getVendorId());
		vmsRoles.setOrganizationId(organizationId);
		vmsRolesRepository.save(vmsRoles);
		logger.info("added row [{}] in panna_VmsRoles table.",vmsRoles);
		return vendorId;
	}

	public String updateCandidateStatus(String body) throws Exception {

		org.codehaus.jettison.json.JSONObject inputJSON = new org.codehaus.jettison.json.JSONObject(body);
		String appEnvironement = " ";
		
		Boolean isHrManager = inputJSON.getBoolean("isHrManager");
		Boolean isHiringManager = inputJSON.getBoolean("isHiringManager");

		// 1. Update the candidate status in the candidate tracking table..
		commonServices.saveCandidateStatus(inputJSON);

		// 2. Process the dynamic values in common service method..
		Map<String, String> dynamicValues = commonServices.processDynamicValues(inputJSON);

		// 3. Get the mail template from the constants...
		String candidateStatus = inputJSON.getString("candidateStatus");
		String mailTemplate = VendorConstatns.getMailTemplatesMap().get(candidateStatus);
		inputJSON.put("mailTemplate", mailTemplate);
		inputJSON.put("dynamicValues", dynamicValues);

		// 4. Call Emal service to send the mail..
		String mailRecipient = VendorConstatns.getMailRecipientMap().get(mailTemplate);
		List<String> mailRecipientList = Arrays.asList(mailRecipient.split("\\s*,\\s*"));
		
		String mailRecipientEmail ="";
		if (isHiringManager) {
			BigInteger hrManagerId = new BigInteger(inputJSON.getString("hrManagerId"));			
			UserDTO hrDTO =jobRequisitionService.getRecruitersInfo(hrManagerId);
			mailRecipientEmail = hrDTO.getEmailAddress();
		}
		if (isHrManager) {
			BigInteger hiringManagerId = new BigInteger(inputJSON.getString("hiringManagerId"));
			UserDTO hiringManagerDTO = jobRequisitionService.getRecruitersInfo(hiringManagerId);
			mailRecipientEmail = hiringManagerDTO.getEmailAddress();
		}
		
		org.codehaus.jettison.json.JSONArray emailTagsList = new org.codehaus.jettison.json.JSONArray();
		emailTagsList.put(mailRecipientEmail);
		commonServices.sendMail(inputJSON, appEnvironement);
		
		for (String mailRecipientData : mailRecipientList) {
			org.codehaus.jettison.json.JSONArray extraEmailTagsList = new org.codehaus.jettison.json.JSONArray();

			BigInteger hrManagerId = new BigInteger(mailRecipientData);			
			UserDTO userDTO =jobRequisitionService.getRecruitersInfo(hrManagerId);
			
			extraEmailTagsList.put(userDTO.getEmailAddress());
			commonServices.sendMail(inputJSON, appEnvironement);
		}
		
		return null;
	}

	void sendNotificationMails(org.codehaus.jettison.json.JSONObject inputJSON) {

		String appEnvironement = " ";
		commonServices.sendMail(inputJSON, appEnvironement);
	}

	
	
}
