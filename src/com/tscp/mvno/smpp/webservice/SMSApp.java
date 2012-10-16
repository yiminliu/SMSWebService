package com.tscp.mvno.smpp.webservice;

import java.util.List;

import javax.jws.WebService;

import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.manager.SMSMessageManager;

@WebService(endpointInterface = "com.tscp.mvno.smpp.webservice.SMSAppWs")
public class SMSApp implements SMSAppWs{
	
	private SMSMessageManager smsMessageManager;
		
	public SMSApp(){
	    init();
	}			
		
	public String sendSMSMessageString(String destinationTN, String message) throws SmsException{
		return smsMessageManager.processMessage(destinationTN, message);	
	}
	
	public String sendSMSMessage(SMSMessage smsMessage) throws SmsException{			
		return smsMessageManager.processMessage(smsMessage);
	}
		
	public void sendSMSMessageList(List<SMSMessage> smsMessageList) throws SmsException{			
		smsMessageManager.processMessage(smsMessageList);
	}
	
	//@PostConstruct 
	private void init(){			
		smsMessageManager = new SMSMessageManager();
	}
	
	//@PreDestroy
	private void cleanUp(){			
		smsMessageManager.cleanUp();
	}
}
