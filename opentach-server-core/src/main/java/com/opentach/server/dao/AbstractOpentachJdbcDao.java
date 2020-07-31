package com.opentach.server.dao;

import java.util.Map;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import com.opentach.server.dao.autofill.AutoFillHelper;
import com.opentach.server.dao.autofill.IAutoFillDao;

//@formatter:off
/**
 * We want to implement here functionalities common to all application daos.
 * For instance:
 *  · Audit (if configured, that is dao implement IAuditableDao)
 *  · Auto-fill (if configured, that is dao implements IAutofillDao)
 */
//@formatter:on
public class AbstractOpentachJdbcDao extends OntimizeJdbcDaoSupport {

	public AbstractOpentachJdbcDao() {
		super();
	}

	public AbstractOpentachJdbcDao(String configurationFile, String configurationFilePlaceholder) {
		super(configurationFile, configurationFilePlaceholder);
	}

	@Override
	public EntityResult insert(Map<?, ?> attributesValues) {
		if (this instanceof IAutoFillDao) {
			AutoFillHelper.autoFillCols2Hash(attributesValues, ((IAutoFillDao) this).getColumnsAutoFillInsert(), this);
		}
		return super.insert(attributesValues);
	}

	@Override
	public EntityResult update(Map<?, ?> attributesValues, Map<?, ?> keysValues) {
		if (this instanceof IAutoFillDao) {
			AutoFillHelper.autoFillCols2Hash(attributesValues, ((IAutoFillDao) this).getColumnsAutoFillUpdate(), this);
		}
		return super.update(attributesValues, keysValues);
	}

}
