package com.tscp.mvno.smpp.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.tscp.mvno.smpp.domain.SMSMessage;

public interface SmsDao {

	@Autowired
	public void init(HibernateTemplate hibernateTemplate);

	@Transactional(readOnly = true)
	public List<SMSMessage> getAlertMessages(String namedQueryName);

}