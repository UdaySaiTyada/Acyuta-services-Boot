package com.mroads.acyuta.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class InterviewPositionsDTO  implements Serializable{
	private BigInteger interviewPositionId;
	private String interviewID;
	private String positionTitle;
	private String positionDescription;
	private String corporation;
	private String templateType;
	private String positionStatus;
	private String interviewTime;
	private BigInteger organizationId;
	private Date createdDate;
	private Date updatedDate;
	private BigInteger createdBy;
	private BigInteger updatedBy;
}
