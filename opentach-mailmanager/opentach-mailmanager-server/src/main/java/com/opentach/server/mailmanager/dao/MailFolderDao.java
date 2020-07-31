package com.opentach.server.mailmanager.dao;


import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
@Repository(value = "MailFolderDao")
@Lazy
@ConfigurationFile(configurationFile = "mailmanager-dao/MailFolderDao.xml", configurationFilePlaceholder = "mailmanager-dao/placeholders.properties")
public class MailFolderDao extends OntimizeJdbcDaoSupport {
	public MailFolderDao() {
		super();
	}
}
