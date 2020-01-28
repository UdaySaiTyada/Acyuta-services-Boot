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

package com.mroads.acyuta.controller;
/*
 * @author Kishan.G
 * 
 * */

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mroads.acyuta.common.AcyutaUtils;
import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.dto.DashboardDTO;
import com.mroads.acyuta.service.DashboardService;

@CrossOrigin
@RestController
@RequestMapping(path = "/dashboard")
public class DashboardController {

	private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private DashboardService dashboardService;

	private static DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

	@GetMapping(path = "/onLoad")
	public ResponseEntity<DashboardDTO> onLoad(HttpServletRequest request) throws Exception {

		
		Date startDate = AcyutaUtils.getTodayStartDate();
		Date endDate = new Date();
		
		String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		String userIdString = request.getHeader(JobOrderConstants.USER_ID_STRING);
		log.info("userIdString :{}", userIdString + ", orgIdString : {}" + orgIdString);
		
		DashboardDTO dashboardDTO = dashboardService.onLoad(new BigInteger(orgIdString), startDate, endDate );
		ResponseEntity<DashboardDTO> response = new ResponseEntity<DashboardDTO>(dashboardDTO, HttpStatus.OK);
		return response;
	}

	@GetMapping(path = "/filterData")
	public ResponseEntity<DashboardDTO> filterData(HttpServletRequest request) throws Exception {
		
		DashboardDTO dashboardDTO = null;
		String orgIdString = request.getHeader(JobOrderConstants.ORGANIZATION_ID_STRING);
		String jobId = request.getHeader(JobOrderConstants.JOB_ID_STRING);
		String clientName = request.getHeader(JobOrderConstants.CLIENT_NAME_STRING);

		String startDateStr = request.getHeader(JobOrderConstants.START_DATE_STRING);
		String endDateStr = request.getHeader(JobOrderConstants.END_DATE_STRING);

		Date endDate = new Date();
		Date startDate = new Date(0);
		
		if (StringUtils.isNotEmpty(endDateStr)) {
			endDate = dateFormat.parse(endDateStr);
		}

		if (StringUtils.isNotEmpty(startDateStr)) {
			startDate = dateFormat.parse(startDateStr);
		}

		log.info("startDate :{}  orgIdString : {}", startDate, endDate);
		log.info("userIdString :{}", jobId + ", orgIdString : {}" + clientName);

		if(null!=clientName && StringUtils.isNotEmpty(clientName) ) {
			  dashboardDTO = dashboardService.loadFilterData(new BigInteger(orgIdString), startDate, endDate, jobId, clientName);
		}else {
			  dashboardDTO = dashboardService.onLoad(new BigInteger(orgIdString),startDate, endDate );
		}
		
		ResponseEntity<DashboardDTO> response = new ResponseEntity<DashboardDTO>(dashboardDTO, HttpStatus.OK);
		return response;

	}
}