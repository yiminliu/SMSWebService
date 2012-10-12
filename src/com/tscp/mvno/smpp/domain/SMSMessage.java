package com.tscp.mvno.smpp.domain;

public class SMSMessage {
	private String destinationTN;
	private String messageText;
	
	public SMSMessage(){}
	
	public SMSMessage(String destinationTN, String messageText){
		this.destinationTN = destinationTN;
		this.messageText = messageText;
	}
	
	public String getDestinationTN() {
		return destinationTN;
	}
	public void setDestinationTN(String destinationTN) {
		this.destinationTN = destinationTN;
	}
	public String getMessage() {
		return messageText;
	}
	public void setMessage(String messageText) {
		this.messageText = messageText;
	}

}
