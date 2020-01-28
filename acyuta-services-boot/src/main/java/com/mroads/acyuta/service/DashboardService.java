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

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.dto.DashboardDTO;
import com.mroads.acyuta.dto.RecruitersStatsDTO;
import com.mroads.acyuta.model.DashBoardRecruiters;
import com.mroads.acyuta.repository.DashBoardRepository;

/**
 * @author Kishan.G
 * 
 */

@Service
public class DashboardService {

	@Autowired
	private DashBoardRepository dashBoardRepository;

	private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	ObjectMapper mapper = new ObjectMapper();
	SimpleDateFormat dateFormatToView = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

	Date endDate = null;
	Date startDate = null;

	public DashboardDTO onLoad(BigInteger organizationId, Date startDate, Date endDate) throws Exception {

		log.info("startDate: {},  endDate: {}",startDate, endDate);
		log.info("organizationId: {}",organizationId);
		
	Date satrtFrom = new Date(0);
		Integer activeJobs = dashBoardRepository.getActiveJobs(organizationId, satrtFrom, endDate);
		List<DashBoardRecruiters> recruiters = dashBoardRepository.getRecruiters(organizationId);
		List<Object> dashBoardStatistics = dashBoardRepository.
				getDashBoardStatistics(organizationId, startDate, endDate);

		List<Object> actionRequiredData = dashBoardRepository.loadDashBoardDataActionRequired(organizationId, startDate, endDate);
		
		return processDashBoardData(recruiters, activeJobs, dashBoardStatistics, actionRequiredData);

	}

	public DashboardDTO loadFilterData(BigInteger organizationId, Date startDate, Date endDate, String jobId,
			String clientName) throws Exception {

		Map<BigInteger, Long> actionRequiredMap = new HashMap<BigInteger, Long>();
		List<RecruitersStatsDTO> inActiverecruiters = new ArrayList<>();
		List<RecruitersStatsDTO> activerecruiters = new ArrayList<>();
		DashboardDTO dashboardDTO = new DashboardDTO();
		Long resumesAdded = 0L;
		Long clientSubmited = 0L;
		int closures = 0;
		Integer activeJobs = dashBoardRepository.getActiveJobs(organizationId, startDate, endDate);

		log.info("During the Filter #### startDate : {}, endDate: {} ", startDate, endDate);
		
		List<Object> actionRequiredData = dashBoardRepository.loadDashBoardDataActionRequired(organizationId, startDate, endDate);
		
		List<Object> filterData = dashBoardRepository.loadFilterData(organizationId, startDate, endDate, jobId, clientName);
		
		for (Object object : actionRequiredData) {
			String jsonInString = mapper.writeValueAsString(object);
			JSONArray json = new JSONArray(jsonInString);
			actionRequiredMap.put(BigInteger.valueOf(json.getLong(0)), json.getLong(1));
		}

		for (Object object : filterData) {
			
			RecruitersStatsDTO recruiter = new RecruitersStatsDTO();
			String jsonInString = mapper.writeValueAsString(object);
			JSONArray json = new JSONArray(jsonInString);
			
			Date lastLogIn = new Date(json.getLong(1));
			String lastLoginDateTime = dateFormatToView.format(lastLogIn);
			String lastLoginTime = timeFormat.format(lastLogIn);
			BigInteger userId = BigInteger.valueOf(json.getLong(0));
			recruiter.setLastLoginDateTime(lastLoginDateTime);
			recruiter.setLastLoginTime(lastLoginTime);
			recruiter.setRecruiterName(json.getString(3));
			recruiter.setAddedResumes(json.getLong(4));
			recruiter.setClientSubmissions(json.getLong(5));
			if(!actionRequiredMap.isEmpty() && actionRequiredMap.containsKey(userId)) {
				recruiter.setActionRequired(actionRequiredMap.get(userId)); // FIXME: 
			}
			
			resumesAdded +=json.getLong(4);
			clientSubmited +=json.getLong(5); 
			closures += json.getLong(6);
			inActiverecruiters.add(recruiter);
		}
		this.startDate = startDate;
		this.endDate = endDate;
		dashboardDTO.setJobOpenings(activeJobs);
		dashboardDTO.setClosures(closures);
		dashboardDTO.setResumesAdded(resumesAdded);
		dashboardDTO.setClientSubmissions(clientSubmited);
		dashboardDTO.setInActiveRecruiters(inActiverecruiters);
		dashboardDTO.setActiveRecruiters(activerecruiters);
	
		dashboardDTO.setStartDate(dateFormat.format(this.startDate));
		dashboardDTO.setEndDate(dateFormat.format(this.endDate));

		return dashboardDTO;
		 
	}
	
//	public DashboardDTO loadFilterData(BigInteger organizationId, Date startDate, Date endDate, String jobId,
//			String clientName) throws Exception {
//
//		List<Object> data = dashBoardRepository.loadFilterData(organizationId, startDate, endDate, jobId, clientName);
//		
//		this.startDate = startDate;
//		this.endDate = endDate;
//
//		return processDashBoardData(data, organizationId);
//	}

	DashboardDTO processDashBoardData(List<DashBoardRecruiters> recruiters, Integer activeJobs,
			List<Object> dashBoardStatistics, List<Object> actionRequiredData) {

		DashboardDTO dashboardDTO = new DashboardDTO();
		List<RecruitersStatsDTO> activeRecruiters = new ArrayList<>();
		List<RecruitersStatsDTO> inActiverecruiters = new ArrayList<>();
		Map<BigInteger, JSONObject> dashBoardMap = new HashMap<>();
		Map<BigInteger, Long> actionRequiredMap = new HashMap<BigInteger, Long>();
		this.endDate = new Date();
		this.startDate = new Date();
		Long resumesAdded = 0L;
		Long clientSubmited = 0L;
		int closures = 0;
		
		try {
			// Process the dashBoard statistics for each recruiter
			for (Object object : dashBoardStatistics) {
				String jsonInString = mapper.writeValueAsString(object);
				JSONArray json = new JSONArray(jsonInString);
				JSONObject obj = new JSONObject();
				obj.put("added", json.getLong(1));
				obj.put("submitted", json.getLong(2));
				obj.put("closures", json.getLong(3));
				obj.put("updatedDate", json.getLong(4));
				dashBoardMap.put(BigInteger.valueOf(json.getLong(0)), obj);
			}

			for (Object object : actionRequiredData) {
				String jsonInString = mapper.writeValueAsString(object);
				JSONArray json = new JSONArray(jsonInString);
				log.info("actionRequired ##### : {}", json);
				actionRequiredMap.put(BigInteger.valueOf(json.getLong(0)), json.getLong(1));
			}
			log.info("actionRequiredMap : {}", actionRequiredMap);
			for (DashBoardRecruiters dashBoardRecruiter : recruiters) {

				RecruitersStatsDTO recruiter = new RecruitersStatsDTO();
				JSONObject obj = dashBoardMap.get(dashBoardRecruiter.getUserId());

				recruiter.setRecruiterName(dashBoardRecruiter.getName());
				recruiter.setRecruiterId(dashBoardRecruiter.getUserId());
				Date lastLoginDate = dashBoardRecruiter.getLastLogin();
				String lastLoginDateTime = "";
				String lastLoginTime = "";
				if(null!=lastLoginDate) {
					 lastLoginDateTime = dateFormatToView.format(lastLoginDate);
					 lastLoginTime = timeFormat.format(lastLoginDate);
				}

				recruiter.setLastLoginDateTime(lastLoginDateTime);
				recruiter.setLastLoginTime(lastLoginTime);
				if(!actionRequiredMap.isEmpty() && actionRequiredMap.containsKey(recruiter.getRecruiterId())) {
					
					recruiter.setActionRequired(actionRequiredMap.get(recruiter.getRecruiterId()));
				}
				// Date dateNowRecruiterZone = AcyutaUtils.getZoneTime(dashBoardRecruiter.getTimeZoneId());
  
				Date dateNowRecruiterZone = AcyutaUtils.getZoneTime(dashBoardRecruiter.getTimeZoneId());
				
				// FIXME: Need to understand why we getting the lastLoginDate as null.
				if(null!=lastLoginDate) {
					long diffInMillies = dateNowRecruiterZone.getTime() - lastLoginDate.getTime();
					long lastLogInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
					if (lastLogInMinutes < 240) {
						activeRecruiters.add(recruiter);
					} else {
						inActiverecruiters.add(recruiter);
					}
				}
				
				
			
				

				if (null != obj) {
					recruiter.setAddedResumes(obj.getLong("added"));
					recruiter.setClientSubmissions(obj.getLong("submitted"));
					resumesAdded += obj.getLong("added");
					clientSubmited += obj.getLong("submitted");
					closures += obj.getLong("closures");

					// NOTE: We are saving the user status and comments in UTC.
					 Date dateINUTC = AcyutaUtils.getZoneTime("UTC");
					 long updateDateDiffInMillies = dateINUTC.getTime() - new Date(obj.getLong("updatedDate")).getTime();
					 long updateDateDiffInMinutes = TimeUnit.MINUTES.convert(updateDateDiffInMillies, TimeUnit.MILLISECONDS);
					 recruiter.setLastAction(updateDateDiffInMinutes);
				}
				
			}

			dashboardDTO.setActiveRecruiters(activeRecruiters);
			dashboardDTO.setInActiveRecruiters(inActiverecruiters);
			dashboardDTO.setJobOpenings(activeJobs);
			dashboardDTO.setResumesAdded(resumesAdded);
			dashboardDTO.setClientSubmissions(clientSubmited);
			dashboardDTO.setClosures(closures);
			dashboardDTO.setStartDate(dateFormat.format(this.startDate));
			dashboardDTO.setEndDate(dateFormat.format(this.endDate));

		} catch (Exception e) {
			log.error("Exception in processDashBoardData : ", e);
		}
		return dashboardDTO;

	}

//	DashboardDTO processDashBoardData(List<Object> data, BigInteger organizationId) {
//
//		DashboardDTO dashboardDTO = new DashboardDTO();
//		Map<Integer, Integer> actionRequiredMap = new HashMap<Integer, Integer>();
//
//		try {
//			Integer activeJobs = dashBoardRepository.getActiveJobs(organizationId, startDate, endDate);
//
//			List<Object> ActionRequiredData = dashBoardRepository.loadDashBoardDataActionRequired(organizationId);
//
//			for (Object object : ActionRequiredData) {
//				String jsonInString = mapper.writeValueAsString(object);
//				JSONArray json = new JSONArray(jsonInString);
//				actionRequiredMap.put(json.getInt(0), json.getInt(1));
//			}
//
//			List<RecruitersStatsDTO> activeRecruiters = new ArrayList<>();
//			List<RecruitersStatsDTO> inActiverecruiters = new ArrayList<>();
//			Long resumesAdded = 0L;
//			Long clientSubmited = 0L;
//			int closures = 0;
//
//			for (Object object : data) {
//
//				RecruitersStatsDTO recruiter = new RecruitersStatsDTO();
//				String jsonInString = mapper.writeValueAsString(object);
//				JSONArray json = new JSONArray(jsonInString);
//
//				recruiter.setRecruiterName(json.getString(0));
//				recruiter.setRecruiterId(json.getInt(1));
//
//				Integer resumes = json.getInt(3);
//				Integer submited = json.getInt(4);
//				Integer closuresount = json.getInt(5);
//				String timeZoneId = json.getString(7);
//				Date lastLoginDate = new Date(json.getLong(2));
//
//				String lastLoginDateTime = dateFormatToView.format(lastLoginDate);
//				String lastLoginTime = timeFormat.format(new Date(json.getLong(2)));
//
//				Date dateNowRecruiterZone = AcyutaUtils.getZoneTime(timeZoneId);
//				long diffInMillies = dateNowRecruiterZone.getTime() - lastLoginDate.getTime();
//				long lastLogInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
//
//				log.info(" :lastLoginTime ##### {}:  lastLoginDateTime:::  {}", lastLoginTime, lastLoginDateTime);
//
//				recruiter.setLastLoginTime(lastLoginTime);
//				recruiter.setLastLoginDateTime(lastLoginDateTime);
//
//				Date updatedDate = new Date(json.getLong(6));
//
//				long updateDateDiffInMillies = dateNowRecruiterZone.getTime() - updatedDate.getTime();
//				long updateDateDiffInMinutes = TimeUnit.MINUTES.convert(updateDateDiffInMillies, TimeUnit.MILLISECONDS);
//
//				recruiter.setLastAction(updateDateDiffInMinutes);
//				// recruiter.setAddedResumes(resumes);
//				// recruiter.setClientSubmissions(submited);
//				// recruiter.setActionRequired(actionRequiredMap.get(recruiter.getRecruiterId()));
//
//				if (lastLogInMinutes < 240) {
//					activeRecruiters.add(recruiter);
//				} else {
//					inActiverecruiters.add(recruiter);
//				}
//
//				resumesAdded += resumes;
//				clientSubmited += submited;
//				closures += closuresount;
//
//			}
//
//			log.info("resumesAdded :{} , clientSubmited {} : ", resumesAdded, clientSubmited);
//			dashboardDTO.setStartDate(dateFormat.format(this.startDate));
//			dashboardDTO.setEndDate(dateFormat.format(this.endDate));
//			dashboardDTO.setJobOpenings(activeJobs);
//			dashboardDTO.setResumesAdded(resumesAdded);
//			dashboardDTO.setClientSubmissions(clientSubmited);
//			dashboardDTO.setClosures(closures);
//			dashboardDTO.setActiveRecruiters(activeRecruiters);
//			dashboardDTO.setInActiveRecruiters(inActiverecruiters);
//
//		} catch (Exception e) {
//			log.error("Exception in processDashBoardData : ", e);
//		}
//		return dashboardDTO;
//
//	}

}