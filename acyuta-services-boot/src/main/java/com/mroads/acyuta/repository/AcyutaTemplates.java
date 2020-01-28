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
package com.mroads.acyuta.repository;

import java.math.BigInteger;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mroads.acyuta.model.AcyutaTemplate;

/**
 * @author Mahi K
 *
 */
//@Repository("AcyutaPropertiesRepository")
@Scope("prototype")
public interface AcyutaTemplates extends JpaRepository<AcyutaTemplate, BigInteger> {


	
	public AcyutaTemplate getByTemplateNameAndOrganizationId(String templateName,BigInteger organizationId);

	public AcyutaTemplate findByTemplateId(BigInteger templateId);
	
	public AcyutaTemplate findByTemplateName(String templateName);
	
}
