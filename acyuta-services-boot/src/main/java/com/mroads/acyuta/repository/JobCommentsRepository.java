/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2016 mRoads LLC. All Rights Reserved.
 *
 */

package com.mroads.acyuta.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mroads.acyuta.model.JobComments;
/**
 * @author p vani sree
 *
 */

public interface JobCommentsRepository extends JpaRepository<JobComments,BigInteger> {

	List<JobComments> findByJobOrderIdAndActive(String jobOrderId,int active);
}
