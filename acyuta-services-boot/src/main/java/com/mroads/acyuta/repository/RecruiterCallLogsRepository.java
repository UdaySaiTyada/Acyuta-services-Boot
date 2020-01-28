package com.mroads.acyuta.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mroads.acyuta.model.RecruiterCallLogs;

@Repository
public interface RecruiterCallLogsRepository extends JpaRepository<RecruiterCallLogs, BigInteger>{
	
	
}
