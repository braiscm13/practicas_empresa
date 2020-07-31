package com.opentach.server.dms.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.dms.DMSNaming;
import com.opentach.common.dms.DmsException;
import com.opentach.common.dms.DocumentIdentifier;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.entities.EPreferenciasServidor.IPreferenceChangeListener;
import com.utilmize.tools.exception.UException;

public class DMSServiceFileHelper extends AbstractDMSServiceHelper implements IPreferenceChangeListener {

	/** The Constant logger. */
	private static final Logger						logger				= LoggerFactory.getLogger(DMSServiceFileHelper.class);
	private static final String						DMS_BASE_PROPERTY	= "DMS_BASE_PATH";

	/** The path name id formatter. */
	private static DecimalFormat					pathNameIdFormatter	= new DecimalFormat("#");

	private final DMSServiceFileTransfererHelper	fileTransferer;
	private final DMSServiceFileReceiverHelper		fileReceiver;
	private Path									dmsBasePath;

	public DMSServiceFileHelper(IOpentachServerLocator locator) throws DmsException {
		super(locator);
		this.fileTransferer = new DMSServiceFileTransfererHelper(this);
		this.fileReceiver = new DMSServiceFileReceiverHelper(this);
		EPreferenciasServidor ePreferences = (EPreferenciasServidor) this.getEntity("EPreferenciasServidor");
		ePreferences.addPropertyChangeListener(this);
		try {
			this.dmsBasePath = this.resolveBasePath(ePreferences.getValue(DMSServiceFileHelper.DMS_BASE_PROPERTY, this.getSessionId(-1, ePreferences)));
		} catch (UException err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Returns the file physical path.
	 *
	 * @param fileId
	 * @return the file path
	 * @throws DmsException
	 */
	public Path fileGetPath(Serializable fileId, Connection con) throws DmsException {
		Serializable versionId = this.getCurrentFileVersion(fileId, con);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		return this.fileGetPathOfVersion(versionId, con);
	}

	/**
	 * Returns the file physical path.
	 *
	 * @param fileVersionId
	 * @return the file path
	 * @throws DmsException
	 */
	public Path fileGetPathOfVersion(Serializable fileVersionId, Connection con) throws DmsException {
		return this.getPhysicalFileFor(fileVersionId, con);
	}

	/**
	 * File insert.
	 *
	 * @param documentId
	 *            the document id
	 * @param av
	 *            the av
	 * @param is
	 *            the is
	 * @return the object
	 * @throws DmsException
	 */
	public DocumentIdentifier fileInsert(Serializable documentId, Hashtable<?, ?> av, String user, InputStream is, Connection con) throws DmsException {
		String fileName = (String) av.get(this.getColumnHelper().getFileNameColumn());
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_FILE_NAME_MANDATORY);
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);

		// insertamos en la tabla de ficheros
		Hashtable<Object, Object> avFile = new Hashtable<>();
		avFile.putAll(av);// Pass other columns (extended implementations)
		MapTools.safePut(avFile, this.getColumnHelper().getFileNameColumn(), fileName);
		MapTools.safePut(avFile, this.getColumnHelper().getDocumentIdColumn(), documentId);
		EntityResult res;
		try {
			res = this.getFileEntity().insert(avFile, this.getSessionId(-1, this.getFileEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
		Serializable fileId = (Serializable) res.get(this.getColumnHelper().getFileIdColumn());
		CheckingTools.failIfNull(fileName, DMSNaming.ERROR_ERROR_CREATING_FILE);

		// insertamos en las versiones
		Serializable fileVersionId = this.createNewVersionForFile(fileId, av, user, is, con);

		DocumentIdentifier result = new DocumentIdentifier(documentId, fileId, fileVersionId);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileUpdate(java.lang.Object , java.util.Map, java.io.InputStream)
	 */
	/**
	 * File update.
	 *
	 * @param fileId
	 *            the file id
	 * @param attributesValues
	 *            the attributes values
	 * @param is
	 *            the is
	 * @throws DmsException
	 */
	public DocumentIdentifier fileUpdate(Serializable fileId, Hashtable<?, ?> attributesValues, String user, InputStream is, Connection con) throws DmsException {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);

		// si el inputstream es nulo actualizamos los campos
		if (is == null) {
			// Obtenemos el id de la versión actual
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			EntityResult er;
			try {
				er = this.getFileVersionEntity().query(kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()),
						this.getSessionId(-1, this.getFileVersionEntity()), con);
			} catch (Exception err) {
				throw new DmsException(err);
			}
			CheckingTools.failIf(er.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
			Serializable currentVersionId = (Serializable) er.getRecordValues(0).get(this.getColumnHelper().getVersionIdColumn());
			this.updateCurrentVersionAttributes(fileId, currentVersionId, attributesValues, con);

			return new DocumentIdentifier(null, fileId, currentVersionId);// FIXME Consider to catch documentId
		}
		// En este caso hay que crear una nueva versión
		// Si viene el nombre del fichero lo actualizamos en la tabla de ficheros
		String fileName = (String) attributesValues.remove(this.getColumnHelper().getFileNameColumn());
		if (fileName != null) {
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			Hashtable<String, Object> avUpdate = new Hashtable<>();
			MapTools.safePut(avUpdate, this.getColumnHelper().getFileNameColumn(), fileName);
			try {
				this.getFileEntity().update(avUpdate, kv, this.getSessionId(-1, this.getFileEntity()), con);
			} catch (Exception err) {
				throw new DmsException(err);
			}
		}
		Serializable versionId = this.createNewVersionForFile(fileId, attributesValues, user, is, con);

		return new DocumentIdentifier(null, fileId, versionId);// FIXME Consider to catch documentId
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileQuery(java.util.List, java.util.Map)
	 */
	/**
	 * File query.
	 *
	 * @param criteria
	 *            the criteria
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws DmsException
	 */
	public EntityResult fileQuery(Hashtable<?, ?> criteria, Vector<?> attributes, Connection con) throws DmsException {
		// TODO anadir filtro de dueno?
		try {
			return this.getFileEntity().query(criteria, attributes, this.getSessionId(-1, this.getFileEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.common.services.dms.IDMSService#fileDelete(java.lang.Object )
	 */
	/**
	 * File delete.
	 *
	 * @param fileId
	 *            the file id
	 * @throws DmsException
	 */
	public void fileDelete(Serializable fileId, Connection con) throws DmsException {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		EntityResult res = this.fileGetVersions(fileId, new Hashtable<>(), EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()), con);
		List<Serializable> fileVersionIds = (List<Serializable>) res.get(this.getColumnHelper().getVersionIdColumn());

		// borramos las versiones, sin borrar los ficheros
		List<Path> toDelete = this.deleteFileVersionsWithoutDeleteFiles(fileId, fileVersionIds, con);

		// borramos el fichero
		Hashtable<String, Object> kvFile = new Hashtable<>();
		MapTools.safePut(kvFile, this.getColumnHelper().getFileIdColumn(), fileId);
		try {
			this.getFileEntity().delete(kvFile, this.getSessionId(-1, this.getFileEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
		// si todo fue bien y no se va a hacer rollback, borramos
		for (Path file : toDelete) {
			FileTools.deleteQuitely(file);
		}
	}

	/**
	 * File version query.
	 *
	 * @param fileVersionId
	 *            the file version id
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws DmsException
	 * @throws DmsException
	 *             the ontimize jee runtime exception
	 */
	public EntityResult fileVersionQuery(Serializable fileVersionId, Vector<?> attributes, Connection con) throws DmsException {
		Hashtable<String, Object> kv = new Hashtable<String, Object>();
		MapTools.safePut(kv, this.getColumnHelper().getVersionIdColumn(), fileVersionId);
		try {
			return this.getFileVersionEntity().query(kv, attributes, this.getSessionId(-1, this.getFileVersionEntity()), con);
		} catch (Exception ex) {
			throw new DmsException(ex);
		}
	}

	/**
	 * File get versions.
	 *
	 * @param fileId
	 *            the file id
	 * @param kv
	 *            the kv
	 * @param attributes
	 *            the attributes
	 * @return the entity result
	 * @throws DmsException
	 */
	public EntityResult fileGetVersions(Serializable fileId, Hashtable<?, ?> kv, Vector<?> attributes, Connection con) throws DmsException {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		((Hashtable<Object, Object>) kv).put(this.getColumnHelper().getFileIdColumn(), fileId);
		try {
			return this.getFileVersionEntity().query(kv, attributes, this.getSessionId(-1, this.getFileVersionEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * File - revocer previous version
	 *
	 * @param fileId
	 *            the file id
	 * @param acceptNotPreviousVersion
	 * @return
	 * @throws DmsException
	 */
	public void fileRecoverPreviousVersion(Serializable fileId, boolean acceptNotPreviousVersion, Connection con) throws DmsException {
		EntityResult availableVersions = this.fileGetVersions(fileId, new Hashtable<>(), this.getColumnHelper().getVersionColumns(), con);
		if ((!acceptNotPreviousVersion && (availableVersions == null)) || (availableVersions.calculateRecordNumber() < 2)) {
			throw new DmsException("E_NOT_AVAILABLE_VERSIONS_TO_RECOVER");
		}

		// Detect current version and previous
		List<Serializable> vId = (List) availableVersions.get(this.getColumnHelper().getVersionIdColumn());
		List<Serializable> vActive = (List) availableVersions.get(this.getColumnHelper().getVersionActiveColumn());
		List<Number> vVersion = (List<Number>) availableVersions.get(this.getColumnHelper().getVersionVersionColumn());
		int currentVersionIdx = vActive.indexOf(OntimizeDMSEngine.ACTIVE);
		if (currentVersionIdx < 0) {
			throw new DmsException("E_NOT_CURRENT_ACTIVE_VERSION");
		}

		long currentVersion = vVersion.get(currentVersionIdx).longValue();
		// Look for max previous version of current (usually currentVersion-1)
		int previousVersionIdx = -1;
		long previousVersion = 0;
		int idx = 0;
		for (Number version : vVersion) {
			if ((version.longValue() > previousVersion) && (version.longValue() < currentVersion)) {
				previousVersion = version.longValue();
				previousVersionIdx = idx;
			}
			idx++;
		}
		if ((previousVersionIdx < 0) && !acceptNotPreviousVersion) {
			throw new DmsException("E_INVALID_PREVIOUS_VERSION");
		}

		if (previousVersionIdx >= 0) {
			// Mark previous version as current active
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getVersionIdColumn(), vId.get(previousVersionIdx));
			Hashtable<String, Object> avUpdate = new Hashtable<>();
			MapTools.safePut(avUpdate, this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
			try {
				this.getFileVersionEntity().update(avUpdate, kv, this.getSessionId(-1, this.getFileVersionEntity()), con);
			} catch (Exception err) {
				throw new DmsException(err);
			}
		}

		// Delete last version
		Path file = this.deleteFileVersionWithoutDeleteFile(fileId, vId.get(currentVersionIdx), con);
		FileTools.deleteQuitely(file);
	}

	/* ############## */
	/* HELPER METHODS */
	/* ############## */

	/**
	 * Obtiene la ruta fisica a un fichero.
	 *
	 * @param idVersion
	 *            the id version
	 * @return the physical file for
	 * @throws DmsException
	 */
	public Path getPhysicalFileFor(Serializable idVersion, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(idVersion, DMSNaming.ERROR_FILE_VERSION_ID_IS_MANDATORY);
			Hashtable<String, Object> kvVersion = new Hashtable<>();
			MapTools.safePut(kvVersion, this.getColumnHelper().getVersionIdColumn(), idVersion);
			EntityResult res = this.getFileVersionEntity().query(kvVersion,
					EntityResultTools.attributes(this.getColumnHelper().getVersionVersionColumn(), this.getColumnHelper().getFileIdColumn()),
					this.getSessionId(-1, this.getFileVersionEntity()), con);
			CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_VERSION_NOT_FOUND);
			Hashtable<?, ?> rv = res.getRecordValues(0);
			Serializable fileId = (Serializable) rv.get(this.getColumnHelper().getFileIdColumn());

			Hashtable<String, Object> kvFile = new Hashtable<>();
			MapTools.safePut(kvFile, this.getColumnHelper().getFileIdColumn(), fileId);
			res = this.getAllFileEntity().query(kvFile, EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()), this.getSessionId(-1, this.getAllFileEntity()),
					con);
			CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_FILE_NOT_FOUND);

			rv = res.getRecordValues(0);
			Serializable documentId = (Serializable) rv.get(this.getColumnHelper().getDocumentIdColumn());
			return this.getPhysicalFileFor(documentId, fileId, idVersion);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Obtiene la ruta fisica a un fichero.
	 *
	 * @param documentId
	 *            the document id
	 * @param fileId
	 *            the file id
	 * @param versionId
	 *            the version id
	 * @return the physical file for
	 * @throws DmsException
	 */
	protected Path getPhysicalFileFor(Serializable documentId, Serializable fileId, Serializable versionId) throws DmsException {
		CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
		CheckingTools.failIfNull(versionId, DMSNaming.ERROR_NO_FILE_VERSION);
		String id = DMSServiceFileHelper.pathNameIdFormatter.format(fileId);
		String version = DMSServiceFileHelper.pathNameIdFormatter.format(versionId);
		return this.getDocumentBasePathForDocumentId(documentId).resolve(id + "_" + version);
	}

	/**
	 * Gets the document base path for document id.
	 *
	 * @param documentId
	 *            the document id
	 * @return the document base path for document id
	 * @throws DmsException
	 */
	protected Path getDocumentBasePathForDocumentId(Serializable documentId) throws DmsException {
		CheckingTools.failIfNull(documentId, DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		return this.getDocumentsBasePath().resolve(DMSServiceFileHelper.pathNameIdFormatter.format(documentId));
	}

	/**
	 * Gets the documents base path.
	 *
	 * @return the documents base path
	 * @throws DmsException
	 */
	protected Path getDocumentsBasePath() throws DmsException {
		if (!Files.exists(this.dmsBasePath)) {
			try {
				Files.createDirectories(this.dmsBasePath);
			} catch (IOException e) {
				throw new DmsException("Could not create base folder for DMS", e);
			}
		}
		return this.dmsBasePath;
	}

	/**
	 * Delete file versions without delete files.
	 *
	 * @param fileId
	 *            the file id
	 * @param fileVersionIds
	 *            the file version ids
	 * @return the list
	 * @throws DmsException
	 */
	public List<Path> deleteFileVersionsWithoutDeleteFiles(Serializable fileId, List<Serializable> fileVersionIds, Connection con) throws DmsException {
		List<Path> toDelete = new ArrayList<>(fileVersionIds.size());
		for (Serializable idVersion : fileVersionIds) {
			Path deleteFileVersion = this.deleteFileVersionWithoutDeleteFile(fileId, idVersion, con);
			toDelete.add(deleteFileVersion);
		}
		return toDelete;
	}

	/**
	 * Delete a file version.
	 *
	 * @param fileId
	 *            the file id
	 * @param idVersion
	 *            the id version
	 * @return the file that need to be deleted
	 * @throws DmsException
	 */
	protected Path deleteFileVersionWithoutDeleteFile(Serializable fileId, Serializable idVersion, Connection con) throws DmsException {
		Hashtable<String, Object> kvVersion = new Hashtable<>();
		Path physicalFileFor = this.getPhysicalFileFor(idVersion, con);
		MapTools.safePut(kvVersion, this.getColumnHelper().getVersionIdColumn(), idVersion);
		try {
			this.getFileVersionEntity().delete(kvVersion, this.getSessionId(-1, this.getFileVersionEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
		return physicalFileFor;
	}

	/**
	 * Update current version attributes.
	 *
	 * @param fileId
	 *            the file id
	 * @param currentVersionId
	 *            the current version id
	 * @param attributesValues
	 *            the attributes values
	 * @throws DmsException
	 */
	protected void updateCurrentVersionAttributes(Serializable fileId, Serializable currentVersionId, Hashtable<?, ?> attributesValues, Connection con) throws DmsException {
		try {
			// Split columns for FILE and for VERSION daos
			List<String> columnsDocumentFile = this.getColumnHelper().getFileColumns();
			List<String> columnsDocumentFileVersion = this.getColumnHelper().getVersionColumns();
			Hashtable<String, Object> avFile = new Hashtable<>();
			Hashtable<String, Object> avVersion = new Hashtable<>();
			for (Entry<?, ?> entry : attributesValues.entrySet()) {
				if (columnsDocumentFile.contains(entry.getKey())) {
					MapTools.safePut(avFile, (String) entry.getKey(), entry.getValue());
				}
				if (columnsDocumentFileVersion.contains(entry.getKey())) {
					MapTools.safePut(avVersion, (String) entry.getKey(), entry.getValue());
				}
			}
			if (avFile.isEmpty() && avVersion.isEmpty()) {
				throw new DmsException("dms.E_NO_DATA_TO_UPDATE");
			}

			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			this.getFileEntity().update(avFile, kv, this.getSessionId(-1, this.getFileEntity()), con);

			kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getVersionIdColumn(), currentVersionId);
			this.getFileVersionEntity().update(avVersion, kv, this.getSessionId(-1, this.getFileVersionEntity()), con);
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Returns current active version of a file
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version
	 * @throws DmsException
	 */
	protected Serializable getCurrentFileVersion(Serializable fileId, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
			Hashtable<String, Object> kv = new Hashtable<String, Object>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			MapTools.safePut(kv, this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.ACTIVE);
			EntityResult res = this.getFileVersionEntity().query(kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn()),
					this.getSessionId(-1, this.getFileVersionEntity()), con);
			CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);
			Serializable versionId = (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getVersionIdColumn());
			return versionId;
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Ensures to deprecate current ACTIVE version of file (if exists) and return next DEFAULT AUTOMATIC version.
	 *
	 * @param fileId
	 *            the file id
	 * @return the current file version and deprecate
	 * @throws DmsException
	 */
	protected Number getCurrentFileVersionAndDeprecate(Serializable fileId, Connection con) throws DmsException {
		try {
			Number fileVersion = Long.valueOf(1);

			// cogemos la referencia a la versión actual
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			EntityResult er = this.fileQuery(kv, EntityResultTools.attributes(this.getColumnHelper().getVersionIdColumn(), this.getColumnHelper().getVersionVersionColumn()), con);
			CheckingTools.failIf(er.calculateRecordNumber() > 1, DMSNaming.ERROR_ACTIVE_VERSION_NOT_FOUND);

			// Si existe la marcamos como no activa y sumamos 1 a la versión que vamos a insertar
			if (er.calculateRecordNumber() == 1) {
				Hashtable<?, ?> record = er.getRecordValues(0);
				Serializable oldVersionId = (Serializable) record.get(this.getColumnHelper().getVersionIdColumn());
				if (oldVersionId != null) {
					Number oldVersion = (Number) record.get(this.getColumnHelper().getVersionVersionColumn());
					fileVersion = Long.valueOf(oldVersion.longValue() + 1);

					kv = new Hashtable<>();
					MapTools.safePut(kv, this.getColumnHelper().getVersionIdColumn(), oldVersionId);
					Hashtable<String, Object> avUpdate = new Hashtable<>();
					MapTools.safePut(avUpdate, this.getColumnHelper().getVersionActiveColumn(), OntimizeDMSEngine.INACTIVE);
					this.getFileVersionEntity().update(avUpdate, kv, this.getSessionId(-1, this.getFileVersionEntity()), con);
				}
			}
			return fileVersion;
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Move files to a new idDmsDoc. It ensures to change the reference in database and move files to the new directory
	 *
	 * @param idDmsDocFiles
	 * @param idDmsDoc
	 * @throws DmsException
	 */
	public void moveFilesToDoc(List<Serializable> idDmsDocFiles, Serializable idDmsDoc, Connection con) throws DmsException {
		try {
			// Comprobamos parámetros
			if ((idDmsDocFiles == null) || (idDmsDocFiles.isEmpty())) {
				throw new DmsException("ErrorNoDocFiles");
			}
			if ((idDmsDoc == null) || (idDmsDoc instanceof NullValue)) {
				throw new DmsException("ErrorNoIdDmsDoc");
			}
			Hashtable<String, Object> av = new Hashtable<>();
			MapTools.safePut(av, "ID_DMS_DOC", idDmsDoc);
			Hashtable<String, Object> kv = new Hashtable<>();

			for (final Serializable idDmsDocFile : idDmsDocFiles) {
				// Cogemos todas las versiones del fichero que vamos a mover
				Serializable documentId = this.getDocumentIdForFile(idDmsDocFile, con);
				Path pathIdDMSDocFile = this.getDocumentBasePathForDocumentId(documentId);
				File dir = pathIdDMSDocFile.toFile();
				File[] foundFiles = dir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.startsWith(idDmsDocFile.toString());
					}
				});
				// Movemos los ficheros al nuevo directorio determinado por el parámetros idDmsDoc
				for (File file : foundFiles) {
					try {
						FileUtils.moveToDirectory(file, this.getDocumentBasePathForDocumentId(idDmsDoc).toFile(), true);
					} catch (IOException error) {
						throw new DmsException(error);
					}
				}
				// Actualizamos las referencias en base de datos
				MapTools.safePut(kv, "ID_DMS_DOC_FILE", idDmsDocFile);
				this.getFileEntity().update(av, kv, this.getSessionId(-1, this.getFileEntity()), con);
			}
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Creates the new version for file.
	 *
	 * @param fileId
	 *            the file id
	 * @param attributes
	 *            the attributes
	 * @param is
	 *            the is
	 * @return the Object
	 * @throws DmsException
	 */
	protected Serializable createNewVersionForFile(Serializable fileId, Hashtable<?, ?> attributes, String user, InputStream is, Connection con) throws DmsException {
		try {
			Serializable documentId = this.getDocumentIdForFile(fileId, con);
			Serializable fileVersion = null;

			// Si recibimos la versión por parte del usuario utilizamos esa, siempre y cuando no exista ya
			if (attributes.containsKey(this.getColumnHelper().getVersionVersionColumn())) {
				fileVersion = (Serializable) attributes.get(this.getColumnHelper().getVersionVersionColumn());
				// Check if conflict
				Hashtable<Object, Object> kvCheck = new Hashtable<>();
				MapTools.safePut(kvCheck, this.getColumnHelper().getVersionVersionColumn(), fileVersion);
				EntityResult resVersions = this.fileGetVersions(fileId, kvCheck, EntityResultTools.attributes(this.getColumnHelper().getVersionVersionColumn()), con);
				CheckingTools.failIf(resVersions.calculateRecordNumber() > 0, DMSNaming.ERROR_VERSION_ALREADY_EXISTS);
			} else {
				fileVersion = this.getCurrentFileVersionAndDeprecate(fileId, con);
			}

			CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
			CheckingTools.failIfNull(is, DMSNaming.ERROR_INPUTSTREAM_IS_MANDATORY);
			Hashtable<String, Object> avVersion = new Hashtable<>();
			MapTools.safePut(avVersion, this.getColumnHelper().getFileIdColumn(), fileId);
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionAddedDateColumn(), new Date());
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionAddedUserColumn(), user);
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionDescriptionColumn(), attributes.get(this.getColumnHelper().getVersionDescriptionColumn()));
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionPathColumn(), attributes.get(this.getColumnHelper().getVersionPathColumn()));
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionActiveColumn(), attributes.containsKey(this.getColumnHelper().getVersionActiveColumn()) ? attributes
					.get(this.getColumnHelper().getVersionActiveColumn()) : OntimizeDMSEngine.ACTIVE);
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionVersionColumn(), fileVersion);
			MapTools.safePut(avVersion, this.getColumnHelper().getVersionThumbnailColumn(), attributes.get(this.getColumnHelper().getVersionThumbnailColumn()));
			EntityResult resVersion = this.getFileVersionEntity().insert(avVersion, this.getSessionId(-1, this.getFileVersionEntity()), con);
			Serializable versionId = (Serializable) resVersion.get(this.getColumnHelper().getVersionIdColumn());
			CheckingTools.failIfNull(versionId, DMSNaming.ERROR_CREATING_FILE_VERSION);
			Path file = this.getPhysicalFileFor(documentId, fileId, versionId);
			try {
				CheckingTools.failIf(Files.exists(file), DMSNaming.ERROR_FILE_ALREADY_EXISTS);
				Files.createDirectories(file.getParent());
				// TODO ver si es necesario hacer esto antes para no ocupar memoria,
				// aunque las operaciones anteriores deber?an ser inmediatas
				long time = System.currentTimeMillis();
				try (OutputStream output = Files.newOutputStream(file)) {
					IOUtils.copy(is, output);
				}
				// update filesize
				long fileSize = Files.size(file);
				Hashtable<String, Object> kvVersion = new Hashtable<String, Object>();
				MapTools.safePut(kvVersion, this.getColumnHelper().getVersionIdColumn(), versionId);
				Hashtable<String, Object> avVersionSize = new Hashtable<String, Object>();
				MapTools.safePut(avVersionSize, this.getColumnHelper().getVersionSizeColumn(), fileSize);
				this.getFileVersionEntity().update(avVersionSize, kvVersion, this.getSessionId(-1, this.getFileVersionEntity()), con);

				DMSServiceFileHelper.logger.debug("Time copying file: {}", (System.currentTimeMillis() - time));
				return versionId;
			} catch (Exception error) {
				FileTools.deleteQuitely(file);
				throw error;
			}
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	/**
	 * Gets the document id for file.
	 *
	 * @param fileId
	 *            the file id
	 * @return the document id for file
	 * @throws DmsException
	 */
	protected Serializable getDocumentIdForFile(Serializable fileId, Connection con) throws DmsException {
		try {
			CheckingTools.failIfNull(fileId, DMSNaming.ERROR_FILE_ID_MANDATORY);
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), fileId);
			EntityResult res = this.getAllFileEntity().query(kv, EntityResultTools.attributes(this.getColumnHelper().getDocumentIdColumn()),
					this.getSessionId(-1, this.getAllFileEntity()), con);
			CheckingTools.failIf(res.calculateRecordNumber() != 1, DMSNaming.ERROR_DOCUMENT_NOT_FOUND);
			return (Serializable) res.getRecordValues(0).get(this.getColumnHelper().getDocumentIdColumn());
		} catch (Exception err) {
			throw new DmsException(err);
		}
	}

	public void moveFilesToCategory(Serializable idCategory, List<Serializable> idFiles, Connection con) throws DmsException {
		try {
			if ((idFiles == null) || idFiles.isEmpty()) {
				return;
			}
			Hashtable<String, Object> av = new Hashtable<>();
			MapTools.safePut(av, this.getColumnHelper().getCategoryIdColumn(), idCategory == null ? new NullValue() : idCategory);
			Hashtable<String, Object> kv = new Hashtable<>();
			MapTools.safePut(kv, this.getColumnHelper().getFileIdColumn(), new SearchValue(SearchValue.IN, new Vector<>(idFiles)));
			this.getFileEntity().update(av, kv, this.getSessionId(-1, this.getFileEntity()), con);
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

	private TransactionalEntity getFileVersionEntity() {
		return this.getEntity("EDocFileVersion");
	}

	private TransactionalEntity getAllFileEntity() {
		return this.getEntity("EDocFileAllFiles");
	}

	@Override
	public void propertyChanged(String key, String value) {
		if (DMSServiceFileHelper.DMS_BASE_PROPERTY.equals(key)) {
			this.dmsBasePath = this.resolveBasePath(value);
		}
	}

	protected Path resolveBasePath(String basePath) {
		try {
			if (!basePath.endsWith("/")) {
				basePath += "/";
			}
			Path path = Paths.get(new URI(basePath));
			DMSServiceFileHelper.logger.info("Final path / class {} / {}", path, path.getClass().getName());
			return path;
		} catch (Exception ex) {
			DMSServiceFileHelper.logger.error(null, ex);
		}
		return null;
	}

	public String prepareToReceive(Serializable documentId, Hashtable<?, ?> av) throws DmsException {
		return this.fileReceiver.prepareToReceive(documentId, av);
	}

	public void cancelReceiving(String receivingId) throws DmsException {
		this.fileReceiver.cancelReceiving(receivingId);
	}

	public void putBytes(String receivingId, BytesBlock bytesBlock) throws DmsException {
		this.fileReceiver.putBytes(receivingId, bytesBlock);
	}

	public DocumentIdentifier finishReceiving(String receivingId, String user, Connection con) throws DmsException {
		return this.fileReceiver.finishReceiving(receivingId, user, con);
	}

	public String prepareToTransfer(Serializable versionId, Connection con) throws DmsException {
		return this.fileTransferer.prepareToTransfer(versionId, con);
	}

	public BytesBlock getBytes(String rId, int offset, int lenght) throws DmsException {
		return this.fileTransferer.getBytes(rId, offset, lenght);
	}
}
