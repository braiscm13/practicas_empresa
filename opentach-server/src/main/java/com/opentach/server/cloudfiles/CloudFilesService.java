package com.opentach.server.cloudfiles;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.cloudfiles.ICloudFilesService;
import com.opentach.common.exception.OpentachRuntimeException;
import com.opentach.messagequeue.api.IMessageQueueListener;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.IMessageQueueMessage;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.CloudFileReceivedQueueMessage;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.services.AbstractFilesService;

public class CloudFilesService extends AbstractFilesService implements ICloudFilesService, IMessageQueueListener {

	private static final String	E_DEVICE_PIN_MANDATORY	= "E_DEVICE_PIN_MANDATORY";
	private static final String	E_DEVICE_NOT_FOUND		= "E_DEVICE_NOT_FOUND";

	private static final String	EBLACKBERRY				= "EBlackberry";
	private static final String	ECLOUDFILE				= "ECloudFile";
	public static final String	CLOUDFILES_BASEPATH		= "CloudFiles.basePath";

	private static final String	COLUMN_UPLOADDATE		= "UPLOADDATE";
	private static final String	COLUMN_NOTES			= "NOTES";
	private static final String	COLUMN_IDBLACKBERRY		= "IDBLACKBERRY";
	private static final String	COLUMN_PIN				= "PIN";
	private static final String	COLUMN_NAME				= "NAME";
	private static final String	COLUMN_IDCLOUDFILE		= "IDCLOUDFILE";

	public CloudFilesService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	protected void init(Hashtable hconfig) {
		super.init(hconfig);
		((AbstractOpentachServerLocator) this.getLocator()).getBean(IMessageQueueManager.class).registerListener(QueueNames.CLOUDFILE_FILE_RECEIVED, this);
	}

	@Override
	public void onQueueEvent(String queueName, IMessageQueueMessage message) {
		CloudFileReceivedQueueMessage info = (CloudFileReceivedQueueMessage) message;
		try {
			this.uploadFile(info.getDevicePin(), info.getFileName(), info.getNotes(), info.getInputStream());
		} catch (Exception ex) {
			throw new OpentachRuntimeException(ex.getMessage(), ex);
		}
	}

	/////// NEW PUBLIC METHOD /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void uploadFile(final String devicePin, final String fileName, final String notes, final InputStream inputStream) throws Exception {
		Map<Object, Object> values = new HashMap<>();
		MapTools.safePut(values, CloudFilesService.COLUMN_PIN, devicePin);
		MapTools.safePut(values, CloudFilesService.COLUMN_NAME, fileName);
		MapTools.safePut(values, CloudFilesService.COLUMN_NOTES, notes);
		this.uploadFile(values, -1, inputStream);
	}

	/////// STANDARD METHODS /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected String getRootFilesPath() {
		return CloudFilesService.CLOUDFILES_BASEPATH;
	}

	@Override
	protected InputStream downloadFile(Object fileId, int sessionId, Connection con) throws Exception {
		TransactionalEntity eCloudFile = this.getEntity(CloudFilesService.ECLOUDFILE);
		EntityResult resCloudQuery = eCloudFile.query(EntityResultTools.keysvalues(CloudFilesService.COLUMN_IDCLOUDFILE, fileId),
				EntityResultTools.attributes(CloudFilesService.COLUMN_NAME), this.getSessionId(-1, eCloudFile), con);
		CheckingTools.checkValidEntityResult(resCloudQuery, CloudFilesService.E_DEVICE_NOT_FOUND, true, true, new Object[] {});
		String name = (String) resCloudQuery.getRecordValues(0).get(CloudFilesService.COLUMN_NAME);

		Path cloudPath = this.getCloudPath(fileId, name, con);
		if (!Files.exists(cloudPath)) {
			throw new Exception(AbstractFilesService.E_FILE_NOT_FOUND);
		}
		return Files.newInputStream(cloudPath);

	}

	@Override
	protected Object uploadFile(Map<Object, Object> values, int sessionId, Connection con, InputStream inputStream) throws Exception {
		// Capture input data
		String devicePin = (String) values.get(CloudFilesService.COLUMN_PIN);
		CheckingTools.failIfNull(devicePin, CloudFilesService.E_DEVICE_PIN_MANDATORY);
		String fileName = (String) values.get(CloudFilesService.COLUMN_NAME);
		CheckingTools.failIfNull(fileName, AbstractFilesService.E_FILENAME_MANDATORY);
		String notes = (String) values.get(CloudFilesService.COLUMN_NOTES);

		// buscar el smartphone con el pin indicado
		TransactionalEntity eBb = this.getEntity(CloudFilesService.EBLACKBERRY);
		EntityResult resBb = eBb.query(EntityResultTools.keysvalues(CloudFilesService.COLUMN_PIN, devicePin), EntityResultTools.attributes(CloudFilesService.COLUMN_IDBLACKBERRY),
				this.getSessionId(-1, eBb), con);
		CheckingTools.checkValidEntityResult(resBb, CloudFilesService.E_DEVICE_NOT_FOUND, true, true, new Object[] {});
		Object idBb = resBb.getRecordValues(0).get(CloudFilesService.COLUMN_IDBLACKBERRY);
		CheckingTools.failIfNull(idBb, CloudFilesService.E_DEVICE_NOT_FOUND);
		// insertar par obtener el idFile
		TransactionalEntity eCloudFile = this.getEntity(CloudFilesService.ECLOUDFILE);
		EntityResult cloudInsert = eCloudFile.insert(EntityResultTools.keysvalues(CloudFilesService.COLUMN_IDBLACKBERRY, idBb, CloudFilesService.COLUMN_NAME, fileName,
				CloudFilesService.COLUMN_NOTES, notes, CloudFilesService.COLUMN_UPLOADDATE, new Date()), this.getSessionId(-1, eCloudFile), con);
		Object idCloudFile = cloudInsert.get(CloudFilesService.COLUMN_IDCLOUDFILE);
		// guardar fichero
		Path filePath = this.getCloudPath(idCloudFile, fileName, con);
		try (OutputStream os = Files.newOutputStream(filePath)) {
			IOUtils.copy(inputStream, os);
		}
		// actualizar size
		long fileSize = Files.size(filePath);
		eCloudFile.update(EntityResultTools.keysvalues("FILESIZE", fileSize), EntityResultTools.keysvalues(CloudFilesService.COLUMN_IDCLOUDFILE, idCloudFile),
				this.getSessionId(-1, eCloudFile), con);
		return idCloudFile;
	}

	@Override
	public void deleteFile(Object fileId, int sessionId) throws Exception {
		throw new Exception("UNSUPPORTED");
	}

	@Override
	protected void deleteFile(Object fileId, int sessionId, Connection con) throws Exception {
		throw new Exception("UNSUPPORTED");
	}

	private Path getCloudPath(Object idCloudFile, String fileName, Connection con) throws Exception {
		Path rootPath = this.getRootPath(con);
		if (!Files.exists(rootPath)) {
			Files.createDirectories(rootPath);
		}
		return rootPath.resolve(idCloudFile + "_" + fileName);
	}

}
