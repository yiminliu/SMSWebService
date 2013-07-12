package com.tscp.mvno.smpp.dao;


import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tscp.mvno.smpp.domain.Sms;
import com.tscp.mvno.smpp.domain.SmsFailed;
import com.tscp.mvno.smpp.service.LoggingService;


@Repository("hibernateSmsDao")
@Scope("singleton")
public class HibernateSmsDao extends HibernateDaoSupport implements SmsDao {
	/* (non-Javadoc)
	 * @see com.tscp.mvno.smpp.dao.SmsDao#init(org.springframework.orm.hibernate3.HibernateTemplate)
	 */
	@Autowired
	public void init(HibernateTemplate hibernateTemplate) {
	   setHibernateTemplate(hibernateTemplate);
    } 
    
	public HibernateSmsDao(){}
	
	/* (non-Javadoc)
	 * @see com.tscp.mvno.smpp.dao.SmsDao#saveSmsMessage(com.tscp.mvno.smpp.domain.SMSMessage)
	 */
	@Override
	public int saveSmsMessage(Sms sms){
		HibernateTemplate ht = getHibernateTemplate();
		sms.setMessageTimestamp(new Timestamp(System.currentTimeMillis()));	 
		return (Integer)ht.save(sms);					
	}	
}
