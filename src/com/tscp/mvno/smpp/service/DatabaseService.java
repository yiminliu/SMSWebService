package com.tscp.mvno.smpp.service;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.SMSMessage;

@Service
@Scope("singleton")
public class DatabaseService {

    @Autowired
    private SmsDao smsDao;  
     
    public DatabaseService(){}
       
    public List<SMSMessage> getSMSMessageList(AlertAction messageType) throws Exception {
		
    	return smsDao.getAlertMessages(messageType.getActionProcedureName());
    }	
            
    public int saveSmsMessage(SMSMessage sms){
    	return smsDao.saveSmsMessage(sms);
    }
    
    private void initForTest() {    	
    	ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");
    	smsDao = (SmsDao)appCtx.getBean("smsDao");
    }    
    
    public static void main(String[] args) { 
    	    	  	
    	DatabaseService ds = new DatabaseService();
    	ds.initForTest();
    	System.out.println("Testing SMPP Project ConnectionInfo class....");
    	SMSMessage sms = new SMSMessage();
    	try {
    	sms.setDestinationTN("2132566431");
    	sms.setMessageText("test");
    	}
    	catch(WebServiceException e){
    		e.printStackTrace();
    		sms.setResult("Failed::"+e.getMessage());
    	}
    	ds.saveSmsMessage(sms);
	    System.out.println("Done Testing SMPP Project ConnectionInfo Class.");
    }    
}
