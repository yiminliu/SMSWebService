package com.tscp.mvno.smpp.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="Sms_Message")
public class Sms implements Serializable{
	private static final long serialVersionUID = -1984305003999836500L;
	
	@Id
	@Column(name="Sms_Id", unique=true, nullable=false, updatable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name ="Destination_TN")
	private String destinationTN;
	
	@Column(name="Message_Text")
	private String messageText;
	
	@Column(name="Message_Timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date messageTimestamp;
	
	@Column(name="Result")
	String result = "Successful";
	
	//@OneToOne//(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	//@PrimaryKeyJoinColumn
	//private Error_Records;
	
	public Sms(){}
	
	public Sms(String destinationTN, String messageText){
		this.destinationTN = destinationTN;
		this.messageText = messageText;
	}
	
	public Sms(String destinationTN, String messageText, String result){
		this.destinationTN = destinationTN;
		this.messageText = messageText;
		if(result != null && !result.equalsIgnoreCase("Successful"))
		   this.result = "Failed :: "+result;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Date getMessageTimestamp() {
		return messageTimestamp;
	}

	public void setMessageTimestamp(Date messageTimestamp) {
		this.messageTimestamp = messageTimestamp;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
