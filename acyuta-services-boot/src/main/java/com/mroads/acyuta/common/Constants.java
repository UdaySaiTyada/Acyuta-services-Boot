/**
 * This computer program is the confidential information and proprietary trade
 * secret of mRoads LLC. Possessions and use of this program must conform 
 * strictly to the license agreement between the user and mRoads LLC,
 * and receipt or possession does not convey any rights to divulge, reproduce, 
 * or allow others to use this program without specific written authorization 
 * of mRoads LLC.
 * 
 * Copyright (c) 2015 mRoads LLC. All Rights Reserved.
 *
 */
package com.mroads.acyuta.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author Mahi K
 *
 */
public class Constants {

	
	public static final String APP_ACYUTA_ENVIRONMENT = "ACYUTA";
	public static final String NO_REPLY_EMAIL = "noreply@panna.ai";
	
	
	
	public static final String ROLE_ATS_SUNSET_RECRUITER = "ATSSunSetRecruiter";
	public static List<String> RECRUITERS_ROLE_ARRAYLIST = new ArrayList<>(Arrays.asList("atsRecruiter", "atsRecruiterAdmin",ROLE_ATS_SUNSET_RECRUITER));
	public static final String JOB_ACTIVE_STATUS="ACTIVE";
	public static final String JOB_HALTED_STATUS="HALTED";
	public static final String JOB_DEFAULT_STATUS="DEFAULT";
	public static final String JOB_ARCHIVED_STATUS="ARCHIVED";
	public static final int JOB_COMMENT_ACTIVE_STATUS=1;
	public static final String PAGE_SIZE="size";
	public static final String PAGE_NUMBER="page";
	public static final String SKILLS="skills";
	public static final String JOBS="jobs";
	public static final String MAIL_BODY="mailBody";
	public static final String MAIL_SUBJECT="mailSubject";
	public static final String MAIL_TEMPLATE="mailTemplate";

	public static final String MAIL_LIST="emailTagsList";
	public static final String ENVIRONMENT="email.track.env";
	public static final String LOCATION_STRING="location";
	public static final String CLIENT_STRING="client";
	public static final String LOG_IN_USER_ID_STRING="logInUserId";
	public static final String START_DATE_STRING="startDate";
	public static final String END_DATE_STRING="endDate";
	
	public static final String IS_RECRUITER_ADMIN = "isRecruiterAdmin";
	public static final String IS_VENDOR_MANAGER = "isVendorManager";
	public static final String IS_ACCOUNT_MANAGER = "isAccountManager";
	public static final String IS_D3_NonTech_MANAGER = "isD3NonTechManager";
	public static final String IS_TSM_MANAGER = "isTsmManager";
	
	
	
	
	public static final String RECRUITER_MAPPING_ACTIVE_STATUS = "ACTIVE";
	public static final String RECRUITER_MAPPING_INACTIVE_STATUS = "INACTIVE";
	public static final String LOCATION_MAPPING_ACTIVE_STATUS = "ACTIVE";
	public static final String LOCATION_MAPPING_INACTIVE_STATUS = "INACTIVE";
	public static final String DELETED_RECRUITERS="recruitersDeleted";
	public static final String ADDED_RECRUITERS="addedRecruiters";
	public static final String MAPPING_ID_STRING = "mappingId";
	public static final String RECRUITER_ID_STRING = "recruiterId";
	public static final String UTC_TIME_FORMAT_STRING="UTC";
	public static final String CST_TIME_FORMAT_STRING="CST";
	public static final int APPLY_TEMPLATE=1;
	public static final int DO_NOT_APPLY_TEMPLATE=0;
	public static final String TIME_ZONE_STRING = "timeZone";
	public static final String RECRUITER_STRING = "recruiter";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String CANDIDATE_INFO_STR = "candidateInfo";
	public static final String ORGANIZATIONID_STRING = "organizationId";
	public static final String JOB_ORDER_ID_STR = "jobOrderId";
	public static final String ARCHIVED_STATUS="ARCHIVED";
	public static final String VENDOR_INFO="vendorInfo";
	
	
	public static final String STATUS_STRING="status";
	public static final String AUTO_SCHEDULE_STRING="Auto Schedule";
	public static final String MANUAL_SCHEDULE_STRING="Manual Schedule";
	public static final String JOB_SCHEDULE_TYPE_STRING = "scheduleType";

	public static final String VMS_JOB_CLOSED = "VMS_JOB_CLOSED";
	public static final String VMS_JOB_HALTED = "VMS_JOB_HALTED";
	public static final String VMS_JOB_UPDATED = "VMS_JOB_UPDATED";
	public static final String VMS_COMMENT_UPDATED = "VMS_COMMENT_UPDATED";
	public static final String VMS_JOB_ACTIVE = "VMS_JOB_ACTIVE";

	
	public static final String CANDIDATE_WITHDRAWN_FROM_JOB = "CANDIDATE WITHDRAWN FROM JOB";
	public static final String CANDIDATE_APPLIED_FROM_WEBSITE = "APPLIED FROM WEBSITE";
	public static final String APPLIED_FROM_WEBSITE_RECRUITER_NOTIFY_MAIL = "APPLIED_FROM_WEBSITE_RECRUITER_NOTIFY_MAIL";
	public static final String APPLIED_FROM_WEBSITE_CANDIDIATE_NOTIFY_MAIL = "APPLIED_FROM_WEBSITE_CANDIDIATE_NOTIFY_MAIL";
	public static final String INITIATE_ONBOARDING_STRING = "INITIATE_ONBOARDING";
	public static final String HIRED = "HIRED";
	
	
	
	public static final String VMS_CANDIDATE_WITHDRAWN_FROM_JOB = "VMS_CANDIDATE_WITHDRAWN_FROM_JOB";
	public static final String VMS_CANDIDATE_PROFILE_ADDED = "VMS_CANDIDATE_PROFILE_ADDED";
	public static final String SCHEDULED_PANNA_INTERVIEW = "SCHEDULED PANNA INTERVIEW";
	public static final String ADDED_STR = "ADDED";	
	public static final String CANDIDATE_PROFILE_ADDED = "CANDIDATE_PROFILE_ADDED";
	
	public static final String HR_MANAGER_STR = "HRManager";
	public static final String VENDOR_ID_STR = "vendorId";
	public static final String REPORTING_MANAGER_STR = "reportingManager";
	
	
	
	public static final String JOB_ASSIGN_NOTIFICATION_HM = "JOB_ASSIGN_NOTIFICATION_HM";
	public static final String VMS_CANDIDATE_WITHDRAWN_NOTIFICATION = "VMS_CANDIDATE_WITHDRAWN_NOTIFICATION";
	public static final String JOB_ASSIGN_NOTIFICATION = "JOB_ASSIGN_NOTIFICATION";
	public static final String ACYUTA_JOB_UNASSIGN_NOTIFICATION = "ACYUTA_JOB_UNASSIGN_NOTIFICATION";
	public static final String VMS_SHORTLISTED_MAIL_TEMPLATE = "VMS_SHORTLISTED_NOTIFICATION";
	public static final String VMS_SATUS_CHANGE_MAIL_TEMPLATE = "VMS_SATUS_CHANGE_NOTIFICATION";
//	public static final String VMS_JOB_ASSIGN_NOTIFICATION = "VMS_JOB_ASSIGN_NOTIFICATION";
	public static final String VMS_CANDIDATE_PROFILE_UPDATION = "VMS_CANDIDATE_PROFILE_UPDATION";
	
	
	public static final String VMS_SCHEDULE_ROUND_1 = "VMS_SCHEDULE_ROUND_1";
	public static final String VMS_SCHEDULE_ROUND_2 = "VMS_SCHEDULE_ROUND_2";
	public static final String VMS_TIME_SLOTS_NOT_SUITABLE = "VMS_TIME_SLOTS_NOT_SUITABLE";
	
	public static final String FILE_UPLOAD_TO_S3 = "fileUploadToS3";
	
	public static final String DIGITAL_SIGN_IN_FORM_RECRUITER_NOTIFY_STATUS_MAIL = "DIGITAL_SIGN_IN_FORM_RECRUITER_NOTIFY_STATUS_MAIL";
	
	// Creating Constants for Current & Completed intervies

	public static final String SEARCH_TEXT = "searchText";
	public static final String INTERVIEW_TIME="interviewTime";
	public static final String ZONE="timeZone";
	public static final String DURATION_TIME="durationTime";
	public static final String INTERVIEWER_NAME="interviewerName";
	public static final String CLIENT_INTERVIEW_LOCATION="clientInterviewLocation";
	public static final String INTERVIEW_DATE="interviewDate";
}
