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
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mroads.acyuta.model.AcyutaProperties;

/**
 * @author Mahi K
 *
 */
//@Repository("AcyutaPropertiesRepository")
@Scope("prototype")
public interface AcyutaPropertiesRepository extends JpaRepository<AcyutaProperties, BigInteger> {

	/**
	 * @param propertyId
	 * @return
	 */
	public AcyutaProperties findByPropertyId(BigInteger propertyId);

	/**
	 * @param type
	 * @param organizationId
	 * @return
	 */
	@Query("SELECT DISTINCT p.value FROM AcyutaProperties p WHERE p.type = :type and p.organizationId=:organizationId")
	public List<String> findByTypeAndOrganizationId(@Param("type") String type, @Param("organizationId") BigInteger organizationId);

	/**
	 * @param type
	 * @param organizationId
	 * @return
	 */
	@Query("SELECT DISTINCT p.value FROM AcyutaProperties p WHERE p.type = :type")
	public List<String> findByType(@Param("type") String type);

	
	/**
	 * @param type
	 * @return
	 */
	@Query("SELECT DISTINCT p.value FROM AcyutaProperties p WHERE p.type = :type")
	public List<String> getAllSkills(@Param("type") String type);
	
	/**
	 * @param type
	 * @param value
	 * @param organizationId
	 * @return
	 */
	@Query("SELECT p FROM AcyutaProperties p WHERE  p.type = :type and p.value=:value and (p.organizationId=:organizationId or p.organizationId=0) group by p.value")
	public AcyutaProperties findByTypeAndValue(@Param("type") String type, @Param("value") String value,
			@Param("organizationId") BigInteger organizationId);
	
	
	
}
