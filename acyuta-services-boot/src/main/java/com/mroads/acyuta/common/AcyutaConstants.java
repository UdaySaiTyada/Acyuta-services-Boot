package com.mroads.acyuta.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mahi K
 *
 */

public class AcyutaConstants {
	public static final String UPDATEDDATE = "updatedDate";
	public static final String LOGGED_IN_USER = "loggedInUser";
	public static final String LOG_EVENT_SERVICE_URL = "logEventServiceUrl";
	public static final String LOCATION_SUGGESTIONS_URL = "locationSuggestionsUrl";

	public static final String ROLE_ATS_SUNSET_RECRUITER = "ATSSunSetRecruiter";
	public static final String ROLE_ATS_INTERVIEWER = "ATSInterviewer";
	public static final String ROLE_ATS_RECRUITER = "atsRecruiter";
	public static final String ATS_RECRUITER = "atsRecruiter";
	public static final String RPO_RECRUITER = "RPORecruiter";
	public static final String ISSUNSETRECRUITER = "isSunSetRecruiter";
	public static final String ERROR_PAGE = "candidateTracking/errorPage";
	public static final String CANDIDATE_TRACK_PAGE = "candidateTracking/candidateList";
	public static final String EXPANDO_USER_ORGANIZATION_ID = "user-organization-id";
	public static final String ACYUTA_ATANDT_ORGANIZATION = "AT&T";
//	public static final String PANNA_STANDARD_SIGNATURE = PropsUtil.get("panna.stnadard.signature");
//	public static final String PANNA_HEADER_MAIL_TEMPLATE = PropsUtil.get("panna.header.mail.template");
//	public static final String PANNA_FOOTER_MAIL_TEMPLATE = PropsUtil.get("panna.footer.mail.template");
//	public static final String PANNA_HEADER_FOOTER_MAIL_TEMPLATE = PropsUtil.get("panna.headerfooter.mail.template");
//	public static final String CAMPAIN_THANK_YOU_MAIL_TEMPALTE = PropsUtil.get("compain.thankyou.mail.template");
//	public static final String CAMPAIN_SM_BULK_MAIL_TEMPALTE = PropsUtil.get("salesmodule.bulk.meetpanna.template");
//	public static final String CAMPAIN_THANK_YOU_MAIL_TEMPALTE_2015 = PropsUtil.get("compain.thankyou.mail.lasvegas.template");
//	public static final String CAMPAIN_THANK_YOU_MAIL_TEMPALTE_USA = PropsUtil.get("compain.thankyou.mail.orlando.template");
	public static final String MROADS = "MROADS";
	public static final String KRMROADS = "KRMROADS";
	public static final String SAMPLE_ORGANIZATION = "SAMPLE_ORGANIZATION";


	
	private static final List<String> RECRUITERS_ROLE_ARRAYLIST;
	static {
		RECRUITERS_ROLE_ARRAYLIST = new ArrayList<>();
		getRecruitersList().add("atsRecruiter");
		getRecruitersList().add("atsRecruiterAdmin");
		// getRecruitersList().add(AConstants.ROLE_ATS_SUNSET_RECRUITER.getValue());
	}

	protected static final List<String> ATT_RECRUITERS_ROLE_ARRAYLIST;
	static {
		ATT_RECRUITERS_ROLE_ARRAYLIST = new ArrayList<>();
		ATT_RECRUITERS_ROLE_ARRAYLIST.add("ATTRecruiter");
	}

	protected static final List<String> LIST_OF_USERS_ROLE_AS_INTERVIEWER;
	static {
		LIST_OF_USERS_ROLE_AS_INTERVIEWER = new ArrayList<>();
		LIST_OF_USERS_ROLE_AS_INTERVIEWER.add("ATSInterviewer");
	}
	
	protected static final Map<String, String> ARTICLE_STATUS_MULTIPLE_MAIL_CONSTANTS;
	static {
		ARTICLE_STATUS_MULTIPLE_MAIL_CONSTANTS = new HashMap<>();
		ARTICLE_STATUS_MULTIPLE_MAIL_CONSTANTS.put("MAIL_MERGE_TEMPLATE", "SENT_MAIL_MERGE_TEMPLATE");
	}
	
	
	protected static final Map<String, String> ARTICLE_STATUS_CONSTANTS;
	static {
		// FIXME: Need to check do we need all the statues.. 
		ARTICLE_STATUS_CONSTANTS = new HashMap<>();
		ARTICLE_STATUS_CONSTANTS.put(AConstants.RATE_CONFIRMATION_WITH_CANDIDATE.getValue(), "SENT_RATE_CONFIRMATION");
		ARTICLE_STATUS_CONSTANTS.put(AConstants.RATE_CONFIRMATION_WITH_CONSULTANCY.getValue(), "SENT_RATE_CONFIRMATION");
		ARTICLE_STATUS_CONSTANTS.put(AConstants.RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE.getValue(), "SENT_RIGHT_TO_REPRESENT");
		ARTICLE_STATUS_CONSTANTS.put("SEND_JOB_DESCRIPTION_TO_CANDIDATE_ON_VOICEMAIL", "SENT_JOB_DESCRIPTION_ON_VOICEMAIL");
 		ARTICLE_STATUS_CONSTANTS.put("SEND_JOBDESCRIPTION_TO_CANDIDATE_DURING_CALL", "SENT_JOB_DESCRIPTION_DURING_CALL");
 		ARTICLE_STATUS_CONSTANTS.put("SEND_JOBDESCRIPTION_TO_CONSULTANCY_DURING_CALL", "SENT_JOB_DESCRIPTION_DURING_CALL");
 		ARTICLE_STATUS_CONSTANTS.put("SEND_JOB_DESCRIPTION_TO_CONSULTANCY_ON_VOICEMAIL", "SENT_JOB_DESCRIPTION_ON_VOICEMAIL");
		
		ARTICLE_STATUS_CONSTANTS.put("CANDIDATE_REQUESTED_JOBDESCRIPTION", "SENT_CANDIDATE_REQUESTED_JOBDESCRIPTION");
		ARTICLE_STATUS_CONSTANTS.put("SEND_TOYOTA_JOBDESCRIPTION_DURING_CALL", "SENT_TOYOTA_JOBDESCRIPTION_DURING_CALL");
		ARTICLE_STATUS_CONSTANTS.put("ON_VOICEMAIL", "SENT_ON_VOICEMAIL");
		ARTICLE_STATUS_CONSTANTS.put("AUTHORIZATION_FORM", "SENT_AUTHORIZATION_FORM");
				
		ARTICLE_STATUS_CONSTANTS.put("SEND_EMAIL_RESUME", "SENT_EMAIL_RESUME");
		ARTICLE_STATUS_CONSTANTS.put(AConstants.SCHEDULE_SCREENING_INTERVIEW.getValue(), "SENT_SCHEDULE_SCREENING_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_PANNA_INTERVIEW", "SENT_SCHEDULE_PANNA_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_mLIVE_INTERVIEW", "SENT_SCHEDULE_mLIVE_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("POST_SUBMISSION_FOLLOWUP", "SENT_POST_SUBMISSION_FOLLOWUP");
		ARTICLE_STATUS_CONSTANTS.put("SHORTLIST FOR INTERVIEW", "SHORTLISTED FOR INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("FACE 2 FACE", "SCHEDULED FACE 2 FACE INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("PHONE INTERVIEW", "SCHEDULED PHONE INTERVIEW");
		
		ARTICLE_STATUS_CONSTANTS.put("REMINDER_INTERVIEW", "SENT_REMINDER_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("UPDATE_INTERVIEW", "SENT_UPDATE_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("CANCEL_INTERVIEW", "SENT_CANCEL_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_CLIENT_INTERVIEW", "SENT_SCHEDULED_CLIENT_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_CLIENT_PHONE_INTERVIEW", "SENT_SCHEDULED_CLIENT_PHONE_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_CLIENT_FACE2FACE_INTERVIEW", "SENT_SCHEDULED_CLIENT_FACE2FACE_INTERVIEW");
		ARTICLE_STATUS_CONSTANTS.put("SCHEDULE_CLIENT_VIDEOCONFERENCE_INTERVIEW", "SENT_SCHEDULE_CLIENT_VIDEOCONFERENCE_INTERVIEW");
 
		
		ARTICLE_STATUS_CONSTANTS.put("RATE_CONFIRMATION_WITH_CANDIDATE_FORM", "SENT_RATE_CONFIRMATION");
		ARTICLE_STATUS_CONSTANTS.put("RATE_CONFIRMATION_WITH_CONSULTANCY_FORM", "SENT_RATE_CONFIRMATION");
		ARTICLE_STATUS_CONSTANTS.put("RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE_FORM", "SENT_RIGHT_TO_REPRESENT");
		ARTICLE_STATUS_CONSTANTS.put("AUTHORIZATION_FORM_DIGITAL_SIGN", "SENT_AUTHORIZATION_FORM");
		ARTICLE_STATUS_CONSTANTS.put("HIRED", "REQUESTED_FOR_FUNDING_APPROVAL");

	}

	//FIXME: reduce the complexicity.
	protected static final Map<String, String> ARTICLEFORMS;
	static {
		ARTICLEFORMS = new HashMap<>();
		ARTICLEFORMS.put(AConstants.RATE_CONFIRMATION_WITH_CANDIDATE.getValue(), "RATE_CONFIRMATION_WITH_CANDIDATE_FORM");
		ARTICLEFORMS.put(AConstants.RATE_CONFIRMATION_WITH_CONSULTANCY.getValue(), "RATE_CONFIRMATION_WITH_CONSULTANCY_FORM");
		ARTICLEFORMS.put("RATE_CONFIRMATION_WITH_CONSULTANCY_FORM", "RATE_CONFIRMATION_WITH_CONSULTANCY_FORM");
		ARTICLEFORMS.put(AConstants.RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE.getValue(), "RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE_FORM");
		ARTICLEFORMS.put(AConstants.SCHEDULE_SCREENING_INTERVIEW.getValue(), "SCHEDULE_SCREENING_INTERVIEW_FORM");
		ARTICLEFORMS.put("SCHEDULE_SCREENING_INTERVIEW_NOTIFICATION", "SCHEDULE_SCREENING_INTERVIEW_NOTIFICATION");
		ARTICLEFORMS.put("AUTHORIZATION_FORM", "AUTHORIZATION_SIGN_IN_FORM");
//		ARTICLEFORMS.put("JOB_ASSIGN_NOTIFICATION", "JOB_ASSIGN_NOTIFICATION");
		ARTICLEFORMS.put("SIGNATURE_FOOTER_TEMPALTE_FORM", "SIGNATURE_FOOTER_TEMPALTE_FORM");
	}

	
	protected static final Map<String, String> ARTICLEFORMS_DISPLAY_NAMES;
	static {
		ARTICLEFORMS_DISPLAY_NAMES = new HashMap<>();
		ARTICLEFORMS_DISPLAY_NAMES.put("AUTHORIZATION_SIGN_IN_FORM_ACCEPTED", "Authorization form");
		ARTICLEFORMS_DISPLAY_NAMES.put("AUTHORIZATION_SIGN_IN_FORM_REJECTED", "Authorization form");
		ARTICLEFORMS_DISPLAY_NAMES.put("RATE_CONFIRMATION_WITH_CONSULTANCY_FORM_ACCEPTED", "Rate confirmation with consultancy");
		ARTICLEFORMS_DISPLAY_NAMES.put("RATE_CONFIRMATION_WITH_CONSULTANCY_FORM_REJECTED", "Rate confirmation with consultancy");
		ARTICLEFORMS_DISPLAY_NAMES.put("RATE_CONFIRMATION_WITH_CONSULTANCY_FORM", "Rate confirmation with consultancy");
		ARTICLEFORMS_DISPLAY_NAMES.put("AUTHORIZATION_SIGN_IN_FORM", "Authorization form");
		
		
		
	}
	
	protected static final List<String> ARTICLE_STATUS_SIGNATURE_CONSTANTS;
	static {
		ARTICLE_STATUS_SIGNATURE_CONSTANTS = new ArrayList<>();
		ARTICLE_STATUS_SIGNATURE_CONSTANTS.add(AConstants.RATE_CONFIRMATION_WITH_CANDIDATE.getValue());
		ARTICLE_STATUS_SIGNATURE_CONSTANTS.add(AConstants.RATE_CONFIRMATION_WITH_CONSULTANCY.getValue());
		ARTICLE_STATUS_SIGNATURE_CONSTANTS.add(AConstants.RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE.getValue());
		ARTICLE_STATUS_SIGNATURE_CONSTANTS.add(AConstants.SCHEDULE_SCREENING_INTERVIEW.getValue());
		ARTICLE_STATUS_SIGNATURE_CONSTANTS.add("AUTHORIZATION_FORM");
	}

	protected static final List<String> NON_REGISTERED_INTERVIEW_STATUS_LIST;
	static {
		NON_REGISTERED_INTERVIEW_STATUS_LIST = new ArrayList<>();
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("EVALUATION");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("EXPIRED");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("COMPLETED");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("DISQUALIFIED");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("ABORTED");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("REJECTED");
		NON_REGISTERED_INTERVIEW_STATUS_LIST.add("CANCELED");
	}

	protected static final List<String> CURRENT_INTERVIEW_STATUS_LIST;
	static {
		CURRENT_INTERVIEW_STATUS_LIST = new ArrayList<>();
		CURRENT_INTERVIEW_STATUS_LIST.add("INPROGRESS");
		CURRENT_INTERVIEW_STATUS_LIST.add("REGISTERED");

	}
	protected static final List<String> visaStausList;
	static {
		visaStausList = new ArrayList<>();
		visaStausList.add("F1");
		visaStausList.add("F2");
		visaStausList.add("CPT-EAD");
		visaStausList.add("OPT-EAD");
		visaStausList.add("H1B");
		visaStausList.add("H4");
		visaStausList.add("GC-EAD");
		visaStausList.add("GC");
		visaStausList.add("USC");
		visaStausList.add("L1");
		visaStausList.add("L2-EAD");
		visaStausList.add("TN1");
		visaStausList.add("TN2");
		visaStausList.add("TD");
		visaStausList.add("E3");
		visaStausList.add("E3D");
		visaStausList.add("INDIAN");
		visaStausList.add("H1B(Transfer case)");
		visaStausList.add("E3(Transfer case)");

	}

	protected static final List<String> payRateList;
	static {
		payRateList = new ArrayList<>();
		payRateList.add("W2-Hourly (with benefits)");
		payRateList.add("w2-Hourly (without benefits)");
		payRateList.add("W2-Yearly (with benefits)");
		payRateList.add("w2-Yearly(without benefits)");
		payRateList.add("1099");
		payRateList.add("c2c (All Inclusive)");
		payRateList.add("Annual Salary");

	}

	protected static final Map<String, String> trackEditableArticles;
	static {
		trackEditableArticles = new HashMap<>();
//		trackEditableArticles.put(AConstants.RATE_CONFIRMATION_WITH_CANDIDATE.getValue(), "RATE CONFIRMATION WITH CANDIDATE ");
		trackEditableArticles.put(AConstants.RATE_CONFIRMATION_WITH_CONSULTANCY.getValue(), "RATE CONFIRMATION WITH CONSULTANCY ");
		trackEditableArticles.put(AConstants.RIGHT_TO_REPRESENT_FORM_WITH_CANDIDATE.getValue(), "RIGHT TO REPRESENT FORM WITH CANDIDATE ");
//		trackEditableArticles.put("SEND_JOB_DESCRIPTION_TO_CANDIDATE_ON_VOICEMAIL", "SEND JOB DESCRIPTION TO CANDIDATE ON VOICEMAIL ");
//		trackEditableArticles.put("SEND_JOB_DESCRIPTION_TO_CONSULTANCY_ON_VOICEMAIL", "SEND JOB DESCRIPTION TO CONSULTANCY ON VOICEMAIL");
//		trackEditableArticles.put("SEND_JOBDESCRIPTION_TO_CANDIDATE_DURING_CALL", "SEND JOB DESCRIPTION DURING CALL ");
//		trackEditableArticles.put("SEND_JOBDESCRIPTION_TO_CONSULTANCY_DURING_CALL", "SEND JOB DESCRIPTION TO CONSULTANCY DURING CALL");
		
		trackEditableArticles.put("CANDIDATE_REQUESTED_JOBDESCRIPTION", "CANDIDATE REQUESTED JOBDESCRIPTION");
		trackEditableArticles.put("SEND_TOYOTA_JOBDESCRIPTION_DURING_CALL", "SEND TOYOTA JOBDESCRIPTION DURING CALL");
		trackEditableArticles.put("ON_VOICEMAIL", "ON VOICEMAIL");
		trackEditableArticles.put("AUTHORIZATION_FORM", "AUTHORIZATION FORM");
		trackEditableArticles.put("SEND_EMAIL_RESUME", "SEND EMAIL RESUME");
		trackEditableArticles.put("SCHEDULE_SCREENING_INTERVIEW", "SCHEDULE SCREENING INTERVIEW");
		trackEditableArticles.put("POST_SUBMISSION_FOLLOWUP", "POST SUBMISSION FOLLOWUP");
	}

	protected static final Map<String, String> vendor_articleStatusConstants;
	static {
		vendor_articleStatusConstants = new HashMap<>();
		vendor_articleStatusConstants.put("GENERIC_TEMPLATE", "SENT GENERIC TEMPLATE MAIL");
	}

	public static List<String> getListOfUsersRoleAsInterviewer() {
		return LIST_OF_USERS_ROLE_AS_INTERVIEWER;
	}

	public static Map<String, String> getTrackEditableArticles() {
		return trackEditableArticles;

	}

	public static Map<String, String> getArticleStatusConstants() {
		return ARTICLE_STATUS_CONSTANTS;
	}
	
	public static Map<String, String> getArticleStatusMultipleMailConstants() {
		return ARTICLE_STATUS_MULTIPLE_MAIL_CONSTANTS;
	}
	
	public static Map<String, String> getArticleForms() {
		return ARTICLEFORMS;
	}

	public static List<String> getArticleStatusSignatureConstants() {
		return ARTICLE_STATUS_SIGNATURE_CONSTANTS;
	}

	public static List<String> getNonRegisteredInterviewStatusList() {
		return NON_REGISTERED_INTERVIEW_STATUS_LIST;
	}
	public static List<String> getCurrentInterviewStatusList() {
		return CURRENT_INTERVIEW_STATUS_LIST;
	}
	
	public static Map<String, String> getVendorArticleStatusConstants() {
		return vendor_articleStatusConstants;
	}

	public static List<String> getRecruitersList() {
		return RECRUITERS_ROLE_ARRAYLIST;
	}
	
	public static Map<String, String> getTemplateDisplayNames() {
		return ARTICLEFORMS_DISPLAY_NAMES;
	}
	
	

	/**
	 * default test method
	 */
	public void test() {
		// method 
	}
}
