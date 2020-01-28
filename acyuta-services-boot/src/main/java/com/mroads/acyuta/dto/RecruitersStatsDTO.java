package com.mroads.acyuta.dto;

import java.math.BigInteger;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class RecruitersStatsDTO {
    public String recruiterName;
    public BigInteger recruiterId;
    public String lastLoginTime;
    public String lastLoginDateTime;
    public long lastAction =0;
    public long addedResumes =0;
    public long clientSubmissions =0;
    public long actionRequired =0;
	 
}