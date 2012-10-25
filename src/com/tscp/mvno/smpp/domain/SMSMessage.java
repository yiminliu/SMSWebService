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
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

}
