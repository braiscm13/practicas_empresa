package com.opentach.common.dms;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.util.remote.BytesBlock;

public interface IDMSService extends Remote {
	final static String ID = "DMSService";

	EntityResult fileQuery(Hashtable<?, ?> filter, Vector<?> attrs, int sesId) throws Exception;

	void fileDelete(Serializable fileId, int sesId) throws Exception;

	void fileUpdate(Serializable fileId, Hashtable<String, Object> av, int sesId) throws Exception;

	Serializable categoryInsert(Serializable idDocument, String categoryName, Serializable idParentCategory, Hashtable<?, ?> otherData, int sesId) throws Exception;

	void categoryDelete(Serializable idCategory, int sesId) throws Exception;

	DMSCategory categoryGetForDocument(Serializable idDocument, Vector<?> attributes, int sesId) throws Exception;

	void moveFilesToCategory(Serializable idCategory, Vector<Serializable> data, int sesId) throws Exception;

	EntityResult fileVersionQuery(Serializable idVersionFile, Vector<String> asList, int sesId) throws Exception;

	// DOWNLOAD
	String prepareToTransfer(Serializable idVersionFile, int sessionId) throws Exception;

	BytesBlock getBytes(String rId, int leidosTotales, int blockSize, int sessionId) throws Exception;

	// UPLOAD
	String prepareToReceive(Serializable documentId, Hashtable<?, ?> av, int sessionId) throws Exception;

	void putBytes(String rId, BytesBlock bytesBlock, int sesId) throws Exception;

	DocumentIdentifier finishReceiving(String rId, int sessionId) throws Exception;

	void cancelReceiving(String rId, int sesId) throws Exception;

	// OTHER
	EntityResult fileGetVersions(Serializable fileId, Hashtable<?, ?> kv, Vector<?> attributes, int sesId) throws Exception;

	void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion, int sessionId) throws Exception;

	EntityResult documentQuery(Vector<?> attributes, Hashtable<?, ?> criteria, int sessionId) throws Exception;

	DocumentIdentifier documentInsert(Hashtable<?, ?> av, int sessionId) throws Exception;

	void documentUpdate(Serializable documentId, Hashtable<?, ?> attributesValues, int sessionId) throws Exception;

	void documentDelete(Serializable documentId, int sessionId) throws Exception;

	void documentAddProperties(Serializable documentId, Hashtable<String, String> properties, int sessionId) throws Exception;

	void documentDeleteProperties(Serializable documentId, Vector<String> propertyKeys, int sessionId) throws Exception;

	String documentGetProperty(Serializable documentId, String propertyKey, int sessionId) throws Exception;

	Hashtable<String, String> documentGetProperties(Serializable documentId, Hashtable<?, ?> kv, int sessionId) throws Exception;

	EntityResult documentGetFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, int sessionId) throws Exception;

	EntityResult documentGetAllFiles(Serializable documentId, Hashtable<?, ?> kv, Vector<?> attributes, int sessionId) throws Exception;

	EntityResult getRelatedDocument(Serializable documentId, int sessionId) throws Exception;

	void setRelatedDocuments(Serializable masterDocumentId, Serializable childDocumentId, int sessionId) throws Exception;

	void categoryUpdate(Serializable categoryId, Hashtable<?, ?> av, int sessionId) throws Exception;


}
