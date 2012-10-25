package com.tscp.mvno.smpp.webservice;


import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import javax.xml.ws.WebServiceException;

import com.tscp.mvno.smpp.domain.SMSMessage;

@WebService
//@SOAPBinding(style = Style.RPC)
public interface SMSAppWs {
				
		@WebMethod
		public TSCPSMPPResponse sendSMSMessageString(String destinationTN, String message) throws WebServiceException;
		
		@WebMethod
		public TSCPSMPPResponse sendSMSMessage(SMSMessage smsMessage) throws WebServiceException;
				
		@WebMethod
		public void sendSMSMessageList(List<SMSMessage> smsMessageList) throws WebServiceException;
				
}
