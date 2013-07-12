package com.tscp.mvno.smpp.webservice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import javax.xml.ws.WebServiceException;

import com.tscp.mvno.smpp.domain.Sms;
import com.tscp.mvno.smpp.helper.TSCPSMPPResponse;

@WebService
//@SOAPBinding(style = Style.RPC)
public interface SMSGatewayWs {
				
		@WebMethod
		public TSCPSMPPResponse sendSMSMessageString(String destinationTN, String message) throws WebServiceException;
		
		@WebMethod
		public TSCPSMPPResponse sendSMSMessage(Sms smsMessage) throws WebServiceException;
				
		@WebMethod
		public void sendSMSMessageList(List<Sms> smsMessageList) throws WebServiceException;				
}
