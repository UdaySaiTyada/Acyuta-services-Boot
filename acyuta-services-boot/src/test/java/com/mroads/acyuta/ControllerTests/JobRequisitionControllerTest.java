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

package com.mroads.acyuta.ControllerTests;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.mroads.acyuta.common.JobOrderConstants;
import com.mroads.acyuta.service.JobRequisitionService;



@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages= {"com.mroads.acyuta.controller,com.mroads.acyuta.service"})
@EntityScan(basePackages= {"com.mroads.acyuta.model"})
@PropertySource(value = {"classpath:application.properties"})
public class JobRequisitionControllerTest {
	private static final Logger log=LoggerFactory.getLogger(JobRequisitionControllerTest.class);
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;

	@MockBean
	private JobRequisitionService mockService;
	
	
	private MockHttpServletRequest mockRequest;
	private MockHttpServletResponse mockResponse;
	
	@Before
	public void setup(){
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		mockRequest=new MockHttpServletRequest();
		mockResponse=new MockHttpServletResponse();
	}
	
	@Test
	public void skillsTest() throws Exception  {
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/skills").contentType(MediaType.APPLICATION_JSON)).andReturn();
		log.debug(result.getResponse().getContentAsString());
	}
	
	@Test
	public void recruitersTest() throws Exception {
		log.info("Entered recruiters test");
		mockService=Mockito.mock(JobRequisitionService.class);
		mockRequest.setParameter(JobOrderConstants.ORGANIZATION_ID_STRING,"44932");
		mockMvc.perform(MockMvcRequestBuilders.get("/recruiters").header(JobOrderConstants.ORGANIZATION_ID_STRING,"44932")).andExpect(status().is(399));
		/*List<Map<String,String>> recruiters=new ArrayList<>();
		Map<String, String> map=new HashMap<String,String>();
		map.put("Recruiter", "Test");
		recruiters.add(map);
		//when(mockService.getRecruiters(Mockito.anyString())).thenReturn(recruiters);
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/recruiters").contentType(MediaType.APPLICATION_JSON))
								.andExpect(status().is4xxClientError())
								.andReturn();
		*///assertEquals(mockService.getRecruiters(Mockito.anyString()),recruiters);
	}
}
