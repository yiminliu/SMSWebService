package com.tscp.mvno.smpp.service;

import ie.omk.smpp.Connection;
import ie.omk.smpp.NotBoundException;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.net.TcpLink;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.exception.SmsNetworkException;


/**
 * Class designed to open the connection using Sprint as the SMSC
 *
 */

@Service("smppService")
public class SMSService {
 
    public static String inputProperties = "client.properties";
	   
	private static String		sprintSmscSite;
	private static int			sprintSmscPort;		
	private static String		telscapeUserName;
	private static String		telscapePassword;
	private static String		systemType;
	private static int			maximumMessageCount;
	private static String 		shortCode;
	
    @Autowired
    private LoggingService logger;    	    
	private Connection 	smppConnection = null;
					
	public SMSService(){
		init();
	}
	
	private void init() {
		try {
			Properties props = new Properties();
			ClassLoader cl = SMSService.class.getClassLoader();
			InputStream in = cl.getResourceAsStream(inputProperties);
			if(in != null) {
			   props.load(in);
			}			
			sprintSmscSite 		= props.getProperty("SMSC.URL", "68.28.216.140");
		    sprintSmscPort 		= Integer.parseInt(props.getProperty("SMSC.PORT", "16910"));
		    telscapeUserName	= props.getProperty("TSCP.USERNAME", "tscp");
		    telscapePassword	= props.getProperty("TSCP.PASSWORD", "tscp2008");
		    systemType			= props.getProperty("SYSTEM.TYPE", "systype");
		    maximumMessageCount	= Integer.parseInt(props.getProperty("MESSAGE.MAXIMUM","100"));
		    shortCode			= props.getProperty("SHORT.CODE", "87276");	
		    
		    if(logger == null)
		    	logger = new LoggingService();
		} 
		catch(IOException ioe) {
			logger.error("Error loading properties file!! due to: " + ioe.getMessage());	
			throw new RuntimeException(ioe);
		}
		catch(RuntimeException rte) {
			logger.error("Error loading properties file!! due to: " + rte.getMessage());	
			throw rte;
		}
	}
	
	public Connection setupSMSCServerInfo() throws UnknownHostException, Exception{
				
		TcpLink		tcpLink = null;		
		logger.info("Begining setupSMSCServerInfo(). SMSC/Port: " + sprintSmscSite + "/" + sprintSmscPort);
		try {		
			tcpLink = new TcpLink(sprintSmscSite,sprintSmscPort);
			smppConnection = new Connection(tcpLink);   
			smppConnection.autoAckMessages(true);		    
		}
		catch(UnknownHostException uhe ) {
			logger.error("UnknownHostException during setting up SMSC server info, due to: " + uhe.getMessage());	    	
			throw uhe;
		}    					
	    catch(Exception e ) {	
	    	logger.error("Exception occured during setting up SMSC server info, due to: " + e.getMessage());
	    	if(smppConnection != null ) 
	    	   releaseConnection(smppConnection);
	    	throw e;
	    }	
	    logger.info("Finished setupSMSCServerInfo()"); 
	    return smppConnection;	
	}  
	
	/**
	 * Binding in SMPP is the same as logging into the remote SMSC.
	 * <p>Important parameters for Connection.bind() are:
	 * <table border=true>
	 * 	<th>Field</th><th>datatype</th>
	 * 	<tr><td>Connection Type</td><td align=center>int</td></tr>
	 * 	<tr><td>System ID</td><td align=center>String</td></tr>
	 * 	<tr><td>Password</td><td align=center>String</td></tr>
	 * 	<tr><td>System Type</td><td align=center>String</td></tr>
	 * </table>
	 * </p>
	 * @return
	 */
	public boolean bind(Connection smppConnection) throws NotBoundException, IOException, Exception{
		BindResp response = null;
		if(smppConnection == null)
		   throw new SmsException("SMPPConection is null");
		
		logger.info("Begin bind SMPP Connection");
		if(!smppConnection.isBound()) {
		   try {			  
			  response = smppConnection.bind(Connection.TRANSMITTER, telscapeUserName, telscapePassword, systemType);			  
	       } 
		   catch(ConnectException ce){
				 logger.error("ConnectException occured while tring to bind SMSSC server, due to "+ ce.getMessage());
				 throw ce;
		   }		   
		   catch(NotBoundException nbe){
			  logger.error("NotBoundException occured while binding, due to "+ nbe.getMessage());
			  throw nbe;
		   }
		   catch(IOException ioe){
				  logger.error("IOException occured while binding, due to "+ ioe.getMessage());
				  throw ioe;
		   }
		   catch(Exception e){
			  logger.error("!!Binding Exception Occurred, due to: " + e.getMessage());
			  if(smppConnection != null ) {
				 releaseConnection(smppConnection);
			  }
			  throw e;
		   }
		   if( response != null ) {
			     logger.info("Binding response System ID :: "+response.getSystemId());
				 logger.info("Binding response Destination :: "+response.getDestination());
			  }
		} 
		logger.info("SMPP Connection bound succesfully");
		logger.info("Finished Binding SMPP Connection");
		return true;
	}
	
	public boolean unbind(Connection smppConnection){ 
	   if(smppConnection != null){
  		  logger.info("Begining Unbinding SMPP Connection ");
		  try {
			  if(smppConnection.isBound() ) {
		         smppConnection.unbind();
		         logger.info("SMPP Connection Unbound successfully");
			  }					
		  } 
		  catch (Exception e) {
			logger.error("!!UnBinding Exception Occurred, due to: "+ e.getMessage());
	      }		
		  logger.info("Finished Unbinding SMPP Connection");
	   }
	   return true;
	}

	public void releaseConnection(Connection smppConnection) {
   	   if(smppConnection != null ) {
		  try {
			  if(smppConnection.isBound()){
			     smppConnection.unbind();
			  }   
			  smppConnection.closeLink();
		  } 
		  catch(Exception ex) {
				smppConnection = null;
		  }
		  finally{
			 smppConnection = null;
		  }
		}	
	}		

	public SMPPResponse sendRequest(Connection smppConnection, SubmitSM shortMsg) throws SmsException{
		
		SMPPResponse response = null;		
	    if(smppConnection == null)
	       throw new SmsException("SMCC Connection is null");
	    
	    try {
		   response = smppConnection.sendRequest(shortMsg);
	    }
	    catch(SocketTimeoutException ste){
	    	throw new SmsNetworkException(this, "sendRequest", shortMsg.getDestination().getAddress(), "SocketTimeoutException occured while sending request", ste);
	    }
	    catch(IOException ioe){
	    	throw new SmsNetworkException(this, "sendRequest", shortMsg.getDestination().getAddress(), "IOException occured while sending request", ioe);
	    }
	    return response;
	}
	
	public String getTelscapeUserName() {
		return telscapeUserName;
	}
	
	public String getTelscapePassword() {
		return telscapePassword;
	}
	
	public String getSystemType() {
		return systemType;
	}
	
	public int getMaximumMessageCount() {
		return maximumMessageCount;
	}
	
	public static String getShortCode() {
		return shortCode;
	}	
}
