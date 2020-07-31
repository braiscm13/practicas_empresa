package com.opentach.server.i18n;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.infraction.RegimenType;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.cache.AbstractGenericCache;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.i18n.ITranslationService;
import com.utilmize.server.services.UAbstractService;

/**
 * The Class TranslationService.
 */
public class TranslationService extends UAbstractService implements ITranslationService {

	/** The Constant logger. */
	private static final Logger				logger				= LoggerFactory.getLogger(TranslationService.class);

	/** The tp actividad cache. */
	AbstractGenericCache<Object, String>	tpActividadCache	= new DatabaseKeyValueCache("ETipoActividad", "TPACTIVIDAD", "DSCR");
	AbstractGenericCache<Object, String>	constantCache		= new DatabaseKeyValueCache("EI18nConstant", "CON_KEY", "CON_VALUE");

	/**
	 * Instantiates a new translation service.
	 *
	 * @param port
	 *            the port
	 * @param erl
	 *            the erl
	 * @param hconfig
	 *            the hconfig
	 * @throws Exception
	 *             the exception
	 */
	public TranslationService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	/**
	 * Gets the activity desctiption.
	 *
	 * @param key
	 *            the key
	 * @return the activity desctiption
	 */
	public String getActivityDesctiption(Object key) {
		if (key instanceof Number) {
			return this.tpActividadCache.get(((Number) key).intValue());
		}
		return null;
	}

	/**
	 * Gets the slot desctiption.
	 *
	 * @param slot
	 *            the slot
	 * @return the slot desctiption
	 */
	public String getSlotDesctiption(String slot) {
		if (slot == null) {
			return null;
		}
		return this.constantCache.get(ObjectTools.decode(slot, "0", "SLOT_DRIVER", "1", "SLOT_DRIVER2", null));
	}

	public String getRegimenDesctiption(RegimenType regimen) {
		if (regimen == null) {
			return null;
		}
		return this.constantCache.get(ObjectTools.decode(regimen, RegimenType.ALONE, "REGIMEN_ALONE", RegimenType.TEAM, "REGIMEN_TEAM", RegimenType.UNDEFINED, "UNDEFINED", null));
	}

	public String getOriginDescription(Object origin) {
		if (origin == null) {
			return null;
		}
		return this.constantCache.get(ObjectTools.decode(origin, "M", "ORIGIN_MANUAL", "A", "ORIGIN_AUTO", null));
	}

	public String getSlotCardStatusDescription(String slotCardStatus) {
		if (slotCardStatus == null) {
			return null;
		}
		return this.constantCache.get(ObjectTools.decode(slotCardStatus, "0", "SLOTSTATUS_INSERT", "1", "SLOTSTATUS_NOTINSERT", null));
	}

	/**
	 * The Class DatabaseKeyValueCache.
	 */
	class DatabaseKeyValueCache extends AbstractGenericCache<Object, String> {

		/** The entity. */
		private final String	entity;

		/** The key column name. */
		private final String	keyColumnName;

		/** The dscr column name. */
		private final String	dscrColumnName;

		/**
		 * Instantiates a new database key value cache.
		 *
		 * @param entity
		 *            the entity
		 * @param keyColumnName
		 *            the key column name
		 * @param dscrColumnName
		 *            the dscr column name
		 */
		public DatabaseKeyValueCache(String entity, String keyColumnName, String dscrColumnName) {
			super(60l * 60l * 1000l);
			this.entity = entity;
			this.keyColumnName = keyColumnName;
			this.dscrColumnName = dscrColumnName;
		}

		/*
		 * (non-Javadoc)
		 * @see com.ontimize.jee.common.cache.AbstractGenericCache#requestData(java.lang.Object)
		 */
		@Override
		protected String requestData(Object key) throws OntimizeJEEException {
			try {
				TableEntity etpActividad = (TableEntity) TranslationService.this.getEntity(this.entity);
				EntityResult res = etpActividad.query(EntityResultTools.keysvalues(this.keyColumnName, key), EntityResultTools.attributes(this.dscrColumnName),
						TranslationService.this.getEntityPrivilegedId((TransactionalEntity) etpActividad));
				int nrecord = res.calculateRecordNumber();
				if (nrecord == 1) {
					return (String) res.getRecordValues(0).get(this.dscrColumnName);
				}
			} catch (Exception err) {
				TranslationService.logger.error(null, err);
			}
			return null;
		}
	}
}
