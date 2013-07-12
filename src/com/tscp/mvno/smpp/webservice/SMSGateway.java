package com.tscp.mvno.smpp.webservice;

import java.util.List;

import javax.jws.WebService;

import com.tscp.mvno.smpp.domain.Sms;
import com.tscp.mvno.smpp.exception.SmsException;
import com.tscp.mvno.smpp.helper.TSCPSMPPResponse;
import com.tscp.mvno.smpp.manager.SMSMessageManager;


@WebService(endpointInterface = "com.tscp.mvno.smpp.webservice.SMSGatewayWs")
public class SMSGateway implements SMSGatewayWs{
	
	private SMSMessageManager smsMessageManager = new SMSMessageManager();
	
	public SMSGateway(){}
			
	@Override
	public TSCPSMPPResponse sendSMSMessageString(String destinationTN, String message) throws SmsException{
		return smsMessageManager.processMessage(destinationTN, message);	
	}
	
	@Override
	public TSCPSMPPResponse sendSMSMessage(Sms smsMessage) throws SmsException{			
		return smsMessageManager.processMessage(smsMessage);
	}
		
	@Override
	public void sendSMSMessageList(List<Sms> smsMessageList) throws SmsException{			
		smsMessageManager.processMessage(smsMessageList);
	}	
}
