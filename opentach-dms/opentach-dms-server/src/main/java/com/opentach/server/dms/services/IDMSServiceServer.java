package com.opentach.server.dms.services;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DMSCategory;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;

/**
 * Funcionalidades extendidas del DMS sólo disponibles desde el lado del servidor
 *
 */
public interface IDMSServiceServer {

	/**
	 * Obtiene el path de un fichero.
	 *
	 * @param fileId
	 *            the file id
	 * @return the input stream
	 * @throws Exception
	 *             the ontimize jee runtime exception
	 * @throws Exception
	 * @throws Exception
	 */
	Path fileGetPath(Serializable fileId, Connection con) throws DmsException, Exception;

	/**
	 * Obtiene el path de una versión dada de un fichero.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @return the input stream
	 * @throws Exception
	 *             the ontimize jee runtime exception
	 * @throws Exception
	 */
	Path fileGetPathOfVersion(Serializable fileVersionId, Connection con) throws DmsException, Exception;

	EntityResult fileQuery(Hashtable<?, ?> filter, Vector<?> attrs, Connection con) throws DmsException;

	void fileDelete(Serializable fileId, Connection con) throws DmsException;

	Serializable categoryInsert(Serializable idDocument, String categoryName, Serializable idParentCategory, Hashtable<?, ?> otherData, Connection con) throws DmsException;

	void categoryDelete(Serializable idCategory, Connection con) throws DmsException;

	DMSCategory categoryGetForDocument(Serializable idDocument, Vector<?> attributes, Connection con) throws DmsException;

	void moveFilesToCategory(Serializable idCategory, Vector<Serializable> data, Connection con) throws DmsException;

	EntityResult fileVersionQuery(Serializable idVersionFile, Vector<String> asList, Connection con) throws DmsException;

	EntityResult fileGetVersions(Serializable fileId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException;

	void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion, Connection con) throws DmsException;

	// DOWNLOAD
	String prepareToTransfer(Serializable idVersionFile, Connection con) throws DmsException;

	BytesBlock getBytes(String rId, int leidosTotales, int blockSize) throws DmsException;

	// UPLOAD
	String prepareToReceive(Serializable documentId, Hashtable<?, ?> av) throws DmsException;

	void cancelReceiving(String rId) throws DmsException;

	void putBytes(String rId, BytesBlock bytesBlock) throws DmsException;

	DocumentIdentifier finishReceiving(String rId, String user, Connection con) throws DmsException;

	EntityResult documentQuery(Vector<?> attributes, Hashtable<?, ?> criteria, Connection con) throws DmsException;

	DocumentIdentifier documentInsert(Hashtable<?, ?> av, Connection con) throws DmsException;

	void documentDelete(Serializable documentId, Connection con) throws DmsException;

	void documentAddProperties(Serializable documentId, Hashtable<String, String> properties, Connection con) throws DmsException;

	void documentDeleteProperties(Serializable documentId, Vector<String> propertyKeys, Connection con) throws DmsException;

	String documentGetProperty(Serializable documentId, String propertyKey, Connection con) throws DmsException;

	Hashtable<String, String> documentGetProperties(Serializable documentId, Hashtable<?, ?> kv, Connection con) throws DmsException;

	EntityResult documentGetFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException;

	EntityResult documentGetAllFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException;

	EntityResult getRelatedDocument(Serializable documentId, Connection con) throws DmsException;

	void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId, Connection con) throws DmsException;

	void categoryUpdate(Serializable categoryId, Hashtable<?, ?> av, Connection con) throws DmsException;

	void documentUpdate(Serializable documentId, Hashtable<?, ?> attributesValues, Connection con) throws DmsException;

	DocumentIdentifier fileInsert(Serializable documentId, Hashtable<?, ?> av, String user, InputStream is, Connection con) throws DmsException;

	DocumentIdentifier fileUpdate(Serializable fileId, Hashtable<?, ?> attributesValues, String user, InputStream is, Connection con) throws DmsException;

}
