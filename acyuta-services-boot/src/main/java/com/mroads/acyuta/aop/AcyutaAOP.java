package com.mroads.acyuta.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class AcyutaAOP {

	private Logger log;

	public AcyutaAOP() {

		log = LoggerFactory.getLogger(getClass());
		log.info("In acyutaAOP constructor");
	}

	@Around("execution(* com.mroads.acyuta.controller.*.*(..)) ||"+""
			+ "execution(* com.mroads.acyuta.service.*.*(..)) || execution(* com.mroads.acyuta.gmailapi.*.*(..))")
	public Object log(ProceedingJoinPoint joinPoint){

		return printEnterAndExitLogs(joinPoint, 100);// If execution time more than 100 millisec we print log info
	}  

	private Object printEnterAndExitLogs(ProceedingJoinPoint joinPoint, long milliSec)  {

		Object retVal = null;
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.trace("Entering into {}.{} ", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());
		try {
			retVal = joinPoint.proceed();
		} catch (Throwable e) {
			log.error("Error in printEnterAndExitLogs method",e );
		}
		stopWatch.stop();

		StringBuilder logMessage = new StringBuilder();
		logMessage.append(joinPoint.getTarget().getClass().getSimpleName() +" ");
		logMessage.append(joinPoint.getSignature().getName());

		logMessage.append(" execution time : ");
		logMessage.append(stopWatch.getTotalTimeMillis());
		logMessage.append(" ms");

		if(stopWatch.getTotalTimeMillis() >milliSec){
			log.warn("{}", logMessage);
		}
		log.info("Exiting from {}.{}", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName());


		return retVal;

	}

}
