package com.mroads.acyuta.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;


@Data
@Entity
public class DashBoardRecruiters {

	@Id
	private BigInteger userId ; 
	private String name;
	private Date lastLogin;  
	private String timeZoneId;
	
	public DashBoardRecruiters() {
		
	};
	public DashBoardRecruiters(String name,BigInteger userId, Date lastLogin,String timeZoneId ) {
		super();
		this.name = name;
		this.userId = userId;
		this.lastLogin =lastLogin;
		this.timeZoneId = timeZoneId;
		
		
	}
}
