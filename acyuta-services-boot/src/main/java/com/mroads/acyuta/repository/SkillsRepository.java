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

import java.util.List;

/*
 * @author SaiRameshGupta
 * 
 * */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.Skills;
/**
 * 
 * @author SaiRameshGupta
 *
 */
@Repository
public interface SkillsRepository extends JpaRepository<Skills, Long>{
	@Query("SELECT DISTINCT nodeSkill FROM Skills  where nodeSkill!='  '")
	public List<String> findDistinctNodeSkill();
}
