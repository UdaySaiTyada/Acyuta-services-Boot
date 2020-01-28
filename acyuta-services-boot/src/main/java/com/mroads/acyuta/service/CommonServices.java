package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaConstants;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.JobCommentsDTO;
import com.mroads.acyuta.dto.JobOrderDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaMissingLocations;
import com.mroads.acyuta.model.AcyutaProperties;
import com.mroads.acyuta.model.AcyutaTemplate;
import com.mroads.acyuta.model.CandidateStatusTracking;
import com.mroads.acyuta.model.JobLocation;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaMissingLocationsRepository;
import com.mroads.acyuta.repository.AcyutaPropertiesRepository;
import com.mroads.acyuta.repository.CandidateStatusTrackingRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.UserRepository;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;

/**
 * @author Mahendar K
 * 
 */


@Service
public class CommonServices {

	private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	public UserRepository userRepository;

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;
	
	@Autowired
	AcyutaPropertiesRepository acyutaPropertiesRepository;

	@Autowired
	CandidateStatusTrackingRepository candidateStatusTrackingRepository;

	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	public AcyutaMissingLocationsRepository missingLocationsRepository;

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
	private Environment environment;

	public Map<String, String> processDynamicValues(JSONObject inputJSON) {

		Map<String, String> dynamicValues = new HashMap<>();

		// process job information..
		if (inputJSON.has("jobOrderId")) {
			
			BigInteger jobOrderId = new BigInteger("jobOrderId");
			JobOrder jobOrder = jobOrderRepository.findByJobOrderId(jobOrderId);
			String jobLocation = "";
			List<JobLocation> locations = jobOrder.getJobLocations();

			if (locations.size() > 0) {
				for (JobLocation location : locations) {
					if ("ACTIVE".equals(location.getStatus())) {
						jobLocation = jobLocation + location.getLocation() + " / ";
					}
				}
				jobLocation = jobLocation.substring(0, (jobLocation.length() - 2));
			}

			dynamicValues.put("[$JOBDESCRIPTION$]", jobOrder.getJobDescription());
			dynamicValues.put("[$JOBLOCATION$]", "[" + jobLocation + "]");
			dynamicValues.put("[$JOBID$]", jobOrder.getJobId());
			dynamicValues.put("[$JOBTITLE$]", jobOrder.getJobTitle());
			dynamicValues.put("[$JOBTYPE$]", jobOrder.getJobType());
			dynamicValues.put("[$JOBDURATION$]", jobOrder.getJobDuration());
			dynamicValues.put("[$CLIENT_NAME$]", jobOrder.getClientName());
			dynamicValues.put("[$JOB_PAY_RATE$]", jobOrder.getPayRate());

		}

		// process acyuta candidate information..
		if (inputJSON.has("candidateId")) {
			
			BigInteger candidateId = new BigInteger("candidateId");
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
			
			dynamicValues.put("[$SKYPE_ID$]", StringUtils.isNotBlank(candidate.getSkypeId()) ? candidate.getSkypeId() : " ");
			dynamicValues.put("[$CONSULTANCY_NAME$]", checkIsValueEmpty(candidate.getConsultancyName(), ""));
			dynamicValues.put("[$CANDIDATE_NAME$]", checkIsValueEmpty(candidate.getFirstName(), "") + " " + checkIsValueEmpty(candidate.getLastName(), ""));
			dynamicValues.put("[$CONSULTANCY_CONTANCT_NAME$]", checkIsValueEmpty(candidate.getConsultancyContactPerson(), ""));
			dynamicValues.put("[$PAY_RATE$]", (StringUtils.isNotBlank(candidate.getPayRate()) ? candidate.getPayRate() : "")
					+ (StringUtils.isNotBlank(candidate.getPayType()) ? " on " + candidate.getPayType() : ""));
			dynamicValues.put("[$AT_PAY_RATE$]", StringUtils.isNotBlank(candidate.getPayRate()) ? ("at " + candidate.getPayRate())
					: "" + (StringUtils.isNotBlank(candidate.getPayType()) ? " on " + candidate.getPayType() : ""));
			String resumeUrl = "https://docs.google.com/gview?url=" + candidate.getFileURL() + "&embedded=true";
			dynamicValues.put("[$CANDIDATE_PHONE$]", candidate.getPhoneNumber());
			dynamicValues.put("[$CONSULTANCY_PHONE$]", candidate.getConsultancyAddress());
			dynamicValues.put("[$CANDIDATE_EMAIL$]", checkIsEmailEmpty(candidate.getEmailAddress(), ""));
			dynamicValues.put("[$AT$]", StringUtils.isBlank(candidate.getPhoneNumber()) ? "" : "at ");

			dynamicValues.put("[$RESUME_URL_FORM$]", resumeUrl);
			dynamicValues.put("[$FIRSTNAME$]", checkIsValueEmpty(candidate.getFirstName(), ""));
			dynamicValues.put("[$CANDIDATE_LAST_NAME$]", checkIsValueEmpty(candidate.getLastName(), ""));
			dynamicValues.put("[$RE_LOCATION$]", checkIsValueEmpty(candidate.getReLocation(), ""));
			dynamicValues.put("[$LOCATION$]", checkIsValueEmpty(candidate.getCandidateLocation(), ""));
			dynamicValues.put("[$LINKEDIN$]", checkIsValueEmpty(candidate.getLinkedIn(), ""));
			dynamicValues.put("[$PANNA_INTERVIEW$]", checkIsValueEmpty(candidate.getPannaReportURL(), ""));
			dynamicValues.put("[$PANNA_INTERVIEW$]", checkIsValueEmpty(candidate.getPannaReportURL(), ""));
			dynamicValues.put("[$WORK_AUTHORIZATION$]", checkIsValueEmpty(candidate.getVisaStatus(), ""));
		}
		
		// process recruiter information..
		if (inputJSON.has("recruiterId")) {

			// userRepository.findByUserId
		}

		// process interview information..
 
		
		return dynamicValues;
	}
	
	
	public void saveCandidateStatus(JSONObject inputJSON) {

		logger.info("Inside saveCandidateStatus :");
	
		try {
			BigInteger jobOrderId = new BigInteger(inputJSON.getString("jobOrderId"));
			BigInteger candidateId = new BigInteger(inputJSON.getString("candidateId"));
			String candidateStatus = inputJSON.getString("candidateStatus");
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
			candidate.setCandidateStatus(candidateStatus);
			addCandidateRepository.save(candidate);
			
			AcyutaProperties acyutaProperty = acyutaPropertiesRepository.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(), candidateStatus,
					candidate.getOrganizationId());

			if (null == acyutaProperty) {
				logger.info("acyutaProperty does not exist with status {}, for organizationId {} :  ", candidateStatus, candidate.getOrganizationId());
				acyutaProperty = acyutaPropertiesRepository.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(), candidateStatus, BigInteger.ZERO);
				logger.info("acyutaProperty with default oranization loaded.");
			}

			CandidateStatusTracking statusModel = new CandidateStatusTracking();
			statusModel.setCandidateId(candidate.getCandidateId());
			statusModel.setOrganizationId(candidate.getOrganizationId());
			statusModel.setJobOrderId(jobOrderId);
			statusModel.setUpdatedBy(candidate.getCreatedBy());
			// FIXME: Store date formats in UTC.
			// statusModel.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			statusModel.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));

			if (acyutaProperty != null) {
				statusModel.setPropertyId(acyutaProperty.getPropertyId());
			}

			logger.debug("statusModel : {}", statusModel);
			candidateStatusTrackingRepository.save(statusModel);

		} catch (Exception e) {
			logger.error("Exception in saveCandidateStatus : ", e);
		}
	}
	
	
	public void sendMail(JSONObject inputJSON, String appEnvironement){

		 try {
			List<String> toAddressList=new ArrayList<String>();
			String emailAddress = inputJSON.getString("emailAddress");
			String candidateId = inputJSON.getString("candidateId");
			String mailTemplate = inputJSON.getString("mailTemplate");
			
			Map<String, String> dynamicValues =  (Map<String, String>) inputJSON.get("dynamicValues");
		
			String logInUserFullName=inputJSON.getString(JobOrderConstants.RECRUITER_STRING);
			
			 JSONArray emailsTagList=inputJSON.getJSONArray(JobOrderConstants.MAIL_LIST);
			 for(int i=0;i<emailsTagList.length();i++) {
				 toAddressList.add(emailsTagList.getString(i));
			 }
			 logger.info("inputJSON >>> toAddressList: {}, fromUser: {}",toAddressList,emailAddress);
			BigInteger organizationId = new BigInteger(inputJSON.getString("organizationId"));
			
			 AuthenticationConfiguration auth=new AuthenticationConfiguration();
			 auth.setFromUser(emailAddress);
			 EmailParticipant emailParticipant = new EmailParticipant();
			 emailParticipant.setTo(toAddressList);
			 
			 List<String> ccList = new ArrayList<>();
			 ccList.add(emailAddress);
			 emailParticipant.setApplicationSource("ACYUTA");
			 emailParticipant.setCandidateId(candidateId);
			 emailParticipant.setCc(ccList);
			 emailParticipant.setOrganizationId(organizationId.toString());
			 emailParticipant.setOrgTeamName(logInUserFullName);
			 emailParticipant.setReplyTo(emailAddress);
			 emailParticipant.setEnvironment(appEnvironement);
			 
			 
			 MessageData messageData = new MessageData();
			 messageData.setTemplate(mailTemplate);
			 messageData.setHeaderTemplate("NO_TEMPLATE");
			 messageData.setFooterTemplate("NO_TEMPLATE");
			 messageData.setIsEventCancelled(false);
			 messageData.setCalendarId(candidateId);
			 
			 messageData.setDynamicValues(dynamicValues);
			 
			 //sender mail-id  validity should be checked as exception cannot be caught here
			 EmailService.sendEmail(auth, emailParticipant, messageData);
		} catch (Exception e) {
			logger.error("Exception in send mail :", e);
		}

	}

	
	String getJobLications(JobOrder jobOrder){
		
		String jobLocation = "";
		List<JobLocation> locations = jobOrder.getJobLocations();

		for (JobLocation location : locations) {
			if ("ACTIVE".equals(location.getStatus())) {
				jobLocation = jobLocation + location.getLocation() + " / ";
			}
		}
		jobLocation = jobLocation.substring(0, (jobLocation.length() - 2));
		
		jobLocation = "[" + jobLocation+ "]" ;
		return jobLocation;
	}
	
	
	public String checkIsValueEmpty(String value, String replaceValue) {
		return StringUtils.isNotBlank(value) ? StringUtils.capitalize(value.toLowerCase()) : replaceValue;
	}
	private String checkIsEmailEmpty(String value, String replaceValue) {
		return StringUtils.isNotBlank(value) ? value.toLowerCase() : replaceValue;
	}

	
	public String getActiveMailTemplates() {
		List<String> mailTemplateList = null;

		try {
			String orgsList = environment.getProperty("load.templates.organizations.list");

			List<String> organizationList = new ArrayList<String>(Arrays.asList(orgsList.split(",")));
			AcyutaUtils.templatesMap = new HashMap<String, AcyutaTemplate>();
			for (String organizationIdStr : organizationList) {
				BigInteger organizationId = new BigInteger(organizationIdStr);

				List<String> acyutaClients = loadAcyutClient(organizationId);
				AcyutaUtils.acyutaCleintsMap.put(organizationId, acyutaClients);

				mailTemplateList = acyutaPropertiesService.getActiveMailTemplateList(AConstants.ACYUTA_MAIL_TEMPLATE.getValue(), organizationId);

				AcyutaUtils.jobTitleList = acyutaPropertiesService.getjobTitleList(AConstants.TITLE.getValue());

				for (String mailTemplate : mailTemplateList) {
					AcyutaTemplate acyutaTemplate = acyutaPropertiesService.getActiveMailTemplateContent(mailTemplate, organizationId);
					if (null == acyutaTemplate) {
						organizationIdStr = "0";
						acyutaTemplate = acyutaPropertiesService.getActiveMailTemplateContent(mailTemplate, BigInteger.ZERO);
					}
					if (null != acyutaTemplate) {
						AcyutaUtils.templatesMap.put(mailTemplate + "-" + organizationIdStr, acyutaTemplate);
					}
					
					logger.debug("mailTemplate :{} ", mailTemplate);
					logger.debug("acyutaTemplate :{} ", acyutaTemplate);
					
					AcyutaUtils.templatesNameIdMap.put(mailTemplate, acyutaTemplate.getTemplateId().longValue());
					
					AcyutaUtils.templatesIdNameMap.put(acyutaTemplate.getTemplateId().toString(),mailTemplate);
				}
				AcyutaUtils.mailTemplateListMap.put(organizationId, mailTemplateList);
			} 

			logger.info("templatesMap :{} ", AcyutaUtils.templatesMap.keySet());
			// We are preparing the templateFormsMap to send the user acceptance forms
			// Corresponding the template selected.
			// If we send AUTHORIZATION_FORM to user, user has to accept it using
			// AUTHORIZATION_FORM_DIGITAL_SIGN.
			Map<String, String> map = AcyutaConstants.getArticleForms();
			
			for (Map.Entry<String, String> entry : map.entrySet()) {
				
				AcyutaTemplate acyutaTemplate = acyutaPropertiesService.getTemplateForm(entry.getValue());

				AcyutaUtils.templateFormsMap.put(entry.getKey(), acyutaTemplate.getTemplateId());
			}
			logger.info("templateFormsMap: {}", AcyutaUtils.templateFormsMap);

		} catch (Exception e) {
			logger.error(" exception in getActiveMailTemplates method", e);
		}
		return "templates loaded successfully.";
	}
	

	private List<String> loadAcyutClient(BigInteger organizationId) {

		return acyutaPropertiesService.getActiveMailTemplateList(AConstants.ACYUTA_CLIENT.getValue(), organizationId);
	}


	// If location missing in out listed locations, we are storing in the missing locations.
	// We configure the missing locations in neo4j for quick search.
	public void saveMissingLocations(JobOrder jobOrder, String locationString) {

		AcyutaMissingLocations missingLocations = new AcyutaMissingLocations();

		missingLocations.setLocations(locationString);
		missingLocations.setJobOrderId(jobOrder.getJobOrderId());
		missingLocations.setCreatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
		missingLocations.setRecruiterId(jobOrder.getCreatedBy());
	
		missingLocationsRepository.save(missingLocations);
	}
	
	public void saveComments(String commentDescription, JobOrderDTO jobOrderDTO) throws Exception {
		Log.info("Inside save saveComments for JobOrderId: {}",jobOrderDTO.getJobOrderId());
		JobCommentsDTO jobCommentsDTO = new JobCommentsDTO();
		jobCommentsDTO.setCommentDescription(commentDescription);
		jobCommentsDTO.setJobOrderId(jobOrderDTO.getJobOrderId().toString());
		jobCommentsDTO.setCreatedBy(jobOrderDTO.getCreatedBy());
		jobCommentsDTO.setUpdatedBy(jobCommentsDTO.getCreatedBy());
		Date date = AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING);
		jobCommentsDTO.setCreatedDate(date);
		jobCommentsDTO.setUpdatedDate(date);
		jobCommentsDTO.setActive(Constants.JOB_COMMENT_ACTIVE_STATUS);
		jobCommentsDTO.setRecruiter(jobOrderDTO.getAssignedRecruiters());
		logger.debug("JobCommentsDTO created with following data:{}", jobCommentsDTO.toString());
		jobRequisitionService.addNewComment(jobCommentsDTO);
	}
	
	
}
