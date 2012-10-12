package com.tscp.mvno.smpp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("loggingService")
public class LoggingService {
		
	//private static final String	 logConfigFile = "./log4j.xml";
	
	//private static Logger logger = LoggerFactory.getLogger("LoggingService");
	private static Logger logger = LoggerFactory.getLogger("");
	/*
	static{
		try {
			DOMConfigurator.configure(logConfigFile);
		} 
		catch(Exception e ) {
			System.out.println("Error loading log config file!! due to: " + e.getMessage() + ". Now use BasicConfigurator.configure()");
			BasicConfigurator.configure();
		}
	}	
	*/
	public LoggingService(){	   
	}
	
	public void debug(String message) {
		logger.debug(message);
	}

	public void trace(String message) {
		logger.trace(message);
	}

	public void info(String message) {
		logger.info(message);
	}

	public void warn(String message) {
		logger.warn(message);
	}
	
	public void warn(String message, Throwable throwable) {
		logger.warn(message, throwable);
	}
		
	public void error(String message) {
		logger.error(message);
	}		
	
	public void debug(Object obj, String message) {
		logger.debug(obj.getClass().getName()+": "+ message);
	}

	public void trace(Object obj,String message) {
		logger.trace(obj.getClass().getName()+": "+ message);
	}

	public void info(Object obj, String message) {
		logger.info((obj == null? "" : obj.getClass().getName())+": "+ message);
	}
	
	public void info(Object obj, String methodName, String message) {
		logger.info((obj == null? "" : obj.getClass().getName())+ ": "+ methodName + ". returned value = " +message);
	}

	public void warn(Object obj,String message) {
		logger.warn(obj.getClass().getName()+": "+ message);
	}
	
	public void warn(Object obj, String message, Throwable throwable) {
		logger.warn(obj.getClass().getName()+": "+ message, throwable);
	}
	
	public void warn(Object obj, String methodName, String message) {
		logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "():"+ message);
	}

	public void error(Object obj, String methodName, String message) {
		logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "():"+ message);
	}
		
	public void error(Object obj, String methodName, String tscpmvneErrorCode, String tscpmvneErrorMessage, Throwable causeException){
		if(causeException != null) 
		   logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "(): " + tscpmvneErrorCode + "--" + tscpmvneErrorMessage +". Caused by: "+ causeException.getMessage()); 	
		else
			logger.error(obj == null? "" : obj.getClass().getName()+"."+ methodName + "(): " + tscpmvneErrorCode + "--" + tscpmvneErrorMessage); 	
	}		

}

