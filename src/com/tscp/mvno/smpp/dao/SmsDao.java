package com.tscp.mvno.smpp.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tscp.mvno.smpp.domain.Sms;

@Repository
public interface SmsDao {
	
	@Transactional
	public abstract int saveSmsMessage(Sms sms);

}