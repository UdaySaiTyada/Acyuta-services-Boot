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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroads.acyuta.model.AcyutaTemplate;
import com.mroads.acyuta.repository.AcyutaPropertiesRepository;
import com.mroads.acyuta.repository.AcyutaTemplates;

/**
 * @author Mahi.K
 * 
 */

@Service
public class AcyutaPropertiesService {

	@Autowired
	private AcyutaPropertiesRepository acyutaPropertiesRepository;
	
	@Autowired
	private AcyutaTemplates acyutaTemplates;

	private static final Logger log = LoggerFactory.getLogger(JobRequisitionService.class);

	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	ObjectMapper mapper = new ObjectMapper();
	SimpleDateFormat dateFormatToView = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

	
	

	public List<String> getActiveStatusList(String statusType, BigInteger organizationId) {
		return acyutaPropertiesRepository.findByTypeAndOrganizationId(statusType, organizationId); 
	}
	
	public List<String> getjobTitleList(String statusType) {
		return acyutaPropertiesRepository.findByType(statusType); 
	}
	
	public List<String> getActiveMailTemplateList(String statusType, BigInteger organizationId) {
		return acyutaPropertiesRepository.findByTypeAndOrganizationId(statusType, organizationId); 
	}
	
	public AcyutaTemplate getActiveMailTemplateContent(String statusType, BigInteger organizationId) {
		return acyutaTemplates.getByTemplateNameAndOrganizationId(statusType, organizationId); 
	}
	
	public AcyutaTemplate getTemplateForm(String templateName) {
		return acyutaTemplates.findByTemplateName(templateName); 
	}
	public List<String> getAcyutaProperties(String statusType, BigInteger organizationId) {
		return acyutaPropertiesRepository.findByTypeAndOrganizationId(statusType, organizationId); 
	}
	
}