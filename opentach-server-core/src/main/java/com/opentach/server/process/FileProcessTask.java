package com.opentach.server.process;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.gui.DataFile;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.Template;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.util.ZipUtils;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.RecomputeDirtyQueueMessage;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class FileProcessTask extends AbstractDelegate implements Priorizable, Runnable {

	private static final Logger				logger			= LoggerFactory.getLogger(FileProcessTask.class);

	private static final String				FILE_PREFIX		= "process";
	private static final String				FILE_SUFFIX		= ".tgd";
	private static final String				XMLFILE_SUFFIX	= ".xml";

	private final FileInfo						fileInfo;
	private long								startTime;
	private long								endTime;
	private boolean								hasError;
	private final ErrorLogTableManagementThread	errorLogTableThread;

	public FileProcessTask(FileInfo fileInfo, ErrorLogTableManagementThread errorLogTableThread, IOpentachServerLocator locator) {
		super(locator);
		this.startTime = 0;
		this.endTime = 0;
		this.fileInfo = fileInfo;
		this.hasError = false;
		this.errorLogTableThread = errorLogTableThread;
		CheckingTools.failIfNull(fileInfo, "Null fileinfo");
	}

	@Override
	public int getPriority() {
		return this.fileInfo.getPriority();
	}

	public FileInfo getFileInfo() {
		return this.fileInfo;
	}

	@Override
	public void run() {
		Chronometer chrono = new Chronometer().start();
		FileProcessTask.logger.info("File {} is being processed", this.fileInfo);
		try {

			new OntimizeConnectionTemplate<Void>() {

				@Override
				protected Void doTask(Connection con) throws UException {
					try {
						FileProcessTask.this.doProcess(FileProcessTask.this.fileInfo, con);
						return null;
					} catch (Exception err) {
						throw new UException(err);
					}
				}

				@Override
				protected void onErrorAfterRollback(Throwable error, Connection conn) throws UException {
					try {
						FileProcessTask.this.hasError = true;
						FileProcessTask.logger.error(null, error);
						FileProcessTask.this.saveIncidences(FileProcessTask.this.fileInfo, error.getMessage() == null ? "nullpi" : error.getMessage(), conn);
						conn.commit();
					} catch (Exception err) {
						throw new UException(err);
					}
				}

				@Override
				protected void onFinishedAfterAutocommitTrue(Connection con) {
					try {
						FileProcessTask.this.markProcess(FileProcessTask.this.fileInfo.getIdFile(), (String) FileProcessTask.this.fileInfo.getContracts().toArray()[0], con);
					} catch (Exception e) {
						FileProcessTask.logger.error("ERROR FATAL MARCANDO FICHERO COMO PROCESADO!!!!", e);
					}
				}
			}.execute(this.getConnectionManager(), false);
		} catch (Exception ex) {
			FileProcessTask.logger.error(null, ex);
		}
		FileProcessTask.logger.info("File {} fully processed in {} ms", this.fileInfo, chrono.stopMs());
	}

	private void doProcess(FileInfo fInfo, Connection con) throws Exception {
		ProcessedRecord processedRecord = this.processFile(fInfo, con);
		con.commit();
		this.getService(TachoFileProcessService.class).getProcessTaskPostOperationHelper().onFileProcessed(fInfo);

		// Notify dirtydates
		this.notityDirtyDates(this.fileInfo, processedRecord, con);
	}

	private ProcessedRecord processFile(FileInfo fInfo, Connection conn) throws Exception {
		Chronometer chrono = new Chronometer().start();
		if (fInfo.isAlreadyProcessed()) {
			FileProcessTask.logger.info("\tFile is already processed, skip process");
			return null;
		}
		File tmpFile = this.loadFile(fInfo, conn);
		FileProcessTask.logger.info("File loaded in {} ms, size {}", chrono.elapsedMs(), tmpFile == null ? -1 : tmpFile.length());

		try {
			ProcessedRecord processedRecord = new FileProcessTaskProcessHelper().process(null, fInfo, tmpFile, this.errorLogTableThread, conn);
			FileProcessTask.logger.info("\tPart - Processed in {} ms with result {}", chrono.elapsedMs(), processedRecord);
			return processedRecord;
		} finally {
			tmpFile.delete();
		}

	}

	private TableEntity getFileEntity() {
		return (TableEntity) this.getEntity(ITGDFileConstants.FILE_ENTITY);
	}

	private void markProcess(Number idFile, String cgContrato, Connection conn) throws Exception {
		String sql = new Template("sql/process/markProcess.sql").getTemplate();
		new UpdateJdbcTemplate().execute(conn, sql, idFile.intValue(), cgContrato);
	}

	private File loadFile(FileInfo fInfo, Connection conn) throws Exception {
		InputStream is = null;
		Vector<Object> vQuery = new Vector<Object>(1);
		vQuery.add(ITGDFileConstants.FILE_FIELD);
		Hashtable<String, Object> htQuery = new Hashtable<String, Object>(1);
		htQuery.put(OpentachFieldNames.IDFILE_FIELD, fInfo.getIdFile());
		int privSessionID = TableEntity.getEntityPrivilegedId(this.getFileEntity());
		EntityResult res = this.getFileEntity().query(htQuery, vQuery, privSessionID, conn);
		int cuenta = res.calculateRecordNumber();
		if (cuenta == 1) {
			Hashtable<String, Object> fila = res.getRecordValues(0);
			Object file = fila.get(ITGDFileConstants.FILE_FIELD);
			if (file instanceof DataFile) {
				DataFile ff = (DataFile) file;
				BytesBlock bb = ff.getBytesBlock();
				if (bb != null) {
					is = new ByteArrayInputStream(bb.getBytes());
				}
			} else if (file instanceof BytesBlock) {
				BytesBlock bb = (BytesBlock) file;
				is = new ByteArrayInputStream(bb.getBytes());
			}
		}
		if (is == null) {
			throw new Exception("File InputStream is null!");
		}

		File tmpFile = null;
		List<ByteArrayInputStream> lUnzip = ZipUtils.unzip(is);
		for (ByteArrayInputStream isUnzip : lUnzip) {
			if ("DA".equals(fInfo.getFileType())) {
				tmpFile = File.createTempFile(FileProcessTask.FILE_PREFIX, FileProcessTask.XMLFILE_SUFFIX);
			} else {
				tmpFile = File.createTempFile(FileProcessTask.FILE_PREFIX, FileProcessTask.FILE_SUFFIX);
			}
			tmpFile.deleteOnExit();
			try {
				FileTools.copyFile(isUnzip, tmpFile);
			} finally {
				if (isUnzip != null) {
					isUnzip.close();
				}
			}
		}
		return tmpFile;
	}

	private void saveIncidences(FileInfo fInfo, String obsr, Connection conn) {
		if ((obsr == null) || (obsr.trim().length() == 0)) {
			return;
		}
		obsr = obsr.trim();
		try {
			Hashtable<String, Object> cv = new Hashtable<>(1);
			cv.put(OpentachFieldNames.IDFILE_FIELD, fInfo.getIdFile());
			Hashtable<String, Object> av = new Hashtable<>(1);
			av.put(ITGDFileConstants.OBSR_FIELD, obsr);
			int privSessionID = TableEntity.getEntityPrivilegedId(this.getFileEntity());
			this.getFileEntity().update(av, cv, privSessionID, conn);

			TableEntity eFileContract = (TableEntity) this.getEntity(ITGDFileConstants.FILECONTRACT_ENTITY);
			av = new Hashtable<>(1);
			av.put(ITGDFileConstants.F_PROCESADO_FIELD, new Date());
			cv = new Hashtable<>(2);
			cv.put(OpentachFieldNames.IDFILE_FIELD, fInfo.getIdFile());
			int privSessID = TableEntity.getEntityPrivilegedId(eFileContract);
			Set<String> sCgContrato = fInfo.getContracts();
			for (String contrato : sCgContrato) {
				cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, contrato);
				eFileContract.update(av, cv, privSessID, conn);
			}
		} catch (Exception e) {
			FileProcessTask.logger.error("Error saving incidences", e);
		}
	}

	public void setStartTime(long currentTimeMillis) {
		this.startTime = currentTimeMillis;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public long getEndTime() {
		return this.endTime;
	}

	public boolean hasError() {
		return this.hasError;
	}

	//TODO JOk revisar esto porque va a ralentizar el procesado y no puede ser
	/**
	 * This method tries to mark as dirty from first activity loaded, to force recompute labor-data of this employee
	 *
	 * @param fileInfo2
	 * @param processedRecord
	 * @param conn
	 */
	private void notityDirtyDates(FileInfo fileInfo2, ProcessedRecord processedRecord, Connection conn) {
		try {
			if (processedRecord == null) {
				// fichero duplicado
				return;
			}
			IMessageQueueManager messageQueueManager = this.getBean(IMessageQueueManager.class);// If not available not necessary request more info
			List<String> driverIds = this.localizeDrivers(fileInfo2, processedRecord, conn);
			String cif = this.localizeCif(fileInfo2.getContracts(), driverIds, conn);
			Date dirtyDateCandidate = processedRecord.getOldestActivityTimeStamp();
			if ((driverIds == null) || (driverIds.isEmpty()) || (cif == null) || (dirtyDateCandidate == null)) {
				FileProcessTask.logger.warn("W_NOTIFYING_DIRTY_DATE__NOT_ENOUGHT_DATA:  CIF:{}  IDCONDUCTORES:{}  DIRTYDATECANDIDATE:{}", cif, driverIds, dirtyDateCandidate);
			} else {
				for (String driverId : driverIds) {
					messageQueueManager.pushEventInBackground(QueueNames.RECOMPUTE_DIRTY, new RecomputeDirtyQueueMessage(cif, driverId, dirtyDateCandidate));
				}
			}
		} catch (Exception err) {
			FileProcessTask.logger.error("E_NOTIFYING_DIRTY", err);
		}
	}

	private List<String> localizeDrivers(FileInfo fileInfo2, ProcessedRecord processedRecord, Connection conn) {
		List<String> driverIds = new ArrayList<String>();
		if ("TC".equals(fileInfo2.getFileType())) {
			driverIds.add(fileInfo2.getIdSource());
		} else if ("VU".equals(fileInfo2.getFileType())) {
			// Do nothing by now
			// Consider in the future using processedRecord.data.get(Conductor.class.getName())
			// Same contract/cif for all employees?!?
		}
		return driverIds;
	}

	private String localizeCif(Set<String> contracts, List<String> driverIds, Connection conn) {
		try {
			if ((contracts == null) || contracts.isEmpty() || (driverIds == null) || driverIds.isEmpty()) {
				return null;
			}
			TransactionalEntity eConductorCont = this.getEntity("EConductorCont");
			EntityResult resQuery = eConductorCont.query(//
					EntityResultTools.keysvalues(//
							OpentachFieldNames.IDCONDUCTOR_FIELD, new SearchValue(SearchValue.IN, new Vector(driverIds)), //
							OpentachFieldNames.CG_CONTRATO_FIELD, new SearchValue(SearchValue.IN, new Vector(contracts))//
							), //
					EntityResultTools.attributes(OpentachFieldNames.CIF_FIELD), //
					this.getEntityPrivilegedId(eConductorCont), conn);
			if ((resQuery != null) && (resQuery.calculateRecordNumber() > 0)) {
				return (String) ((List) resQuery.get(OpentachFieldNames.CIF_FIELD)).get(0);
			}
		} catch (Exception err) {
			FileProcessTask.logger.error("E_GETTING_CIF__FROM: IDCONDUCTOR:{} CONTRACTS:{}", driverIds, contracts, err);
		}
		return null;
	}
}
