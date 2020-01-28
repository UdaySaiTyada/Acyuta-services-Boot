package com.mroads.acyuta.gmailapi;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;



@Controller
@RequestMapping(value = "/gmailapi")
public class GoogleMailController {
	
	final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	GmailAPIService gmailAPIService;
	
	@Value("${gmail.client.redirectUri}")
	private String redirectUri;
	
	@RequestMapping(value="/test", method=RequestMethod.GET)
	@ResponseBody
	public String welcome() {
		log.info("This is test for gmail service");
		return "GMAIL_TEST_200_OK";
	}

	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
		return new RedirectView(gmailAPIService.authorize());
	}

	@RequestMapping(value = "/gmailCallback", method = RequestMethod.GET, params = "code")
	public ResponseEntity<String> oauth2Callback(@RequestParam(value = "code") String code) {
		log.info("code "+code);
		String message="<h1>Your verification is failed.Please try again after some time</h1>";
		
		try {
			gmailAPIService.saveCredentials(code);
			message="<h1>Your successfully verified your account.Please close this window.</h1>";
			message=message+"<script>"+"window.close();"+"</script>";
		} catch (Exception e) {
            log.error("Exception in callback gmail"+e);
            message=message+"<script>"+"window.close();"+"</script>";
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin
	@RequestMapping(value = "/checkGmailVerificationStatus")
	@ResponseBody
	public String checkGmailVerificationStatus(@RequestBody String body) throws JSONException  {
		log.info("In /checkEmailVerificationStatus ");
		  Boolean verificationStatus=false;
		JSONObject outPut = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data" + input);
			String loginEmail=input.getString("userEmail");
			
			verificationStatus=gmailAPIService.checkGmailVerificationStatus(loginEmail);
			outPut.put("gmailVerificationStatus",verificationStatus);
			outPut.put("message","success");
			outPut.put("status","200");
		} catch (JSONException e) {
			outPut.put("message","failure");
			outPut.put("status","400");
			outPut.put("gmailVerificationStatus",false);
			log.error("Exception due to bad JSON request in checkEmailVerificationStatus", e);
		} catch (Exception e) {
			outPut.put("gmailVerificationStatus",false);
			outPut.put("message","failure");
			outPut.put("status","500");
			log.error("Exception in /messages ", e);
		} 
       return outPut.toString();
	}
	
	
	
	
	@CrossOrigin
	@RequestMapping(value = "/messages")
	@ResponseBody
	public String getInboxMessages(@RequestBody String body) throws JSONException  {
		log.info("In /getInboxMessages ");
		JSONObject outPut = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data" + input);
			
			String loginEmail=input.getString("userEmail");
			String candidateEmail=input.getString("candidateEmail");
			
			String accessToken=gmailAPIService.getAccessTokenFromRefreshToken(loginEmail);
			
			if(accessToken!=null) {
			String query="(to:"+candidateEmail+" in:sent) OR (from:"+candidateEmail+" in:inbox";
			String result=gmailAPIService.getEmailThreads(accessToken, query);
			
			outPut.put("messages", result);
			outPut.put("message","success");
			outPut.put("status","200");
			}else {
				outPut.put("message","Unauthorized");
				outPut.put("status","401");	
				outPut.put("messages", "[]");
			}
		} catch (JSONException e) {
			outPut.put("message","failure");
			outPut.put("status","400");
			log.error("Exception due to bad JSON request in getInboxMessages", e);
		} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			outPut.put("message","Unauthorized");
			outPut.put("status","401");
			log.error("Unauthorized request ", e);
		}catch (Exception e) {
			outPut.put("message","failure");
			outPut.put("status","500");

			log.error("Exception in /messages ", e);
		} 
       return outPut.toString();
	}
	
	
	
	@CrossOrigin
	@RequestMapping(value = "/filterRecruiterInboxMessages")
	@ResponseBody
	public String filterRecruiterInboxMessages(@RequestBody String body) throws JSONException  {
		log.info("In /filterRecruiterInboxMessages ");
		JSONObject outPut = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data" + input);
			
			String loginEmail=input.getString("userEmail");
			//String candidateEmail=input.getString("candidateEmail");
			String fromDate=input.getString("fromDate");
			String toDate=input.getString("toDate");
			String nextPageToken=input.getString("nextPageToken");
			
			String accessToken=gmailAPIService.getAccessTokenFromRefreshToken(loginEmail);
			
			if(accessToken!=null) {
//			String query="(to:"+candidateEmail+" in:sent) OR (from:"+candidateEmail+" in:inbox";
			String query="in:sent "+(fromDate.equals("")?"":"after:"+fromDate)+(toDate.equals("")?"":"before:"+toDate);
			Map<String, String> resultMap=gmailAPIService.getRecruiterEmailThreads(accessToken, query,nextPageToken);
			String result=resultMap.get("result");
			String nextpageToken=resultMap.get("nextpageToken");
			outPut.put("messages", result);
			outPut.put("nextpageToken", nextpageToken);
			outPut.put("message","success");
			outPut.put("status","200");
			}else {
				outPut.put("message","Unauthorized");
				outPut.put("status","401");	
				outPut.put("messages", "[]");
			}
		} catch (JSONException e) {
			outPut.put("message","failure");
			outPut.put("status","400");
			log.error("Exception due to bad JSON request in filterRecruiterInboxMessages", e);
		} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			outPut.put("message","Unauthorized");
			outPut.put("status","401");
			log.error("Unauthorized request ", e);
		}catch (Exception e) {
			outPut.put("message","failure");
			outPut.put("status","500");

			log.error("Exception in /filterRecruiterInboxMessages ", e);
		} 
       return outPut.toString();
	}
	
	@CrossOrigin
	@RequestMapping(value = "/communication")
	@ResponseBody
	public String communication(@RequestBody String body) throws JSONException  {
		log.info("In /communication ");
		JSONObject outPut = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data" + input);
			
			String loginEmail=input.getString("userEmail");
			
			
			String accessToken=gmailAPIService.getAccessTokenFromRefreshToken(loginEmail);
			
//			if(accessToken!=null) {
			Map<String, String> resultMap=gmailAPIService.getCommunicationDetails(accessToken, input);
			String result=resultMap.get("result");
			String nextpageToken=resultMap.get("nextpageToken");
			outPut.put("messages", result);
			outPut.put("nextpageToken", nextpageToken);
			outPut.put("message","success");
			outPut.put("status","200");
//			}else {
//				outPut.put("message","Unauthorized");
//				outPut.put("status","401");	
//				outPut.put("messages", "[]");
//			}
		} catch (JSONException e) {
			outPut.put("message","failure");
			outPut.put("status","400");
			log.error("Exception due to bad JSON request in communication", e);
		} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			outPut.put("message","Unauthorized");
			outPut.put("status","401");
			log.error("Unauthorized request ", e);
		}catch (Exception e) {
			outPut.put("message","failure");
			outPut.put("status","500");

			log.error("Exception in /communication ", e);
		} 
       return outPut.toString();
	}
	
	
	@CrossOrigin
	@RequestMapping(value = "/candidateCommunication")
	@ResponseBody
	public String candidateCommunication(@RequestBody String body) throws JSONException  {
		log.info("In /communication ");
		JSONObject outPut = new JSONObject();
		try {
			JSONObject input = new JSONObject(body);
			log.info("Input Json data" + input);
			
			String loginEmail=input.getString("userEmail");
			
			
			String accessToken=gmailAPIService.getAccessTokenFromRefreshToken(loginEmail);
			
//			if(accessToken!=null) {
			Map<String, String> resultMap=gmailAPIService.getCandidateConversationsDetails(accessToken, input);
			String result=resultMap.get("result");
			String nextpageToken=resultMap.get("nextpageToken");
			outPut.put("messages", result);
			outPut.put("nextpageToken", nextpageToken);
			outPut.put("message","success");
			outPut.put("status","200");
//			}else {
//				outPut.put("message","Unauthorized");
//				outPut.put("status","401");	
//				outPut.put("messages", "[]");
//			}
		} catch (JSONException e) {
			outPut.put("message","failure");
			outPut.put("status","400");
			log.error("Exception due to bad JSON request in communication", e);
		} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			outPut.put("message","Unauthorized");
			outPut.put("status","401");
			log.error("Unauthorized request ", e);
		}catch (Exception e) {
			outPut.put("message","failure");
			outPut.put("status","500");

			log.error("Exception in /communication ", e);
		} 
       return outPut.toString();
	}
	
	
}