package com.opentach.client.util.directorywatcher.ftp.test;

import java.nio.file.Path;

import com.opentach.client.util.directorywatcher.DirectoryWatcherException;
import com.opentach.client.util.directorywatcher.ftp.FtpEngine;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings;
import com.opentach.client.util.directorywatcher.ftp.FtpWatcherSettings.FtpConnectionType;
import com.opentach.client.util.directorywatcher.ftp.IFtpEngineListener;
import com.opentach.client.util.directorywatcher.ftp.IFtpFile;

public class TestFtp {
	static FtpEngine engine;

	public static void main(String[] args) throws DirectoryWatcherException {

		IFtpEngineListener ftpUploader = new IFtpEngineListener() {

			@Override
			public void onFileToUpload(Path path, IFtpFile ftpFile, FtpWatcherSettings info) {
				TestFtp.engine.backupFile(info, ftpFile);
			}

			@Override
			public void onError(FtpWatcherSettings info, IFtpFile ftpFile, Exception ex) {
				// TODO Auto-generated method stub
			}
		};
		TestFtp.engine = new FtpEngine(ftpUploader);
		TestFtp.engine.checkFtp(new FtpWatcherSettings("127.0.0.1", "opentach1234", "opentach", "files", FtpConnectionType.SFTP));
	}
}
