package com.opentach.server.proxy.tachofiletransfer;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.imatia.tacho.model.TachoFile;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;

@Component("FileStore")
public class FileStoreComponent {

	private static final Logger logger = LoggerFactory.getLogger(FileStoreComponent.class);

	@Value("${app.backup-location}")
	private Path				folder;

	public void onNewFileReceived(byte[] file, String originalFilename, TachoFileUploadRequest parameters) {
		try {
			TachoFile tachoFile = TachoFile.readTachoFile(file);
			String computeFileName = tachoFile.computeFileName(null, TachoFile.FILENAME_FORMAT_SPAIN, null);
			FileStoreComponent.logger.info("Saving original file name {} for cif {} of type {} as {}", originalFilename, parameters.getCif(), parameters.getSourceType(),
					computeFileName);
			// <path>/year/week/type/
			Calendar cal = Calendar.getInstance();
			String year = "" + cal.get(Calendar.YEAR);
			String week = new DecimalFormat("00").format(cal.get(Calendar.WEEK_OF_YEAR));
			String type = parameters.getSourceType() == null ? "UNKNOW" : parameters.getSourceType();
			Path subFolder = this.folder.resolve(year).resolve(week).resolve(type);
			if (!Files.exists(subFolder)) {
				Files.createDirectories(subFolder);
			}
			int count = 1;
			Path destFile = subFolder.resolve(computeFileName);
			while (Files.exists(destFile)) {
				destFile = subFolder.resolve(computeFileName + "_" + count);
				count++;
			}
			Files.copy(new ByteArrayInputStream(file), destFile);

		} catch (Exception err) {
			FileStoreComponent.logger.warn("Error Saving original file name {} for cif {} of type {}", originalFilename, parameters.getCif(), parameters.getSourceType(), err);
		}
	}
}
