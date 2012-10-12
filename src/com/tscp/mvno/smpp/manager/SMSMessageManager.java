package com.tscp.mvno.smpp.manager;

import ie.omk.smpp.Connection;
import ie.omk.smpp.NotBoundException;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.AlertAction;
import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.exception.SmsIOException;
import com.tscp.mvno.smpp.exception.SmsNetworkException;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMSService;


@Service("smsMessageManager")
public class SMSMessageManager {

	@Autowired
	private SMSService smppService;
	@Autowired
	private LoggingService logger;
	
	private Connection smppConnection;
	
	public SMSMessageManager() {	
		init();
	}
	
	public void init(){
	   try{
		    //ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");	
		    //logger = (LoggingService)appCtx.getBean("loggingService");
		    //dbService = (DatabaseService)appCtx.getBean("databaseService");	
		    //smppService = (SMSService)appCtx.getBean("smppService");	
		    //smsMessageManager = (SMSMessageManager)appCtx.getBean("smsMessageManager");	
						      		
			logger = new LoggingService(); 
			smppService = new SMSService();
			
	   }    
	   catch(RuntimeException e){
		  e.printStackTrace();
		  logger.error("Error occured while initializing connections, due to: " + e.getMessage());
	      throw e;
	   }		
	   try {
	      smppConnection = smppService.setupSMSCServerInfo();
	   }
	   catch(UnknownHostException uhe ) {
		    throw new SmsNetworkException("UnknownHostException occured during connecting to SMSC server, due to: " + uhe.getMessage());
	   }    					
       catch(Exception e ) {	
    	    e.printStackTrace();
    	    throw new SmsNetworkException("Exception occured during connecting to SMSC server, due to: " + e.getMessage());
    		
       }	
	}	
	    	
	public String processMessage(String destinationTN, String message) throws SmsNetworkException, SmsIOException{	
		
		logger.info("**** Begin processMessage() for TN: "+ destinationTN + "****");
		String messageId = null;
			    		
		try {
	       	smppService.bind(smppConnection);	    	   
	        //just propaganda the exceptions if they occur
	       	messageId = sendRequest(destinationTN, message);		    
		}		
		catch(ConnectException ce){
			logger.error("ConnectException ocured while trying to bind SMSSC servcer, due to "+ ce.getMessage());
			throw new SmsNetworkException(this, "bind", destinationTN, "ConnectException occured while trying to bind SMSSC servcer", ce);
		}
		catch(NotBoundException nbe){
			logger.error("NotBoundException occured while binding, due to "+ nbe.getMessage());
			throw new SmsNetworkException(this, "bind", destinationTN, "NotBoundException occured while binding SMPP connection", nbe);
		}
		catch(IOException ioe){
			logger.error("IOException occured while binding, due to "+ ioe.getMessage());
			throw new SmsIOException(this, "bind", destinationTN, "IOException occured while binding SMPP connection", ioe);
		}	
		catch(Exception e){
			logger.error("Exception occured while binding, due to "+ e.getMessage());
			throw new SmsException(this, "bind", destinationTN, "IOException occured while binding SMPP connection", e);
		}
		finally{
			//once we're done sending SMS message, we want to unbind our connection.
	    	 smppService.unbind(smppConnection);
	    	 cleanUp();
	 	}
	    logger.info("Message was sent out successfully to: " + destinationTN + " Message ID = " + messageId);
	    logger.info("**** Finished processMessage with message String****");
		
	    return messageId;
	}
	
    public String processMessage(SMSMessage smsMessage) throws SmsNetworkException, SmsIOException{				
    	logger.info("**** Begin processMessage with Message Object****");
    	String messageId = null;				
	    try {
	       	smppService.bind(smppConnection);	
	       	//Any exception occurred here will be rethrown
		    messageId = sendRequest(smsMessage.getDestinationTN(), smsMessage.getMessage());
		    logger.info("Message was sent out successfully to: " + smsMessage.getDestinationTN());
		}
	    catch(ConnectException ce){
			logger.error("ConnectException ocured while trying to bind SMSSC servcer, due to "+ ce.getMessage());
			throw new SmsNetworkException(this, "bind", smsMessage.getDestinationTN(), "ConnectException occured while trying to bind SMSSC servcer", ce);
		}
	    catch(NotBoundException nbe){
			logger.error("NotBoundException occured while binding, due to "+ nbe.getMessage());
			throw new SmsNetworkException(this, "bind", smsMessage.getDestinationTN(), "NotBoundException occured while binding SMPP connection", nbe);
		}
		catch(IOException ioe){
			logger.error("IOException occured while binding, due to "+ ioe.getMessage());
			throw new SmsIOException(this, "bind", smsMessage.getDestinationTN(), "IOException occured while binding SMPP connection", ioe);
		}	
		catch(Exception e){
			logger.error("Exception occured while binding, due to "+ e.getMessage());
			throw new SmsException(this, "bind", smsMessage.getDestinationTN(), "IOException occured while binding SMPP connection", e);
		}
		finally{
			//once we're done sending SMS message, we want to unbind our connection.
	    	 smppService.unbind(smppConnection);
	    	 cleanUp();
	 	}
	    logger.info("Message was sent out successfully to: " + smsMessage.getDestinationTN() + " Message ID = " + messageId);
	    logger.info("**** Finished processMessage with Message Object ****");
	    return messageId;
	}
    
    public void processMessage(List<SMSMessage> smsList) throws SmsNetworkException, SmsIOException{				
    	logger.info("**** Begin processMessage with Message List****");
    	int messageCounter = 0;	
		String destinationTN = null;
	    try { 
		    for(int j = 0; j < smsList.size(); j++ ) {
		    	destinationTN = smsList.get(j).getDestinationTN();
	    	    try {
	    	    	smppService.bind(smppConnection);
	    	    	//Any exception occurred here will be re-thrown
				    sendRequest(destinationTN, smsList.get(j).getMessage());
			        logger.info("Message was sent out successfully to: " + destinationTN);
			    }
	    	    catch(ConnectException ce){
	    			logger.error("ConnectException ocured while trying to bind SMSSC servcer, due to "+ ce.getMessage());
	    			if(j == smsList.size() - 1)
	    			throw new SmsNetworkException(this, "bind", destinationTN, "ConnectException occured while trying to bind SMSSC servcer", ce);
	    		}
	    	    catch(NotBoundException nbe){
					logger.error("NotBoundException occured while binding, due to "+ nbe.getMessage());
					if(j == smsList.size() - 1)
                       throw new SmsNetworkException(this, "bind", destinationTN, "NotBoundException occured while binding SMPP connection", nbe);
				}
	    	    catch(IOException ioe){
	    			logger.error("IOException occured while binding, due to "+ ioe.getMessage());
	    			if(j == smsList.size() - 1)
  	    			   throw new SmsIOException(this, "bind", destinationTN, "IOException occured while binding SMPP connection", ioe);
	    		}	
			    catch(Exception e){
			         logger.error("Exception occured in processMessage(): " + e.getMessage());
				     if(j == smsList.size() - 1)			
				    	 throw new SmsException(this, "bind", destinationTN, "Exception occured while binding SMPP connection", e);
				 	
			    }
			    
			    ++messageCounter;
		    }    
		 }
	     finally{
			//once we're done traversing the list of pending SMS messages, we want to unbind our connection.
	    	 smppService.unbind(smppConnection);
	    	 cleanUp();
		}
		logger.info("Total number of the destinations being sent to the messages = " + messageCounter);
		logger.info("**** Finished processMessage with Message List****");
    }
	
	public String sendRequest(String destinationTN, String message) throws SmsException{
		
		String messageId = null;
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
			logger.error("!!Error sending request!! due to:  " + e.getMessage());
			throw new SmsException(this, "sendRequest", destinationTN, "Exception occured in sendRequest()", e);

		}			
		logger.info("------ SMPPRequest -------");
		//logger.info("SMPPRequest Source Address   = "+shortMsg.getSource().getAddress());
		logger.info("SMPPRequest Dest Address     = "+shortMsg.getDestination().getAddress());
		logger.info("SMPPRequest Message Text     = "+shortMsg.getMessageText());
		
		//just propaganda the exceptions if they occur
		SMPPResponse smppResponse = smppService.sendRequest(smppConnection, shortMsg);
			
		if( smppResponse != null ) {
			logger.info("------ SMPPResponse -------");
			logger.info("SMPPResponse MessageID       = "+smppResponse.getMessageId());
			logger.info("SMPPResponse MessageStatus   = "+smppResponse.getMessageStatus());
			//logger.info("SMPPResponse Message         = "+smppResponse.getMessage());
			//logger.info("SMPPResponse MessageText     = "+smppResponse.getMessageText());
			//if( smppResponse.getMessageId() == null || smppResponse.getMessageId().trim().length() == 0 )
			messageId = smppResponse.getMessageId();
			logger.info("Message was sent out successfully to: " + destinationTN);
		} 
		else {
			logger.warn("SMPPResponse is null!!!");
		}		
		return messageId;
	}	
	
	/*
	private void validateInput(String destinationTN, String message) throws InputException{	
	
		if(destinationTN.length() !=10)
			throw new InputException("Please make sure the phone number is correct and in the format of 'xxxxxxxxxx'");
		
		if(message.length() < 1)
			throw new InputException("SMS message is empty");

	}
	*/
	
	//@PreDestroy
    public void cleanUp() {
    	logger.trace("********** CleanUp **********");    
    	smppService.releaseConnection(smppConnection);
	}	
}
