package com.opentach.server.mailmanager.dao;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
@Repository(value = "MailAccountDao")
@Lazy
@ConfigurationFile(configurationFile = "mailmanager-dao/MailAccountDao.xml", configurationFilePlaceholder = "mailmanager-dao/placeholders.properties")
public class MailAccountDao extends OntimizeJdbcDaoSupport {
	public MailAccountDao() {
		super();
	}
}
