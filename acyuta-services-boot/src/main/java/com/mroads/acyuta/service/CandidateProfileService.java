package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.util.Date;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.dto.AcyutaPropertiesDTO;
import com.mroads.acyuta.dto.CandidateStatusTrackingDTO;
import com.mroads.acyuta.dto.InterviewerSlotsDTO;
import com.mroads.acyuta.dto.UserDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.AcyutaProperties;
import com.mroads.acyuta.model.CandidateCommentsTracking;
import com.mroads.acyuta.model.CandidateStatusTracking;
import com.mroads.acyuta.model.InterviewerSlot;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.AcyutaPropertiesRepository;
import com.mroads.acyuta.repository.CandidateCommentsTrackingRepository;
import com.mroads.acyuta.repository.CandidateStatusTrackingRepository;
import com.mroads.acyuta.repository.InterviewerSlotsRepository;
import com.mroads.acyuta.repository.JobOrderRepository;

/**
 * @author Mahi.K
 * 
 */

@Service
public class CandidateProfileService {

	private static final Logger log = LoggerFactory.getLogger(CandidateProfileService.class);

	@Autowired
	private AcyutaCandidateRepository addCandidateRepository;

	@Autowired
	private AcyutaPropertiesRepository acyutaPropertiesRepository;

	@Autowired
	private CandidateStatusTrackingRepository candidateStatusTrackingRepository;

	@Autowired
	private CandidateCommentsTrackingRepository candidateCommentsTrackingRepository;
	
	@Autowired
	private VendorService vendorService;	
	
	@Autowired
	private JobRequisitionService jobRequisitionService;
	
	@Autowired
	private InterviewerSlotsRepository interviewerSlotsRepository;
	
	
	
	@Autowired
	private JobOrderRepository jobOrderRepository;

	public String updateCandidateInfo(JSONObject caniddateInfoJSON) {

		String updateStatus = "success";
		try {
			Long candidateId = caniddateInfoJSON.getLong("candidateId");
			AcyutaCandidate candidate = addCandidateRepository.findByCandidateId(BigInteger.valueOf(candidateId));

			// Basic Details
			//FIXME: use Object Mapper to convert JSON to class
			candidate.setFirstName(caniddateInfoJSON.getString("firstName"));
			candidate.setLastName(caniddateInfoJSON.getString("lastName"));
			candidate.setCandidateTitle(caniddateInfoJSON.has("candidateTitle")?caniddateInfoJSON.getString("candidateTitle"):"");
			candidate.setReLocation(caniddateInfoJSON.has("reLocation")?caniddateInfoJSON.getString("reLocation"):"");
			candidate.setCandidateLocation(caniddateInfoJSON.has("candidateLocation")? caniddateInfoJSON.getString("candidateLocation"):"");
			candidate.setPhoneNumber(caniddateInfoJSON.has("phoneNumber")? caniddateInfoJSON.getString("phoneNumber"):"");
			candidate.setExperienceLevel(caniddateInfoJSON.has("experienceLevel")?caniddateInfoJSON.getString("experienceLevel"):"");
			candidate.setSocialLinks(caniddateInfoJSON.has("socialLinks")?caniddateInfoJSON.getString("socialLinks"):"");
			candidate.setEduAndEmpDetails(caniddateInfoJSON.has("eduAndEmpDetails")?caniddateInfoJSON.getString("eduAndEmpDetails"):"");

			// Additional Details
			candidate.setDateOfBirth(caniddateInfoJSON.has("dateOfBirth")?caniddateInfoJSON.getString("dateOfBirth"):"");
			candidate.setVisaStatus(caniddateInfoJSON.has("visaStatus")?caniddateInfoJSON.getString("visaStatus"):"");
			candidate.setOtherVisaStatus(caniddateInfoJSON.has("otherVisaStatus")?caniddateInfoJSON.getString("otherVisaStatus"):"");
			candidate.setPayRate(caniddateInfoJSON.has("payRate")?caniddateInfoJSON.getString("payRate"):"");
			candidate.setBillRate(caniddateInfoJSON.has("billRate")?caniddateInfoJSON.getString("billRate"):"");
			candidate.setLoadedRate(caniddateInfoJSON.has("loadedRate")?caniddateInfoJSON.getString("loadedRate"):"");
			candidate.setOtherVisaStatus(caniddateInfoJSON.has("otherVisaStatus")?caniddateInfoJSON.getString("otherVisaStatus"):"");
			candidate.setPayType(caniddateInfoJSON.has("payType")? caniddateInfoJSON.getString("payType"):"");
			candidate.setQuestions(caniddateInfoJSON.has("questions")?caniddateInfoJSON.getString("questions"):"");
			

			

			// Consultancy Details
			candidate.setConsultancyName(caniddateInfoJSON.has("consultancyName")? caniddateInfoJSON.getString("consultancyName"):"");
			candidate.setConsultancyContactPerson(caniddateInfoJSON.has("consultancyContactPerson")?caniddateInfoJSON.getString("consultancyContactPerson"):"");
			candidate.setConsultancyAddress(caniddateInfoJSON.has("consultancyAddress")?caniddateInfoJSON.getString("consultancyAddress"):"");
			candidate.setConsultancyContactDetails(caniddateInfoJSON.has("consultancyContactDetails")? caniddateInfoJSON.getString("consultancyContactDetails"):"");
			candidate.setUpdatedDate(AcyutaUtils.getZoneTime(Constants.UTC_TIME_FORMAT_STRING));
			
			
			if(candidate.getPayType().equalsIgnoreCase("C2C") || candidate.getPayType().equalsIgnoreCase("Other")){
				log.info("Update subVendor Details:");
				candidate.setSubVendorName(caniddateInfoJSON.has("subVendorName") ? caniddateInfoJSON.getString("subVendorName") : "");
				candidate.setSubVendorContactPerson(caniddateInfoJSON.has("subVendorContactPerson") ? caniddateInfoJSON.getString("subVendorContactPerson") : "");
				candidate.setSubVendorContactDetails(caniddateInfoJSON.has("subVendorContactDetails") ? caniddateInfoJSON.getString("subVendorContactDetails") : "");
				candidate.setSubVendorAddress(caniddateInfoJSON.has("subVendorAddress") ? caniddateInfoJSON.getString("subVendorAddress") : "");

			}

			candidate.setAvailabilityToJoin(caniddateInfoJSON.has("availabilityToJoin")? caniddateInfoJSON.getString("availabilityToJoin"):"");
			candidate.setBenefits(caniddateInfoJSON.has("benefits")? caniddateInfoJSON.getString("benefits"):"");
			candidate.setInterviewAvailability(caniddateInfoJSON.has("interviewAvailability")? caniddateInfoJSON.getString("interviewAvailability"):"");

			
			
			
			String jobOrderId = caniddateInfoJSON.has("jobOrderId") ? caniddateInfoJSON.getString("jobOrderId"):"";
			
			
			//  If user enter comments in the candidate info.. update the comments also.
			//FIXME: simplify the logic...
			String comments = caniddateInfoJSON.has("comments") ? caniddateInfoJSON.getString("comments") : "";
			if (!jobOrderId.isEmpty() && null != comments && !comments.isEmpty() && "null" != comments
					&& !comments.equalsIgnoreCase(candidate.getComments())) {
				saveCandidateComments(caniddateInfoJSON);
				candidate.setComments(comments);
			}

			addCandidateRepository.save(candidate);

			
			
			//FIXME: add the jobOrderId updateCandidateInfo service. To avoid the update candidate info issue.
			if(null!=jobOrderId && !jobOrderId.isEmpty()) {
				
				BigInteger recruiterId = new BigInteger(caniddateInfoJSON.getString("recruiterId"));
				candidate.setCandidateStatus("CANDIDATE_PROFILE_UPDATED");
				jobRequisitionService.saveCandidateStatus(candidate,new BigInteger(jobOrderId),recruiterId);
				
				JobOrder jobOrder = jobOrderRepository.findByJobOrderId(new BigInteger(jobOrderId));
				
				String organizationId = caniddateInfoJSON.getString("organizationId");
				String emailAddress = caniddateInfoJSON.getString("emailAddress");
				String jobId = caniddateInfoJSON.getString("jobId");
				
				String firstName = caniddateInfoJSON.has("firstName")? caniddateInfoJSON.getString("firstName"):"";
				String lastName = caniddateInfoJSON.has("lastName")? caniddateInfoJSON.getString("lastName"):"";
				
	
				String mailSubject = "Candidate "+firstName+" "+lastName+" profile has been updated for RFS # "+jobOrder.getJobId();
				
				// Send candidate profile modification mail to HR Manager
				BigInteger  hrMngerId	= jobOrder.getCreatedBy();
				UserDTO hrManager = jobRequisitionService.getRecruitersInfo(hrMngerId);
				vendorService.vendorNotificationMail(recruiterId, hrManager,jobOrder,mailSubject, Constants.VMS_CANDIDATE_PROFILE_UPDATION, emailAddress);
				log.info("Candidate profile modification mail sent to HR Manager: {}",hrManager.getEmailAddress());
				
				BigInteger vendorManagerId = new BigInteger(caniddateInfoJSON.getString("createdBy"));
				
				// Send candidate profile modification mail to  vendor Manager.
				UserDTO vendorManager = jobRequisitionService.getRecruitersInfo(vendorManagerId);
				vendorService.vendorNotificationMail(recruiterId, vendorManager,  jobOrder, mailSubject, Constants.VMS_CANDIDATE_PROFILE_UPDATION, emailAddress);
				
				log.info("Candidate profile modification mail sent to Vendor Manager: {}",vendorManager.getEmailAddress());
			}
	
		} catch (Exception e) {
			updateStatus = "failure";
			log.error("Exception in updateCandidateInfo: ", e);
		}
		return updateStatus;
	}

	public void addCandidateStatus(JSONObject statusInfo) {

		log.info("Inside addCandidateStatus method");
		try {

			BigInteger organizationId = new BigInteger(statusInfo.getString("organizationId"));
			BigInteger jobOrderId = new BigInteger(statusInfo.getString("jobOrderId"));
			BigInteger candidateId = new BigInteger(statusInfo.getString("candidateId"));
			BigInteger recruiterId = new BigInteger(statusInfo.getString("recruiterId"));
			String candidateStatus = statusInfo.getString("candidateStatus");
			AcyutaProperties acyutaProperty = acyutaPropertiesRepository.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(), candidateStatus, organizationId);
			CandidateStatusTracking statusModel = new CandidateStatusTracking();
			statusModel.setCandidateId(candidateId);
			statusModel.setOrganizationId(organizationId);
			statusModel.setJobOrderId(jobOrderId);
			statusModel.setUpdatedBy(recruiterId);
			// FIXME: Store date formats in UTC.
			// statusModel.setUpdatedDate(AcyutaUtils.getTimeInZone(ZoneOffset.UTC));
			statusModel.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));

			if (null != acyutaProperty) {
				statusModel.setPropertyId(acyutaProperty.getPropertyId());
			} else {
				log.warn("acyutaProperty does not exist with status {}, for organizationId {} :  ", candidateStatus, organizationId);
			}
			log.debug("statusModel : {}", statusModel);
			candidateStatusTrackingRepository.save(statusModel);

		} catch (Exception e) {
			log.error("Exception in saveCandidateStatus : ", e);
		}

	}

	public void saveCandidateComments(JSONObject statusInfo) {

		log.info("Inside saveCandidateComments method");
		try {
			String comments = statusInfo.getString("comments");
			BigInteger organizationId = new BigInteger(statusInfo.getString("organizationId"));
			BigInteger jobOrderId = new BigInteger(statusInfo.getString("jobOrderId"));
			BigInteger candidateId = new BigInteger(statusInfo.getString("candidateId"));
			BigInteger recruiterId = new BigInteger(statusInfo.getString("recruiterId"));

			CandidateCommentsTracking commentsModel = new CandidateCommentsTracking();

			commentsModel.setCandidateId(candidateId);
			commentsModel.setComments(comments);
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

	public void updateUser(JSONObject statusInfo) {

		try {
			BigInteger candidateId = new BigInteger(statusInfo.getString("candidateId"));
			String comments = statusInfo.getString("comments");
			String candidateStatus = statusInfo.getString("candidateStatus");
			BigInteger recruiterId = new BigInteger(statusInfo.getString("recruiterId"));

			AcyutaCandidate addCandidate = addCandidateRepository.findByCandidateId(candidateId);
			if (null != candidateStatus && !candidateStatus.isEmpty()) {
				addCandidate.setCandidateStatus(candidateStatus);
			}
			if (null != comments && !comments.isEmpty()) {
				addCandidate.setComments(comments);
				// FIXME 
			//	addCandidate.setInterviewComments(comments);
			}
			
			addCandidate.setUpdatedDate(AcyutaUtils.getZoneTime(JobOrderConstants.TIME_ZONE_CST));
			addCandidate.setUpdatedBy(recruiterId);
			addCandidateRepository.save(addCandidate);

		} catch (Exception e) {
			log.error("Exception in updateUser during the status and comments : ", e);
		}

	}

	/**
	 * @param candidateId
	 * @return candidateTrackingDTO
	 */
	public AcyutaCandidateDTO findByCandidateId(BigInteger candidateId) {

		AcyutaCandidateDTO acyutaCandidateDTO = null;
		try {
			log.info("get the candidate(candidateId=" + candidateId + ") details from acyuta_user table ");
			if (candidateId.compareTo(BigInteger.ZERO) <= 0) {
				return acyutaCandidateDTO;
			}
			AcyutaCandidate acyutaCandidate = addCandidateRepository.findByCandidateId(candidateId);
			if (acyutaCandidate == null) {
				throw new NullPointerException();
			}
			acyutaCandidateDTO = (AcyutaCandidateDTO) ObjectConverter.convert(acyutaCandidate, new AcyutaCandidateDTO());
		} catch (Exception e) {
			log.error("Exception : getting the candidate details with candidateId >>" + candidateId + "<<", e);
			acyutaCandidateDTO = null;
		}
		return acyutaCandidateDTO;
	}

	public AcyutaCandidateDTO updateMliveInterviewDetails(JSONObject mLiveInterviewDetails) {

		AcyutaCandidateDTO acyutaCandidateDTO = null;
		try {
			BigInteger candidateId = new BigInteger(mLiveInterviewDetails.getString("candidateId"));
			BigInteger mliveScheduleId = new BigInteger(mLiveInterviewDetails.getString("mliveScheduleId"));
			String reportURL = mLiveInterviewDetails.getString("reportURL");
			Float technicalRating = Float.valueOf(mLiveInterviewDetails.getString("technicalRating"));
			Float communicationRating = Float.valueOf(mLiveInterviewDetails.getString("communicationRating"));
			String candidateStatus = mLiveInterviewDetails.getString("candidateStatus");
			String interviewComments = mLiveInterviewDetails.getString("comments");

			addCandidateRepository.updateMliveInterviewDetails(reportURL, technicalRating, communicationRating, candidateStatus,
					interviewComments, mliveScheduleId, candidateId);
			
		
			
			AcyutaCandidate acyutaCandidate=addCandidateRepository.findByCandidateId(candidateId);
			if (null == acyutaCandidate) {
				throw new NullPointerException();
			}
			acyutaCandidateDTO = (AcyutaCandidateDTO) ObjectConverter.convert(acyutaCandidate, new AcyutaCandidateDTO());
		} catch (Exception e) {
			log.error("Exception : when updating mlive interview details into AcyutaCandidate (panna_Acyuta_User table) >>", e);
			acyutaCandidateDTO = null;
		}
		return acyutaCandidateDTO;
	}

	public InterviewerSlotsDTO getTimeSlotsByInterviewerSlotId(BigInteger interviewerSlotId) {

		InterviewerSlotsDTO resultDto = new InterviewerSlotsDTO();
		try {
			return (InterviewerSlotsDTO) ObjectConverter
					.convert(interviewerSlotsRepository.findByInterviewerSlotId(interviewerSlotId), resultDto);
		} catch (Exception e) {
			log.error("Exception : getTimeSlotsByInterviewerSlotId ", e);
		}
		return resultDto;
	}

	/**
	 * 
	 * @param interviewerSlotsDTO
	 * 
	 * @return
	 * 
	 */

	public InterviewerSlotsDTO saveInterviewerSlots(InterviewerSlotsDTO interviewerSlotsDTO) {

		InterviewerSlotsDTO slotDto = new InterviewerSlotsDTO();
		try {
			InterviewerSlot slots = new InterviewerSlot();
			slots = interviewerSlotsRepository
					.save((InterviewerSlot) ObjectConverter.convert(interviewerSlotsDTO, slots));
			slotDto = (InterviewerSlotsDTO) ObjectConverter.convert(slots, slotDto);
		} catch (Exception e) {
			log.error("saveInterviewerSlots : Exception ", e);
		}
		return slotDto;
	}

	/**
	 * 
	 * @param candidateId
	 * 
	 * @param status
	 * 
	 * @param organizationId
	 * 
	 * @param loggedInUserId
	 * 
	 */
	public void saveCandidateStatus(BigInteger candidateId, String status, BigInteger organizationId,
			BigInteger loggedInUserId, BigInteger jobOrderId) {

		try {
			/* getting the property details of this status */
			AcyutaPropertiesDTO acyutaPropertyDto = this.findByTypeAndValue(AConstants.SYSTEM_STATUS.getValue(), status,
					organizationId);
			CandidateStatusTrackingDTO statusTrackingDto = new CandidateStatusTrackingDTO();
			statusTrackingDto.setCandidateId(candidateId);
			statusTrackingDto.setOrganizationId(organizationId);
			statusTrackingDto.setUpdatedBy(loggedInUserId);
			statusTrackingDto.setJobOrderId(jobOrderId);
			log.info("acyutaPropertyDto >>" + acyutaPropertyDto);
			// TimeZone.setDefault(UserLocalServiceUtil.fetchUser(loggedInUserId.longValue()).getTimeZone());
			statusTrackingDto.setUpdatedDate(new Date());
			statusTrackingDto.setPropertyId(acyutaPropertyDto.getPropertyId());
			candidateStatusTrackingRepository.save((CandidateStatusTracking) ObjectConverter.convert(statusTrackingDto,
					new CandidateStatusTracking()));
		}
		catch (Exception e) {
			log.error("Exception in saveCandidateStatus :" + e);
		}
	}

	public AcyutaPropertiesDTO findByTypeAndValue(String type, String status, BigInteger organizationId) {
		AcyutaPropertiesDTO acyutaPropertyDto = null;

		try {
			AcyutaProperties acyutaProperty = acyutaPropertiesRepository.findByTypeAndValue(type, status,
					organizationId);
			if (acyutaProperty == null) {
				return null;
			}
			acyutaPropertyDto = (AcyutaPropertiesDTO) ObjectConverter.convert(acyutaProperty,
					new AcyutaPropertiesDTO());
		} catch (Exception e) {
			acyutaPropertyDto = null;
			log.error("findByTypeAndValue : Exception ", e);
			return acyutaPropertyDto;
		}
		return acyutaPropertyDto;
	}

}
