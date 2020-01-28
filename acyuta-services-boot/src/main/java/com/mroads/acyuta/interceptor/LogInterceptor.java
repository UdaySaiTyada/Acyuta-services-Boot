package com.mroads.acyuta.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mroads.acyuta.common.JobOrderConstants;

@Component
public class LogInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userId = request.getHeader(JobOrderConstants.USER_ID_STRING);
		String candidateId = request.getHeader(JobOrderConstants.CANDIDATE_ID_STRING);
		String logString = userId+":0";
		if(null!=candidateId && !candidateId.isEmpty()) {
			logString = userId+":"+candidateId;
		}
		
		MDC.put("userId", logString);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		MDC.remove(JobOrderConstants.USER_ID_STRING);	
		
	}	

}
