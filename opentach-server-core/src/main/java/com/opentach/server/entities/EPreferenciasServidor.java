package com.opentach.server.entities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.security.GeneralSecurityException;
import com.ontimize.security.NotInPeriodException;
import com.ontimize.security.SessionNotFoundException;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.common.util.EncryptDecryptUtils;
import com.utilmize.tools.exception.UException;

/**
 * The Class EPreferenciasServidor.
 */
public class EPreferenciasServidor extends TableEntity {

	/** The Constant logger. */
	private static final Logger						logger							= LoggerFactory.getLogger(EPreferenciasServidor.class);

	/** The Constant SERVER_PREFERENCES_ENTITY_NAME. */
	public static final String						SERVER_PREFERENCES_ENTITY_NAME	= "EPreferenciasServidor";

	/** The Constant FILESTORE_WHERE. */
	public static final String						FILESTORE_WHERE					= "FileStore.Where";

	/** The Constant FILESTORE_PATH. */
	public static final String						FILESTORE_PATH					= "FileStore.Path";

	/** The Constant FILESTORE_UNZIP. */
	public static final String						FILESTORE_UNZIP					= "FileStore.UNZIP";

	/** The Constant FILESTORE_UNZIP_PATH. */
	public static final String						FILESTORE_UNZIP_PATH			= "FileStore.UNZIP_PATH";

	/** The Constant ANALYZE_MAXDRIVERS. */
	public static final String						ANALYZE_MAXDRIVERS				= "Analyze.MaxDrivers";

	/** The Constant DUMMY_CONTRACT. */
	public static final String						DUMMY_CONTRACT					= "FileReception.DummyContract";

	/** The columna nombre. */
	public static String							COLUMNA_NOMBRE					= "NOMBRE";

	/** The columna descripcion. */
	public static String							COLUMNA_DESCRIPCION				= "DESCRIPCION";

	/** The columna valor. */
	public static String							COLUMNA_VALOR					= "VALOR";

	/** The listeners. */
	private final List<IPreferenceChangeListener>	listeners;

	/**
	 * Instantiates a new e preferencias servidor.
	 *
	 * @param b
	 *            the b
	 * @param g
	 *            the g
	 * @param p
	 *            the p
	 * @throws Exception
	 *             the exception
	 */
	public EPreferenciasServidor(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
		this.listeners = new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#update(java.util.Hashtable, java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult update(Hashtable av, Hashtable kv, int sesionId, Connection con) throws Exception {
		// TODO REMOVE THIS PART FROM THE CORE
		if ("Opentach.Pass".equals(kv.get("NOMBRE"))) {
			((Map<String, Object>) av).put("VALOR", EncryptDecryptUtils.encrypt((String) av.get("VALOR")));
		}
		//
		EntityResult rs = super.update(av, kv, sesionId, con);
		if (rs.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
			for (IPreferenceChangeListener listener : this.listeners) {
				listener.propertyChanged((String) kv.get(EPreferenciasServidor.COLUMNA_NOMBRE), (String) av.get(EPreferenciasServidor.COLUMNA_VALOR));
			}
		}
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#delete(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult delete(Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("NO_DELETE_ALLOWED");
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#insert(java.util.Hashtable, int, java.sql.Connection)
	 */
	@Override
	public EntityResult insert(Hashtable atributosValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("NO_INSERT_ALLOWED");
	}

	/**
	 * Gets the value.
	 *
	 * @param propertyName
	 *            the property name
	 * @param sesionId
	 *            the sesion id
	 * @param con
	 *            the con
	 * @return the value
	 * @throws Exception
	 *             the exception
	 */
	public String getValue(String propertyName, int sesionId, Connection con) throws UException {
		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		cv.put(EPreferenciasServidor.COLUMNA_NOMBRE, propertyName);
		Vector<String> av = new Vector<String>();
		av.add(EPreferenciasServidor.COLUMNA_VALOR);
		EntityResult res = null;
		try {
			if (con == null) {
				res = this.query(cv, av, sesionId);
				CheckingTools.checkValidEntityResult(res);
			} else {
				res = this.query(cv, av, sesionId, con);
			}
			if (res.calculateRecordNumber() == 0) {
				return null;
			}
			return (String) res.getRecordValues(0).get(EPreferenciasServidor.COLUMNA_VALOR);
		} catch (Exception err) {
			throw new UException(err);
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param propertyName
	 *            the property name
	 * @param sesionId
	 *            the sesion id
	 * @return the value
	 * @throws Exception
	 *             the exception
	 */
	public String getValue(String propertyName, int sesionId) throws UException {
		return this.getValue(propertyName, sesionId, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.db.TableEntity#checkPermissions(int, java.lang.String)
	 */
	@Override
	public void checkPermissions(int sessionId, String action) throws NotInPeriodException, GeneralSecurityException, SessionNotFoundException {}

	/**
	 * Adds the property change listener.
	 *
	 * @param listener
	 *            the listener
	 */
	public void addPropertyChangeListener(IPreferenceChangeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * The listener interface for receiving IPreferenceChange events. The class that is interested in processing a IPreferenceChange event implements this interface, and the object
	 * created with that class is registered with a component using the component's <code>addIPreferenceChangeListener<code> method. When the IPreferenceChange event occurs, that
	 * object's appropriate method is invoked.
	 *
	 * @see IPreferenceChangeEvent
	 */
	public static interface IPreferenceChangeListener {

		/**
		 * Property changed.
		 *
		 * @param string
		 *            the string
		 * @param string2
		 *            the string 2
		 */
		void propertyChanged(String string, String string2);

	}

	// TODO: this method is implemented as a temporary workaround.
	// See RingCentral/ Team Tacholab 2020-03-17. Meanwhile it is
	// stored as a server property.
	// CAUTION!!!!!!!!!!!!!!!!!!!!!!!!!!
	// Used in TachoLab! In OpenTach, this method should return GMT
	// time zone (i.e., no "Client.TimeZone" server property should
	// exist, or its value should be "GMT").
	public static TimeZone getClientTimeZone(IOpentachServerLocator erl) {
		try {
			Entity serverPreferences = erl.getEntityReferenceFromServer(EPreferenciasServidor.SERVER_PREFERENCES_ENTITY_NAME);
			int sessionId = TableEntity.getEntityPrivilegedId(serverPreferences);

			EntityResult entityResult = serverPreferences.query( //
					EntityResultTools.keysvalues(EPreferenciasServidor.COLUMNA_NOMBRE, "Client.TimeZone"), //
					EntityResultTools.attributes(EPreferenciasServidor.COLUMNA_VALOR), //
					sessionId);

			if (entityResult.calculateRecordNumber() > 0) {
				String timeZoneString = ((Vector<String>) (entityResult.get(EPreferenciasServidor.COLUMNA_VALOR))).get(0);

				return TimeZone.getTimeZone(timeZoneString);
			}
		} catch (Exception exception) {
			EPreferenciasServidor.logger.warn("ERROR_GETTING_CLIENT_TIMEZONE", exception);
		}
		return TimeZone.getTimeZone("GMT");
	}
}
