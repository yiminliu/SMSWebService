package com.tscp.mvno.smpp.exception;


public class SmsNetworkException extends SmsException {
	private static final long serialVersionUID = -7412354082633059506L;

	public SmsNetworkException() {
		super();
	}
	
	public SmsNetworkException(String smsMessage) {
		super(smsMessage);
	}
		

	public SmsNetworkException(Object inputObject, String methodName, String destinationTN, String smsMessage) {
		super(inputObject, methodName, destinationTN, smsMessage);
	}
	
	public SmsNetworkException(Object inputObject, String methodName, String destinationTN, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, destinationTN, smsMessage, causeException);
	}
	
	public SmsNetworkException(Object inputObject, String methodName, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, smsMessage, causeException);
	}
}
