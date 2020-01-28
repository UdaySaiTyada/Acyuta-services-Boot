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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mroads.acyuta.interceptor.LogInterceptor;

/**
 * @author SaiRameshGupta
 * 
 * */

@ComponentScan(basePackages= {"com.mroads.acyuta.*","com.mroads.email"})
@EntityScan(basePackages= {"com.mroads.acyuta.model","com.mroads.email.jpa.model"})
@EnableJpaRepositories(basePackages= {"com.mroads.acyuta.repository","com.mroads.email.jpa.repository"})
@SpringBootApplication
public class AcyutaServicesBootApplication
{
	@Autowired
	LogInterceptor logInterceptor;
	public static void main(String[] args) 
	{
		SpringApplication.run(AcyutaServicesBootApplication.class, args);
	}
	/**
	 * @return
	 * for adding interceptors
	 */
	@Bean
	public WebMvcConfigurerAdapter adapter()
	{
		return new WebMvcConfigurerAdapter()
		{
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor((HandlerInterceptor) logInterceptor)
						.addPathPatterns("/jobs/*")
						.addPathPatterns("/candidateProfile/*")
						.addPathPatterns("/vendor/*")
						.addPathPatterns("/gmailapi/*")
						.addPathPatterns("/interviews/*");
				super.addInterceptors(registry);
			}
		};
	}

}
