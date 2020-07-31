package com.opentach.server.util.db;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.ResourceManager;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;

public class TranslateTableEntity extends TableEntity implements OpentachFieldNames {
	private final static Logger	logger			= LoggerFactory.getLogger(TranslateTableEntity.class);
	private static final String	sTRANSLATECOLS	= "TranslateColumns";
	private Set<String>			hsLocaleKeys;


	public TranslateTableEntity(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
		this.initialize();
	}

	public TranslateTableEntity(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
		this.initialize();
	}

	protected void initialize() {
		if (this.properties != null) {
			final String translateCols = this.properties.getProperty(TranslateTableEntity.sTRANSLATECOLS);
			if (translateCols != null && translateCols.length() > 0) {
				if (this.hsLocaleKeys == null) {
					this.hsLocaleKeys = new HashSet<String>();
				}
				final StringTokenizer str = new StringTokenizer(translateCols, ";");
				while (str.hasMoreTokens()) {
					this.hsLocaleKeys.add(str.nextToken());
				}
			}
		}
	}


	@Override
	public EntityResult query(Hashtable cv, Vector v, int sessionID, Connection conn) throws Exception {
		final EntityResult res = super.query(cv, v, sessionID, conn);
		if (this.hsLocaleKeys != null) {
			final long tini = System.currentTimeMillis();
			this.translate(res, sessionID);
			final long tfin = System.currentTimeMillis();
			TranslateTableEntity.logger.debug("Tiempo traducción " + this.getName() + " = " + (tfin - tini) / 1000f + " s");
		}
		return res;
	}

	protected void translate(EntityResult res, int sessionID) {
		if (res.getCode() != EntityResult.OPERATION_WRONG && res.size() > 0 && sessionID > 0) {
			try {
				final IUserData du = this.getLocator().getUserData(sessionID);
				if (du != null) {
					final Locale locale = du.getLocale();
					if (locale != null) {
						TranslateTableEntity.translate(this.hsLocaleKeys, res, locale);
					}
				}
			} catch (final Exception e) {
				TranslateTableEntity.logger.error(null, e);
			}
		}
	}

	private static final void translate(Set<String> hsLocaleKeys, EntityResult res, Locale locale) {
		Object key = null;
		final Enumeration<Object> keys = res.keys();
		Vector<Object> vdata = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			if (key instanceof String) {
				if (hsLocaleKeys.contains(key)) {
					vdata = (Vector) res.get(key);
					TranslateTableEntity.translate(vdata, locale);
				}
			}
		}
	}

	private static final void translate(Vector vData, Locale locale) {
		Object key = null;
		String sKey = null;
		final int count = vData.size();
		for (int i = 0; i < count; i++) {
			key = vData.elementAt(i);
			if (key != null && key instanceof String) {
				sKey = ((String) key).trim();
				vData.setElementAt(ResourceManager.translate(locale, sKey), i);

			}
		}
	}

	protected int getSessionId(int sessionId, Entity ent) {
		return TableEntity.getEntityPrivilegedId(this) == sessionId ? TableEntity.getEntityPrivilegedId(ent) : sessionId;
	}

	public <T extends UAbstractService> T getService(Class<T> clazz) throws Exception {
		return this.getLocator().getService(clazz);
	}

	public IOpentachServerLocator getLocator() {
		return (IOpentachServerLocator) this.locator;
	}

	public <T> T getBean(Class<T> clazz) {
		return this.getLocator().getBean(clazz);
	}
}
