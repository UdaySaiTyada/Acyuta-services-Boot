package com.mroads.acyuta.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.DateTime;

import lombok.Data;


@Entity
@Data
public class DashBoard implements Serializable{

  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String recruiter;
	@Id
    public BigInteger userId;
    public DateTime lastLogin;
    public Integer addedResumes;
    public Integer submitted;
    public Integer closures;
    public Integer actionRequired;
    
    
    public DashBoard(String recruiter, BigInteger userId, DateTime lastLogin, Integer addedResumes, Integer submitted, Integer closures, Integer actionRequired ) {
    	super();
    		this.recruiter =recruiter;
      	this.userId =userId;
      	this.lastLogin =lastLogin;
      	this.addedResumes =addedResumes;
      	this.submitted =submitted;
      	this.closures =closures;
      	this.actionRequired =actionRequired;
      	
    }
    
}
