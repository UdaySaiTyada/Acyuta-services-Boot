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

import com.mroads.acyuta.model.PannaSignature;

/**
 * @author chiranjeevi, Mahi K
 *
 */
//@Repository("SignatureRepository")
@Scope("prototype")
public interface SignatureRepository extends JpaRepository<PannaSignature, BigInteger> {

	/**
	 * @param candidateId
	 * @return
	 */
	@Query("SELECT p from PannaSignature p where p.candidateId=:candidateId")
	public List<PannaSignature> getRecordsWithCandidateId(@Param("candidateId") BigInteger candidateId);

	/**
	 * @param candidateId
	 * @param jobOrderId
	 * @param artilceId
	 * @return
	 */
	public PannaSignature findByCandidateIdAndJobOrderIdAndArtilceId(BigInteger candidateId, BigInteger jobOrderId, BigInteger artilceId);
	
	/**
	 * @param candidateId
	 * @param jobOrderId
	 * @param artilceTitle
	 * @return
	 */
	public PannaSignature findByCandidateIdAndJobOrderIdAndArtilceTitle(BigInteger candidateId, BigInteger jobOrderId, String artilceTitle);
	

	/**
	 * @param candidateId
	 * @param jobOrderId
	 * @return
	 */
	public List<PannaSignature> findByCandidateIdAndJobOrderId(BigInteger candidateId, BigInteger jobOrderId);

	/**
	 * @param signature
	 * @return
	 */
	public PannaSignature save(PannaSignature signature);


	/**
	 * @param candidateId
	 * @param jobOrderId
	 * @param interviewId
	 * @return
	 */
	@Query("SELECT p FROM PannaSignature p WHERE p.candidateId=:candidateId and p.jobOrderId =:jobOrderId and p.interviewId=:interviewId")
	public List<PannaSignature> findByCandidateIdAndJobOrderIdAndinterviewId(@Param("candidateId") BigInteger candidateId, @Param("jobOrderId") BigInteger jobOrderId,
	        @Param("interviewId") BigInteger interviewId);
}
