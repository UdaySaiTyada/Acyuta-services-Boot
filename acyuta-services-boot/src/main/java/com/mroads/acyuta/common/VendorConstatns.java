package com.mroads.acyuta.common;

import java.util.HashMap;
import java.util.Map;

public class VendorConstatns {

	
 
	
	public static final String IS_FUNDING_APPROVED="isFundingApproved";
	public static final String HIRED_NOTES="hiredNotes";
	public static final String TIER1_VENDOR="tier1";
	public static final String TIER2_VENDOR="tier2";
	
	
	
	
	
	protected static final Map<String, String> VMS_MAILTEMPLATES;
	static {
		// FIXME: Need to check do we need all the statues.. 
		VMS_MAILTEMPLATES = new HashMap<>();
 
		VMS_MAILTEMPLATES.put("SCHEDULE_PANNA_INTERVIEW", "VMS_PANNA_INTERVIEW_SCHEDULED");
		
		VMS_MAILTEMPLATES.put("PHONE INTERVIEW", "PHONE INTERVIEW");
		VMS_MAILTEMPLATES.put("PHONE INTERVIEW_SUB", "Phone Interview request for ");
		
		VMS_MAILTEMPLATES.put("FACE 2 FACE", "FACE 2 FACE");
		VMS_MAILTEMPLATES.put("FACE 2 FACE_SUB", "Face To Face Interview request for ");

		
		VMS_MAILTEMPLATES.put("REJECTED", "VMS_CANDIDATE_REJECTED");
		VMS_MAILTEMPLATES.put("REJECTED_SUB", "Feedback for ");
		
		
		VMS_MAILTEMPLATES.put("HIRED", "REQUEST_FOR_FUNDING_APPROVAL");
		VMS_MAILTEMPLATES.put("REQUESTED_FOR_FUNDING_APPROVAL", "REQUEST_FOR_FUNDING_APPROVAL");
		
		VMS_MAILTEMPLATES.put("hrManagerId_REQUESTED_FOR_FUNDING_APPROVAL", "REQUEST_FOR_FUNDING_APPROVAL_TSM");
		VMS_MAILTEMPLATES.put("hrManagerId_HIRED", "REQUEST_FOR_FUNDING_APPROVAL_TSM");
		
		VMS_MAILTEMPLATES.put("TIME_SLOTS_NOT_SUITABLE", "VMS_TIME_SLOTS_NOT_SUITABLE");
		VMS_MAILTEMPLATES.put("HIRED_SUB", "Request for funding approval");
		VMS_MAILTEMPLATES.put("TIME_SLOTS_NOT_SUITABLE_SUB", "Time slots are not suitable for ");
		
		
		VMS_MAILTEMPLATES.put("FUNDING_APPROVED", "VMS_FUNDING_APPROVED");
		VMS_MAILTEMPLATES.put("FUNDING_NOT_APPROVED", "VMS_FUNDING_NOT_APPROVED");
		
		VMS_MAILTEMPLATES.put("INITIATE_ONBOARDING", "VMS_INITIATE_ONBOARDING");
		VMS_MAILTEMPLATES.put("INITIATE ONBOARDING", "VMS_INITIATE_ONBOARDING");
		VMS_MAILTEMPLATES.put("Hilton Worldwide Headquarters-McLean", "VMS_INITIATE_ONBOARDING_MCLEAN");
		VMS_MAILTEMPLATES.put("Dallas Regional Office-Colonnade", "VMS_INITIATE_ONBOARDING_DALLAS");
		VMS_MAILTEMPLATES.put("HRCC-Dallas", "VMS_INITIATE_ONBOARDING_DALLAS");
		VMS_MAILTEMPLATES.put("Glasgow CADR-UK", "VMS_FUNDING_APPROVED-UK");
		VMS_MAILTEMPLATES.put("Hilton Hotel Operations Center-Memphis", "VMS_INITIATE_ONBOARDING_MEMPHIS");
		
		
		
	}
	
	
	protected static final Map<String, String> VMS_MAIL_RECIPIENT_MAP;
	static {
		// FIXME: Need to check: do we need all the statues.. 
		VMS_MAIL_RECIPIENT_MAP = new HashMap<>();
 
		VMS_MAIL_RECIPIENT_MAP.put("PHONE INTERVIEW", "vendorUserId,hrManagerId,hiringManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("TIME_SLOTS_NOT_SUITABLE", "vendorUserId,hrManagerId,hiringManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("FACE 2 FACE", "vendorUserId,hrManagerId,hiringManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("REJECTED", "vendorUserId,hrManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("HIRED", "hrManagerId,accountManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("REQUESTED_FOR_FUNDING_APPROVAL", "hrManagerId,accountManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("hrManagerId_HIRED", "hrManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("REQUEST_FOR_FUNDING_APPROVAL", "accountManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("FUNDING_APPROVED", "hrManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("FUNDING_NOT_APPROVED", "hrManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("CANDIDATE WITHDRAWN FROM JOB", "hrManagerId,hiringManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("INITIATE_ONBOARDING", "hiringManagerId");
		VMS_MAIL_RECIPIENT_MAP.put("SCHEDULE_PANNA_INTERVIEW", "hrManagerId,vendorUserId");
		
		
		
 

	}
	
	
	
	public static Map<String, String> getMailTemplatesMap() {
		return VMS_MAILTEMPLATES;
	}
	
	
	public static Map<String, String> getMailRecipientMap() {
		return VMS_MAIL_RECIPIENT_MAP;
	}
	
}
