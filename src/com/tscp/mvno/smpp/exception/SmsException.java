package com.tscp.mvno.smpp.exception;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebServiceException;

@XmlRootElement
public class SmsException extends WebServiceException {
	private static final long serialVersionUID = -1984305003999836500L;
	/** This is the base class of SMS exceptions
	 * Value that can be referenced in the database with the exception transaction
	 * and parameters
	 */
	private String  destinationTN;
	private String className;
	private String methodName;
	//private String tscpmvneErrorCode;
	private String smsErrorMessage;
	//private String causeErrorCode;
	private String causeErrorMessage;
	private Throwable causeException = null;
		
	public SmsException() {
		//super();
	}

	public SmsException(String message) {
		super(message);
	}
	
	public SmsException(String message, Throwable t) {
		super(message, t);
	}
		
	public SmsException(Object obj, String methodName, String destinationTN, String smsErrorMessage) {
		super(smsErrorMessage);
		this.className = (obj == null? "" : obj.getClass().getName());
		this.methodName = methodName;
		this.destinationTN = destinationTN;
		this.smsErrorMessage = smsErrorMessage;
		//ErrorInfoUtil.logAndSaveMVNEException(this);
	}
		
	public SmsException(Object obj, String methodName, String smsErrorMessage, Throwable causeException) {
		super(smsErrorMessage, causeException);
		this.className = (obj == null? "" : obj.getClass().getName());
		this.methodName = methodName;
		this.smsErrorMessage = smsErrorMessage;
		this.causeErrorMessage = causeException == null? "" : causeException.getMessage();
		this.causeException = causeException;
		
		//ErrorInfoUtil.logAndSaveMVNEException(this);
	}
	
	public SmsException(Object obj, String methodName, String destinationTN, String smsErrorMessage, Throwable causeException) {
		super(smsErrorMessage, causeException);
		this.className = (obj == null? "" : obj.getClass().getName());
		this.methodName = methodName;
		this.destinationTN = destinationTN;
		this.smsErrorMessage = smsErrorMessage;
		this.causeErrorMessage = causeException == null? "" : causeException.getMessage();
		this.causeException = causeException;
		
		//ErrorInfoUtil.logAndSaveMVNEException(this);
	}
		
	
	public String getdestinationTN() {
		return destinationTN;
	}

	public void setdestinationTN(String destinationTN) {
		this.destinationTN = destinationTN;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMessage() {
		return causeErrorMessage;
	}
	
	public String getCauseErrorMessage() {
		return causeErrorMessage;
	}

	public void setCauseErrorMessage(String causeErrorMessage) {
		this.causeErrorMessage = causeErrorMessage;
	}
	
	public void setCauseException(Throwable causeException) {
		this.causeException = causeException;
	}
		
	public String getSmsErrorMessage() {
		return smsErrorMessage;
	}

	public void setSmsErrorMessage(String smsErrorMessage) {
		this.smsErrorMessage = smsErrorMessage;
	}

	public Throwable getCauseException(){
		return causeException;
	}

}
