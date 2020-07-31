package com.opentach.server.dms.services;

import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;
import com.opentach.server.IOpentachServerLocator;

/**
 * The Class DMSServiceDocumentHelper.
 */
public class DMSServiceDocumentHelper extends AbstractDMSServiceHelper {

	/** The service file helper. */
	protected DMSServiceFileHelper serviceFileHelper;

	public DMSServiceDocumentHelper(IOpentachServerLocator locator, DMSServiceFileHelper serviceFileHelper) {
		super(locator);
		this.serviceFileHelper = serviceFileHelper;
	}

	/**
	 * Document query.
	 *
	 * @param attributes
	 *            the attributes
	 * @param criteria
	 *            the criteria
	 * @return the entity result
	 * @throws DmsException
	 */
	public EntityResult documentQuery(Vector<?> attributes, Hashtable<?, ?> criteria, Connection con) throws DmsException {
		try {
			return this.getDocumentEntity().query(criteria, attributes, this.getSessionId(-1, this.getDocumentEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document insert.
	 *
	 * @param av
	 *            the av
	 * @return the object
	 * @throws DmsException
	 */
	public DocumentIdentifier documentInsert(Hashtable<?, ?> av, Connection con) throws DmsException {
		// TODO cubir la información del usuario actual
		// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// UserInformation userInfo = (UserInformation) authentication.getPrincipal();
		// userInfo.getUsername();
		try {
			if (av.get(this.getColumnHelper().getDocumentOwnerColumn()) == null) {
				((Map<Object, Object>) av).put(this.getColumnHelper().getDocumentOwnerColumn(), Integer.valueOf(1));
			}
			EntityResult res = this.getDocumentEntity().insert(av, this.getSessionId(-1, this.getDocumentEntity()), con);

			DocumentIdentifier result = new DocumentIdentifier((Serializable) res.get(this.getColumnHelper().getDocumentIdColumn()));
			return result;
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document update.
	 *
	 * @param documentId
	 *            the document id
	 * @param attributesValues
	 *            the attributes values
	 * @throws DmsException
	 */
	public void documentUpdate(Serializable documentId, Hashtable<?, ?> attributesValues, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			this.getDocumentEntity().update(attributesValues, kv, this.getSessionId(-1, this.getDocumentEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document delete.
	 *
	 * @param documentId
	 *            the document id
	 * @throws DmsException
	 */
	public void documentDelete(Serializable documentId, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			// borramos las propiedades
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			this.getDocumentPropertyEntity().delete(kv, this.getSessionId(-1, this.getDocumentPropertyEntity()), con);
			// borramos las relaciones
			kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentRelatedMasterColumn(), documentId);
			this.getDocumentRelatedEntity().delete(kv, this.getSessionId(-1, this.getDocumentRelatedEntity()), con);
			kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentRelatedChildColumn(), documentId);
			this.getDocumentRelatedEntity().delete(kv, this.getSessionId(-1, this.getDocumentRelatedEntity()), con);
			kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			this.getCategoryEntity().delete(kv, this.getSessionId(-1, this.getCategoryEntity()), con);

			// borramos los ficheros
			EntityResult resFiles = this.documentGetFiles(documentId, new Hashtable<>(), EntityResultTools.attributes(this.getColumnHelper().getFileIdColumn()), con);
			List<Serializable> vDocumentId = (List<Serializable>) resFiles.get(this.getColumnHelper().getFileIdColumn());
			List<Path> toDelete = new ArrayList<>();
			if (vDocumentId != null) {
				for (Serializable fileId : vDocumentId) {

					EntityResult res = this.serviceFileHelper.fileGetVersions(fileId, new Hashtable<>(), EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()),
							con);
					List<Serializable> fileVersionIds = (List<Serializable>) res.get(this.getColumnHelper().getVersionIdColumn());

					// borramos las versiones, sin borrar los ficheros
					List<Path> toDeletePartial = this.serviceFileHelper.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds, con);

					// borramos el fichero
					Hashtable<String, Object> kvFile = new Hashtable<>();
					MapTools.safePut(kvFile, this.getColumnHelper().getFileIdColumn(), fileId);
					this.getFileEntity().delete(kvFile, this.getSessionId(-1, this.getFileEntity()), con);
					toDelete.addAll(toDeletePartial);
				}
			}
			// Borramos el documento
			kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			this.getDocumentEntity().delete(kv, this.getSessionId(-1, this.getDocumentEntity()), con);

			// si todo fue bien y no se va a hacer rollback, borramos
			for (Path path : toDelete) {
				FileTools.deleteQuitely(path);
			}
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document add properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param properties
	 *            the properties
	 * @throws DmsException
	 */
	public void documentAddProperties(Serializable documentId, Map<String, String> properties, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			CheckingTools.failIfNull(properties, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);

			Map<String, Object>[] batch = new HashMap[properties.size()];
			int i = 0;
			for (Entry<String, String> entry : properties.entrySet()) {
				Hashtable<String, Object> av = new Hashtable<>();
				MapTools.safePut(av, this.getColumnHelper().getDocumentIdColumn(), documentId);
				MapTools.safePut(av, this.getColumnHelper().getPropertyKeyColumn(), entry.getKey());
				MapTools.safePut(av, this.getColumnHelper().getPropertyValueColumn(), entry.getValue());
				batch[i++] = av;
				this.getDocumentPropertyEntity().insert(av, this.getSessionId(-1, this.getDocumentPropertyEntity()), con);
			}
			// this.documentPropertyDao.insertBatch(batch);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document delete properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKeys
	 *            the property keys
	 * @throws DmsException
	 */
	public void documentDeleteProperties(Serializable documentId, List<String> propertyKeys, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			CheckingTools.failIfNull(propertyKeys, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
			if (propertyKeys.isEmpty()) {
				return;
			}

			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			MapTools.safePut(kv, this.getColumnHelper().getPropertyKeyColumn(), new SearchValue(SearchValue.IN, propertyKeys));
			this.getDocumentPropertyEntity().delete(kv, this.getSessionId(-1, this.getDocumentPropertyEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document get property.
	 *
	 * @param documentId
	 *            the document id
	 * @param propertyKey
	 *            the property key
	 * @return the string
	 * @throws DmsException
	 */
	public String documentGetProperty(Serializable documentId, String propertyKey, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			CheckingTools.failIfNull(propertyKey, DMSNaming.ERROR_PROPERTY_KEY_MANDATORY);
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentIdColumn(), documentId);
			MapTools.safePut(kv, this.getColumnHelper().getPropertyKeyColumn(), propertyKey);
			EntityResult rs = this.getDocumentPropertyEntity().query(kv, this.getColumnHelper().getPropertyColumns(), this.getSessionId(-1, this.getDocumentPropertyEntity()), con);
			if (rs.calculateRecordNumber() == 0) {
				return null;
			}
			return (String) rs.getRecordValues(0).get(this.getColumnHelper().getPropertyValueColumn());
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document get properties.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @return the map
	 * @throws DmsException
	 */
	public Hashtable<String, String> documentGetProperties(Serializable documentId, Hashtable<?, ?> kv, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
			EntityResult rs = this.getDocumentPropertyEntity().query(kv, this.getColumnHelper().getPropertyColumns(), this.getSessionId(-1, this.getDocumentPropertyEntity()), con);
			Hashtable<String, String> res = new Hashtable<>();
			int nregs = rs.calculateRecordNumber();
			for (int i = 0; i < nregs; i++) {
				Map<?, ?> record = rs.getRecordValues(i);
				MapTools.safePut(res, (String) record.get(this.getColumnHelper().getPropertyKeyColumn()), (String) record.get(this.getColumnHelper().getPropertyValueColumn()));
			}
			return res;
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document get files.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws DmsException
	 */
	public EntityResult documentGetFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
			return this.getFileEntity().query(kv, attributes, this.getSessionId(-1, this.getFileEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Document get all files.
	 *
	 * @param documentId
	 *            the document id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws DmsException
	 */
	public EntityResult documentGetAllFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			((Map<Object, Object>) kv).put(this.getColumnHelper().getDocumentIdColumn(), documentId);
			return this.getAllFileEntity().query(kv, attributes, this.getSessionId(-1, this.getAllFileEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Gets the related document.
	 *
	 * @param documentId
	 *            the document id
	 * @return the related document
	 * @throws DmsException
	 */
	public EntityResult getRelatedDocument(Serializable documentId, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getDocumentRelatedMasterColumn(), documentId);
			return this.getDocumentRelatedEntity().query(kv, this.getColumnHelper().getDocumentRelatedColumns(), this.getSessionId(-1, this.getDocumentRelatedEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Sets the related documents.
	 *
	 * @param masterDocumentId
	 *            the master document id
	 * @param childDocumentId
	 *            the child document id
	 * @throws DmsException
	 */
	public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(masterDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			CheckingTools.failIfNull(childDocumentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
			Hashtable<String, Object> av = new Hashtable<>();
			MapTools.safePut(av, this.getColumnHelper().getDocumentRelatedMasterColumn(), masterDocumentId);
			MapTools.safePut(av, this.getColumnHelper().getDocumentRelatedChildColumn(), childDocumentId);
			this.getDocumentRelatedEntity().insert(av, this.getSessionId(-1, this.getDocumentRelatedEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	private TransactionalEntity getDocumentEntity() {
		return this.getEntity("EDocument");
	}

	private TransactionalEntity getDocumentPropertyEntity() {
		return this.getEntity("EDocProperty");
	}

	private TransactionalEntity getDocumentRelatedEntity() {
		return this.getEntity("EDocRelated");
	}

	private TransactionalEntity getCategoryEntity() {
		return this.getEntity("EDocCategory");
	}

	private TransactionalEntity getFileEntity() {
		return this.getEntity("EDocFile");
	}

	private TransactionalEntity getAllFileEntity() {
		return this.getEntity("EDocFileAllFiles");
	}
}
