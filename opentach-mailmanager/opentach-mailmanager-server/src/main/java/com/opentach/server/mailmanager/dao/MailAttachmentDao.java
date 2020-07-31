package com.opentach.server.mailmanager.dao;


import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.BeanPropertyRowMapper;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
@Repository(value = "MailAttachmentDao")
@Lazy
@ConfigurationFile(configurationFile = "mailmanager-dao/MailAttachmentDao.xml", configurationFilePlaceholder = "mailmanager-dao/placeholders.properties")
public class MailAttachmentDao extends OntimizeJdbcDaoSupport {
	public MailAttachmentDao() {
		super();
	}

	@Override
	protected <T> BeanPropertyRowMapper<T> createRowMapper(final Class<T> clazz) {
		return new BeanPropertyRowMapper<T>(this.getNameConverter(), this.getDataSource(), clazz) {
			@Override
			protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
				if (pd.getPropertyType().isArray() && (rs.getMetaData().getColumnType(index) != Types.BINARY) && (rs.getMetaData().getColumnType(index) != Types.BLOB)) {
					return rs.getArray(index).getArray();
				}
				return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
			}
		};
	}
}
