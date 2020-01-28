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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mroads.acyuta.common.Constants;
import com.mroads.acyuta.common.ObjectConverter;
import com.mroads.acyuta.dto.JobOrderDTO;
import com.mroads.acyuta.dto.UserDto;
import com.mroads.acyuta.model.JobOrder;
import com.mroads.acyuta.model.User;
import com.mroads.acyuta.repo.JobOrderRepository;
import com.mroads.acyuta.repo.SkillsRepository;
import com.mroads.acyuta.repo.UserRepository;



/**
 * @author SaiRameshGupta,vanisree
 * 
 * */
 
@Service
public class JobRequisitionService 
{
	@Autowired
	private SkillsRepository skillsRepository;
	
	@Autowired
	public UserRepository userrepository;
	
	@Autowired
	private JobOrderDTO jobOrderDTO=new JobOrderDTO();
	
	@Autowired
	private JobOrderRepository jobOrderRepository;
	
	/*
	 * @return List<String>
	 * */
	
	public List<String> getSkills()
	{
		List<String> skills=skillsRepository.findDistinctNodeSkill();
		return skills;
	}
	
	
	
	public List<UserDto> getUsersListByRoleNameAndOrgId(List<String> roleNamesList, BigInteger organizationId) 
	{
		List<UserDto> resultDTOList = new ArrayList<>();
		List<User> userList = userrepository.getUsersListByRoleNameAndOrgId(roleNamesList, organizationId);
		for (User user : userList) 
		{
			UserDto resultDTO = new UserDto();
			resultDTOList.add((UserDto) ObjectConverter.convert(user, resultDTO));
		}
		return resultDTOList;
	}
	
	public Map<String, String> getRecruiters() 
	{
		Map<String, String> userNames = new HashMap<>();
		List<UserDto> recruitersList = getUsersListByRoleNameAndOrgId(Constants.RECRUITERS_ROLE_ARRAYLIST,new BigInteger("44730"));
//		String s=Stringu
		//	for (UserDto recruiter : recruitersList) 
		//	{
//			String userFullName = StringUtils.isBlank(recruiter.getFirstName()) ? "" : recruiter.getFirstName()
//					+ (StringUtils.isBlank(recruiter.getLastName()) ? "" : " " + recruiter.getLastName());
//			userNames.put(String.valueOf(recruiter.getUserId()), userFullName);
//			}
		
		return userNames;
	}
	
	/*
	 * @Param String
	 * 
	 * */
	public JobOrderDTO createNewJobInJobOrder(String body) 
	{
		//JobOrderDTO jobOrderDTO=new JobOrderDTO();
		try 
		{
			Date date=new Date();
			JSONObject input=new JSONObject(body);
			jobOrderDTO.setClientName(input.getString("client"));
			jobOrderDTO.setJobId(input.getString("jobId"));
			jobOrderDTO.setJobTitle(input.getString("jobTitle"));
			jobOrderDTO.setJobType(input.getString("jobType"));
			jobOrderDTO.setJobDuration(input.getString("duration"));
			jobOrderDTO.setJobLocation(input.getString("city")+","+input.getString("state"));
			jobOrderDTO.setPayRate(input.getString("payRate"));
			
			/*Skills is obtained as Array
			 * Use JSONArray to iterate over the array
			 * Create a comma seperated skills string */
			
			JSONArray skillsArray=input.getJSONArray("skills");
			String skills="";
			if(skillsArray.length()>1)
				for(int i=0,size=skillsArray.length();i<size;i++)
				{
					skills=skills+","+skillsArray.getString(i);
				}
			else
				skills=skillsArray.getString(0);
			
			jobOrderDTO.setSkills(skills);
			
			jobOrderDTO.setJobDescription(input.getString("jobDescription"));
			//jobOrderDTO.setJobComment(input.getString("jobComment"));
			
			/*Recruiters is obtained as Array
			 * Use JSONArray to iterate over the array
			 * Create a comma seperated recruiters string */
			JSONArray recruitersArray=input.getJSONArray("recruiters");
			String recruiters="";
			if(recruitersArray.length()>1)
				for(int i=0,size=recruitersArray.length();i<size;i++) 
				{
					recruiters=recruiters+","+recruitersArray.getString(i);
				}
			else
				recruiters=recruitersArray.getString(0);
			
			jobOrderDTO.setAssignedRecruiters(recruiters);
			
			// TODO fix the timezone problem
			//set current time zone to logged in users time zone 
			//		obtained from User_ table
			
			jobOrderDTO.setCreatedDate(new Date());
			jobOrderDTO.setUpdatedDate(new Date());
			jobOrderDTO.setStatus(Constants.JOB_ACTIVE_STATUS);
			jobOrderDTO.setCompanyId(input.getLong("companyId"));
			jobOrderDTO.setOrganizationId(input.getLong("organizationId"));
			jobOrderDTO.setCreatedBy(input.getLong("userId"));
			jobOrderDTO.setUpdatedBy(input.getLong("userId"));
			
			JobOrder newJob=(JobOrder) ObjectConverter.convert(jobOrderDTO,JobOrder.class);
			
			JobOrder curJob=jobOrderRepository.save(newJob);
			
			jobOrderDTO=(JobOrderDTO) ObjectConverter.convert(curJob,JobOrderDTO.class);
			return jobOrderDTO;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return jobOrderDTO;
		}
		
	
	}

	public void addNewComment(String string)
	{
		// TODO Auto-generated method stub
		
	}
	
	

}

