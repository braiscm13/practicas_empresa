package com.opentach.server.maintenance;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.maintenance.MaintenanceStatus.MaintenanceStatusType;
import com.opentach.common.util.ZipUtils;
import com.opentach.server.entities.EFicherosTGD;
import com.opentach.server.filereception.InOutFileLog;

public class MaintenanceTaskBackupDeleteTachoFiles extends AbstractMaintenanceTask {

	@Override
	protected void doInnerMaintenance(String backupFolder, Timestamp filterDate) throws Exception {
		CheckingTools.failIf(backupFolder == null, "Invalid backup folder");
		Path outputFolder = Paths.get(backupFolder);
		if (Files.exists(outputFolder)) {
			throw new Exception("OutputFolder already exists");
		}
		Files.createDirectories(outputFolder);
		Path logFile = outputFolder.resolve("File.log");
		Files.createFile(logFile);
		PrintWriter logWriter = new PrintWriter(logFile.toFile());
		try {
			float progress = 1;
			this.updateStatus("maintenance.queringfiles", (int) progress, false);
			EFicherosTGD ent = (EFicherosTGD) this.getEntityReference(ITGDFileConstants.FILE_ENTITY);
			Hashtable<Object, Object> cv = EntityResultTools.keysvalues(OpentachFieldNames.FECFIN_FIELD, new SearchValue(SearchValue.LESS, filterDate));
			Vector<String> av = EntityResultTools.attributes(OpentachFieldNames.IDFILE_FIELD, OpentachFieldNames.FILENAME_PROCESSED_FIELD);
			EntityResult res = ent.query(cv, av, this.getSessionId(ent));
			CheckingTools.checkValidEntityResult(res);
			int nrec = res.calculateRecordNumber();
			float divs = nrec / 100.0f;
			for (int i = 0; i < nrec; i++) {
				Hashtable recordValues = res.getRecordValues(i);
				Object idFile = recordValues.get(OpentachFieldNames.IDFILE_FIELD);
				String fileName = (String) recordValues.get(OpentachFieldNames.FILENAME_PROCESSED_FIELD);
				Hashtable<Object, Object> filter = EntityResultTools.keysvalues(OpentachFieldNames.IDFILE_FIELD, idFile);
				InOutFileLog fileLog = ent.createTransferFileLogToTransfer(filter, this.getSessionId(ent));
				if (fileLog == null) {
					logWriter.println("No se encontro fichero: " + idFile);
					continue;
				}
				Path outputFile = outputFolder.resolve(MaintenanceTaskBackupDeleteTachoFiles.cleanName(fileName));
				FileInputStream fis = new FileInputStream(fileLog.getFile().toFile());
				List<ByteArrayInputStream> unZippedFiles = ZipUtils.unzip(fis);
				int idx = 0;
				while (Files.exists(outputFile)) {
					idx++;
					outputFile = outputFolder.resolve(fileName + idx);
				}
				logWriter.println("Fichero con id " + idFile + " localizado en " + outputFile.getFileName().toString());
				Files.copy(unZippedFiles.get(0), outputFile);

				EntityResult deleteRes = ent.delete(filter, this.getSessionId(ent));
				CheckingTools.checkValidEntityResult(deleteRes);
				this.updateStatus("maintenance.deletingfiles", (i * 100f) / nrec, false, i, nrec);
			}
		} finally {
			logWriter.close();
		}
		this.updateStatus("Finished", 100, false);
	}

	private static String cleanName(String fileName) {
		return fileName.replaceAll("\\?", "-").replaceAll("<", "-");
	}

	@Override
	protected MaintenanceStatusType getTaskKey() {
		return MaintenanceStatusType.BACKUP_DELETE_TACHO_FILES;
	}

}
