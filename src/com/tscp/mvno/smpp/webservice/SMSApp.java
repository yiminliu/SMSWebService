package com.tscp.mvno.smpp.webservice;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jws.WebService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.manager.SMSMessageManager;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMSService;

@WebService(endpointInterface = "com.tscp.mvno.smpp.webservice.SMSAppWs")
public class SMSApp implements SMSAppWs{
	
	private SMSMessageManager smsMessageManager;
	private SMSService smppService;
	private LoggingService logger;
	private static boolean initialized = false;
		
	public SMSApp(){
		if(!initialized)
	       init();
	}			
		
	@Override
	public String sendSMSMessageString(String destinationTN, String message) throws SmsException{
		return smsMessageManager.processMessage(destinationTN, message);	
	}
	
	@Override
	public String sendSMSMessage(SMSMessage smsMessage) throws SmsException{			
		return smsMessageManager.processMessage(smsMessage);
	}
		
	@Override
	public void sendSMSMessageList(List<SMSMessage> smsMessageList) throws SmsException{			
		smsMessageManager.processMessage(smsMessageList);
	}
	
	@PostConstruct 
	private void init(){	
		try {
		    ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");	
	        logger = (LoggingService)appCtx.getBean("loggingService");
	        //dbService = (DatabaseService)appCtx.getBean("databaseService");	
	        smppService = (SMSService)appCtx.getBean("smsService");	
	        smsMessageManager = (SMSMessageManager)appCtx.getBean("smsMessageManager");	
	        initialized = true;
		}
		catch(Exception e){
			e.printStackTrace();
  		    logger = new LoggingService(); 
		    smsMessageManager = new SMSMessageManager();
		}    
	}
	
	@PreDestroy
	private void cleanUp(){			
		smsMessageManager.cleanUp();
	}
}
