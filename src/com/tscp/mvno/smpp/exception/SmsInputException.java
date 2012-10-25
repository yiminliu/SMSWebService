package com.tscp.mvno.smpp.exception;

public class SmsInputException extends SmsException {

	/**This is the base class of SMS exceptions
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public SmsInputException() {
		super();
	}
	
	public SmsInputException(String smsMessage) {
		super(smsMessage);
	}
		
	public SmsInputException(Object inputObject, String methodName, String destinationTN, String smsMessage) {
		super(inputObject, methodName, destinationTN, smsMessage);
	}
	
	public SmsInputException(Object inputObject, String methodName, String destinationTN, String smsMessage, Throwable causeException) {
		super(inputObject, methodName, destinationTN, smsMessage, causeException);
	}
		
}

