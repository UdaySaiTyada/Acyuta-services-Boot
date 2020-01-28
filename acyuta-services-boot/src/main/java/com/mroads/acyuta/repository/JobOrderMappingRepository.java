package com.mroads.acyuta.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.JobOrderMapping;

/**
 * @author Sreekanth
 *
 */
@Repository
public interface JobOrderMappingRepository extends JpaRepository<JobOrderMapping, BigInteger> {
		
}
