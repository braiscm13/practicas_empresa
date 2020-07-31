package com.opentach.client.util.directorywatcher.ftp.sftp;

import java.util.Calendar;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.opentach.client.util.directorywatcher.ftp.IFtpFile;

public class SFTPFtpFile implements IFtpFile {
	private final LsEntry ftpFile;

	public SFTPFtpFile(LsEntry ftpFile) {
		super();
		this.ftpFile = ftpFile;
	}

	public LsEntry getFtpFile() {
		return this.ftpFile;
	}

	@Override
	public String getName() {
		return this.ftpFile.getFilename();
	}

	@Override
	public boolean isFile() {
		return !this.ftpFile.getAttrs().isDir();
	}

	@Override
	public Calendar getTimestamp() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.ftpFile.getAttrs().getMTime() * 1000);
		return cal;
	}

}
