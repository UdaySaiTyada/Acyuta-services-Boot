/* 
\* Copyright (c) 2017 mRoads LLC. All Rights Reserved.
* mailto:support@mroads.com
* This computer program is the confidential information and proprietary trade
* secret of mRoads LLC. Possessions and use of this program must conform
* strictly to the license agreement between the user and mRoads LLC,
* and receipt or possession does not convey any rights to divulge, reproduce,
* or allow others to use this program without specific written authorization
* of mRoads LLC.
*/
package com.mroads.acyuta.common;

/**
 * @author chiranjeevi
 *
 */
public enum AConstants {
	
	TEAM(" Team"),
	ANDEMBEDDED_TRUE ("&embedded=true"),
	ROOT_STATIC_CONTENT ("/root/static-content"),
	ARTICLE_ID ("articleId"),
	ARTICLE_CONTENT ("articleContent"),
	EMAIL ("email"),
	FOOTER_CONTENT ("footerContent"),
	BODY_CONTENT ("bodyContent"),
	FALSE ("false"),
	TRUE ("true"),
	RESUME ("resume"),
	STATE ("state"),
	EMAIL_PASSWORD ("EmailPassword"),
	YOUR_SMTP_PASSWORD("YOUR_SMTP_PASSWORD"),
	SMTP_PASSWORD("ApyOdF+p+PtezC2azZ8/P22Hb5+/OzVjt3LIuXWYNRzg"),
	CLIENT_NAME_ENABLE("clientNameEnable"),
	
	HTTP_WITH_SLASH ("http://"),
	ROLE_TESTTAKER ("TestTaker"),
	BACK_SLASH ("/"),
	HYPHEN ("-"),
	ROLE_ATS_SUNSET_RECRUITER ("ATSSunSetRecruiter"),
	ROLE_ATS_INTERVIEWER ("ATSInterviewer"),
	ATS_INTERVIEWER ("ATSInterviewer"),
	ROLE_ATS_RECRUITER ("atsRecruiter"),
	ROLE_ATS_RECRUITER_ADMIN ("atsRecruiterAdmin"),
	ROLE_SALES_MODULE ("SalesRep"),
	STATUS_ACTIVE("ACTIVE"),
	STATUS_ARCHIVED("ARCHIVED"),
	
	INTERVIEW_POSITION_ACTIVE ("ACTIVE"),
	
	
	UPDATEDDATE ("updatedDate"),
	RECRUITER ("recruiter"),
	ATS_RECRUITER ("atsRecruiter"),
	ATS_RECRUITER_ADMIN ("atsRecruiterAdmin"),
	ISSUNSETRECRUITER ("isSunSetRecruiter"),
	INTERVIEWER_SLOT_OPEN_STATUS ("OPEN"),
	INTERVIEWER_SLOT_RESERVED_STATUS ("RESERVED"),
	INTERVIEWER_SLOT_ARCHIVED_STATUS ("ARCHIVED"),
	DDMMYYYY("dd-MM-yyyy"),
	
	INTERVIEW_DATE ("interviewDate"),
	TIME_SLOT ("timeSlot"),
	
	VENDOR_STATUS_OPEN ("OPEN"),
	VENDOR_STATUS_NOT_OPEN ("NOT OPEN"),
	MASS_MAIL_VENDOR_CONTACT_VALID ("VALID"),
	MASS_MAIL_VENDOR_CONTACT_INVALID ("INVALID"),
	INTERVIEW_CONFIG_PANNA_TYPE ("PANNA_TEMPLATE"),
	INTERVIEW_CONFIG_CUSTOM_TYPE ("CUSTOM_TEMPLATE"),
	INTERVIEW_CONFIG_HYBRID_TYPE ("HYBRID_TEMPLATE"),
	ACYUTA_INTERVIEW_TYPE_IS_PANNA ("PANNA"),
	ACYUTA_INTERVIEW_TYPE_IS_SCREENING ("SCREENING"),
	SCREENING_INTERVIEW_ID ("screeningInterviewId"),
	
	PASSED_SCREENING_INTERVIEW ("PASSED_SCREENING_INTERVIEW"),
	FAILED_SCREENING_INTERVIEW ("FAILED_SCREENING_INTERVIEW"),
	SIGNATURE_FOOTER_TEMPALTE_FORM ("SIGNATURE_FOOTER_TEMPALTE_FORM"),
	VIEWED_CANDIDATE_RESUME("VIEWED_CANDIDATE_RESUME"),
	PERFORMED_TALENTPOOL_SEARCH("PERFORMED_TALENTPOOL_SEARCH"),
	SENT_SCHEDULE_mLIVE_INTERVIEW("SENT_SCHEDULE_mLIVE_INTERVIEW"),
	
	/* candidate tracking page jsp pages */
	CANDIDATE_TRACK_PAGE ("candidateTracking/candidateList"),
	ADD_NEW_CANDIDATE_DEFAULT_PAGE ("candidateTracking/addNewCandidate"),
	ADD_CANDIDATE_TRACK_PAGE ("candidateTracking/addCandidate"),
	CANDIDATE_TRACKING_HISTORY_PAGE ("candidateTracking/candidateHistory"),
	MAIL_COMPOSE_PAGE ("candidateTracking/mailTemplate"),
	ERROR_PAGE ("candidateTracking/errorPage"),
	/* current Interviews controller page jsp Pages */
	CURRENT_INTERVIEW_DEFAULT_PAGE ("myInterviewsList"),
	CURRENT_INTERVIEW_EDIT_PAGE ("editInterviewCandidate"),
	CANDIDATE_STATUS ("STATUS"),
	CANDIDATE_FINAL_STATUS ("CANDIDATE_FINAL_STATUS"),
	VISA_STATUS ("VISA_STATUS"),
	TITLE ("TITLE"),
	SKILLS ("SKILLS"),
	SKILL ("skill"),
	DEFAULT ("default"),
	ALL ("ALL"),
	
	NO_REPLY_MAIL_ID("noreply@mroads.com"),
	ROW ("row"),
	ERROR ("error"),
	ADD_JOB ("addJob"),
	LOGGED_IN_USER ("loggedInUser"),
	CANDIDATE ("candidate"),
	INTERVIEWER ("interviewer"),
	INTERVIEWER_ID ("interviewerId"),
	INTERVIEWERS_LIST ("interviewersList"),
	IS_SCREENING ("isScreening"),
	SIGNATURE_RTR_FORM ("signature/rtrForm"),
	SIGNATURE_JSON ("signatureJson"),
	NOT_POSTED ("notPosted"),
	
	
	
	CANDIDATE_ID ("candidateId"),
	ORGANIZATION_ID ("organizationId"),
	SIGNATUR_DATA("signatureData"),
	CONSULTANCY_ID("consultancyId"),
	JOB_ID ("jobId"),
	JOB_ORDER_ID ("jobOrderId"),
	STATUS ("status"),
	COMMENT ("comment"),
	CONSULTANCY ("consultancy"),
	COMPLETED ("COMPLETED"),
	SCHEDULE_SCREENING_INTERVIEW_NOTIFICATION ("SCHEDULE_SCREENING_INTERVIEW_NOTIFICATION"),
	SYSTEM_STATUS ("SYSTEM_STATUS"),
	ACYUTA_MAIL_TEMPLATE ("ACYUTA_MAIL_TEMPLATE"),
	ACYUTA_CLIENT ("ACYUTA_CLIENT"),
	CANDIDATE_NOT_ACCEPTED ("CANDIDATE NOT ACCEPTED"),
	CANDIDATE_ACCEPTED ("CANDIDATE ACCEPTED"),
	FORM_SENT ("FORM SENT"),
	DEFAULT_STATUS ("ADDED"),
	WEBSITE_ADDED_STATUS ("WEBSITE_ADDED"),
	SUNSET_DEFAULT_STATUS ("FOR_REVIEW"),
	CANDIDATE_UPDATED_STATUS ("CANDIDATE_PROFILE_UPDATED"),
	APPLIED_JOB("APPLIED_JOB"),
	REJECTED ("REJECTED"),
	RIGHT_TO_REPRESENT ("RIGHT TO REPRESENT"),
	SCHEDULE_SCREENING_INTERVIEW ("SCHEDULE_SCREENING_INTERVIEW"),
	RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE ("RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE"),
	RATE_CONFIRMATION_WITH_CONSULTANCY ("RATE_CONFIRMATION_WITH_CONSULTANCY"),
	SCHEDULE_PANNA_INTERVIEW ("SCHEDULE_PANNA_INTERVIEW"),
	SCHEDULE_MLIVE_INTERVIEW ("SCHEDULE_mLIVE_INTERVIEW"),
	SM_THANK_YOU_MAIL_TEMPALTE ("SM_THANK_YOU_MAIL_TEMPALTE"),
	MATCH_TYPE_EMAIL_MOBILE ("Email & Mobile"),
	MATCH_TYPE_EMAIL ("Email"),
	MATCH_TYPE_MOBILE ("Mobile"),
	ACYUTA_INTERVIEW_TYPE_IS_MLIVE ("mLive"),
	ACYUTA_MLIVE_INTERVIEW_CANDIDATE( AConstants.CANDIDATE.getValue()),
	ACYUTA_MLIVE_INTERVIEW_INTERVIEWER(AConstants.INTERVIEWER.getValue()),
	ACYUTA_MLIVE_MAIL_NOTIFICATION_TRUE ("true"),
	ACYUTA_MLIVE_INTERVIEW_STATUS_REGISTERED ("REGISTERED"),
	ACYUTA_MLIVE_INTERVIEW_STATUS_COMPLETED( AConstants.COMPLETED.getValue()),
	INTERVIEWER_SLOT_STATUS_COMPLETED( AConstants.COMPLETED.getValue()),
	ACYUTA_ENVIRONMENT_NAME ("ACYUTA"),
	ACYUTA_MLIVE_INTERVIEW_TEMP_STATUS_IS_RESHEDULE ("ResheduleMlive"),
	ACYUTA_MLIVE_INTERVIEW_TEMP_STATUS_IS_REASSIGN ("ReAssignMlive"),
	MLIVE_SCHEDULE_ID ("mliveScheduleId"),
	RATE_CONFIRMATION_WITH_CANDIDATE("RATE_CONFIRMATION_WITH_CANDIDATE"),
	
	/* mlive Interview Status */
	INTERVIEW_STATUS_REGISTERED ("REGISTERED"),
	INTERVIEW_STATUS_INPROGRESS ("INPROGRESS"),
	INTERVIEW_STATUS_EVALUATION ("EVALUATION"),
	INTERVIEW_STATUS_COMPLETED (AConstants.COMPLETED.getValue()),
	INTERVIEW_STATUS_EXPIRED ("EXPIRED"),
	LIVE_INTERVIEW_CANDIDATE ("candidate"),
	LIVE_INTERVIEW_INTERVIEWER ("interviewer"),
	DYNAMIC_ORG_NAME_UP ("[$ORG_NAME_UP$]"),
	TEXT_HTML ("text/html"),
	PANNA_HOST_URL ("panna.host.url"),
	DOC_GOOGLE_GVIEW_URL("https://docs.google.com/gview?url="),
	PAY_TYPE ("[\"W2-Hourly (with benefits)\",\"w2-Hourly (without benefits)\",\"W2-Yearly (with benefits)\",\"w2-Yearly(without benefits)\",\"1099\",\"c2c (All Inclusive)\",\"Annual Salary\"]"),
	;
	
	
	
	private  String value;
	
	
	
	private AConstants(String value){
		this.value=value;
	}
	
	/**
	 * @return value
	 */
	public String getValue(){
		return value;
	}
}


