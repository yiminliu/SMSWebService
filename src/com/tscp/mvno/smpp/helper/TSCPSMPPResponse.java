package com.tscp.mvno.smpp.helper;

import java.util.Arrays;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.SMPPResponse;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.util.MessageEncoding;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;


/**
 * This class is a wrapper/delegation class of ie.omk.smpp.message.SMPPResponse. The main reason for this class is to comply with WebService 
 * requirements of no-arg constructor, the ie.omk.smpp.message.SMPPResponse class lacks no-arg constructor. It exposes all properties of 
 * the ie.omk.smpp.message.SMPPResponse class through calling corresponding methods in the ie.omk.smpp.message.SMPPResponse class.   
 *
 */
public class TSCPSMPPResponse {
	
	private int commandId;
	private int commandStatus;
	private int dataEncoding;
	private SMPPDate deliveryTime;
	private Address	destination;
	private int errorCode;
	private SMPPDate expiryTime;
	private SMPPDate finalDate;
	private int length;
	private byte[] message;
	private String messageId;
	private int messageLen;
	private int messageStatus;
	private String messageText;
	private int	priority;
	private int protocolID;
	private int registered;
	private int sequenceNum;
	private String serviceType;
	private Address source;
	private int bodyLength;
	private int commandLen;
	private int defaultMsg;
	private int esmClass;
	private MessageEncoding messageEncoding;
	private int replaceIfPresent;
	private TLVTable tlvTable;
	private SMPPVersion version;
	
	private SMPPResponse smppResponse;
	
	public TSCPSMPPResponse(){		
	}
	
	public TSCPSMPPResponse(SMPPResponse smppResponse ){	
		this.smppResponse = smppResponse;
		errorCode = smppResponse.getErrorCode();
		commandId = smppResponse.getCommandId();
		commandStatus = smppResponse.getCommandStatus();
		messageId = smppResponse.getMessageId();
		messageStatus = smppResponse.getMessageStatus();
		messageText = smppResponse.getMessageText();
		message = smppResponse.getMessage();
		messageLen = smppResponse.getMessageLen();
		messageEncoding = smppResponse.getMessageEncoding();
		defaultMsg = smppResponse.getDefaultMsg();
		dataEncoding = smppResponse.getDataCoding();
		deliveryTime = smppResponse.getDeliveryTime();
		destination = smppResponse.getDestination();
		expiryTime = smppResponse.getExpiryTime();
		finalDate = smppResponse.getFinalDate();
		length = smppResponse.getLength();		
		priority = smppResponse.getPriority();
		protocolID = smppResponse.getProtocolID();
		sequenceNum = smppResponse.getSequenceNum();
		serviceType = smppResponse.getServiceType();
		source = smppResponse.getSource();
		bodyLength = smppResponse.getBodyLength();		
		esmClass = smppResponse.getEsmClass();
		registered = smppResponse.getRegistered();
		replaceIfPresent = smppResponse.getReplaceIfPresent();
		tlvTable = smppResponse.getTLVTable();
		version = smppResponse.getVersion();
		
	}


	
	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public void setCommandStatus(int commandStatus) {
		this.commandStatus = commandStatus;
	}

	public void setDataEncoding(int dataEncoding) {
		this.dataEncoding = dataEncoding;
	}

	public void setDestination(Address destination) {
		this.destination = destination;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setExpiryTime(SMPPDate expiryTime) {
		this.expiryTime = expiryTime;
	}

	public void setFinalDate(SMPPDate finalDate) {
		this.finalDate = finalDate;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setMessageLen(int messageLen) {
		this.messageLen = messageLen;
	}

	public void setMessageStatus(int messageStatus) {
		this.messageStatus = messageStatus;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setProtocolID(int protocolID) {
		this.protocolID = protocolID;
	}

	public void setRegistered(int registered) {
		this.registered = registered;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public void setSource(Address source) {
		this.source = source;
	}

	public int getCommandId() {
		return commandId;
	}

	public int getCommandStatus() {
		return commandStatus;
	}
	
	public int getDataEncoding() {
		return dataEncoding;
	}
	
	public SMPPDate getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(SMPPDate deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Address getDestination() {
		return destination;
	}

	public int getErrorCode() {
		return errorCode;
	}
	
	public SMPPDate getExpiryTime() {
		return expiryTime;
	}

	public SMPPDate getFinalDate() {
		return finalDate;
	}

	public int getLength() {
		return length;
	}

	public byte[] getMessage() {
		return message;
	}

	public String getMessageId() {
		return messageId;
	}

	public int getMessageLen() {
		return messageLen;
	}

	public int getMessageStatus() {
		return messageStatus;
	}

	public String getMessageText() {
		return messageText;
	}

	public int getPriority() {
		return priority;
	}
	
	public int getProtocolID() {
		return protocolID;
	}

	public int getRegistered() {
		return registered;
	}
	
	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public String getServiceType() {
		return serviceType;
	}

	public Address getSource() {
		return source;
	}
	
	
	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	public int getCommandLen() {
		return commandLen;
	}

	public void setCommandLen(int commandLen) {
		this.commandLen = commandLen;
	}

	public int getDefaultMsg() {
		return defaultMsg;
	}

	public void setDefaultMsg(int defaultMsg) {
		this.defaultMsg = defaultMsg;
	}

	public int getEsmClass() {
		return esmClass;
	}

	public void setEsmClass(int esmClass) {
		this.esmClass = esmClass;
	}

	public MessageEncoding getMessageEncoding() {
		return messageEncoding;
	}

	public void setMessageEncoding(MessageEncoding messageEncoding) {
		this.messageEncoding = messageEncoding;
	}

	public int getReplaceIfPresent() {
		return replaceIfPresent;
	}

	public void setReplaceIfPresent(int replaceIfPresent) {
		this.replaceIfPresent = replaceIfPresent;
	}

	public TLVTable getTlvTable() {
		return tlvTable;
	}

	public void setTlvTable(TLVTable tlvTable) {
		this.tlvTable = tlvTable;
	}

	public SMPPVersion getVersion() {
		return version;
	}

	public void setVersion(SMPPVersion version) {
		this.version = version;
	}

	public SMPPResponse getSmppResponse() {
		return smppResponse;
	}
	
	public void setSmppResponse(SMPPResponse smppResponse) {
		this.smppResponse = smppResponse;
	}
	
	@Override
	public String toString() {
		return "TSCPSMPPResponse [commandId=" + commandId + ", commandStatus="
				+ commandStatus + ", dataEncoding=" + dataEncoding
				+ ", deliveryTime=" + deliveryTime + ", destination="
				+ destination + ", errorCode=" + errorCode + ", expiryTime="
				+ expiryTime + ", finalDate=" + finalDate + ", length="
				+ length + ", message=" + Arrays.toString(message)
				+ ", messageId=" + messageId + ", messageLen=" + messageLen
				+ ", messageStatus=" + messageStatus + ", messageText="
				+ messageText + ", priority=" + priority + ", protocolID="
				+ protocolID + ", registered=" + registered + ", sequenceNum="
				+ sequenceNum + ", serviceType=" + serviceType + ", source="
				+ source + ", bodyLength=" + bodyLength + ", commandLen="
				+ commandLen + ", defaultMsg=" + defaultMsg + ", esmClass="
				+ esmClass + ", messageEncoding=" + messageEncoding
				+ ", replaceIfPresent=" + replaceIfPresent + ", tlvTable="
				+ tlvTable + ", version=" + version + ", smppResponse="
				+ smppResponse + "]";
	}		
}
