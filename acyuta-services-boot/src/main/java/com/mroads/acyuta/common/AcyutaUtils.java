package com.mroads.acyuta.common;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mroads.acyuta.model.AcyutaTemplate;
import com.mroads.email.dto.AuthenticationConfiguration;
import com.mroads.email.dto.CalendarParticipants;
import com.mroads.email.dto.EmailParticipant;
import com.mroads.email.dto.MessageData;
import com.mroads.email.jpa.model.PannaTemplates;
import com.mroads.email.jpa.service.TemplateJpaService;
import com.mroads.email.service.EmailService;




public class AcyutaUtils {

	private static final Logger log=LoggerFactory.getLogger(AcyutaUtils.class);
	
	public static Map<String, Long> templatesNameIdMap = new HashMap<String, Long>();
	public static Map<String,String> templatesIdNameMap = new HashMap <String,String>();
	
	public static Map<String, AcyutaTemplate> templatesMap = new HashMap<String, AcyutaTemplate>();
	public static Map<String, BigInteger> templateFormsMap = new HashMap<String, BigInteger>();
	public static Map<BigInteger, List<String>> mailTemplateListMap = new HashMap<BigInteger, List<String>>();
	public static List<String> jobTitleList = new ArrayList<String>();
	
	public static Map<BigInteger, List<String>> acyutaCleintsMap = new HashMap<BigInteger, List<String>>();

	
	public static String getEncodedValue(String id) {
		byte[] idInBytes = id.getBytes();
		try {
		return DatatypeConverter.printBase64Binary(idInBytes);
		}
		catch(Exception e) {
			
			log.error("Exception in encoding id:"+ e);
		}
		return "";
	}

	public static BigInteger getDecodedValue(String coded) {
		byte[] decoded = DatatypeConverter.parseBase64Binary(coded);

		try {
			String id = new String(decoded, "UTF-8");
			return new BigInteger(id);
		} catch (UnsupportedEncodingException e) {
			log.error("Exception in decoding id:"+ e);
		}
		
		return BigInteger.ZERO;
	}
	
	public static Date changeTimeZone(String scheduleTime, String recruiterZone, String toTimeZone) {
		Date reminderDate = null;
		try {
		//	log.info("reminderTime : "+scheduleTime);
			DateFormat formatterFrom = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			formatterFrom.setTimeZone(TimeZone.getTimeZone(recruiterZone));
			Date date = formatterFrom.parse(scheduleTime);
			
			// log.info("reminderTime:  " +scheduleTime+" ,date in UTC : "+date);
			DateFormat formatterTO = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			formatterTO.setTimeZone(TimeZone.getTimeZone(toTimeZone));
			String temp = formatterTO.format(date);


			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			reminderDate = formatter.parse(temp);
			
			// log.info("temp:  " +temp+", final reminderDate : "+reminderDate);

		} catch (Exception e) {
			log.error("Exception in changeTimeZone : ",e);
		}
		return reminderDate;
	}
	public static Date getTimeInZone(ZoneOffset zone){

		LocalDateTime localUTCDateTime = ZonedDateTime.now(zone).toLocalDateTime();
		return java.sql.Timestamp.valueOf(localUTCDateTime.truncatedTo(ChronoUnit.SECONDS));
		
		}
	
	
	public static Date getTodayStartDate(){

		  Calendar cal = Calendar.getInstance();  
	        cal.setTime(new Date());  
	        cal.set(Calendar.HOUR_OF_DAY, 0);  
	        cal.set(Calendar.MINUTE, 0);  
	        cal.set(Calendar.SECOND, 0);  
	        cal.set(Calendar.MILLISECOND, 0); 
	        return cal.getTime();
		}
	
	
	public static Date getZoneTime(String zone) throws ParseException {
		Date dateInZone =new Date();
		 try {
			SimpleDateFormat dateFormatToView=new SimpleDateFormat("MM/dd/yyyy HH:mm a");
			  dateFormatToView.setTimeZone(TimeZone.getTimeZone(zone));
			  String date =  dateFormatToView.format(new Date());
			  DateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
			  dateInZone = formatter2.parse(date);
		} catch (Exception e) {
		 log.error("Exception in getZoneTime: ",e);
		}
		 return dateInZone;
	}
	
	public static String getTemplateContent(BigInteger organizationId, String templateName) {
		String content = "";
		try {
			PannaTemplates pannaTemplate = TemplateJpaService.getTemplate(new BigInteger("0"), organizationId, "EMAIL", templateName);
			if (pannaTemplate != null) {
				content = pannaTemplate.getContent();
			} else {
				content = TemplateJpaService.getTemplate(new BigInteger("0"), BigInteger.ZERO, "EMAIL", templateName) .getContent();
			}
		} catch (Exception e) {
			log.info("Exception while fetching content for " + templateName);
		}
		return content;

	}
	
	
	
	public static void sendMail(JSONObject inputJSON, String appEnvironement){

		 try {
			List<String> toAddressList=new ArrayList<String>();
			String emailAddress = inputJSON.getString("emailAddress");
			String mailSubject = inputJSON.getString("mailSubject");
			String candidateId = inputJSON.getString("candidateId");
			String mailBody = inputJSON.has("mailBody")? inputJSON.getString("mailBody"):"";
		
			String logInUserFullName= inputJSON.has(JobOrderConstants.RECRUITER_STRING) ?
					inputJSON.getString(JobOrderConstants.RECRUITER_STRING): Constants.NO_REPLY_EMAIL;
			
			 JSONArray emailsTagList=inputJSON.getJSONArray(JobOrderConstants.MAIL_LIST);
			 for(int i=0;i<emailsTagList.length();i++) {
				 toAddressList.add(emailsTagList.getString(i));
			 }
			 log.info("inputJSON >>> toAddressList: {}, fromUser: {}",toAddressList,emailAddress);
			BigInteger organizationId = new BigInteger(inputJSON.getString("organizationId"));
			
			 AuthenticationConfiguration auth=new AuthenticationConfiguration();
			 auth.setFromUser(emailAddress);
			 EmailParticipant emailParticipant = new EmailParticipant();
			 emailParticipant.setTo(toAddressList);
			 
			 String hostEmail="";
			
			 List<CalendarParticipants> panList = new ArrayList<>();
			 hostEmail=inputJSON.has("interviewerName") ?inputJSON.getString("interviewerName"):" ";
			 CalendarParticipants ps = new CalendarParticipants();
				ps.setAttendeeName(hostEmail); 
				ps.setEmailId(hostEmail); 
				panList.add(ps);
			 
			 
			 List<String> ccList = new ArrayList<>();
			 ccList.add(emailAddress);
			 emailParticipant.setApplicationSource("ACYUTA");
			 emailParticipant.setCandidateId(candidateId);
			 emailParticipant.setCc(ccList);
			 emailParticipant.setOrganizationId(organizationId.toString());
			 emailParticipant.setOrgTeamName(logInUserFullName);
			 emailParticipant.setReplyTo(emailAddress);
			 emailParticipant.setEnvironment(appEnvironement);
			 emailParticipant.setParticipantsList(panList);
			 emailParticipant.setHostName(hostEmail);
			 
			 
			 
			 MessageData messageData = new MessageData();
			 messageData.setTemplate("NO_TEMPLATE");
			 messageData.setSubject(mailSubject);
			 messageData.setDynamicTemplateText(mailBody);
			 messageData.setHeaderTemplate("NO_TEMPLATE");
			 messageData.setFooterTemplate("NO_TEMPLATE");
			 messageData.setIsEventCancelled(false);
			 messageData.setCalendarId(candidateId);
			 messageData.setCalendarSummary(messageData.getSubject());
			 
			 if(inputJSON.has("mailTemplate") && (inputJSON.getString("mailTemplate").equals("SCHEDULE_CLIENT_PHONE_INTERVIEW") 
					 || (inputJSON.getString("mailTemplate").equals("SCHEDULE_CLIENT_FACE2FACE_INTERVIEW")
				 	 || (inputJSON.getString("mailTemplate").equals("SCHEDULE_CLIENT_VIDEOCONFERENCE_INTERVIEW")
					 || inputJSON.getString("mailTemplate").equals("UPDATE_INTERVIEW")
					 || inputJSON.getString("mailTemplate").equals(Constants.APPLIED_FROM_WEBSITE_RECRUITER_NOTIFY_MAIL)
					 || inputJSON.getString("mailTemplate").equals(Constants.APPLIED_FROM_WEBSITE_CANDIDIATE_NOTIFY_MAIL)
					 || inputJSON.getString("mailTemplate").equals("REMINDER_INTERVIEW"))))){
			 
				
			 messageData.setTemplate(inputJSON.getString("mailTemplate"));	 

			 messageData.setDynamicValue("[$CANDIDATE_NAME$]", inputJSON.has("candidateName") ? inputJSON.getString("candidateName"): " ");	 
			 messageData.setDynamicValue("[$JOBTITLE$]",inputJSON.has("jobTitle") ? inputJSON.getString("jobTitle"): " ");	
			 messageData.setDynamicValue("[$CLIENT_INTERVIEW_MODE$]",inputJSON.has("clientInterviewMode") ? inputJSON.getString("clientInterviewMode"): " ");
			 messageData.setDynamicValue("[$VIEW_PROFILE$]",inputJSON.has("viewProfileURL") ? inputJSON.getString("viewProfileURL"): " ");
			 messageData.setDynamicValue("[$CLIENT_INTERVIEW_LOCATION$]", inputJSON.has("clientInterviewLocation")?inputJSON.getString("clientInterviewLocation"):" ");
			 messageData.setDynamicValue("[$CLIENT_INTERVIEWER_NAME$]",inputJSON.has("interviewerName")?inputJSON.getString("interviewerName"):" ");
			 messageData.setDynamicValue("[$CLIENT_DURATION$]", inputJSON.has("durationTime")?inputJSON.getString("durationTime"):" ");
			 messageData.setDynamicValue("[$CLIENT_NAME$]",inputJSON.has("clientName")?inputJSON.getString("clientName"):" ");
			 messageData.setDynamicValue("[$JOBDESCRIPTION$]",inputJSON.has("jobDescription")?inputJSON.getString("jobDescription"):" ");
			 
			 
			
			 if(inputJSON.has("interviewDate") && inputJSON.has("interviewTime")) {
//				 messageData.setDynamicValue("[$INTERVIEW_DATE$]", inputJSON.getString(JobOrderConstants.INTERVIEW_DATE) + " "+
//						 inputJSON.getString("interviewTime")+" "+JobOrderConstants.TIME_ZONE_CST);	 
				 messageData.setDynamicValue("[$DATE$]", inputJSON.getString(JobOrderConstants.INTERVIEW_DATE));
				 messageData.setDynamicValue("[$TIME_SLOT$]", inputJSON.getString("interviewTime"));
				 messageData.setDynamicValue("[$ZONE$]", inputJSON.getString("timeZone"));
				 messageData.setInterviewStartDate(inputJSON.getString("interviewDate")+" "+inputJSON.getString("interviewTime")+" "+inputJSON.getString("timeZone"));
				 messageData.setCalendarLocation(inputJSON.getString("clientInterviewLocation"));	
				 messageData.setCalendarDescription(messageData.getSubject());
			 }
		
			 }
			 
			 //sender mail-id  validity should be checked as exception cannot be caught here
			 EmailService.sendEmail(auth, emailParticipant, messageData);
		} catch (Exception e) {
			log.error("Exception in send mail :", e);
		}

	}

	
	// Note:This method loads the mailTemplates  and stores in with 
	// templateName as key and primaryKey as value..
	// We this map during the signature mail template..
	public static Map<String, Long> getMap(){
		Map<String, Long> map = new HashMap<String, Long>();
		return map;
	}

	
	
	
	
}