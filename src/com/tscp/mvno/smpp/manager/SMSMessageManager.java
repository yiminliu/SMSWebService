package com.tscp.mvno.smpp.manager;

import ie.omk.smpp.Connection;
import ie.omk.smpp.NotBoundException;
import ie.omk.smpp.message.SubmitSM;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.domain.SMSMessage;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.exception.SmsIOException;
import com.tscp.mvno.smpp.exception.SmsInputException;
import com.tscp.mvno.smpp.exception.SmsNetworkException;
import com.tscp.mvno.smpp.service.LoggingService;
import com.tscp.mvno.smpp.service.SMSService;
import com.tscp.mvno.smpp.webservice.TSCPSMPPResponse;


@Service("smsMessageManager")
public class SMSMessageManager {

	//@Autowired
	private SMSService smsService;
	//@Autowired
	//private DatabaseService dbService;
	//@Autowired
	private LoggingService logger;	
	private Connection smppConnection;
	
	public SMSMessageManager() {	
		init();
	}
			    	
public TSCPSMPPResponse processMessage(String destinationTN, String message) throws SmsNetworkException, SmsIOException{	
    	validateInput(destinationTN, message);
		logger.info("**** Begin processMessage() for TN: "+ destinationTN + "****");
		TSCPSMPPResponse smppResponse = null;
		String result = null;
			
		try {
	       	smsService.bind(smppConnection);	    	   
	        //just propaganda the exceptions if they occur
	       	smppResponse = sendRequest(destinationTN, message);	       	
		}		
		catch(ConnectException ce){
			logger.error("ConnectException ocured while trying to bind SMSSC servcer, due to "+ ce.getMessage());
			result = ce.getMessage();
			throw new SmsNetworkException(this, "bind", destinationTN, "ConnectException occured while trying to bind SMSSC servcer", ce);
		}
		catch(NotBoundException nbe){
			logger.error("NotBoundException occured while binding, due to "+ nbe.getMessage());
	        result=nbe.getMessage();
			throw new SmsNetworkException(this, "bind", destinationTN, "NotBoundException occured while binding SMPP connection", nbe);
		}
		catch(SocketException se){
			logger.error("SocketException occured while binding, due to "+ se.getMessage());
	        result=se.getMessage();		
	    	throw new SmsIOException(this, "sendRequest", destinationTN, "SocketException occured while sending request, due to "+ se.getMessage(), se);
	    }
		catch(IOException ioe){
			logger.error("IOException occured while binding, due to "+ ioe.getMessage());
			result = ioe.getMessage();
			throw new SmsIOException(this, "bind", destinationTN, "IOException occured while binding SMPP connection", ioe);
		}	
		catch(Exception e){
			logger.error("Exception occured while binding, due to "+ e.getMessage());
			result = e.getMessage();
			throw new SmsException(this, "bind", destinationTN, "IOException occured while binding SMPP connection", e);
		}
		finally{
			//once we're done SMS messages, we want to unbind our connection.
		    //smsService.unbind(smppConnection);
		    cleanUp();
		}
		logger.info("Message was sent out successfully to: " + destinationTN);
	    logger.info("**** Finished processMessage with message String****");
		
	    return smppResponse;
	}
	
    public TSCPSMPPResponse processMessage(SMSMessage smsMessage) throws SmsNetworkException, SmsIOException{				
    	validateInput(smsMessage);
    	String destinationTN = smsMessage.getDestinationTN();
    	String messageText = smsMessage.getMessageText();    	
    	return processMessage(destinationTN, messageText);    	
	}
    
    public void processMessage(List<SMSMessage> smsList) throws SmsNetworkException, SmsIOException{				
    	logger.info("**** Begin processMessage with Message List****");
       	int messageCounter = 0;	
		String destinationTN = null;
	    try { 
		    for(int j = 0; j < smsList.size(); j++ ) {
		    	destinationTN = smsList.get(j).getDestinationTN();
	    	    try {
	    	    	smsService.bind(smppConnection);
	    	    	//Any exception occurred here will be re-thrown
				    sendRequest(destinationTN, smsList.get(j).getMessageText());
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
	    	    catch(SocketException ste){
	    	    	throw new SmsIOException(this, "sendRequest", destinationTN, "SocketException occured while sending request", ste);
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
	    	 smsService.unbind(smppConnection);
	    	 cleanUp();
		}
		logger.info("Total number of the destinations being sent to the messages = " + messageCounter);
		logger.info("**** Finished processMessage with Message List****");
    }
	
	public TSCPSMPPResponse sendRequest(String destinationTN, String message) throws SmsException{
		
		validateInput(destinationTN, message);
		TSCPSMPPResponse smppResponse = null;
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
		smppResponse = new TSCPSMPPResponse(smsService.sendRequest(smppConnection, shortMsg));
			
		if( smppResponse != null ) {
			logger.info("------ TSCPSMPPResponse -------");
			logger.info("TSCPSMPPResponse MessageID       = "+smppResponse.getMessageId());
			logger.info("TSCPSMPPResponse MessageStatus   = "+smppResponse.getMessageStatus());
			//logger.info("TSCPSMPPResponse Message         = "+smppResponse.getMessage());
			//logger.info("TSCPSMPPResponse MessageText     = "+smppResponse.getMessageText());
			//if( smppResponse.getMessageId() == null || smppResponse.getMessageId().trim().length() == 0 )
			logger.info("Message was sent out successfully to: " + destinationTN);
		} 
		else {
			logger.warn("TSCPSMPPResponse is null!!!");
		}		
		return smppResponse;
	}
	
	public void init(){
		   try{
			    //ApplicationContext appCtx = new ClassPathXmlApplicationContext("application-context.xml");	
			    //logger = (LoggingService)appCtx.getBean("loggingService");
			    //dbService = (DatabaseService)appCtx.getBean("databaseService");	
			    //smppService = (SMSService)appCtx.getBean("smppService");	
			    //smsMessageManager = (SMSMessageManager)appCtx.getBean("smsMessageManager");	
					
			    if(logger == null)
		           logger = new LoggingService(); 
				if(smsService == null)
			       smsService = new SMSService();				
				
		   }    
		   catch(RuntimeException e){
			  e.printStackTrace();
			  logger.error("Error occured while initializing connections, due to: " + e.getMessage());
		      throw e;
		   }		
		   try {
		      smppConnection = smsService.setupSMSCServerInfo();
		   }
		   catch(UnknownHostException uhe ) {
			    throw new SmsNetworkException("UnknownHostException occured during connecting to SMSC server, due to: " + uhe.getMessage());
		   }    					
	       catch(Exception e ) {	
	    	    e.printStackTrace();
	    	    throw new SmsNetworkException("Exception occured during connecting to SMSC server, due to: " + e.getMessage());
	       }	
		}	
	
	//@PreDestroy
    public void cleanUp() {
    	logger.trace("********** CleanUp **********");    
    	smsService.releaseConnection(smppConnection);
	}	
        
	private void validateInput(String destinationTN, String message) throws SmsInputException{	
	
		if(destinationTN == null || destinationTN.length() == 0)
			throw new SmsInputException("Destinatiion number is empty!");	
		if(destinationTN.length() !=10)
			throw new SmsInputException("Error with destination number. Please make sure the phone number is 10 digits and in the format of 'xxxxxxxxxx'");
		
		if(message == null || message.length() < 1)
			throw new SmsInputException("SMS message is empty!");
	}
	
	private void validateInput(SMSMessage smsMessage) throws SmsInputException{	
		
		if(smsMessage == null)
			throw new SmsInputException("SmsMessage object is null!");
		
		validateInput(smsMessage.getDestinationTN(), smsMessage.getMessageText());
	}
}
