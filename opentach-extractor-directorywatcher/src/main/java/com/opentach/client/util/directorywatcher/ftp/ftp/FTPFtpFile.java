package com.opentach.client.util.directorywatcher.ftp.ftp;

import java.util.Calendar;

import org.apache.commons.net.ftp.FTPFile;

import com.opentach.client.util.directorywatcher.ftp.IFtpFile;

public class FTPFtpFile implements IFtpFile {
	private final FTPFile ftpFile;

	public FTPFtpFile(FTPFile ftpFile) {
		super();
		this.ftpFile = ftpFile;
	}

	public FTPFile getFtpFile() {
		return this.ftpFile;
	}

	@Override
	public String getName() {
		return this.ftpFile.getName();
	}

	@Override
	public boolean isFile() {
		return this.ftpFile.isFile();
	}

	@Override
	public Calendar getTimestamp() {
		return this.ftpFile.getTimestamp();
	}

}
