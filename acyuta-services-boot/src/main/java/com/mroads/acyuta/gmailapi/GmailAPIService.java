package com.mroads.acyuta.gmailapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;
import com.mroads.acyuta.model.Recruiter_Call_Logs;
import com.mroads.acyuta.model.UserGmail;
import com.mroads.acyuta.repository.Recruiter_Call_LogsRepository;
import com.mroads.acyuta.repository.UserGmailRepository;

@Service
public class GmailAPIService {

	final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String APPLICATION_NAME = "panna";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_READONLY,"https://www.googleapis.com/auth/userinfo.email");
	
	public static GoogleAuthorizationCodeFlow flow;


	@Value("${gmail.client.redirectUri}")
	private String redirectUri;
	
	@Autowired
	UserGmailRepository userGmailRepository;
	
	@Autowired
	private Recruiter_Call_LogsRepository recruiter_Call_LogsRepository;
	
	 private  SimpleDateFormat dbDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private  SimpleDateFormat dbDateFormatView=new SimpleDateFormat("MM/dd/yyyy");


	public String authorize() throws Exception {
		AuthorizationCodeRequestUrl authorizationUrl;

		InputStream in = GmailAPIService.class.getResourceAsStream("/credentials.json");
		log.info("Input stream "+in);

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		// Build flow and trigger user authorization request.
		  flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setAccessType("offline").setApprovalPrompt("force")
				.build();
		authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
		log.info("gmail authorizationUrl ->" + authorizationUrl);
		return authorizationUrl.build();
	}


	public  String exchangeRefreshToken(String refreshtoken) throws Exception {

		String url = "https://www.googleapis.com/oauth2/v4/token";					

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		// add header
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("client_id", getcredentialsMap().get("clientId")));
		urlParameters.add(new BasicNameValuePair("client_secret", getcredentialsMap().get("clientSecret")));
		urlParameters.add(new BasicNameValuePair("redirect_uri", redirectUri));
		urlParameters.add(new BasicNameValuePair("refresh_token", refreshtoken));				
		urlParameters.add(new BasicNameValuePair("grant_type", "refresh_token"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		HttpResponse response = client.execute(post);
		log.info("\n Sending 'POST' request to URL : " + url);
		log.info("Post parameters : " + post.getEntity());
		log.info("Response Code : " +
				response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		log.info(result.toString());	
		
		
		return new JSONObject(result.toString()).getString("access_token");

	}
	
	
	public String getEmailThreads(String token ,String query) throws IOException, JSONException, GeneralSecurityException{ 

		  Credential credential=new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);
		   final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME)
					.build();
			
			ListThreadsResponse response = service.users().threads().list("me").setQ(query).execute();
			   
			   List<Thread> threads = new ArrayList<Thread>();
			    while(response.getThreads() != null) {
			      threads.addAll(response.getThreads());
			      if(response.getNextPageToken() != null) {
			        String pageToken = response.getNextPageToken();
			        response = service.users().threads().list("me").setQ(query).setPageToken(pageToken).execute();
			      } else {
			        break;
			      }
			    }
			   log.info("Threads size "+threads.size());
			    
			    JSONArray responseArray=new JSONArray();

			    for(Thread thread : threads) {
			    	
			    	JSONObject threadObj=new JSONObject();
			    	
			    	threadObj.put("threadId", thread.getId());
			    	threadObj.put("snippet", thread.getSnippet());
			    	
			    	Thread threadResponse=service.users().threads().get("me", thread.getId()).execute();
			    	
			    	if(threadResponse.getMessages()!=null) {
			    		threadObj.put("threadCount", threadResponse.getMessages().size());
			    		
			    		JSONArray msgArray=new JSONArray();
			    		for(Message msg:threadResponse.getMessages()) {
			    			try {
			    				JSONObject msgObj=new JSONObject();
				    		byte[] bodyBytes = Base64.decodeBase64(msg.getPayload().getParts().get(0).getBody().getData().trim().toString()); 
				            String body = new String(bodyBytes, "UTF-8");
				            log.info("message body  "+body);
				            msgObj.put("body", body);
				            msgArray.put(msgObj);
				          String subject="(no subject)";
				          String to="";
				          String from="";
				            List<MessagePartHeader> headersList = msg.getPayload().getHeaders();
				            for(MessagePartHeader headr:headersList) {
				            	if(headr.get("name").equals("Subject") && !headr.get("value").equals("")) {
				            		subject=""+headr.get("value");
				            	}
				            	if(headr.get("name").equals("From")) {
				            		from=""+headr.get("value");
				            	}
                                if(headr.get("name").equals("To")) {
                                	to=""+headr.get("value");
				            	}
				            }
				            threadObj.put("subject", subject);
				            threadObj.put("from", from);
				            threadObj.put("to", to);
				            log.info("subject  "+subject+" To "+to+" from "+from);
			    			}catch(Exception e) {
			    			log.error("Error while parsing email body ",e);	
			    			}
				    	}
			    		threadObj.put("messages", msgArray);
			    	}
			    	responseArray.put(threadObj);
			    }
				return responseArray.toString();
		
	}
	
	
	
	public Map<String,String> getRecruiterEmailThreads(String token ,String query,String nextPageToken) throws IOException, JSONException, GeneralSecurityException{ 
          Map<String,String> resultMap=new HashMap<>();
          String nextResultToken="";
		log.info("query  "+query);
		  Credential credential=new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);
		   final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME)
					.build();
			
			ListThreadsResponse response = null;
			
			if(nextPageToken.equals("")) {
				 response = service.users().threads().list("me").setMaxResults(20l).setQ(query).execute();	
			}else {
				response = service.users().threads().list("me").setMaxResults(20l).setQ(query).setPageToken(nextPageToken).execute();
			}
			   
			   List<Thread> threads = new ArrayList<Thread>();
			    if(response.getThreads() != null) {
			      threads.addAll(response.getThreads());
			      if(response.getNextPageToken() != null) {
			        String pageToken = response.getNextPageToken();   
			        nextResultToken=pageToken;
			       // response = service.users().threads().list("me").setQ(query).setPageToken(pageToken).execute();
			      }
//			      else {
//			        break;
//			      }
			    }
			   log.info("Threads size "+threads.size());
			    
			    JSONArray responseArray=new JSONArray();

			    for(Thread thread : threads) {
			    	
			    	JSONObject threadObj=new JSONObject();
			    	
			    	threadObj.put("threadId", thread.getId());
			    	threadObj.put("snippet", thread.getSnippet());
			    	
			    	Thread threadResponse=service.users().threads().get("me", thread.getId()).execute();
			    	
			    	if(threadResponse.getMessages()!=null) {
			    		threadObj.put("threadCount", threadResponse.getMessages().size());
			    		
			    		JSONArray msgArray=new JSONArray();
			    		for(Message msg:threadResponse.getMessages()) {
			    			try {
			    				JSONObject msgObj=new JSONObject();
				    		byte[] bodyBytes = Base64.decodeBase64(msg.getPayload().getParts().get(0).getBody().getData().trim().toString()); 
				            String body = new String(bodyBytes, "UTF-8");
				            log.info("message body  "+body);
				            msgObj.put("body", body);
				            msgArray.put(msgObj);
				          String subject="(no subject)";
				          String to="";
				          String from="";
				          String date="";
				            List<MessagePartHeader> headersList = msg.getPayload().getHeaders();
				            for(MessagePartHeader headr:headersList) {
				            	if(headr.get("name").equals("Subject") && !headr.get("value").equals("")) {
				            		subject=""+headr.get("value");
				            	}
				            	if(headr.get("name").equals("From")) {
				            		from=""+headr.get("value");
				            	}
                              if(headr.get("name").equals("To")) {
                              	to=""+headr.get("value");
				            	}
                              if(headr.get("name").equals("Date")) {
                            	  date =""+headr.get("value");
                              }
				            }
				            threadObj.put("subject", subject);
				            threadObj.put("from", from);
				            threadObj.put("to", to);
				            threadObj.put("date", date);
				            log.info("subject  "+subject+" To "+to+" from "+from);
			    			}catch(Exception e) {
			    			log.error("Error while parsing email body ",e);	
			    			}
				    	}
			    		threadObj.put("messages", msgArray);
			    	}
			    	responseArray.put(threadObj);
			    }
			    resultMap.put("result", responseArray.toString());
			    resultMap.put("nextpageToken", nextResultToken);
				return resultMap;
		
	}
	
	
	public Map<String,String> getCommunicationDetails(String token ,JSONObject input) throws IOException, JSONException, GeneralSecurityException, ParseException{ 
		Map<String,String> resultMap=new HashMap<>();
		
		long maxResults=20l;

		String fromDate=input.getString("startDate");
		String toDate=input.getString("endDate");
		String nextPageToken=input.getString("nextPageToken");
			
        String userId=""+input.get("userId");
		
		Date startDate=getDayInUTC("1947-08-14"+" 00:00:00",input.getString("timeZone"),true);
		Date endDate=getDayInUTC("3047-08-14"+" 00:00:00",input.getString("timeZone"),true);
		
		Integer pageNumber = 0;
		Integer pageSize = 50;

		if (!"".equals(input.getString("startDate")) && null != input.getString("startDate")) {
			startDate=getDayInUTC(input.getString("startDate")+" 00:00:00",input.getString("timeZone"),true);

		}
		if (!"".equals(input.getString("endDate")) && null != input.getString("endDate")) {
			endDate=getDayInUTC(input.getString("endDate")+" 23:59:59",input.getString("timeZone"),false);
		}
		
		if (!"".equals(input.getString("pageNumber")) && null != input.getString("pageNumber")) {
			pageNumber = Integer.valueOf(input.getString("pageNumber"));
		}
		if (!"".equals(input.getString("pageSize")) && null != input.getString("pageSize")) {
			pageSize = Integer.valueOf(input.getString("pageSize"));
		}
		
		
		DateTimeZone timezone = DateTimeZone.forID("America/Chicago");
		try{
			timezone = DateTimeZone.forID(input.getString("timeZone"));	
		}catch(Exception e){
			log.debug(""+e);
		}
		
		List<JSONObject> responseList=new ArrayList<>();
		String nextResultToken="";
		if(token!=null) {
		//Query for fetching gmail messages
		String query="in:sent "+(fromDate.equals("")?"":"after:"+fromDate)+(toDate.equals("")?"":"before:"+addDaysToDate(toDate,1));
		log.info("query for fetching from gmail  "+query); 
		

		Credential credential=new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();

		//Getting email threads
		ListThreadsResponse response = null;

		if(nextPageToken.equals("") && pageNumber==0) {
			response = service.users().threads().list("me").setMaxResults(maxResults).setQ(query).execute();	
		}else if(!nextPageToken.equals("")) {
			response = service.users().threads().list("me").setMaxResults(maxResults).setQ(query).setPageToken(nextPageToken).execute();
		}

		List<Thread> threads = new ArrayList<Thread>();
		if(response!=null && response.getThreads() != null) {
			threads.addAll(response.getThreads());
			if(response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();   
				nextResultToken=pageToken;
			}
		}
		log.info("Threads size "+threads.size());

		
		for(Thread thread : threads) {

			JSONObject threadObj=new JSONObject();

			threadObj.put("threadId", thread.getId());
			threadObj.put("snippet", thread.getSnippet());
			threadObj.put("type", "email");

			Thread threadResponse=service.users().threads().get("me", thread.getId()).setFields("messages").execute();
			if(threadResponse.getMessages()!=null) {
				threadObj.put("threadCount", threadResponse.getMessages().size());

				JSONArray msgArray=new JSONArray();
				StringJoiner joiner = new StringJoiner(",");
				
				//loop
				threadResponse.getMessages().forEach(msg->{
					try {

						JSONObject msgObj=new JSONObject();
						MessagePart payload=msg.getPayload();
//						String body="";
						msgObj.put("body", "");
						msgObj.put("mimeType", payload.getMimeType());
						log.debug("mimeType "+payload.getMimeType());
						
						if("text/html".equals(payload.getMimeType())) {
							msgObj.put("body", new String(Base64.decodeBase64(payload.getBody().getData().trim().toString()),"UTF-8"));
							
						}else if("multipart/related".equals(payload.getMimeType()) || "multipart/mixed".equals(payload.getMimeType())){		
							List<MessagePart> parts = msg.getPayload().getParts();
							String bdy="";
							for (MessagePart part : parts) {

								if("multipart/alternative".equals(part.getMimeType()) && part.getParts().get(0).getBody().getData()!=null) {
									byte[] bodyBytes = Base64.decodeBase64(part.getParts().get(0).getBody().getData().trim().toString()); 
									String body = new String(bodyBytes, "UTF-8");
									bdy=bdy+body;
									msgObj.put("mimeType", "multipart/alternative");
								}
								if("text/html".equals(part.getMimeType())){
									bdy=bdy+new String(Base64.decodeBase64(part.getBody().getData().trim().toString()),"UTF-8");
								}
//								if("image/png".equals(part.getMimeType())) {
//									log.info("****************** "+part.getBody().getAttachmentId());
//								}
							}					
							msgObj.put("body",bdy);
						}
						else {
							if(payload.getParts()!=null && payload.getParts().get(0).getBody().getData()!=null) {
								byte[] bodyBytes = Base64.decodeBase64(payload.getParts().get(0).getBody().getData().trim().toString()); 
						       String body = new String(bodyBytes, "UTF-8");
						       log.debug("message body  "+body);
						       msgObj.put("body", body);
							}
						}
						
						log.debug("message date  "+msg.getInternalDate());
							
						msgObj.put("snippet", msg.getSnippet());			
						
						String subject="(no subject)";
						String to="";
						String from="";
						String date="";
						String toName="";
						String fromName="";
						
					
						List<MessagePartHeader> headersList = payload.getHeaders();
						for(MessagePartHeader headr:headersList) {
							if(headr.get("name").equals("Subject") && !headr.get("value").equals("")) {
								subject=""+headr.get("value");
							}
							if(headr.get("name").equals("From")) {
								from=""+headr.get("value");
								try {
									fromName=from.split("\\<")[0].trim();
								}catch(Exception e){
									
								}
							}
							if(headr.get("name").equals("To")) {
								to=""+headr.get("value");
								try {
									toName=to.split("\\<")[0].trim();
									if(!joiner.toString().contains(toName))
									joiner.add(toName);
								}catch(Exception e){
									
								}
							}
							if(headr.get("name").equals("Date")) {
								date =""+headr.get("value");
							}
						}
						
						threadObj.put("subject", subject);
						threadObj.put("from", from);
						threadObj.put("to", to);
						threadObj.put("toName", joiner.toString());
						String dateTime=convertMilliSecondsToDate(""+msg.getInternalDate(),input.getString("timeZone"));
						String dateString="";
						String time="";
						
						if(!dateTime.equals("")) {
							try {
								String[] dateArray=dateTime.split("\\s");
								dateString=dateArray[0];
							    time=dateArray[1]+" "+dateArray[2];
							}catch(Exception e) {
								log.debug("",e);
							}
							
						}
						msgObj.put("fromName", fromName);
						msgObj.put("date", dateString);
						msgObj.put("time", time);
						msgArray.put(msgObj);
						threadObj.put("compareDate",convertMilliSecondsToCompareDate(""+msg.getInternalDate(),input.getString("timeZone")));
						threadObj.put("date", dateString);
						threadObj.put("time", time);
						log.info("subject  "+subject+" To "+to+" from "+from);
					}catch(Exception e) {
						log.error("Error while parsing email body ",e);	
					}
				});//loop end
				//sub for end
				threadObj.put("messages", msgArray);
			}
			//responseArray.put(threadObj);
			responseList.add(threadObj);
		}//main for end

	}else {
		log.info("->->->->User not authenticated gmail<-<-<-<-");
	}
		//communication logs
		
		List<Recruiter_Call_Logs> recruiterLogs=recruiter_Call_LogsRepository.findByRecruiterId(new BigInteger(userId),startDate,endDate,new PageRequest(pageNumber, pageSize));
		log.info("size of call logs "+recruiterLogs.size());
		

		for(Recruiter_Call_Logs recLog:recruiterLogs) {
			JSONObject obj=new JSONObject();
			obj.put("id", recLog.getId());
			obj.put("extensionId", recLog.getExtensionId());
			obj.put("calledNumber", recLog.getCalledNumber());
			//obj.put("date", recLog.getDate());
			obj.put("date", dbDateFormatView.format(new DateTime(recLog.getDate()).withZone(timezone).toLocalDateTime().toDate()));
			
			String compareDate=dbDateFormat.format(new DateTime(recLog.getDate()).withZone(timezone).toLocalDateTime().toDate());
			obj.put("compareDate", dbDateFormat.format(new DateTime(recLog.getDate()).withZone(timezone).toLocalDateTime().toDate()));
			obj.put("time", timeConvert24FormatTo12Format(getTimeFromDate(compareDate)));
			obj.put("duration", convertTime(recLog.getDuration()));
			obj.put("callRecordingLink", recLog.getCallRecordingLink());
			obj.put("callType", recLog.getCallType());
			obj.put("type", "calls");
			obj.put("recruiterName", recLog.getRecruiterName()==null?"":recLog.getRecruiterName());
//			responseArray.put(obj);
			responseList.add(obj);
		}
		
		
		Collections.sort(responseList, new Comparator<JSONObject>() {
		    @Override
		    public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
		        int compare = 0;
		        try
		        {
		            String keyA = jsonObjectA.has("compareDate")?jsonObjectA.getString("compareDate"):"";
		            String keyB = jsonObjectB.has("compareDate")?jsonObjectB.getString("compareDate"):"";
		            compare = keyB.compareTo(keyA);
		        }
		        catch(JSONException e)
		        {
		            log.debug("",e);
		        }
		        return compare;
		    }
		});
		
		log.info("list size "+responseList.size());
	
		resultMap.put("result",new JSONArray(responseList).toString());
		
		resultMap.put("nextpageToken", nextResultToken);
		return resultMap;

	}
	
	
	public String getTimeFromDate(String dateString) {
		
		String timeString="";
		try {
			String[] dArray=dateString.split("\\s");
			timeString=dArray[1];
		}catch(Exception e) {
			log.error("invalid date");
		}
		
		return timeString;
	}
	
	
	public Map<String,String> getCandidateConversationsDetails(String token ,JSONObject input) throws IOException, JSONException, GeneralSecurityException, ParseException{ 
		Map<String,String> resultMap=new HashMap<>();
		
		long maxResults=20l;

		//String fromDate=input.getString("startDate");
		//String toDate=input.getString("endDate");
		String nextPageToken=input.getString("nextPageToken");
			
       // String userId=""+input.get("userId");
        String candidateEmail=""+input.get("candidateEmail");
		
		Date startDate=getDayInUTC("1947-08-14"+" 00:00:00",input.getString("timeZone"),true);
		Date endDate=getDayInUTC("3047-08-14"+" 00:00:00",input.getString("timeZone"),true);
		
		Integer pageNumber = 0;
		Integer pageSize = 50;

//		if (!"".equals(input.getString("startDate")) && null != input.getString("startDate")) {
//			startDate=getDayInUTC(input.getString("startDate")+" 00:00:00",input.getString("timeZone"),true);
//
//		}
//		if (!"".equals(input.getString("endDate")) && null != input.getString("endDate")) {
//			endDate=getDayInUTC(input.getString("endDate")+" 23:59:59",input.getString("timeZone"),false);
//		}
//		
//		if (!"".equals(input.getString("pageNumber")) && null != input.getString("pageNumber")) {
//			pageNumber = Integer.valueOf(input.getString("pageNumber"));
//		}
//		if (!"".equals(input.getString("pageSize")) && null != input.getString("pageSize")) {
//			pageSize = Integer.valueOf(input.getString("pageSize"));
//		}
//		
		
		DateTimeZone timezone = DateTimeZone.forID("America/Chicago");
		try{
			timezone = DateTimeZone.forID(input.getString("timeZone"));	
		}catch(Exception e){
			log.debug(""+e);
		}
		
		List<JSONObject> responseList=new ArrayList<>();
		String nextResultToken="";
		if(token!=null) {
		//Query for fetching gmail messages
		//String query="in:sent "+(fromDate.equals("")?"":"after:"+fromDate)+(toDate.equals("")?"":"before:"+addDaysToDate(toDate,1));
		
		String query="(to:"+candidateEmail+" in:sent) OR (from:"+candidateEmail+" in:inbox";
		log.info("query for fetching from gmail  "+query); 
		

		Credential credential=new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(token);
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();

		//Getting email threads
		ListThreadsResponse response = null;

		if(nextPageToken.equals("") && pageNumber==0) {
			response = service.users().threads().list("me").setMaxResults(maxResults).setQ(query).execute();	
		}else if(!nextPageToken.equals("")) {
			response = service.users().threads().list("me").setMaxResults(maxResults).setQ(query).setPageToken(nextPageToken).execute();
		}

		List<Thread> threads = new ArrayList<Thread>();
		if(response!=null && response.getThreads() != null) {
			threads.addAll(response.getThreads());
			if(response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();   
				nextResultToken=pageToken;
			}
		}
		log.info("Threads size:{} ",threads.size());

		
		for(Thread thread : threads) {

			JSONObject threadObj=new JSONObject();

			threadObj.put("threadId", thread.getId());
			threadObj.put("snippet", thread.getSnippet());
			threadObj.put("type", "email");

			Thread threadResponse=service.users().threads().get("me", thread.getId()).setFields("messages").execute();
			if(threadResponse.getMessages()!=null) {
				threadObj.put("threadCount", threadResponse.getMessages().size());

				JSONArray msgArray=new JSONArray();
				StringJoiner joiner = new StringJoiner(",");
				
				//loop
				threadResponse.getMessages().forEach(msg->{
					try {

						JSONObject msgObj=new JSONObject();
						MessagePart payload=msg.getPayload();
//						String body="";
						msgObj.put("body", "");
						msgObj.put("mimeType", payload.getMimeType());
						log.debug("mimeType: {}",payload.getMimeType());
						
						if("text/html".equals(payload.getMimeType())) {
							msgObj.put("body", new String(Base64.decodeBase64(payload.getBody().getData().trim().toString()),"UTF-8"));
							
						}else if("multipart/related".equals(payload.getMimeType()) || "multipart/mixed".equals(payload.getMimeType())){		
							List<MessagePart> parts = msg.getPayload().getParts();
							String bdy="";
							for (MessagePart part : parts) {

								if("multipart/alternative".equals(part.getMimeType()) && part.getParts().get(0).getBody().getData()!=null) {
									byte[] bodyBytes = Base64.decodeBase64(part.getParts().get(0).getBody().getData().trim().toString()); 
									String body = new String(bodyBytes, "UTF-8");
									bdy=bdy+body;
									msgObj.put("mimeType", "multipart/alternative");
								}
								if("text/html".equals(part.getMimeType())){
									bdy=bdy+new String(Base64.decodeBase64(part.getBody().getData().trim().toString()),"UTF-8");
								}
//								if("image/png".equals(part.getMimeType())) {
//									log.info("****************** "+part.getBody().getAttachmentId());
//								}
							}					
							msgObj.put("body",bdy);
						}
						else {
							if(payload.getParts()!=null && payload.getParts().get(0).getBody().getData()!=null) {
								byte[] bodyBytes = Base64.decodeBase64(payload.getParts().get(0).getBody().getData().trim().toString()); 
						       String body = new String(bodyBytes, "UTF-8");
						       log.debug("message body  "+body);
						       msgObj.put("body", body);
							}
						}
						
						log.debug("message date  "+msg.getInternalDate());
							
						msgObj.put("snippet", msg.getSnippet());			
						
						String subject="(no subject)";
						String to="";
						String from="";
						String date="";
						String toName="";
						String fromName="";
						
					
						List<MessagePartHeader> headersList = payload.getHeaders();
						for(MessagePartHeader headr:headersList) {
							if(headr.get("name").equals("Subject") && !headr.get("value").equals("")) {
								subject=""+headr.get("value");
							}
							if(headr.get("name").equals("From")) {
								from=""+headr.get("value");
								try {
									fromName=from.split("\\<")[0].trim();
								}catch(Exception e){
									
								}
							}
							if(headr.get("name").equals("To")) {
								to=""+headr.get("value");
								try {
									toName=to.split("\\<")[0].trim();
									if(!joiner.toString().contains(toName))
									joiner.add(toName);
								}catch(Exception e){
									
								}
							}
							if(headr.get("name").equals("Date")) {
								date =""+headr.get("value");
							}
						}
						
						threadObj.put("subject", subject);
						threadObj.put("from", from);
						threadObj.put("to", to);
						threadObj.put("toName", joiner.toString());
						String dateTime=convertMilliSecondsToDate(""+msg.getInternalDate(),input.getString("timeZone"));
						String dateString="";
						String time="";
						
						if(!dateTime.equals("")) {
							try {
								String[] dateArray=dateTime.split("\\s");
								dateString=dateArray[0];
							    time=dateArray[1]+" "+dateArray[2];
							}catch(Exception e) {
								log.debug("",e);
							}
							
						}
						msgObj.put("fromName", fromName);
						msgObj.put("date", dateString);
						msgObj.put("time", time);
						msgArray.put(msgObj);
						threadObj.put("compareDate",convertMilliSecondsToCompareDate(""+msg.getInternalDate(),input.getString("timeZone")));
						threadObj.put("date", dateString);
						threadObj.put("time", time);
						log.debug("subject  "+subject+" To "+to+" from "+from);
					}catch(Exception e) {
						log.error("Error while parsing email body ",e);	
					}
				});//loop end
				//sub for end
				threadObj.put("messages", msgArray);
			}
			//responseArray.put(threadObj);
			responseList.add(threadObj);
		}//main for end

	}else {
		log.info("->->->->User not authenticated gmail<-<-<-<-");
	}
		
		Collections.sort(responseList, new Comparator<JSONObject>() {
		    @Override
		    public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
		        int compare = 0;
		        try
		        {
		            String keyA = jsonObjectA.has("compareDate")?jsonObjectA.getString("compareDate"):"";
		            String keyB = jsonObjectB.has("compareDate")?jsonObjectB.getString("compareDate"):"";
		            compare = keyB.compareTo(keyA);
		        }
		        catch(JSONException e)
		        {
		            log.debug("",e);
		        }
		        return compare;
		    }
		});
		
		log.info("list size "+responseList.size());
	
		resultMap.put("result",new JSONArray(responseList).toString());
		
		resultMap.put("nextpageToken", nextResultToken);
		return resultMap;

	}



	
	/**
	 * This method is using for save credentioals in DB
	 * @param code
	 * @throws IOException
	 */
	public void saveCredentials( String code) throws IOException {
		TokenResponse response = GmailAPIService.flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();	
		log.info("refresh token "+response.getRefreshToken());
		log.info("access token "+response.getAccessToken());
		
		UserInfo userinfo=getUserInfo(response.getAccessToken());
		
		UserGmail uGmail=userGmailRepository.findByEmail(userinfo.getEmail());
		if(uGmail==null) {
		   uGmail=new UserGmail();	
		}
		uGmail.setEmail(userinfo.getEmail());
		uGmail.setTokenResponse(response.toPrettyString());
		uGmail.setRefreshToken(response.getRefreshToken());
		uGmail.setUpdatedDate(new Date());
		uGmail.setUserInfo(userinfo.toString());
		userGmailRepository.save(uGmail);
		log.info("User credentials successfully saved ");
	}
	
	
	/**
	 * Take refresh token from DB and generate Access Token
	 * @param email
	 * @return
	 */
	public String getAccessTokenFromRefreshToken(String email) {
		
		UserGmail userGmail=userGmailRepository.findByEmail(email);
		
		if(userGmail!=null) {
			try {
				return exchangeRefreshToken(userGmail.getRefreshToken());
			} catch (Exception e) {
				return null;
			}
		}else {
		return null;	
		}
		
	}
	
	/**
	 * Parse the details from credentails.json
	 * @return
	 */
	public Map<String,String> getcredentialsMap(){
		Map<String ,String> credMap=new HashMap<>();

		try {
			InputStream in = GmailAPIService.class.getResourceAsStream("/credentials.json");
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);

			JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
			credMap.put("clientId", jsonObject.getJSONObject("web").getString("client_id"));
			credMap.put("clientSecret", jsonObject.getJSONObject("web").getString("client_secret"));
		} catch (IOException e) {
			log.info("Exception in getcredentialsMap ",e);
		} catch (JSONException e) {
			log.info("Exception in getcredentialsMap ",e);
		}
		
		return credMap;
	}
	
	
	/**
	 * Getting The user information after athentication and savein information in DB
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public UserInfo getUserInfo(String token) throws IOException {
		RestTemplate restTemplate=new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Content-Type", "application/x-www-form-urlencoded");
		headers.set("Authorization", "Bearer "+token);
		headers.set("format", "full");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		final ResponseEntity<UserInfo> userInfoResponseEntity = restTemplate.exchange("https://www.googleapis.com/oauth2/v2/userinfo",HttpMethod.GET,entity, UserInfo.class);	
		log.info("user info "+userInfoResponseEntity.getBody());

		return userInfoResponseEntity.getBody();
	}
	
	
	/**
	 * Returt status of user already verified or not
	 * @param email
	 * @return
	 */
	public Boolean checkGmailVerificationStatus(String email) {
		Boolean status=false;
		try {
			UserGmail uGmail=userGmailRepository.findByEmail(email);
			if(uGmail!=null && uGmail.getRefreshToken()!=null) {
				exchangeRefreshToken(uGmail.getRefreshToken());
				status=true;
			}
		}catch(Exception e) {
			log.error("Exception in checkGmailVerificationStatus",e);
		}
		log.info("status "+status);
		return status;
	}
	
	
	/**
	 * THIS METHOD FOR GET DATE BASED ON DAYS(PLUS/MINUS days)
	 * @param number
	 * @return
	 */
	private String getTempDate(int number) {
	    final Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, number);
        return dbDateFormat.format(cal.getTime());
	}
	
	
	//Converting user time to UTC Time
		public Date getDayInUTC(String scheduleTime,String timeZone, boolean isStartTime) throws ParseException {
			Date utcDate = null;
			try {
				if("".equals(timeZone) || null==timeZone){
					timeZone="America/Chicago";
					log.info("timeZone is empty so takeing default timeZone as America/Chicago");
				}
				TimeZone timeZoneId=TimeZone.getTimeZone(timeZone);
				DateFormat formatterIST = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				formatterIST.setTimeZone(timeZoneId);
				Date date = formatterIST.parse(scheduleTime);
				log.debug(formatterIST.format(date));

				DateFormat formatterUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
				String temp = formatterUTC.format(date);
				log.info("UTC date " + formatterUTC.format(date));

				DateFormat formatterUTC1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				utcDate = formatterUTC1.parse(temp);

			} catch (Exception e) {
				if (isStartTime) {
					utcDate = dbDateFormat.parse(getTempDate(-1000000000));
				} else {
					utcDate = dbDateFormat.parse(getTempDate(100));
				}

			}
			return utcDate;
		}
	

		public String convertMilliSecondsToDate(String dateInMilliSec,String timeZone) {
			String result="";
			try {
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
				
				formatter.setTimeZone(TimeZone.getTimeZone(timeZone));

				long milliSeconds= Long.parseLong(dateInMilliSec);
				log.debug("milli seconds "+milliSeconds);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(milliSeconds);
				log.debug("converted date "+formatter.format(calendar.getTime())); 
				
				result=formatter.format(calendar.getTime());
				
			}catch(Exception e) {
				log.error("",e);
			}
			return result;

					
		}
		
		
		
		public String convertMilliSecondsToCompareDate(String dateInMilliSec,String timeZone) {
			String result="";
			try {
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				
				formatter.setTimeZone(TimeZone.getTimeZone(timeZone));

				long milliSeconds= Long.parseLong(dateInMilliSec);
				log.debug("milli seconds "+milliSeconds);

				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(milliSeconds);
				log.debug("converted date "+formatter.format(calendar.getTime())); 
				
				result=formatter.format(calendar.getTime());
				
			}catch(Exception e) {
				log.error("",e);
			}
			return result;

					
		}
		
		
		private String convertTime(String timeString)
		{
			String finalTime = "";
			try {
				long time=Long.valueOf(timeString.trim());
				long hour = (time%(24*60)) / 60;
				long minutes = (time%(24*60)) % 60;
				long seconds = time / (24*3600);

//				finalTime = String.format("%02d:%02d:%02d",
//						TimeUnit.HOURS.toHours(hour) ,
//						TimeUnit.MINUTES.toMinutes(minutes),
//						TimeUnit.SECONDS.toSeconds(seconds));
				finalTime = String.format("%02d:%02d",
						TimeUnit.HOURS.toHours(hour) ,
						TimeUnit.MINUTES.toMinutes(minutes));
			}catch(Exception e) {
				log.error("",e);
			}

			return finalTime;
		}
		
		
		private String timeConvert24FormatTo12Format(String time) {
			String finalTime="";
			 DateFormat df = new SimpleDateFormat("HH:mm");
			 DateFormat outputformat = new SimpleDateFormat("hh:mm a");
			try {
				Date date= df.parse(time);
				finalTime = outputformat.format(date);	
			}catch(Exception e) {
				log.error("",e);
			}
			
			return finalTime;
		}
		
		private String addDaysToDate(String date,int addDays) {
			String sourceDate = date;
			
			log.debug("intial date "+date);
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date myDate = format.parse(sourceDate);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(myDate );
				cal.add( Calendar.DATE, 1 );
				sourceDate=format.format(cal.getTime());
				log.debug("converted date "+sourceDate);
				
			}catch(Exception e) {
				log.error("Exception in addDaysToDate ",e);
			}
			return sourceDate;
		
		
		}

}
