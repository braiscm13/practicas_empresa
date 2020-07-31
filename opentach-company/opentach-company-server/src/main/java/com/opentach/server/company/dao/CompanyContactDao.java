package com.opentach.server.company.dao;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.opentach.server.dao.AbstractOpentachJdbcDao;

@Repository(value = "CompanyContactDao")
@Lazy
@ConfigurationFile(configurationFile = "company-dao/CompanyContactDao.xml", configurationFilePlaceholder = "company-dao/placeholders.properties")
public class CompanyContactDao extends AbstractOpentachJdbcDao {

	public CompanyContactDao() {
		super();
	}
}