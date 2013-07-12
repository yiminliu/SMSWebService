package com.tscp.mvno.smpp.service;

import ie.omk.smpp.Connection;
import ie.omk.smpp.NotBoundException;
import ie.omk.smpp.SMPPException;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPProtocolException;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.net.TcpLink;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.exception.SmsNetworkException;


/**
 * Class designed to open the connection using Sprint as the SMSC
 *
 */

@Service("smsService")
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
    private static LoggingService logger;    	    
	private Connection 	smppConnection = null;
					
	public SMSService(){
		init();
	}
	
	private final static void init() {
		Lock lock = new ReentrantLock();
		lock.lock();
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
		finally{
			lock.unlock();
		}
	}
	
	public Connection setupSMSCServerInfo() throws UnknownHostException{
				
		TcpLink		tcpLink = null;		
		logger.info("Begining setupSMSCServerInfo(). SMSC/Port: " + sprintSmscSite + "/" + sprintSmscPort);
		
		Lock lock = new ReentrantLock();
		lock.lock();
		try {		
			tcpLink = new TcpLink(sprintSmscSite, sprintSmscPort);
			//Initialize a new SMPP connection object in asyncrhonous mode
			smppConnection = new Connection(tcpLink, true);   
			logger.info("Connection is established to SMSC/Port: " + sprintSmscSite + "/" + sprintSmscPort);
			//smppConnection.autoAckMessages(true);	//only valid in asynchronous mode	    
		}
		catch(UnknownHostException uhe ) {
			throw uhe;
		}    		
		finally{
			lock.unlock();
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
	 * 	State:
	 * 	UNBOUND   =>  0
	 *  BINDING   =>  1
    	BOUND     =>  2
       	UNBINDING =>  3 
     * @return
	 */
	public boolean bindConnectionToSMSC(Connection smppConnection) throws NotBoundException, IOException, Exception{
		boolean failed = false;
		BindResp response = null;
		if(smppConnection == null)
		   throw new SmsException("SMPPConection is null");
		
		logger.info("Beginnig to bind SMPP Connection to the SMSC Server");
		Lock lock = new ReentrantLock();
		lock.lock();
		try {
		   if(!smppConnection.isBound()) {
		      try {			  
			     response = smppConnection.bind(Connection.TRANSMITTER, telscapeUserName, telscapePassword, systemType);			  
			     if( response != null ) {
				     logger.info("Binding response System ID :: "+response.getSystemId());
					 logger.info("Binding response Destination :: "+response.getDestination());
				  }
		      } 
		      catch(ConnectException ce){
		         failed = true;
				 throw ce;
		      }		   
		      catch(NotBoundException nbe){
			     failed = true;
			     throw nbe;
		      }
		      catch(IOException ioe){
			     failed = true;
			     throw ioe;
		      }
		      catch(Exception e){
			     // logger.error("!!Binding Exception Occurred, due to: " + e.getMessage());
			     failed = true;			  
			     throw e;
		      }
		   }    
		}
		finally{
		    lock.unlock();
			if (failed)
				releaseConnection(smppConnection);
	    } 
		logger.info("SMPP Connection bound succesfully");
		logger.info("Finished Binding SMPP Connection");
		return true;
	}
	
	public boolean unbindConnectionFromSMSC(Connection smppConnection){ 
	    if(smppConnection != null && smppConnection.isBound()){
  		     logger.info("Begining Unbinding SMPP Connection ");
		     try {
	             smppConnection.unbind();
		         logger.info("SMPP Connection Unbound successfully");
		     } 
		     catch (SMPPProtocolException smppe) {
			     logger.warn("!!UnBinding SMPPProtocolException Occurred, due to: "+ smppe.getMessage());
			     smppConnection.force_unbind();
		     }		
		     catch (IOException ioe) {
				logger.error("!!UnBinding IOException Occurred, due to: "+ ioe.getMessage());
				smppConnection.force_unbind();
		     }	
		     logger.info("Finished Unbinding SMPP Connection");
	    }
	    return true;
	}

	public void releaseConnection(Connection smppConnection) {
	   Lock lock = new ReentrantLock();
	   lock.lock();
	   try {
   	       if(smppConnection != null ) {
		      try {
			    unbindConnectionFromSMSC(smppConnection);
		      } 
		      catch(Exception ex) {
			     logger.warn("Exception occured while unbinding connection from SMSC due to "+ ex.getMessage());			  			  
  		      }
		      try {
			     smppConnection.closeLink();
		      } 
		      catch(Exception ex) {
			     logger.warn("Exception occured while closing connection.");
			     smppConnection = null;
		      }
		      finally{
			    smppConnection = null;
		      }
   	       }
	   }   
		finally{
			lock.unlock();
		}			
	}		

	public SMPPResponse sendRequest(Connection smppConnection, SubmitSM shortMsg) throws SmsException{
		
		SMPPResponse response = null;		
	    if(smppConnection == null)
	       throw new SmsException("SMCC Connection is null");
	    if(!smppConnection.isBound()) {
	    	releaseConnection(smppConnection);
	    	throw new SmsException("SMCC Connection is not bound, not able to send the sms message to "+ shortMsg.getDestination().getAddress());
	    }
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
