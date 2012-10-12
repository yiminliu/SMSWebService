package com.tscp.mvno.smpp.exception;

public class InputException extends SmsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private Object inputObject;
	
	public InputException() {
		super();
	}
	
	public InputException(String smsMessage) {
		super(smsMessage);
	}
		
	public InputException(Object inputObject, String methodName, String destinationTN, String smsMessage) {
		super(inputObject, methodName, destinationTN, smsMessage);
	}
	
	public InputException(Object inputObject, String methodName, String destinationTN, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, destinationTN, smsMessage, causeException);
	}
		
}

