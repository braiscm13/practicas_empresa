package com.opentach.client.util.directorywatcher.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opentach.client.util.directorywatcher.DirectoryWatcherException;

public class FtpEngine {

	private static final Logger			logger				= LoggerFactory.getLogger(FtpEngine.class);
	/* tiempo desde la última modificación para el que se considera que un fichero no se va a modificar más */
	/** The Constant FINISHED_CHECK_TIME. */
	private static final long			FINISHED_CHECK_TIME	= 30000;
	private final IFtpEngineListener	ftpUploader;

	public FtpEngine(IFtpEngineListener ftpUploader) {
		super();
		this.ftpUploader = ftpUploader;
	}

	public synchronized void backupFile(FtpWatcherSettings info, IFtpFile ftpFile) {
		try {
			synchronized (this) {
				new FtpConnectionTemplate().execute(info, (ftp) -> {
					FtpEngine.this.moveToBaseFolder(info, ftp);
					FtpEngine.this.ensureBackupFoler(ftp);
					int index = 0;
					while (!ftp.rename(ftpFile.getName(), "backup/" + ftpFile.getName() + (index == 0 ? "" : ("_" + index)))) {
						index++;
						if (index == 10) {
							throw new DirectoryWatcherException(
									String.format("El fichero del ftp %s de la empresa %s no se puede mover tras %s intentos", ftpFile, info.getCompanyId(), index));
						}
					}
					return null;
				});
			}
		} catch (Exception err) {
			FtpEngine.logger.error(null, err);
			FtpEngine.this.ftpUploader.onError(info, null, err);
		}
	}

	public void checkFtp(FtpWatcherSettings info) throws DirectoryWatcherException {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis() - FtpEngine.FINISHED_CHECK_TIME);
		List<IFtpFile> filesDetected = Collections.emptyList();
		try {
			synchronized (this) {
				filesDetected = new FtpConnectionTemplate().execute(info, (ftpApi) -> {
					// List the files in the directory
					FtpEngine.this.moveToBaseFolder(info, ftpApi);
					return Arrays.asList(ftpApi.listFiles());
				});
			}
		} catch (Exception ex) {
			throw new DirectoryWatcherException("Error retrieving file list from ftp of company " + info.getCompanyId(), ex);
		}

		for (IFtpFile ftpFile : filesDetected) {
			try {
				File file = null;
				synchronized (this) {
					file = new FtpConnectionTemplate().execute(info, (ftpApi) -> {
						if (ftpFile.isFile() && ftpFile.getTimestamp().before(now)) {
							File innerFile = new File(File.createTempFile("none", "none").getParentFile(), ftpFile.getName());
							if (innerFile.exists()) {
								innerFile.delete();
							}
							FtpEngine.this.moveToBaseFolder(info, ftpApi);
							try (OutputStream os = new FileOutputStream(innerFile)) {
								ftpApi.retrieveFile(ftpFile.getName(), os);
							}
							return innerFile;
						} else if (!ftpFile.getTimestamp().before(now)) {
							FtpEngine.logger.warn("Avoiding file {} recently added {} in company {}", ftpFile.getName(), ftpFile.getTimestamp(), info.getCompanyId());
						}
						return null;
					});
				}
				if (file != null) {
					FtpEngine.this.ftpUploader.onFileToUpload(file.toPath(), ftpFile, info);
				}
			} catch (Exception ex) {
				FtpEngine.logger.error("Error retrieving file {} from ftp of company {}", ftpFile.getName(), info.getCompanyId(), ex);
				FtpEngine.this.ftpUploader.onError(info, ftpFile, ex);
			}
		}

	}

	private void ensureBackupFoler(IFtpEngineStrategy ftp) throws IOException {
		IFtpFile[] directories = ftp.listDirectories();
		for (IFtpFile directory : directories) {
			if (directory.getName().equals("backup")) {
				return;
			}
		}
		if (!ftp.makeDirectory("backup")) {
			FtpEngine.logger.error("cannot backup ftp file");
		}
	}

	private void moveToBaseFolder(FtpWatcherSettings info, IFtpEngineStrategy ftp) throws IOException {
		if ((info.getFtpFolder() != null) && (!"".equals(info.getFtpFolder()))) {
			ftp.changeWorkingDirectory(info.getFtpFolder());
		}
	}
}
