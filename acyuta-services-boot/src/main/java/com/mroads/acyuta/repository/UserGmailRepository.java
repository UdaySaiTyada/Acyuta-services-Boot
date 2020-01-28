/* 
* Copyright (c) 2017 mRoads LLC. All Rights Reserved.
* mailto:support@mroads.com
* This computer program is the confidential information and proprietary trade
* secret of mRoads LLC. Possessions and use of this program must conform
* strictly to the license agreement between the user and mRoads LLC,
* and receipt or possession does not convey any rights to divulge, reproduce,
* or allow others to use this program without specific written authorization
* of mRoads LLC.*/


package com.mroads.acyuta.repository;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.UserGmail;


/**
 * @author praveenkatakam
 *
 */
@Repository("UserGmailRepository")
@Scope("prototype")
public interface UserGmailRepository extends JpaRepository<UserGmail, Long> {


	/**
	 * @param email
	 * @return
	 */
	public UserGmail findByEmail(String email);
	
}
