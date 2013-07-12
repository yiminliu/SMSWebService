package com.tscp.mvno.smpp.service;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.Sms;

@Service
@Scope("singleton")
public class DatabaseService {

    @Autowired
    private SmsDao hibernateSmsDao;  
     
    public DatabaseService(){}
                  
    public int saveSmsMessage(Sms sms){
    	return hibernateSmsDao.saveSmsMessage(sms);
    }
    
    private void initForTest() {    	
    	ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");
    	hibernateSmsDao = (SmsDao)appCtx.getBean("hibernateSmsDao");
    }    
    
    public static void main(String[] args) { 
    	    	  	
    	DatabaseService ds = new DatabaseService();
    	ds.initForTest();
    	System.out.println("Testing SMPP Project ConnectionInfo class....");
    	Sms sms = new Sms();
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
