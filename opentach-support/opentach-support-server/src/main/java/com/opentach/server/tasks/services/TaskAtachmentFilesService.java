package com.opentach.server.tasks.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.tasks.ITaskAttachmentService;
import com.opentach.server.services.AbstractFilesService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class TaskAtachmentFilesService extends AbstractFilesService implements ITaskAttachmentService {

	// CONSTANTS
	/** The DB property with root folder to store all files. */
	public static final String	TASKATACHMENT_BASEPATH	= "TaskAttachments.basePath";
	/** Main entity to save into DB. */
	private static final String	ENTITY_NAME				= "ETaskAttachment";

	// ERRORS
	private static final String	E_ATTACHMENT_NOT_FOUND	= "E_ATTACHMENT_NOT_FOUND";
	private static final String	E_REQUIRED_PARAMETER	= "E_REQUIRED_PARAMETER__";

	/////// STANDARD METHODS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected String getRootFilesPath() {
		return TaskAtachmentFilesService.TASKATACHMENT_BASEPATH;
	}

	public TaskAtachmentFilesService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	protected Object uploadFile(Map<Object, Object> values, int sessionId, Connection con, InputStream inputStream) throws Exception {
		CheckingTools.failIf(!this.getLocator().hasSession(sessionId), AbstractFilesService.E_INVALID_USER);
		// Capture input data
		Hashtable<Object, Object> valuesToInsert = new Hashtable<>();
		this.catchValue(values, ITaskAttachmentService.COLUMN_NAME, valuesToInsert, null, true);
		this.catchValue(values, ITaskAttachmentService.COLUMN_DESC, valuesToInsert, null, false);
		this.catchValue(values, ITaskAttachmentService.COLUMN_TASKID, valuesToInsert, null, true);
		this.catchValue(values, ITaskAttachmentService.COLUMN_DATE, valuesToInsert, new Date(), true);
		this.catchValue(values, ITaskAttachmentService.COLUMN_USER, valuesToInsert, this.getUser(sessionId), true);

		// Insert into DB
		TransactionalEntity entity = this.getEntity(TaskAtachmentFilesService.ENTITY_NAME);
		EntityResult cloudInsert = entity.insert(valuesToInsert, this.getSessionId(sessionId, entity), con);
		Object idFile = cloudInsert.get(ITaskAttachmentService.COLUMN_ID);

		// Save file to disk
		Path filePath = this.getFilePath(idFile, (String) valuesToInsert.get(ITaskAttachmentService.COLUMN_NAME), con);
		try (OutputStream os = Files.newOutputStream(filePath)) {
			IOUtils.copy(inputStream, os);
		}
		return idFile;
	}

	private String getUser(int sessionId) throws Exception {
		return this.getLocator().getUser(sessionId);
	}

	@Override
	protected InputStream downloadFile(Object fileId, int sessionId, Connection con) throws Exception {
		// Check first valid attachment
		String fileName = this.checkFile(fileId, sessionId, con);

		// Second access file
		Path filePath = this.getFilePath(fileId, fileName, con);
		if (!Files.exists(filePath)) {
			throw new Exception(AbstractFilesService.E_FILE_NOT_FOUND);
		}
		return Files.newInputStream(filePath);
	}

	@Override
	protected void deleteFile(Object fileId, int sessionId, Connection con) throws Exception {
		// Check first valid attachment
		String fileName = this.checkFile(fileId, sessionId, con);

		TransactionalEntity entity = this.getEntity(TaskAtachmentFilesService.ENTITY_NAME);
		EntityResult resDelete = entity.delete(EntityResultTools.keysvalues(ITaskAttachmentService.COLUMN_ID, fileId), this.getSessionId(sessionId, entity), con);
		CheckingTools.checkValidEntityResult(resDelete, "E_DELETING_ATTACHMENT");

		// Delete file from disk
		Path filePath = this.getFilePath(fileId, fileName, con);
		if (Files.exists(filePath)) {
			Files.delete(filePath);
		}
	}

	@Override
	public String getFileName(final Object fileId, final int sessionId) throws Exception, RemoteException {
		CheckingTools.failIfNull(fileId, AbstractFilesService.E_FILE_ID_MANDATORY);
		CheckingTools.failIf(!this.getLocator().hasSession(sessionId), AbstractFilesService.E_INVALID_USER);
		return new OntimizeConnectionTemplate<String>() {
			@Override
			protected String doTask(Connection con) throws UException {
				try {
					return TaskAtachmentFilesService.this.getFileName(fileId, sessionId, con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), false);
	}

	protected String getFileName(Object fileId, int sessionId, Connection con) throws Exception, RemoteException {
		TransactionalEntity entity = this.getEntity(TaskAtachmentFilesService.ENTITY_NAME);
		EntityResult resBb = entity.query(EntityResultTools.keysvalues(ITaskAttachmentService.COLUMN_ID, fileId), EntityResultTools.attributes(ITaskAttachmentService.COLUMN_NAME),
				this.getSessionId(sessionId, entity), con);
		CheckingTools.checkValidEntityResult(resBb, "E_GETTING_ATTACHMENT_FILENAME", true, true, new Object[0]);
		return ((List<String>) resBb.get(ITaskAttachmentService.COLUMN_NAME)).get(0);
	}

	protected String checkFile(Object fileId, int sessionId, Connection con) throws Exception {
		TransactionalEntity entity = this.getEntity(TaskAtachmentFilesService.ENTITY_NAME);
		EntityResult resQuery = entity.query(EntityResultTools.keysvalues(ITaskAttachmentService.COLUMN_ID, fileId),
				EntityResultTools.attributes(ITaskAttachmentService.COLUMN_NAME), this.getSessionId(sessionId, entity), con);
		CheckingTools.checkValidEntityResult(resQuery, TaskAtachmentFilesService.E_ATTACHMENT_NOT_FOUND, true, true, new Object[0]);
		return (String) resQuery.getRecordValues(0).get(ITaskAttachmentService.COLUMN_NAME);
	}

	protected Path getFilePath(Object idCloudFile, String fileName, Connection con) throws Exception {
		Path rootPath = this.getRootPath(con);
		if (!Files.exists(rootPath)) {
			Files.createDirectories(rootPath);
		}
		return rootPath.resolve(idCloudFile + "_" + fileName);
	}

	protected void catchValue(Map<Object, Object> sourceValues, String columnName, Hashtable<Object, Object> valuesToInsert, Object defaultValue, boolean mandatory) {
		Object oValue = sourceValues.get(columnName);
		CheckingTools.failIf(mandatory && (oValue == null) && (defaultValue == null), TaskAtachmentFilesService.E_REQUIRED_PARAMETER + columnName, new Object[0]);

		MapTools.safePut(valuesToInsert, columnName, ObjectTools.coalesce(oValue, defaultValue));
	}

}
