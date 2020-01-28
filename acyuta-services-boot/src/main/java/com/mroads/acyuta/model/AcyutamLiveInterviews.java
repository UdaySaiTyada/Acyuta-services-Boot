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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Scope;

import lombok.Data;

/**
 * @author Mahi K
 *
 */
@Entity
@Table(name = "panna_Acyuta_mLive_Interviews")
@Scope("prototype")
@DynamicUpdate(value = true)
@Data
@SuppressWarnings({"squid:S1068","all"})
public class AcyutamLiveInterviews implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "acyutamLiveId")
	private BigInteger acyutamLiveId;

	@Column(name = "acyutaInterviewsId")
	private BigInteger acyutaInterviewsId;

	@Column(name = "mLiveScheduleId")
	private BigInteger mLiveScheduleId;

	@Column(name = "mlivePanelSerialno")
	private BigInteger mlivePanelSerialno;

	@Column(name = "mLiveInterviewerLink")
	private String mLiveInterviewerLink;

	@Column(name = "mLiveFeedbackLink")
	private String mLiveFeedbackLink;

	@Column(name = "sessionId")
	private String sessionId;

	@Column(name = "mLiveStatus")
	private String mLiveStatus;

	@Column(name = "organizationId")
	private BigInteger organizationId;

	@Column(name = "updatedDate")
	private Date updatedDate;

	@Column(name = "updatedBy")
	private BigInteger updatedBy;

	@Column(name = "createdDate")
	private Date createdDate;

	@Column(name = "createdBy")
	private BigInteger createdBy;

	@Column(name = "mLivereportURL")
	private String mLivereportURL;

}
