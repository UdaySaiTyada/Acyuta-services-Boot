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

package com.mroads.acyuta;

//import static org.assertj.core.api.Assertions.assertThat;

//import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

//import com.mroads.acyuta.repo.SkillsRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//@ComponentScan(basePackages= {"com.mroads.acyuta.controller,com.mroads.acyuta.service"})
//@PropertySource(value = {"classpath:application.properties"})
public class AcyutaServicesBootApplicationTests
{	
	@Test
	public void contextLoads() 
	{
		
	}

}
