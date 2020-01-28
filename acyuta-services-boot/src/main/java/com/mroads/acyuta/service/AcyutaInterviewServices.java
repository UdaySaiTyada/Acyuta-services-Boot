//package com.mroads.acyuta.service;
//
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.mroads.acyuta.common.AConstants;
//import com.mroads.acyuta.common.AcyutaConstants;
//import com.mroads.acyuta.common.ObjectConverter;
//import com.mroads.acyuta.dto.InterviewerSlotsDTO;
//import com.mroads.acyuta.model.InterviewerSlot;
//import com.mroads.acyuta.model.User;
//import com.mroads.acyuta.repository.InterviewerSlotsRepository;
//import com.mroads.acyuta.repository.UserRepository;
//
//
//public class AcyutaInterviewServices {
//
//	private static final Logger log=LoggerFactory.getLogger(AcyutaInterviewServices.class);
//	
//	
//	
//	
//	
//	@Autowired
//	private InterviewerSlotsRepository interviewerSlotsRepository;
//	
//	@Autowired
//	public UserRepository userRepository;
//	
//	/**
//	 * @param dateOfInterview
//	 * @param organizationId
//	 * @return timeSlotsList
//	 */
//	public List<InterviewerSlotsDTO> findByDateAndOrganization(String dateOfInterview, BigInteger organizationId) {
//
//		List<InterviewerSlotsDTO> timeSlotsList = new ArrayList<>();
//		log.debug("In findByInterviewerId : ");
//		try {
//			List<InterviewerSlot> objList = interviewerSlotsRepository.findByDateAndOrganization(dateOfInterview, organizationId);
//			for (InterviewerSlot obj : objList) {
//				timeSlotsList.add((InterviewerSlotsDTO) ObjectConverter.convert(obj, new InterviewerSlotsDTO()));
//				log.debug("Obj" + obj);
//			}
//		} catch (Exception e) {
//			log.error("findByDateAndOrganization : Exception ", e);
//		}
//		return timeSlotsList;
//	}
//	
//	/**
//	 * Getting The Interviewers List
//	 * 
//	 * @param request
//	 * @param organizationId
//	 * @return
//	 */
//	public Map<Long, String> getinterviewersList(BigInteger organizationId) {
//
//		HashMap<Long, String> interviewersMap = new HashMap<>();
//		ArrayList<String> interviewerRolesList = new ArrayList<>();
//		interviewerRolesList.add(AConstants.ROLE_ATS_INTERVIEWER.getValue());
//		try {
//			List<User> interviewsDTOList = userRepository.getUsersListByRoleNameAndOrgId(AcyutaConstants.getListOfUsersRoleAsInterviewer(), organizationId);
//			for (User interviewer : interviewsDTOList) {
//				String fullName = StringUtils.isBlank(interviewer.getFirstName()) ? "" : interviewer.getFirstName()
//						+ (StringUtils.isBlank(interviewer.getLastName()) ? "" : " " + interviewer.getLastName());
//				interviewersMap.put(interviewer.getUserId().longValue(), fullName);
//			}
//		} catch (Exception e) {
//			log.error("Exception : while getting the interviewers List ", e);
//			return new HashMap<>();
//		}
//		return interviewersMap;
//	}
//
//}
