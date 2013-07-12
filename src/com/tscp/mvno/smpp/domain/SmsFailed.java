package com.tscp.mvno.smpp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.tscp.mvno.smpp.exception.SmsException;


@Entity
@Table(name="App_Error")
public class SmsFailed implements Serializable{
	private static final long serialVersionUID = 1984305003999836500L;
	
	@Id
	@Column(name="Id", unique=true, nullable=false, updatable=false)
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name ="Failed_Class")
	private String failedClass;
	
	@Column(name="Failed_Method")
	private String failedMethod;
	
	@Column(name="Error_Message")
	private String errorMessage;
	
	@Column(name="Root_Exception")
	private String rootExceptionName;
	
	@Column(name="Root_Error_Message")
	private String rootErrorMEssage;
	
	@Transient
	private SmsException smsException;
	
	@OneToOne(mappedBy="smsFailed")
	private Sms sms;
	
	public SmsFailed(){}
	
	public SmsFailed(String failedClass, String failedMethod, String errorMessage, String rootExceptionName, String rootErrorMessage){
		this.failedClass = failedClass;
		this.failedMethod = failedMethod;
		this.errorMessage = errorMessage;
		this.rootExceptionName = rootExceptionName;
		this.rootErrorMEssage = rootErrorMessage;
	}

	public SmsFailed(SmsException smsException){
		this.failedClass = smsException.getClassName();
		this.failedMethod = smsException.getMethodName();
		this.errorMessage = smsException.getSmsErrorMessage();
		this.rootExceptionName = smsException.getCauseException().getClass().getName();
		this.rootErrorMEssage = smsException.getCauseException().getMessage();
	}	
	
	public String getFailedClass() {
		return failedClass;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if(id != null)
		   this.id = id;
		else
		   this.id = sms.getId();	
	}

	public SmsException getSmsException() {
		return smsException;
	}

	public void setSmsException(SmsException smsException) {
		this.smsException = smsException;
	}

	public void setRootExceptionName(String rootExceptionName) {
		this.rootExceptionName = rootExceptionName;
	}

	public void setFailedClass(String failedClass) {
		this.failedClass = failedClass;
	}

	public String getFailedMethod() {
		return failedMethod;
	}

	public void setFailedMethod(String failedMethod) {
		this.failedMethod = failedMethod;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getRootExceptionName() {
		return rootExceptionName;
	}

	public void setRootException(String rootExceptionName) {
		this.rootExceptionName = rootExceptionName;
	}

	public String getRootErrorMEssage() {
		return rootErrorMEssage;
	}

	public void setRootErrorMEssage(String rootErrorMEssage) {
		this.rootErrorMEssage = rootErrorMEssage;
	}

	public Sms getSms() {
		return sms;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}
	
	
}
