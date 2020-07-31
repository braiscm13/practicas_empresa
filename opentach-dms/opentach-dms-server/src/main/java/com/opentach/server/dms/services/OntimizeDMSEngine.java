package com.opentach.server.dms.services;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ontimize.db.EntityResult;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;
import com.opentach.server.IOpentachServerLocator;

/**
 * The Ontimize standard DMS implementation. Splitted implementation over helpers.
 */
public class OntimizeDMSEngine implements IDMSServiceServer {

	/** The CONSTANT logger */
	private static final Logger					logger		= LoggerFactory.getLogger(OntimizeDMSEngine.class);

	/** The Constant ACTIVE. */
	public static final String					ACTIVE		= "Y";
	/** The Constant INACTIVE. */
	public static final String					INACTIVE	= "N";

	/** The file helper. */
	@Autowired
	protected DMSServiceFileHelper				fileHelper;

	/** The document helper. */
	@Autowired
	protected DMSServiceDocumentHelper			documentHelper;

	/** The category helper. */
	@Autowired
	protected DMSServiceCategoryHelper			categoryHelper;

	public OntimizeDMSEngine(IOpentachServerLocator locator) throws DmsException {
		super();
		this.fileHelper = new DMSServiceFileHelper(locator);
		this.documentHelper = new DMSServiceDocumentHelper(locator, this.fileHelper);
		this.categoryHelper = new DMSServiceCategoryHelper(locator, this.fileHelper);
	}


	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
	 */
	@Override
	public Path fileGetPath(Serializable fileId, Connection con) throws DmsException {
		return this.fileHelper.fileGetPath(fileId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetContentOfVersion (java.lang.Object)
	 */
	@Override
	public Path fileGetPathOfVersion(Serializable fileVersionId, Connection con) throws DmsException {
		return this.fileHelper.fileGetPathOfVersion(fileVersionId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileInsert(java.util.Map, java.io.InputStream)
	 */
	@Override
	public DocumentIdentifier fileInsert(Serializable documentId, Hashtable<?, ?> av, String user, InputStream is, Connection con) throws DmsException {
		return this.fileHelper.fileInsert(documentId, av, user, is, con);
	}

	/**
	 * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC version.
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version and deprecate
	 * @throws DmsException
	 */
	public Number getCurrentFileVersionAndDeprecate(Serializable fileId, Connection con) throws DmsException {
		return this.fileHelper.getCurrentFileVersionAndDeprecate(fileId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	@Override
	public DocumentIdentifier fileUpdate(Serializable fileId, Hashtable<?, ?> attributesValues, String user, InputStream is, Connection con) throws DmsException {
		return this.fileHelper.fileUpdate(fileId, attributesValues, user, is, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult fileQuery(Hashtable<?, ?> criteria, Vector<?> attributes, Connection con) throws DmsException {
		return this.fileHelper.fileQuery(criteria, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	@Override
	public void fileDelete(Serializable fileId, Connection con) throws DmsException {
		this.fileHelper.fileDelete(fileId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileVersionQuery(java.lang.Object, java.util.List)
	 */
	@Override
	public EntityResult fileVersionQuery(Serializable fileVersionId, Vector<String> attributes, Connection con) throws DmsException {
		return this.fileHelper.fileVersionQuery(fileVersionId, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileGetVersions(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult fileGetVersions(Serializable fileId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		return this.fileHelper.fileGetVersions(fileId, kv, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileRecoverPreviousVersion(java.lang.Object)
	 */
	@Override
	public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion, Connection con) throws DmsException {
		this.fileHelper.fileRecoverPreviousVersion(fileId, acceptNotPreviousVersion, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentQuery(java.util.List, java.util.Map)
	 */
	@Override
	public EntityResult documentQuery(Vector<?> attributes, Hashtable<?, ?> criteria, Connection con) throws DmsException {
		return this.documentHelper.documentQuery(attributes, criteria, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentInsert(java.util.Map)
	 */
	@Override
	public DocumentIdentifier documentInsert(Hashtable<?, ?> av, Connection con) throws DmsException {
		return this.documentHelper.documentInsert(av, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentUpdate(Serializable documentId, Hashtable<?, ?> attributesValues, Connection con) throws DmsException {
		this.documentHelper.documentUpdate(documentId, attributesValues, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDelete(java.lang.Object)
	 */
	@Override
	public void documentDelete(Serializable documentId, Connection con) throws DmsException {
		this.documentHelper.documentDelete(documentId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentAddProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public void documentAddProperties(Serializable documentId, Hashtable<String, String> properties, Connection con) throws DmsException {
		this.documentHelper.documentAddProperties(documentId, properties, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentDeleteProperties(java.lang.Object, java.util.List)
	 */
	@Override
	public void documentDeleteProperties(Serializable documentId, Vector<String> propertyKeys, Connection con) throws DmsException {
		this.documentHelper.documentDeleteProperties(documentId, propertyKeys, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public String documentGetProperty(Serializable documentId, String propertyKey, Connection con) throws DmsException {
		return this.documentHelper.documentGetProperty(documentId, propertyKey, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetProperties(java.lang.Object, java.util.Map)
	 */
	@Override
	public Hashtable<String, String> documentGetProperties(Serializable documentId, Hashtable<?, ?> kv, Connection con) throws DmsException {
		return this.documentHelper.documentGetProperties(documentId, kv, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		return this.documentHelper.documentGetFiles(documentId, kv, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#documentGetAllFiles(java.lang.Object, java.util.Map, java.util.List)
	 */
	@Override
	public EntityResult documentGetAllFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		return this.documentHelper.documentGetAllFiles(documentId, kv, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#getRelatedDocument(java.lang.Object)
	 */
	@Override
	public EntityResult getRelatedDocument(Serializable documentId, Connection con) throws DmsException {
		return this.documentHelper.getRelatedDocument(documentId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#setRelatedDocuments(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId, Connection con) throws DmsException {
		this.documentHelper.setRelatedDocuments(masterDocumentId, childDocumentId, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryGetForDocument(java.lang.Object, java.util.List)
	 */
	@Override
	public DMSCategory categoryGetForDocument(Serializable documentId, Vector<?> attributes, Connection con) throws DmsException {
		return this.categoryHelper.categoryGetForDocument(documentId, attributes, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryInsert(java.lang.String, java.lang.Object, java.util.Map)
	 */
	@Override
	public Serializable categoryInsert(Serializable documentId, String name, Serializable parentCategoryId, Hashtable<?, ?> otherData, Connection con) throws DmsException {
		return this.categoryHelper.categoryInsert(documentId, name, parentCategoryId, otherData, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryUpdate(java.lang.Object, java.util.Map)
	 */
	@Override
	public void categoryUpdate(Serializable categoryId, Hashtable<?, ?> av, Connection con) throws DmsException {
		this.categoryHelper.categoryUpdate(categoryId, av, con);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#categoryDelete(java.lang.Object)
	 */
	@Override
	public void categoryDelete(Serializable idCategory, Connection con) throws DmsException {
		this.categoryHelper.categoryDelete(idCategory, con);
	}

	@Override
	public void moveFilesToCategory(Serializable idCategory, Vector<Serializable> idFiles, Connection con) throws DmsException {
		this.fileHelper.moveFilesToCategory(idCategory, idFiles, con);
	}


	@Override
	public String prepareToTransfer(Serializable idVersionFile, Connection con) throws DmsException {
		return this.fileHelper.prepareToTransfer(idVersionFile, con);
	}

	@Override
	public BytesBlock getBytes(String rId, int leidosTotales, int blockSize) throws DmsException {
		return this.fileHelper.getBytes(rId, leidosTotales, blockSize);
	}

	@Override
	public String prepareToReceive(Serializable documentId, Hashtable<?, ?> av) throws DmsException {
		return this.fileHelper.prepareToReceive(documentId, av);
	}

	@Override
	public void cancelReceiving(String rId) throws DmsException {
		this.fileHelper.cancelReceiving(rId);
	}

	@Override
	public void putBytes(String rId, BytesBlock bytesBlock) throws DmsException {
		this.fileHelper.putBytes(rId, bytesBlock);
	}

	@Override
	public DocumentIdentifier finishReceiving(String rId, String user, Connection con) throws DmsException {
		return this.fileHelper.finishReceiving(rId, user, con);
	}

}
