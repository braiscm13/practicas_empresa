package com.opentach.server.dms.services;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;
import com.opentach.common.dms.IDMSService;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;

/**
 * The DMS service generic facade. This will delegate to configured engine : OntimizeDMSEngine ...
 */
public class DMSService extends UAbstractService implements IDMSService {

	private static final Logger	logger					= LoggerFactory.getLogger(DMSService.class);

	private static final String	PERMISSION_QUERY		= "query";
	private static final String	PERMISSION_MODIFY		= "modify";
	private static final String	PERMISSION_ENTITY_DMS	= "DMS";
	/** The implementation. */
	private IDMSServiceServer	engine;

	public DMSService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.setEngine(new OntimizeDMSEngine((IOpentachServerLocator) this.getLocator()));
	}

	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	protected IDMSServiceServer getEngine() {
		CheckingTools.failIfNull(this.engine, "Not engine defined for dms.");
		return this.engine;
	}

	/**
	 * Sets the engine.
	 *
	 * @param engine
	 *            the engine
	 */
	public void setEngine(IDMSServiceServer engine) {
		this.engine = engine;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	@Override
	public void fileUpdate(Serializable fileId, Hashtable<String, Object> attributesValues, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			String user = this.getLocator().getUser(sessionId);
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().fileUpdate(fileId, attributesValues, user, null, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult fileQuery(Hashtable<?, ?> criteria, Vector<?> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().fileQuery(criteria, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	@Override
	public void fileDelete(Serializable fileId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().fileDelete(fileId, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object, java.util.List)
	 */
	@Override
	public EntityResult fileVersionQuery(Serializable fileVersionId, Vector<String> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().fileVersionQuery(fileVersionId, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult fileGetVersions(Serializable fileId, Hashtable<?, ?> kv, Vector<?> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().fileGetVersions(fileId, kv, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
	 */
	@Override
	public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult documentQuery(Vector<?> attributes, Hashtable<?, ?> criteria, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentQuery(attributes, criteria, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
	 */
	@Override
	public DocumentIdentifier documentInsert(Hashtable<?, ?> av, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			return new OntimizeConnectionTemplate<DocumentIdentifier>() {

				@Override
				protected DocumentIdentifier doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentInsert(av, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentUpdate(Serializable documentId, Hashtable<?, ?> attributesValues, int sessionId) throws Exception {
		try {
			this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().documentUpdate(documentId, attributesValues, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
	 */
	@Override
	public void documentDelete(Serializable documentId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().documentDelete(documentId, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentAddProperties(Serializable documentId, Hashtable<String, String> properties, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().documentAddProperties(documentId, properties, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object, java.util.List)
	 */
	@Override
	public void documentDeleteProperties(Serializable documentId, Vector<String> propertyKeys, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().documentDeleteProperties(documentId, propertyKeys, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public String documentGetProperty(Serializable documentId, String propertyKey, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<String>() {

				@Override
				protected String doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentGetProperty(documentId, propertyKey, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public Hashtable<String, String> documentGetProperties(Serializable documentId, Hashtable<?, ?> kv, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<Hashtable<String, String>>() {
				@Override
				protected Hashtable<String, String> doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentGetProperties(documentId, kv, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentGetFiles(documentId, kv, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetAllFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().documentGetAllFiles(documentId, kv, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
	 */
	@Override
	public EntityResult getRelatedDocument(Serializable documentId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<EntityResult>() {

				@Override
				protected EntityResult doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().getRelatedDocument(documentId, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().setRelatedDocuments(masterDocumentId, childDocumentId, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object, java.util.List)
	 */
	@Override
	public DMSCategory categoryGetForDocument(Serializable documentId, Vector<?> attributes, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<DMSCategory>() {
				@Override
				protected DMSCategory doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().categoryGetForDocument(documentId, attributes, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId, Hashtable<?, ?> otherData, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			return new OntimizeConnectionTemplate<Serializable>() {

				@Override
				protected Serializable doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().categoryInsert(documentId, name, parentCategoryId, otherData, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void categoryUpdate(Serializable categoryId, Hashtable<?, ?> av, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().categoryUpdate(categoryId, av, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
	 */
	@Override
	public void categoryDelete(Serializable idCategory, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().categoryDelete(idCategory, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public void moveFilesToCategory(Serializable idCategory, Vector<Serializable> idFiles, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws DmsException {
					DMSService.this.getEngine().moveFilesToCategory(idCategory, idFiles, con);
					return null;
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public String prepareToTransfer(Serializable idVersionFile, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return new OntimizeConnectionTemplate<String>() {

				@Override
				protected String doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().prepareToTransfer(idVersionFile, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public BytesBlock getBytes(String rId, int offset, int blockSize, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_QUERY, sessionId);
		try {
			return DMSService.this.getEngine().getBytes(rId, offset, blockSize);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public String prepareToReceive(Serializable documentId, Hashtable<?, ?> av, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			return DMSService.this.getEngine().prepareToReceive(documentId, av);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public void cancelReceiving(String rId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			DMSService.this.getEngine().cancelReceiving(rId);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public void putBytes(String rId, BytesBlock bytesBlock, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			DMSService.this.getEngine().putBytes(rId, bytesBlock);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

	@Override
	public DocumentIdentifier finishReceiving(String rId, int sessionId) throws Exception {
		this.checkPermission(DMSService.PERMISSION_ENTITY_DMS, DMSService.PERMISSION_MODIFY, sessionId);
		try {
			String user = this.getLocator().getUser(sessionId);
			return new OntimizeConnectionTemplate<DocumentIdentifier>() {

				@Override
				protected DocumentIdentifier doTask(Connection con) throws DmsException {
					return DMSService.this.getEngine().finishReceiving(rId, user, con);
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception error) {
			DMSService.logger.error(null, error);
			throw new Exception(error.getMessage());
		}
	}

}
