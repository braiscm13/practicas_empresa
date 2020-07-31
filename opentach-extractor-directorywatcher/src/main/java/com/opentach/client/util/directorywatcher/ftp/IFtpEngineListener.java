package com.opentach.client.util.directorywatcher.ftp;

import java.nio.file.Path;

public interface IFtpEngineListener {

	void onFileToUpload(Path path, IFtpFile ftpFile, FtpWatcherSettings info);

	void onError(FtpWatcherSettings info, IFtpFile ftpFile, Exception ex);

}