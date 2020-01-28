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

package com.mroads.acyuta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author SaiRameshGupta
 * 
 * */
@Data
@Entity
@Table(name="Panna_Resume_Skill_Words")
public class Skills {
	
	@Id
	@Column(name="sno")
	@GeneratedValue
	public Long skillId; 
	@Column(name="nodeSkill")
	public String nodeSkill;
}
