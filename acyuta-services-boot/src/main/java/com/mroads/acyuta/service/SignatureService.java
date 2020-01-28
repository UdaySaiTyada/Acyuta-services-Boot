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
package com.mroads.acyuta.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
//import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaConstants;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaProperties;
import com.mroads.acyuta.model.CandidateCommentsTracking;
import com.mroads.acyuta.model.CandidateStatusTracking;
import com.mroads.acyuta.model.JobLocation;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.PannaSignature;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaPropertiesRepository;
import com.mroads.acyuta.repository.CandidateCommentsTrackingRepository;
import com.mroads.acyuta.repository.CandidateStatusTrackingRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.SignatureRepository;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.service.EmailService;

/**
 * @author Mahi.K
 * 
 */

@Service
public class SignatureService {

	@Autowired
	private SignatureRepository signatureRepository;

	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;

	@Autowired
	AcyutaPropertiesRepository acyutaPropertiesRepository;

	@Autowired
	CandidateStatusTrackingRepository candidateStatusTrackingRepository;

	@Autowired
	private JobOrderRepository jobOrderRepository;

	@Autowired
	private Environment environment;
	
	@Autowired
	CandidateCommentsTrackingRepository candidateCommentsTrackingRepository;

	@Autowired
	private JobRequisitionService jobRequisitionService;

	private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

	public void updateSignature(String request) throws Exception {

		JSONObject inputJson = new JSONObject(request);

		log.info("Inside updateSignature service.");

		String pdfShiftURL = "";
		String s3PdfURL = "";

		String signatueFormURL = inputJson.getString("signatureFormURL");
		String pdfFileName = inputJson.getString("pdfFile");

		// generatePDF generate the pdf from the user signature form.

		BigInteger candidateId = new BigInteger(inputJson.getString("candidateId"));
		BigInteger recruiterId = new BigInteger(inputJson.getString("recruiterId"));
		BigInteger jobOrderId = new BigInteger(inputJson.getString("jobOrderId"));
		BigInteger articleId = new BigInteger(inputJson.getString("articleId"));
		BigInteger organizationId = new BigInteger(inputJson.getString("organizationId"));

		String signatureJson = inputJson.has("signatureJson") ? inputJson.getString("signatureJson") : "";
		String deviceInfo = inputJson.has("deviceInfo") ? inputJson.getString("deviceInfo") : "";
		String initials = inputJson.has("initials") ? inputJson.getString("initials") : "";
		String signatureName = inputJson.has("signatureName") ? inputJson.getString("signatureName") : "";
		String signatureTitle = inputJson.has("signatureTitle") ? inputJson.getString("signatureTitle") : "";
		String ipAddress = inputJson.getString("ipAddress");
		String isAgree = inputJson.getString("isAgree");
		String digitalSignature = inputJson.has("digitalSignature") ? inputJson.getString("digitalSignature") : "";
		String articleTitle = inputJson.getString("articleTitle");
	
		String adaptedSignature = inputJson.getString("adaptedSignature");

		Map<String, String> articleStatusConstants = AcyutaConstants.getArticleStatusConstants();

		String status = articleStatusConstants.get(articleTitle);
		
		PannaSignature signatureModel = signatureRepository.findByCandidateIdAndJobOrderIdAndArtilceId(candidateId,jobOrderId, articleId);

		signatureModel.setUpdatedDate(new Date());
		signatureModel.setSignature(signatureJson);
		signatureModel.setIntials(initials);
		signatureModel.setName(signatureName);
		signatureModel.setJobTitle(signatureTitle);
		signatureModel.setIsAgree(isAgree);
		signatureModel.setIpAddress(ipAddress);
		signatureModel.setDeviceInfo(deviceInfo);
		signatureModel.setDigitalSignature(digitalSignature);
		signatureModel.setAdaptedSignature(adaptedSignature);

		signatureModel = signatureRepository.save(signatureModel);

		status = articleTitle + "_ACCEPTED";
		String mailTemplate = articleTitle+"_ACCEPTED";
		JSONObject responseJSON = generatePDF(signatueFormURL, pdfFileName);
		if (responseJSON.has("success") && responseJSON.getBoolean("success")) {
			pdfShiftURL = responseJSON.getString("url");
		}

		if (StringUtils.isNotEmpty(pdfShiftURL) && StringUtils.isNotBlank(pdfShiftURL)) {
			s3PdfURL = saveSignatureFormToS3(candidateId, pdfShiftURL, pdfFileName);
		}

		saveCandidateStatus(organizationId, jobOrderId, status, candidateId, recruiterId);

		// send notification mail to Candidate
		 NotificationMail(candidateId,  mailTemplate, s3PdfURL,inputJson);

		mailTemplate = Constants.DIGITAL_SIGN_IN_FORM_RECRUITER_NOTIFY_STATUS_MAIL;
		// send notification mail to recruiter
		NotificationMail(recruiterId, mailTemplate, s3PdfURL, inputJson);

		// FIXME: 1. Send signature notification mail to recruiter. Add signature form
		// in mail template.

		
	}

	private String saveSignatureFormToS3(BigInteger candidateId, String fileUrl, String fileName) throws JSONException {

		JSONObject docsJSON = new JSONObject();
		String s3FileURL = "";
		JSONArray docsJSONArray = new JSONArray();

		String serviceUrl = environment.getProperty("file.upload.url") + "/" + Constants.FILE_UPLOAD_TO_S3;
		log.info("saveSignatureFormToS3 URL : {}", serviceUrl);
		RestTemplate restTemplate = new RestTemplate();
		JSONObject inputJson = new JSONObject();
		inputJson.put("fileUrl", fileUrl);
		ParameterizedTypeReference<String> typeRef = new ParameterizedTypeReference<String>() {
		};
		ResponseEntity<String> responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.POST,
				new HttpEntity<>(inputJson.toString()), typeRef);

		s3FileURL = responseEntity.getBody();
		log.info("saveSignatureFormToS3 Response : {}", s3FileURL);

		// Update user sign in form in the user documents.
		if (!"".equalsIgnoreCase(s3FileURL)) {

			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
			String docs = candidate.getDocuments();
			if (null != docs) {
				docsJSONArray = new JSONArray(candidate.getDocuments());
			}
			docsJSON.put("fileURL", s3FileURL);
			docsJSON.put("fileName", fileName);
			docsJSONArray.put(docsJSON);
			candidate.setDocuments(docsJSONArray.toString());
			addCandidateRepository.save(candidate);
		}
		return s3FileURL;

	}

	// saveCandidateStatus inserts the new record in the
	private void saveCandidateStatus(BigInteger organizationId, BigInteger jobOrderId, String status,
			BigInteger candidateId, BigInteger recruiterId) {

		log.info("Inside SignatureService.saveCandidateStatus : {}", status);
		try {
			// Update the candidate status in the AcyutaUser table.
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(candidateId);
			candidate.setCandidateStatus(status);
			candidate.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));
			addCandidateRepository.save(candidate);

			AcyutaProperties acyutaProperty = acyutaPropertiesRepository
					.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(), status, organizationId);

			if (null == acyutaProperty) {
				log.info("acyutaProperty does not exist with status {}, for organizationId {} :  ", status,
						organizationId);
				acyutaProperty = acyutaPropertiesRepository.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(),
						status, BigInteger.ZERO);
				log.info("acyutaProperty with default oranization loaded.");
			}

			CandidateStatusTracking statusModel = new CandidateStatusTracking();
			statusModel.setCandidateId(candidateId);
			statusModel.setOrganizationId(organizationId);
			statusModel.setJobOrderId(jobOrderId);
			//NOTE: Here status updated by the candidate, but we storing as recruiter updated.
			statusModel.setUpdatedBy(recruiterId);
			// FIXME: Store date formats in UTC.
			statusModel.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));

			if (acyutaProperty != null) {
				statusModel.setPropertyId(acyutaProperty.getPropertyId());
				log.debug("statusModel : {}", statusModel);
				candidateStatusTrackingRepository.save(statusModel);
			} else {
				log.warn("acyutaProperty does not exist.");
			}

		} catch (Exception e) {
			log.error("Exception in SignatureService.saveCandidateStatus : ", e);
		}
	}
	
	public void saveCandidateFeedback(JSONObject inputJson) {

		try {
			
			BigInteger candidateId = new BigInteger(inputJson.getString("candidateId"));
			BigInteger recruiterId = new BigInteger(inputJson.getString("recruiterId"));
			BigInteger jobOrderId = new BigInteger(inputJson.getString("jobOrderId"));
			BigInteger organizationId = new BigInteger(inputJson.getString("organizationId"));
			
			String feedback = inputJson.getString("feedback");
			
			CandidateCommentsTracking commentsModel = new CandidateCommentsTracking();

			commentsModel.setCandidateId(candidateId);
			commentsModel.setComments(feedback);
			// commentsModel.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			// FIXME: Store date formats in UTC.
			commentsModel.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));
			commentsModel.setUpdatedBy(recruiterId);
			commentsModel.setOrganizationId(organizationId);
			commentsModel.setJobOrderId(jobOrderId);
			candidateCommentsTrackingRepository.save(commentsModel);
			
		} catch (Exception e) {
			log.error("Exception in saveCandidateComments : ", e);
		}
	}

	public JSONObject generatePDF(String hrmlFileURL, String pdfFile) {

		log.info("Inside generatePDF ");
		StringBuilder builder = new StringBuilder();
		JSONObject response = new JSONObject();
		try {

			String apiKey = environment.getProperty("pdfshift.api.key");
			String serviceURL = environment.getProperty("pdfshift.service.url");
			String timeout = environment.getProperty("pdfshift.timeout");
			String delay = environment.getProperty("pdfshift.delay");

			String encoding = Base64.getEncoder().encodeToString(apiKey.getBytes());
			HttpPost httppost = new HttpPost(serviceURL);
			httppost.setHeader("Authorization", "Basic " + encoding);
			httppost.setHeader("Content-type", "application/json");

			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("source", hrmlFileURL);
			jsonObject.accumulate("filename", pdfFile);
			jsonObject.accumulate("landscape", "false");
			jsonObject.accumulate("timeout", timeout);
			jsonObject.accumulate("delay", delay);

			log.info("Input jsonObject : {}", jsonObject);
			org.apache.http.HttpEntity postingString = new StringEntity(jsonObject.toString());
			httppost.setEntity(postingString);

			CloseableHttpClient client = HttpClients.createDefault();

			try (CloseableHttpResponse response2 = client.execute(httppost)) {
				org.apache.http.HttpEntity entity = response2.getEntity();
				log.info("entity : {}", entity);
				try (BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						builder.append(inputLine);
					}
					response = new JSONObject(builder.toString());
					log.info("response : {}", response);
				} catch (IOException e) {
					log.error("Exception in generatePDF read response: ", e);
				}
			}

		} catch (Exception e) {
			log.error("Exception in generatePDF : ", e);
		}
		return response;
	}

	// candidateId, mailTemplate, s3PdfURL,inputJson

	// NOTE: reduce the code complexity.
	public void NotificationMail(BigInteger mailNotifyUserId, String mailTemplate, String s3PdfURL, JSONObject inputJson) {

		log.info("Inside NotificationMail mailNotifyUserId :{}",mailNotifyUserId);
		String logInUserEmailAddress = "";
		String logInUserFullName = "";
		List<String> toAddressList = new ArrayList<>();
		UserDTO recruiter = new UserDTO();
		UserDTO mailNotifyUser = new UserDTO();
		String locationString = "";
		String userName ="";

		try {

			BigInteger jobOrderId = new BigInteger(inputJson.getString("jobOrderId"));
			BigInteger recruiterId = new BigInteger(inputJson.getString("recruiterId"));
			BigInteger acyutaCandidateId = new BigInteger(inputJson.getString("candidateId"));
			String isAgree = inputJson.getString("isAgree");
			String articleTitle = inputJson.getString("articleTitle");

			JobOrder job = jobOrderRepository.findByJobOrderId(jobOrderId);
			recruiter = jobRequisitionService.getRecruitersInfo(recruiterId);
			mailNotifyUser = jobRequisitionService.getRecruitersInfo(mailNotifyUserId);
			AcyutaCandidate acyutaCandidate = addCandidateRepository.findByCandidateId(acyutaCandidateId);

			
			// FIXME: If mail notify user is recruiter we have user info in User_ table
			// If mail notify user is acyuta candidate user info in the Acyuta User table. 
			if(!mailNotifyUserId.equals(acyutaCandidateId)){
				mailNotifyUser = jobRequisitionService.getRecruitersInfo(mailNotifyUserId);
				toAddressList.add(mailNotifyUser.getEmailAddress());
				userName = mailNotifyUser.getFirstName() + " " + mailNotifyUser.getLastName();
			}else {
				AcyutaCandidate acyutaCandidate2  = addCandidateRepository.findByCandidateId(mailNotifyUserId);
				toAddressList.add(acyutaCandidate2.getEmailAddress());
				userName = acyutaCandidate2.getFirstName() + " " + acyutaCandidate2.getLastName();
			}
			
			

			logInUserEmailAddress = recruiter.getEmailAddress();
			logInUserFullName = StringUtils.isBlank(recruiter.getFirstName()) ? ""
					: recruiter.getFirstName()
							+ (StringUtils.isBlank(recruiter.getLastName()) ? "" : " " + recruiter.getLastName());

			AuthenticationConfiguration auth = new AuthenticationConfiguration();
			// NOTE: If we doesn't set setFromUser all mails sent from noreply@panna.ai
			auth.setFromUser("noreply@panna.ai");
			if(logInUserEmailAddress.indexOf("@mroads.com") !=-1 || logInUserEmailAddress.indexOf("@panna.ai") !=-1 ) {
				auth.setFromUser(logInUserEmailAddress);
			}
			 log.info("For Other than Hilton organization we are sending from noreply@panna.ai. FromUser: {}",auth.getFromUser());

			EmailParticipant emailParticipant = new EmailParticipant();
			emailParticipant.setTo(toAddressList);
			List<String> ccList = new ArrayList<>();
			// NOTE: If we want CC copy to mail sender...
			// ccList.add(logInUserEmailAddress);
			emailParticipant.setCc(ccList);

			emailParticipant.setOrganizationId(job.getOrganizationId().toString());
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
			messageData.setSubject("");

			// FIXME: We are saving other vendor name as first and last name to avoid filter
			// issue in job list vendor page.
			
			log.info("mailNotifyUser: {}",mailNotifyUser);
			
			log.info("acyutaCandidateName: {}",acyutaCandidate);
			
			String acyutaCandidateName = acyutaCandidate.getFirstName()+ " " + acyutaCandidate.getLastName();
		 
			

			log.debug("jobList obtained---{}", job);
			messageData.setDynamicValue("[$JOBID$]", validateFiedld(job.getJobId()));

			String envURL = environment.getProperty("view.profile.url");

			messageData.setDynamicValue("[$CANDIDATE_NAME$]", validateFiedld(userName));
			messageData.setDynamicValue("[$ACYUTA_CANDIDATE_NAME$]", validateFiedld(acyutaCandidateName));
			messageData.setDynamicValue("[$JOBTITLE$]", validateFiedld(job.getJobTitle()));
			messageData.setDynamicValue("[$CLIENT_NAME$]", validateFiedld(job.getClientName()));
			messageData.setDynamicValue("[$JOB_PAY_RATE$]", validateFiedld(job.getPayRate()));
			messageData.setDynamicValue("[$JOBTYPE$]", validateFiedld(job.getJobType()));
			messageData.setDynamicValue("[$PROJECT_TARGET_DATE$]", validateFiedld(job.getProjectTargetDate()));
			messageData.setDynamicValue("[$JOBDESCRIPTION$]", validateFiedld(job.getJobDescription()));
			
			if(null!=s3PdfURL && StringUtils.isNotEmpty(s3PdfURL)) {
				List<String>  attachmentsLinkList  = new ArrayList<String>();
				attachmentsLinkList.add(s3PdfURL);
				messageData.setAttachmentsLinkList(attachmentsLinkList);
				
			}

			String duration = job.getJobDuration();
			if (null != job.getProjectStartDate() && !job.getProjectStartDate().isEmpty()) {
				duration = job.getProjectStartDate() + "-" + job.getProjectEndDate();
			}
			messageData.setDynamicValue("[$SIGNATURE_STATUS$]", "rejected");
			if (isAgree.equalsIgnoreCase("true")) {
				messageData.setDynamicValue("[$SIGNATURE_STATUS$]", "accepted");
			}
			messageData.setDynamicValue("[$SIGNATURE_PDF_URL$]", s3PdfURL);
			
			//Template display is different from the template name in DB.
			articleTitle = AcyutaConstants.getTemplateDisplayNames().get(articleTitle);
			
			messageData.setDynamicValue("[$SIGNATURE_FORM$]", validateFiedld(articleTitle));

			messageData.setDynamicValue("[$JOBDURATION$]", validateFiedld(duration));
			messageData.setDynamicValue("[$RECRUITER_TITLE$]", " ");
			String phoneNumber = recruiter.getPhoneNumber() != null ? recruiter.getPhoneNumber().trim() : " ";
			messageData.setDynamicValue("[$RECRUITER_PHONE$]", validateFiedld(phoneNumber));
			messageData.setDynamicValue("[$RECRUITER_EMAIL$]", validateFiedld(logInUserEmailAddress));
			String viewJobURL = envURL + "/#/requisitions?jobOrderId=" + job.getJobOrderId();
			messageData.setDynamicValue("[$JOBDESCRIPTION_LINK$]", validateFiedld(viewJobURL));
			List<JobLocation> location = job.getJobLocations();
			for (JobLocation jobLocation : location) {
				if (jobLocation.getStatus().equals(Constants.LOCATION_MAPPING_ACTIVE_STATUS)) {
					locationString += jobLocation.getLocation() + " ";
				}
			}
			messageData.setDynamicValue("[$JOBLOCATION$]", validateFiedld(locationString));
			messageData.setDynamicValue("[$RECRUITER_FIRST_NAME$]", validateFiedld(recruiter.getFirstName()));
			messageData.setDynamicValue("[$RECRUITER_LAST_NAME$]", validateFiedld(recruiter.getLastName()));
	
			log.debug("messageData {}", messageData);
			EmailService.sendEmail(auth, emailParticipant, messageData);
		} catch (Exception e) {
			log.error("Exception in vendorNotificationMail: ", e);
		}
	}

	private String validateFiedld(String field) {
		String resp = " ";
		if (null != field && !field.isEmpty()) {
			resp = field;
		}
		return resp;
	}

	public void updateSignatureFeedback(String request) throws JSONException {

		JSONObject inputJson = new JSONObject(request);
		String pdfShiftURL = "";
		String s3PdfURL = "";

		BigInteger candidateId = new BigInteger(inputJson.getString("candidateId"));
		BigInteger recruiterId = new BigInteger(inputJson.getString("recruiterId"));
		BigInteger jobOrderId = new BigInteger(inputJson.getString("jobOrderId"));
		BigInteger articleId = new BigInteger(inputJson.getString("articleId"));
		BigInteger organizationId = new BigInteger(inputJson.getString("organizationId"));

		String initials = inputJson.has("initials") ? inputJson.getString("initials") : "";
		String signatureName = inputJson.has("signatureName") ? inputJson.getString("signatureName") : "";
		String signatureTitle = inputJson.has("signatureTitle") ? inputJson.getString("signatureTitle") : "";
		String ipAddress = inputJson.getString("ipAddress");
		String isAgree = inputJson.has("isAgree") ? inputJson.getString("isAgree") : "false";
		String articleTitle = inputJson.getString("articleTitle");

		PannaSignature signatureModel = signatureRepository.findByCandidateIdAndJobOrderIdAndArtilceId(candidateId,
				jobOrderId, articleId);

		signatureModel.setUpdatedDate(new Date());
		signatureModel.setSignature("");
		signatureModel.setIntials(initials);
		signatureModel.setName(signatureName);
		signatureModel.setJobTitle(signatureTitle);
		signatureModel.setIsAgree(isAgree);
		signatureModel.setIpAddress(ipAddress);
		signatureModel.setDigitalSignature("");
		signatureModel.setAdaptedSignature("");

		signatureModel = signatureRepository.save(signatureModel);
		
		String status = articleTitle + "_REJECTED";
		saveCandidateStatus(organizationId, jobOrderId, status, candidateId,recruiterId);
		saveCandidateFeedback(inputJson);

		String mailTemplate = articleTitle+"_REJECTED";

		// send notification mail to Candidate
		NotificationMail(candidateId,  mailTemplate, s3PdfURL,inputJson);

		// send notification mail to recruiter
		mailTemplate = Constants.DIGITAL_SIGN_IN_FORM_RECRUITER_NOTIFY_STATUS_MAIL;
		NotificationMail(recruiterId, mailTemplate, s3PdfURL, inputJson);

	}
}
