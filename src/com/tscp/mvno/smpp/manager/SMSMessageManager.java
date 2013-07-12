package com.tscp.mvno.smpp.manager;

import ie.omk.smpp.AlreadyBoundException;
import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.version.VersionException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.dao.HibernateSmsDao;
import com.tscp.mvno.smpp.dao.SmsDao;
import com.tscp.mvno.smpp.domain.Sms;
import com.tscp.mvno.smpp.domain.SmsFailed;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.exception.SmsIOException;
import com.tscp.mvno.smpp.exception.SmsInputException;
import com.tscp.mvno.smpp.exception.SmsNetworkException;
import com.tscp.mvno.smpp.helper.TSCPSMPPResponse;
import com.tscp.mvno.smpp.service.DatabaseService;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMSService;
import com.tscp.mvno.smpp.util.mail.MailClient;


@Service("smsMessageManager")
public class SMSMessageManager {

	@Autowired
	private SMSService smsService;
	@Autowired
	private DatabaseService dbService;
	@Autowired
	private LoggingService logger;	
	@Autowired 
	private SmsDao hibernateSmsDao;
	
	private static boolean initialized = false;
			
	public SMSMessageManager() {
		if(!initialized)
		   init();
	}
			    	
	public TSCPSMPPResponse processMessage(String destinationTN, String message) throws SmsNetworkException, SmsIOException, SmsException{	
		Connection smppConnection = null;
		logger.info("**** Begin processMessage() for TN: "+ destinationTN + "****");
		TSCPSMPPResponse smppResponse = null;
		String result = "Success";			
		try {
		    smppConnection = connectAndBindToSMSC();
		   	smppResponse = sendRequest(smppConnection, destinationTN, message);	       	
		}		
		catch(SmsException se ) {
			result = se.getSmsErrorMessage();
		    SaveAndThrowError(destinationTN, message, se);
		}   		
		finally{
			if("Success".equalsIgnoreCase(result)) {
			   try {
				   dbService.saveSmsMessage(new Sms(destinationTN, message, result));
			   }
			   catch(Exception e){
				  logger.warn("Exception occured while saving SMS info to db, due to "+ e.getMessage());
			   }
			}  
			else {
				sendSmsFailedNotification(result);
			}
			releaseConnection(smppConnection);
	 	}		
	    logger.info("Message was sent out successfully to: " + destinationTN);			
	    return smppResponse;
	}
	
    public TSCPSMPPResponse processMessage(Sms smsMessage) throws SmsNetworkException, SmsIOException{				
    	validateInput(smsMessage);
    	return processMessage(smsMessage.getDestinationTN(), smsMessage.getMessageText());
    } 	
    
    public void processMessage(List<Sms> smsList) throws SmsNetworkException, SmsIOException, SmsException{				
    	Connection smppConnection = null;
    	logger.info("**** Begin processMessage with Message List****");
       	int messageCounter = 0;	
		String destinationTN = null;
		String message = "";
		String result = "Success";
		try {
			smppConnection = connectAndBindToSMSC();
    	}
		catch(SmsException se ) {
			result = se.getSmsErrorMessage();
		    SaveAndThrowError(destinationTN, "", se);
		}  
		try { 
		    for(int j = 0; j < smsList.size(); j++ ) {
		    	destinationTN = smsList.get(j).getDestinationTN();
	    	    try {
	       	        sendRequest(smppConnection, destinationTN, message);
			        logger.info("Message was sent out successfully to: " + destinationTN);
			    }	    	    
	    	    catch(SmsException se){
	    	         result = se.getSmsErrorMessage();	
	    	         logger.error("Exception occured in processMessage(): " + se.getSmsErrorMessage());
				     if(j == smsList.size() - 1)			
				    	SaveAndThrowError(destinationTN, message, se); 
				    	//throw new SmsException(this, "sendRequest", destinationTN, "Exception occured while sending Request", e);
			    }
	    	    finally{
	    			if("Success".equalsIgnoreCase(result)) {
	    			   try {
	    				   dbService.saveSmsMessage(new Sms(destinationTN, message, result));
	    			   }
	    			   catch(Exception e){
	    				  logger.warn("Exception occured while saving SMS info to db, due to "+ e.getMessage());
	    			   }
	    			}  
	    			else {
	    				sendSmsFailedNotification(result);
	    			}
	    	    }		
			    ++messageCounter;
		    }    
		 }
	     finally{
			//once we're done traversing the list of pending SMS messages, we want to unbind our connection.
	    	 releaseConnection(smppConnection);
		}
		logger.info("Total number of the destinations being sent to the messages = " + messageCounter);
		logger.info("**** Finished processMessage with Message List****");
    }
	
	private TSCPSMPPResponse sendRequest(Connection smppConnection, String destinationTN, String message) throws SmsException{
		
		validateInput(destinationTN, message);
		//SMPPResponse smppResponse = null;
		TSCPSMPPResponse tscpSmppResponse = null;
		ie.omk.smpp.message.SubmitSM shortMsg = new SubmitSM();
		try {			
			ie.omk.smpp.Address destAddress = new ie.omk.smpp.Address();
			ie.omk.smpp.Address sendAddress = new ie.omk.smpp.Address();
			
			destAddress.setAddress(destinationTN);						
			sendAddress.setTON(0);
			sendAddress.setNPI(0);
			sendAddress.setAddress(SMSService.getShortCode());
			shortMsg.setDestination(destAddress);
			shortMsg.setSource(sendAddress);
			shortMsg.setMessageText(message);			
		} 
		catch(Exception e ) {
			//logger.error("!!Error sending request!! due to:  " + e.getMessage());
			//throw new SmsException(this, "sendRequest", destinationTN, "Exception occured in sendRequest()", e);
			logAndThrowError("sendRequest", e, new SmsException());

		}			
		logger.info("------ SMPPRequest -------");
		//logger.info("SMPPRequest Source Address   = "+shortMsg.getSource().getAddress());
		logger.info("SMPPRequest Dest Address     = "+shortMsg.getDestination().getAddress());
		logger.info("SMPPRequest Message Text     = "+shortMsg.getMessageText());
		
		try {
			tscpSmppResponse = new TSCPSMPPResponse(smsService.sendRequest(smppConnection, shortMsg));
		}		
		catch(AlreadyBoundException abe){
			logAndThrowError("sendRequest", abe, new SmsException());	
		}
		catch(VersionException ve){
			logAndThrowError("sendRequest", ve, new SmsException());
		}
		catch(SMPPProtocolException se){
			logAndThrowError("sendRequest", se, new SmsException());
		}
		catch(UnsupportedOperationException ue){
			logAndThrowError("sendRequest", ue, new SmsException());
		}
		catch(Exception e){
			logAndThrowError("sendRequest", e, new SmsException());
		}	
		if( tscpSmppResponse != null ) {
			logger.info("------ TSCPSMPPResponse -------");
			logger.info("TSCPSMPPResponse MessageID       = "+tscpSmppResponse.getMessageId());
			logger.info("TSCPSMPPResponse MessageStatus   = "+tscpSmppResponse.getMessageStatus());
			//logger.info("TSCPSMPPResponse Message         = "+smppResponse.getMessage());
			//logger.info("TSCPSMPPResponse MessageText     = "+smppResponse.getMessageText());
			//if( smppResponse.getMessageId() == null || smppResponse.getMessageId().trim().length() == 0 )
			logger.info("Message was sent out successfully to: " + destinationTN);
		} 
		else {
			logger.warn("TSCPSMPPResponse is null!!!");
		}		
		return tscpSmppResponse;
	}
	
	@PostConstruct 
	public void init(){	
		try {
		    ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");	
	        logger = (LoggingService)appCtx.getBean("loggingService");
	        dbService = (DatabaseService)appCtx.getBean("databaseService");	
	        smsService = (SMSService)appCtx.getBean("smsService");	
	        hibernateSmsDao = (HibernateSmsDao)appCtx.getBean("hibernateSmsDao");
	        initialized = true;
		}
		catch(Exception e){
			e.printStackTrace();
			if(logger == null) logger = new LoggingService(); 
			if(smsService == null) smsService = new SMSService();
			if(dbService == null) dbService = new DatabaseService();		
			if(hibernateSmsDao == null) hibernateSmsDao = new HibernateSmsDao();				
		}    
	}
	
	private Connection connectAndBindToSMSC()throws SmsNetworkException, SmsIOException, SmsException {
		Connection smppConn = null;
		try {
		    smppConn = smsService.setupSMSCServerInfo();
		   	smsService.bindConnectionToSMSC(smppConn);
	    }
		catch(UnknownHostException uhe ) {
			logAndThrowError("connectAndBindToSMSC", uhe, new SmsNetworkException());
		}   
		catch(ConnectException ce){
    		logAndThrowError("connectAndBindToSMSC", ce, new SmsNetworkException());
		}
		catch(IOException ioe){
			logAndThrowError("connectAndBindToSMSC", ioe, new SmsIOException()); 
		}	
		catch(Exception e){
			//logger.error("Exception occured while creating/binding SMPP connection, due to "+ e.getMessage());
			//throw new SmsException(this, "connectAndBindToSMSC", "Exception occured while creating/binding SMPP connection, due to "+ e.getMessage(), e);
			//logSaveAndThrowError("Exception ocured while creating/binding SMPP connection, due to "+ e.getMessage(), new SmsException(this, "connectAndBindToSMSC", "Exception occured while creating/binding SMPP connection, due to "+ e.getMessage(), e));
			logAndThrowError("connectAndBindToSMSC", e, new SmsException());
		}		
	    return smppConn;
	}
			    
    private SmsException logAndThrowError(String methodName, Throwable causeException, SmsException smsException){
		try {
		    String smsErrorMessage = causeException.getClass().getName() + " occured in " + methodName + " due to: " + causeException.getMessage(); 	
			logger.error(smsErrorMessage);
			smsException.setSmsErrorMessage(smsErrorMessage);
			smsException.setClassName(this.getClass().getName());
			smsException.setMethodName(methodName);
			smsException.setCauseException(causeException);
			smsException.setCauseErrorMessage(causeException.getMessage());
		}
	    catch(Exception e){
			logger.warn("Exception occured in logAndThrowError(), due to "+ e.getMessage());
		}
	   	throw smsException;
    }	
		    
	private SmsException SaveAndThrowError(String destinationTN, String smsMessageText, SmsException smsException){
		try {
		    SmsFailed smsFailed = new SmsFailed(smsException);
			Sms sms = new Sms();
	        sms.setDestinationTN(destinationTN);
	        sms.setMessageText(smsMessageText);
	    	sms.setResult("Failed :: "+ smsException.getSmsErrorMessage());
			//sms.setSmsFailed(smsFailed);
		    dbService.saveSmsMessage(sms);
		}
		catch(Exception e){
	  	   logger.warn("Exception occured while saving SMS info to db, due to "+ e.getMessage());
	  	   throw smsException;
		}
		throw smsException;		
	}
		
	private void validateInput(String destinationTN, String message) throws SmsInputException{	
	
		if(destinationTN.length() !=10)
			throw new SmsInputException("Erro with destination phone number. Please make sure the phone number is 10 digits and in the format of 'xxxxxxxxxx'");
		
		if(message.length() < 1)
			throw new SmsInputException("SMS message is empty");
	}
	
	private void validateInput(Sms smsMessage) throws SmsInputException{	
		
		if(smsMessage == null)
			throw new SmsInputException("SmsMessage is null!");
		
		validateInput(smsMessage.getDestinationTN(), smsMessage.getMessageText());
	}
	
	@PreDestroy
    public void releaseConnection(Connection smppConnection) {
    	logger.trace("********** CleanUp **********");    
    	smsService.releaseConnection(smppConnection);
    	logger.trace("********** connection was released **********");
	}	
		
	private void sendSmsFailedNotification(String FailureMessage) {
		try{
		   MailClient.sendHTML("yliu@telscape.net", "yliu@telscape.net", "SMS Failure", FailureMessage);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) { 	      
		 System.out.println("Testing SMSGateway...");
		 SMSMessageManager smsApp = new SMSMessageManager();
		 List<Sms> smsList = new ArrayList<Sms>();
		 Sms sms1 = new Sms("2132566431", "test");
		 Sms sms2 = new Sms("6262566412", "test");	
		 smsList.add(sms1);
		 smsList.add(sms2);
		 
  	     try {
	        smsApp.processMessage("2132566431", "test");
	        //smsApp.processMessage(smsList);        
	     }
  	     catch(SmsException se){
  	    	System.out.println("SMS failed with an SmsException, due to : "+se.getMessage()); 
	    	se.printStackTrace();
  	     }
	     catch(WebServiceException e){
	    	System.out.println("SMS failed due to : "+e.getMessage()); 
	    	e.printStackTrace();
	     }
	     System.out.println("Done Testing SMSGateway.");
	 } 	
}
