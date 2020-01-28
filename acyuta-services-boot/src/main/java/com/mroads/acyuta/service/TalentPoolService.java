package com.mroads.acyuta.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.AcyutaCandidateDTO;
import com.mroads.acyuta.model.AcyutaCandidate;
import com.mroads.acyuta.model.TalentPoolView;
import com.mroads.acyuta.repository.AcyutaCandidateRepository;
import com.mroads.acyuta.repository.JobOrderRepository;
import com.mroads.acyuta.repository.TalentPoolViewRepository;

@Service
public class TalentPoolService {

	private static final Logger log = LoggerFactory.getLogger(TalentPoolService.class);

	@Autowired
	private AcyutaCandidateRepository acyutaCandidateRepository;
	
	@Autowired
	private TalentPoolViewRepository talentPoolViewRepository;
	
	@Autowired
	private JobOrderRepository jobOrderRepository;
	
	
//	private static DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
//	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	public JSONObject getCandidateListForRecruiter(JSONObject candidateJSON) throws JSONException {

		JSONObject obj = new JSONObject();
		
		BigInteger organizationId = new BigInteger(candidateJSON.getString("organizationId"));

		Page<AcyutaCandidate> candidatesList =  null;
		List<AcyutaCandidateDTO> candidateDTOList = new  ArrayList<AcyutaCandidateDTO>();
		 
		//FIXME: Use constants.
		BigInteger recruiterId = new BigInteger(candidateJSON.getString("recruiterId"));
		Boolean isRecruiterAdmin = candidateJSON.has(Constants.IS_RECRUITER_ADMIN)? candidateJSON.getBoolean(Constants.IS_RECRUITER_ADMIN):false;

		int page = candidateJSON.getInt(Constants.PAGE_NUMBER);
		int size = candidateJSON.getInt(Constants.PAGE_SIZE);

		PageRequest pageRequest = new PageRequest(page, size);
		
		//If logged in recruiter role is Recruiter Admin, we have to show all the candidates for the particular organization
		if(isRecruiterAdmin) {
			log.info("getCandidateListForRecruiter is requested by Recruiter Admin: ");
			 candidatesList = acyutaCandidateRepository.getCandidateList(organizationId,pageRequest);
			  
		}else {
			log.info("getCandidateListForRecruiter is requested by Recruiter: ");
			 candidatesList = acyutaCandidateRepository.getCandidateListByRecruiter(recruiterId, organizationId,pageRequest);
		}
		
		for (AcyutaCandidate candidate : candidatesList) {
			AcyutaCandidateDTO dto = (AcyutaCandidateDTO) ObjectConverter.convert(candidate, new AcyutaCandidateDTO());
			candidateDTOList.add(dto);
		}
		log.info("candidatesList size :{} ",candidatesList.getSize());
		log.debug("candidatesList size :{} ",candidatesList);
		//FIXME: Use constants.
		obj.put("tableData", candidateDTOList);
		obj.put("totalElements", candidatesList.getTotalElements());
		obj.put("totalPages", candidatesList.getTotalPages());

		return obj;
	}
	
	public String updateBookMarkField(JSONObject inputJSON) {
		
		String response = "failure";
		try {
			//FIXME: Use constants.
			Boolean isBookMarked = inputJSON.has("isBookMarked") ? inputJSON.getBoolean("isBookMarked"):false;
			BigInteger candidateId = new BigInteger(inputJSON.getString(JobOrderConstants.CANDIDATE_ID_STRING));
			
			acyutaCandidateRepository.updateBookMarkField(isBookMarked,candidateId);
			 response = "success";
		} catch (Exception e) {
			log.error("Exception in updateBookMarkField service : ", e);
		}
		return response;
	}
	
	public List<String> getConsultancyName(BigInteger organizationId) {
		log.info("In getConsultancyName service with organizationId = >{}<", organizationId);
		return acyutaCandidateRepository.findConsultancyName(organizationId);
	}
	//FIXME:Move it common services...
	public List<String> getCandidateStatus(BigInteger organizationId) {
		log.info("In getCandidateStatus service with organizationId = >{}<", organizationId);
		return acyutaCandidateRepository.findCandidateStatus(organizationId);
	}
	
	//FIXME:Move it common services...
	public List<String> getClientList(BigInteger organizationId, String clientName) {
		log.info("In getClientList service with organizationId = {} , clientName ={}", organizationId,clientName);
		return jobOrderRepository.findByClientName(organizationId,clientName);
	}
	
	
	public JSONObject talentPoolFilters(String body) {

		JSONObject obj = new JSONObject();
		
		try {
			JSONObject input=new JSONObject(body);
			log.info("getting all the parameters for filtering");
			
			String startDate= input.getString(Constants.START_DATE_STRING);
			String endDate=input.getString(Constants.END_DATE_STRING);  
			String candidateTitle = input.getString("candidateTitle");
			String email = input.getString(Constants.EMAIL_ADDRESS);
			String consultancyName = input.getString("consultancyName");
			String client=input.getString("clientName");
			String jobTitle = input.getString(JobOrderConstants.JOB_TITLE_STRING);
			String location=(input.getString(Constants.LOCATION_STRING));
			String recruiterName = input.getString("recruiterName");
			String status= input.getString("candidateStatus");
			
			//FIXME: Use constants....
			Float startTechRating = Float.parseFloat( input.getString("startTechRating"));
			Float endTechRating = Float.parseFloat( input.getString("endTechRating"));
			Float startCommRating = Float.parseFloat( input.getString("startCommRating"));
			Float endCommRating = Float.parseFloat( input.getString("endCommRating"));
			Float startPannaScore = Float.parseFloat( input.getString("startPannaScore"));
			Float endPannaScore = Float.parseFloat( input.getString("endPannaScore"));
			
		
			String recruiterId = input.getString("recruiterId");
		//	BigInteger recruiterId =new BigInteger(recruiterIdString);
			
			String organizationId=input.getString(JobOrderConstants.ORGANIZATION_ID_STRING);
			
			BigInteger orgId =new BigInteger(organizationId);
			log.info("convering into date format");
//			Date fDate="Invalid Date".equals(startDate)?format.parse("01-01-1900 00:00:00"):format.parse(startDate + " 00:00:00");
//			Date tDate="Invalid Date".equals(endDate)?format.parse("01-01-2900 23:59:59"):format.parse(endDate + " 23:59:59");
//			
			
			Date tDate = dateFormat.parse(endDate);
			Date fDate = dateFormat.parse(startDate);

			Integer page=input.getInt(Constants.PAGE_NUMBER);
			Integer size=input.getInt(Constants.PAGE_SIZE);
			PageRequest pageRequest=new PageRequest(page,size);
			
			
			
			log.info("Filtering jobs using FromDate = {}, ToDate = {}, candidateTitle={},email = {}, consultancyName={}"
					+", client = {}, jobTitle ={}, location = {}, recruiterName = {}, recuiterId ={},status = {},"
					+ "startTechRating ={},endTechRating ={},startCommRating ={}, "
					+ "endCommRating ={},startPannaScore ={},endPannaScore ={},organizationId = {}, pageRequest = {}",
					fDate,tDate,candidateTitle,email,consultancyName,
					client,jobTitle,location,recruiterName,recruiterId,status,startTechRating,
					endTechRating,startCommRating,endCommRating,startPannaScore,endPannaScore,orgId,pageRequest);

			Page<TalentPoolView> candidateList = talentPoolViewRepository.findfilteredlist(fDate,tDate,candidateTitle,email,consultancyName,
					client,jobTitle,location,recruiterName,recruiterId,status,startTechRating,endTechRating,startCommRating,endCommRating,startPannaScore,
					endPannaScore,orgId,pageRequest);

		//	log.info("candidateList: {}",candidateList);
			JSONArray jobsArray=new JSONArray();
			
			for(TalentPoolView talentPoolView : candidateList) {
				 ObjectMapper mapper = new ObjectMapper();
				String data = mapper.writeValueAsString(talentPoolView);
				JSONObject jobJson=new JSONObject(data);
				jobsArray.put(jobJson);
				//FIXME: Print in DEBUG statement..
				log.info("talentPoolView: {}",talentPoolView);
			}
			//FIXME: Print in DEBUG statement..
			log.info("jobsArray: {}",jobsArray);
			obj.put("tableData", jobsArray);
			obj.put("totalElements", candidateList.getTotalElements());
			obj.put("totalPages", candidateList.getTotalPages());

			return obj;
		} catch (Exception e) {
			log.error("Error occurred when filtering jobs from db ",e);
		}

		return null;
	}
}
