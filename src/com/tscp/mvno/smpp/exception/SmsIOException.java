package com.tscp.mvno.smpp.exception;


public class SmsIOException extends SmsException {
	private static final long serialVersionUID = -7412354082633059506L;

	public SmsIOException() {
		super();
	}

	public SmsIOException(Object inputObject, String methodName, String destinationTN, String smsMessage) {
		super(inputObject, methodName, destinationTN, smsMessage);
	}
	
	public SmsIOException(Object inputObject, String methodName, String destinationTN, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, destinationTN, smsMessage, causeException);
	}
	
	public SmsIOException(Object inputObject, String methodName, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, smsMessage, causeException);
	}
}
