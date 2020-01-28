package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.AConstants;
import com.mroads.acyuta.common.AcyutaConstants;
import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.EncryptDecryptUtil;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.AcyutaInterviewsDTO;
import com.mroads.acyuta.dto.AcyutamLiveInterviewsDTO;
import com.mroads.acyuta.dto.InterviewsViewDTO;
import com.mroads.acyuta.model.AcyutaInterviews;
import com.mroads.acyuta.model.AcyutamLiveInterviews;
import com.mroads.acyuta.model.InterviewsView;
import com.mroads.acyuta.repository.AcyutaInterviewsRepository;
import com.mroads.acyuta.repository.AcyutamLiveInterviewsRepository;
import com.mroads.acyuta.repository.InterviewerSlotsRepository;
import com.mroads.acyuta.repository.InterviewsRepository;
import com.mroads.acyuta.repository.InterviewsViewRepository;

@Service
public class InterviewsService {

	private static final Logger log = LoggerFactory.getLogger(InterviewsService.class);

	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Autowired
	InterviewsRepository interviewsRepository;

	@Autowired
	AcyutamLiveInterviewsRepository acyutamLiveInterviewsRepository;

	@Autowired
	AcyutaInterviewsRepository acyutaInterviewsRepository;

	@Autowired
	InterviewerSlotsRepository interviewerSlotsRepository;
	
	@Autowired
	CandidateProfileService candidateProfileService;
	
	@Autowired
	InterviewsViewRepository interviewsViewRepository;
	
	@Autowired
	PostJobService postJobService;

//	public List<AcyutaInterviewsViewDTO> getCurrentInterviews(JSONObject input) {
//		List<AcyutaInterviewsViewDTO> interviewsDTOList = new ArrayList<AcyutaInterviewsViewDTO>();
//
//		List<String> interviewPositionList = new ArrayList<>();
//		try {
//			BigInteger organizationId = new BigInteger(input.getString(JobOrderConstants.ORGANIZATION_ID_STRING));
//			BigInteger recruiterId = new BigInteger(input.getString(JobOrderConstants.RECRUITER_ID_STRING));
//			// String interviwerIdString =
//			// input.getString(JobOrderConstants.INTERVIWER_ID_STRING);
//			Date startDate = dateFormat.parse(input.getString(JobOrderConstants.START_DATE_STRING));
//			Date endDate = dateFormat.parse(input.getString(JobOrderConstants.END_DATE_STRING));
//			String clientName = input.getString(JobOrderConstants.CLIENT_NAME_STRING);
//
//			String interviewStatusArray = input.getString(JobOrderConstants.INTERVIEW_STATUS_STRING);
//
//			if (interviewStatusArray.isEmpty()) {
//				interviewPositionList = AcyutaConstants.getCurrentInterviewStatusList();
//			} else {
//				interviewPositionList = Arrays.asList(interviewStatusArray.split("\\s*,\\s*"));
//			}
//			String interviewMode = input.getString(JobOrderConstants.INTERVIEW_MODE_STRING);
//			String jobId = input.getString(JobOrderConstants.JOB_ID_STRING);
//
//			// NOTE: we are getting all current interviews for the organization.
//			// We does'nt
//			// have filter based on interviewer.
//			BigInteger interviewerId = null;
//
//			// NOTE: get pagination values from front-end
//			PageRequest pageRequest = new PageRequest(0, 250, Sort.Direction.ASC, AConstants.INTERVIEW_DATE.getValue(),
//					AConstants.TIME_SLOT.getValue());
//
//			List<AcyutaInterviewsView> list = interviewsRepository.getCurrentInterviews(interviewPositionList,
//					clientName, interviewMode, jobId, startDate, endDate, recruiterId, interviewerId, organizationId,
//					pageRequest);
//
//			log.info("current interviews size: {}", list.size());
//
//			for (AcyutaInterviewsView acyutaInterviews : list) {
//				AcyutaInterviewsViewDTO dto = (AcyutaInterviewsViewDTO) ObjectConverter.convert(acyutaInterviews,
//						new AcyutaInterviewsViewDTO());
//				dto.setInterviewDate(dateFormat.format(acyutaInterviews.getInterviewDate()));
//				interviewsDTOList.add(dto);
//				dto.setEncryptedMail(EncryptDecryptUtil.getEncryptKey(dto.getEmailAddress()));
//			}
//		} catch (Exception e) {
//			log.error("Exception in getCurrentInterviews service : ", e);
//		}
//
//		return interviewsDTOList;
//	}
//
//	public List<AcyutaInterviewsViewDTO> getCompletedInterviews(JSONObject input) {
//		List<AcyutaInterviewsViewDTO> interviewsDTOList = new ArrayList<AcyutaInterviewsViewDTO>();
//
//		List<String> interviewPositionList = new ArrayList<>();
//		try {
//			String orgIdString = input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);
//			String recruiterIdString = input.getString(JobOrderConstants.RECRUITER_ID_STRING);
//			String interviwerIdString = input.getString(JobOrderConstants.INTERVIWER_ID_STRING);
//			String startDateStr = input.getString(JobOrderConstants.START_DATE_STRING);
//			String endDateStr = input.getString(JobOrderConstants.END_DATE_STRING);
//			String interviewStatusArray = input.getString(JobOrderConstants.INTERVIEW_STATUS_STRING);
//
//			if (interviewStatusArray.isEmpty()) {
//				interviewPositionList = AcyutaConstants.getNonRegisteredInterviewStatusList();
//			} else {
//				interviewPositionList = Arrays.asList(interviewStatusArray.split("\\s*,\\s*"));
//			}
//
//			log.info("Filter Data ##: orgId: {}, recruiterId: {}, interviwerId: {}, startDate: {}, endDate: {}",
//					orgIdString, recruiterIdString, interviwerIdString, startDateStr, endDateStr);
//
//			BigInteger orgId = new BigInteger(orgIdString);
//			BigInteger recruiterId = StringUtils.isNotBlank(recruiterIdString) ? new BigInteger(recruiterIdString)
//					: null;
//			BigInteger interviwerId = StringUtils.isNotBlank(interviwerIdString) ? new BigInteger(interviwerIdString)
//					: null;
//			Date endDate = dateFormat.parse(endDateStr);
//			Date startDate = dateFormat.parse(startDateStr);
//
//			Float startTechSkill = 0f;
//			Float endTechSkill = 05f;
//			Float startOfCommRating = 0f;
//			Float endOfCommRating = 05f;
//			Float startOfPannaRating = 0f;
//			Float endOfPannaRating = 100f;
//
//			PageRequest pageRequest = new PageRequest(0, 250, Sort.Direction.ASC, AConstants.INTERVIEW_DATE.getValue(),
//					AConstants.TIME_SLOT.getValue());
//			List<AcyutaInterviewsView> list = interviewsRepository.getCompletedInterviews(interviewPositionList,
//					startTechSkill, endTechSkill, startOfCommRating, endOfCommRating, startOfPannaRating,
//					endOfPannaRating, startDate, endDate, recruiterId, interviwerId, orgId, pageRequest);
//
//			log.info("completed interviews size: {}", list.size());
//
//			for (AcyutaInterviewsView acyutaInterviews : list) {
//				AcyutaInterviewsViewDTO dto = (AcyutaInterviewsViewDTO) ObjectConverter.convert(acyutaInterviews,
//						new AcyutaInterviewsViewDTO());
//				dto.setInterviewDate(dateFormat.format(acyutaInterviews.getInterviewDate()));
//				dto.setEncryptedMail(EncryptDecryptUtil.getEncryptKey(dto.getEmailAddress()));
//				interviewsDTOList.add(dto);
//			}
//
//		} catch (Exception e) {
//			log.error("Exception in getCompletedInterviews service : ", e);
//		}
//
//		return interviewsDTOList;
//	}

	/* chiranjivi */
	/**
	 * 
	 * get the AcyutamLiveInterviewsDTO by passing mLiveScheduleId
	 * 
	 * @param mLiveScheduleId
	 * @return AcyutamLiveInterviewsDTO
	 */
	public AcyutamLiveInterviewsDTO getAcyutamLiveInterviewsDTOByMLiveScheduleId(BigInteger mLiveScheduleId) {

		log.debug("service ===> acyuta mLiveInterviewsService findByAcyutaInterviewsId method");
		AcyutamLiveInterviewsDTO acyutamLiveInterviewsDTO = new AcyutamLiveInterviewsDTO();
		try {
			AcyutamLiveInterviews acyutamLiveInterviews = acyutamLiveInterviewsRepository
					.findByMLiveScheduleId(mLiveScheduleId);
			acyutamLiveInterviewsDTO = (AcyutamLiveInterviewsDTO) ObjectConverter.convert(acyutamLiveInterviews,
					new AcyutamLiveInterviewsDTO());
		} catch (Exception e) {
			log.error("Exception  : getting the AcyutamLiveinterviewsDTO with acyutaInterviewsId", e);
		}
		return acyutamLiveInterviewsDTO;
	}

	/**
	 * @param interviewId
	 * @return AcyutaInterviewsDTO based on interviewId
	 */
	public AcyutaInterviewsDTO findByInterviewId(BigInteger interviewId) {

		AcyutaInterviewsDTO acyutaInterviewsDTO = new AcyutaInterviewsDTO();
		AcyutaInterviews acyutaInterviews = acyutaInterviewsRepository.findByInterviewId(interviewId);
		try {
			acyutaInterviewsDTO = (AcyutaInterviewsDTO) ObjectConverter.convert(acyutaInterviews, acyutaInterviewsDTO);
		} catch (Exception e) {
			log.error("Exception : findByInterviewId ", e);
		}

		return acyutaInterviewsDTO;
	}

	/**
	 * @param interviewsDTO
	 * @return saved AcyutaInterviewsDTO
	 */
	public AcyutaInterviewsDTO saveAcyutaInterviews(AcyutaInterviewsDTO interviewsDTO) {

		AcyutaInterviewsDTO resultDto = new AcyutaInterviewsDTO();
		try {
			interviewsDTO.setInterviewerId((interviewsDTO.getInterviewerSlotId() != BigInteger.ZERO)
					? interviewerSlotsRepository.findByInterviewerSlotId(interviewsDTO.getInterviewerSlotId())
							.getInterviewerId()
					: BigInteger.ZERO);
			AcyutaInterviews obj = new AcyutaInterviews();
			obj = acyutaInterviewsRepository.save((AcyutaInterviews) ObjectConverter.convert(interviewsDTO, obj));
			resultDto = (AcyutaInterviewsDTO) ObjectConverter.convert(obj, resultDto);
		} catch (Exception e) {
			log.error("saveAcyutaInterviews : Exception ", e);
		}
		return resultDto;
	}

	/**
	 * @param mLiveInterviewsDTO
	 * @return saved AcyutamLiveInterviews dto
	 */
	public AcyutamLiveInterviewsDTO saveAcyutamLiveInterviews(AcyutamLiveInterviewsDTO mLiveInterviewsDTO) {

		AcyutamLiveInterviewsDTO resultDto = new AcyutamLiveInterviewsDTO();
		try {
			AcyutamLiveInterviews obj = new AcyutamLiveInterviews();
			obj = acyutamLiveInterviewsRepository
					.save((AcyutamLiveInterviews) ObjectConverter.convert(mLiveInterviewsDTO, obj));
			resultDto = (AcyutamLiveInterviewsDTO) ObjectConverter.convert(obj, resultDto);

			acyutaInterviewsRepository.updateMLiveInterviewerLink(obj.getMLiveInterviewerLink(),
					obj.getAcyutaInterviewsId());
			
		} catch (Exception e) {
			log.error("Exception :saving live interview details into Acyuta_mLive_Interviews ", e);

		}
		return resultDto;
	}


	
	public JSONObject fetchCurrentInterviews(JSONObject input) {
		List<InterviewsViewDTO> interviewsDTOList = new ArrayList<InterviewsViewDTO>();
		List<String> interviewPositionList = new ArrayList<>();

		JSONObject obj = new JSONObject();

		try {

			BigInteger organizationId = new BigInteger(input.getString(JobOrderConstants.ORGANIZATION_ID_STRING));
			String recruiterIdString = input.getString(JobOrderConstants.RECRUITER_ID_STRING);
			String interviwerIdString = input.getString(JobOrderConstants.INTERVIWER_ID_STRING);
			Date startDate = dateFormat.parse(input.getString(JobOrderConstants.START_DATE_STRING));
			Date endDate = dateFormat.parse(input.getString(JobOrderConstants.END_DATE_STRING));
			String clientName = StringUtils.isNotBlank(input.getString(JobOrderConstants.CLIENT_NAME_STRING))
					? input.getString(JobOrderConstants.CLIENT_NAME_STRING) : null;
			String interviewMode = StringUtils.isNotBlank(input.getString(JobOrderConstants.INTERVIEW_MODE_STRING))
					? input.getString(JobOrderConstants.INTERVIEW_MODE_STRING) : null;
			String jobId = StringUtils.isNotBlank(input.getString(JobOrderConstants.JOB_ID_STRING))
					? input.getString(JobOrderConstants.JOB_ID_STRING) : null;
			String searchText = StringUtils.isNotBlank(input.getString(Constants.SEARCH_TEXT)) ? input.getString(Constants.SEARCH_TEXT) : null;
			String interviewStatusArray = input.getString(JobOrderConstants.INTERVIEW_STATUS_STRING);

			if (interviewStatusArray.isEmpty()) {
				interviewPositionList = AcyutaConstants.getCurrentInterviewStatusList();
			} else {
				interviewPositionList = Arrays.asList(interviewStatusArray.split("\\s*,\\s*"));
			}

			// NOTE: we are getting all current interviews for the organization.
			// We does'nt
			// have filter based on interviewer.
			BigInteger recruiterId = StringUtils.isNotBlank(recruiterIdString) ? new BigInteger(recruiterIdString)
					: null;
			BigInteger interviewerId = StringUtils.isNotBlank(interviwerIdString) ? new BigInteger(interviwerIdString)
					: null;
			// NOTE: get pagination values from front-end

			// FIXME
			if ("Panna Live".equals(interviewMode)) {
				interviewMode = "mlive";
			}

			int page = input.getInt(Constants.PAGE_NUMBER);
			int size = input.getInt(Constants.PAGE_SIZE);

			PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.ASC,
					AConstants.INTERVIEW_DATE.getValue(), AConstants.TIME_SLOT.getValue());
			Page<InterviewsView> list = interviewsViewRepository.getCurrentInterviews(interviewPositionList, clientName,
					interviewMode, jobId, searchText, startDate, endDate, recruiterId, interviewerId, organizationId, pageRequest);

			log.info("current interviews size: {}", list.getTotalElements());

			for (InterviewsView interviewsView : list) {
				InterviewsViewDTO dto = (InterviewsViewDTO) ObjectConverter.convert(interviewsView,
						new InterviewsViewDTO());
				dto.setInterviewDate(dateFormat.format(interviewsView.getInterviewDate()));
				interviewsDTOList.add(dto);
				dto.setEncryptedMail(EncryptDecryptUtil.getEncryptKey(dto.getEmailAddress()));

				dto.setPhoneNumber(formatMobileNumber(dto.getPhoneNumber()));
			}

			obj.put("tableData", interviewsDTOList);
			obj.put("totalElements", list.getTotalElements());
			obj.put("totalPages", list.getTotalPages());

		} catch (Exception e) {
			log.error("Exception in fetchCurrentInterviews service : ", e);
		}

		return obj;
	}
	
	private String formatMobileNumber(String phoneNumber) {
		if(StringUtils.isNotBlank(phoneNumber)) {

		StringBuilder sb = new StringBuilder(phoneNumber.replaceAll("[^0-9]", ""))
		            .insert(3,"-")
		            .insert(7,"-");
		return sb.toString();
		}
		return "";
		}

	public int updateCandidateInterviewStatus(String input) {
		int response = 0;
		try {
			JSONObject inputJSON=new JSONObject(input);
			String status = inputJSON.getString("candidateStatus");
			String comments = inputJSON.getString("comments");
			log.info(status);
			String interviewString = inputJSON.getString(JobOrderConstants.INTERVIEW_ID_STRING);
			BigInteger interviewId = StringUtils.isNotBlank(interviewString) ? new BigInteger(interviewString): null;
			
			String canddiateIdStr = inputJSON.getString(JobOrderConstants.CANDIDATE_ID_STRING);			
			String jobOrderIdStr = inputJSON.getString(JobOrderConstants.JOB_ORDER_ID_STRING);	
			String reIdcString = inputJSON.getString(JobOrderConstants.RECRUITER_ID_STRING);
			String orgIdStr = inputJSON.getString(JobOrderConstants.ORGANIZATION_ID_STRING);

			if(StringUtils.isNotBlank(status) && (status.equals("CLEARED_FOR_NEXT_STEPS") || status.equals("REJECTED_BY_CLIENT")) ){
				// NOTE: Once the status changed for client Interview we are updating the interviewpostion on Acyuta Interviews table
				String interviewPosition = "COMPLETED";
				acyutaInterviewsRepository.updateInterviewStatus(status, interviewId,interviewPosition);
			}
			if(StringUtils.isNotBlank(comments)){
				acyutaInterviewsRepository.updateInterviewComments(comments, interviewId);
			}
			
			// FIXME  we should maintain the same class JSONObject 
			org.codehaus.jettison.json.JSONObject newJson = new org.codehaus.jettison.json.JSONObject();
			newJson.put("candidateStatus", status);
			newJson.put("candidateId", canddiateIdStr);
			newJson.put("jobOrderId", jobOrderIdStr);
			newJson.put("recruiterId", reIdcString);
			newJson.put("organizationId", orgIdStr);
			newJson.put("comments", comments);
			candidateProfileService.updateUser(newJson);
			
		} catch (Exception e) {
			log.error("Exception in updateCandidateInterviewStatus service : ", e);
		}
		return response;
	}
	
	
	public int updateCandidateInterview(JSONObject inputJSON) {
		int response = 0;
		try {
			String interviewString = inputJSON.getString(JobOrderConstants.INTERVIEW_ID_STRING);
			BigInteger interviewId = StringUtils.isNotBlank(interviewString) ? new BigInteger(interviewString): null;
			String interviewDate = inputJSON.getString(Constants.INTERVIEW_DATE);
			
			Date date = dateFormat.parse(interviewDate);
			
			String interviewTime = inputJSON.getString(Constants.INTERVIEW_TIME) +(inputJSON.has(Constants.ZONE) ? " "+ inputJSON.getString(Constants.ZONE):"");
			String interviewStatus = inputJSON.getString(Constants.MAIL_TEMPLATE);
			String duration =inputJSON.has(Constants.DURATION_TIME) ? inputJSON.getString(Constants.DURATION_TIME) : "";
			String clientInterviewerName=inputJSON.has(Constants.INTERVIEWER_NAME) ? inputJSON.getString(Constants.INTERVIEWER_NAME) : "";
			String clientInterviewLocation =inputJSON.has(Constants.CLIENT_INTERVIEW_LOCATION) ? inputJSON.getString(Constants.CLIENT_INTERVIEW_LOCATION) :"";
			
			if(interviewStatus.equals("CANCEL_INTERVIEW"))
			{
			acyutaInterviewsRepository.cancelInterview(date, interviewTime,interviewStatus,JobOrderConstants.CANCELED,interviewId);
			}else
			{
				acyutaInterviewsRepository.updateInterview(date,interviewTime,interviewStatus,duration,clientInterviewerName,clientInterviewLocation,interviewId);
				}
		} catch (Exception e) {
			log.error("Exception in updateCandidateInterview service : ", e);
		}
		return response;
	}
	

	public JSONObject fetchCompletedInterviews(JSONObject input) {
		List<InterviewsViewDTO> interviewsDTOList = new ArrayList<InterviewsViewDTO>();

		List<String> interviewPositionList = new ArrayList<>();

		JSONObject obj = new JSONObject();

		try {
			String orgIdString = input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);
			String recruiterIdString = input.getString(JobOrderConstants.RECRUITER_ID_STRING);
			String interviwerIdString = input.getString(JobOrderConstants.INTERVIWER_ID_STRING);
			String startDateStr = input.getString(JobOrderConstants.START_DATE_STRING);
			String endDateStr = input.getString(JobOrderConstants.END_DATE_STRING);

			String clientName = StringUtils.isNotBlank(input.getString(JobOrderConstants.CLIENT_NAME_STRING))
					? input.getString(JobOrderConstants.CLIENT_NAME_STRING) : null;
			String interviewMode = StringUtils.isNotBlank(input.getString(JobOrderConstants.INTERVIEW_MODE_STRING))
					? input.getString(JobOrderConstants.INTERVIEW_MODE_STRING) : null;
			String jobId = StringUtils.isNotBlank(input.getString(JobOrderConstants.JOB_ID_STRING))
					? input.getString(JobOrderConstants.JOB_ID_STRING) : null;
			String searchText = StringUtils.isNotBlank(input.getString(Constants.SEARCH_TEXT)) ? input.getString(Constants.SEARCH_TEXT) : null;

			String interviewStatusArray = input.getString(JobOrderConstants.INTERVIEW_STATUS_STRING);
			if (interviewStatusArray.isEmpty()) {
				interviewPositionList = AcyutaConstants.getNonRegisteredInterviewStatusList();
			} else {
				interviewPositionList = Arrays.asList(interviewStatusArray.split("\\s*,\\s*"));
			}

			log.info("Filter Data : orgId: {}, recruiterId: {}, interviwerId: {}, startDate: {}, endDate: {}",
					orgIdString, recruiterIdString, interviwerIdString, startDateStr, endDateStr);

			BigInteger orgId = new BigInteger(orgIdString);
			BigInteger recruiterId = StringUtils.isNotBlank(recruiterIdString) ? new BigInteger(recruiterIdString)
					: null;
			BigInteger interviewerId = StringUtils.isNotBlank(interviwerIdString) ? new BigInteger(interviwerIdString)
					: null;

			Date endDate = dateFormat.parse(endDateStr);
			Date startDate = dateFormat.parse(startDateStr);

			Float startTechSkill = 0f;
			Float endTechSkill = 05f;
			Float startOfCommRating = 0f;
			Float endOfCommRating = 05f;
			Float startOfPannaRating = 0f;
			Float endOfPannaRating = 100f;

			// FIXME
			if ("Panna Live".equals(interviewMode)) {
				interviewMode = "mlive";
			}
			int page = input.getInt(Constants.PAGE_NUMBER);
			int size = input.getInt(Constants.PAGE_SIZE);

			PageRequest pageRequest = new PageRequest(page, size, Sort.Direction.DESC,
					AConstants.INTERVIEW_DATE.getValue(), AConstants.TIME_SLOT.getValue());
			Page<InterviewsView> list = interviewsViewRepository.getCompletedInterviews(interviewPositionList,
					startTechSkill, endTechSkill, startOfCommRating, endOfCommRating, startOfPannaRating,
					endOfPannaRating, startDate, endDate, clientName, interviewMode, jobId, searchText, recruiterId, interviewerId,
					orgId, pageRequest);

			log.info("completed interviews size: {}", list.getTotalElements());

			for (InterviewsView interviewsView : list) {
				InterviewsViewDTO dto = (InterviewsViewDTO) ObjectConverter.convert(interviewsView,
						new InterviewsViewDTO());
				dto.setInterviewDate(dateFormat.format(interviewsView.getInterviewDate()));
				dto.setEncryptedMail(EncryptDecryptUtil
						.getEncryptKey(StringUtils.isNotBlank(dto.getEmailAddress()) ? dto.getEmailAddress() : ""));
				interviewsDTOList.add(dto);
				
				dto.setPhoneNumber(formatMobileNumber(dto.getPhoneNumber()));
			}

			obj.put("tableData", interviewsDTOList);
			obj.put("totalElements", list.getTotalElements());
			obj.put("totalPages", list.getTotalPages());

		} catch (Exception e) {
			log.error("Exception in getCompletedInterviews service : ", e);
		}

		return obj;
	}
}
