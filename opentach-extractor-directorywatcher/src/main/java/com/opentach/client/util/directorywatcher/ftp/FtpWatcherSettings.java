package com.opentach.client.util.directorywatcher.ftp;

import com.opentach.client.util.directorywatcher.AbstractWatcherSettings;

public class FtpWatcherSettings extends AbstractWatcherSettings {
	public enum FtpConnectionType {
		SFTP, FTP, FTPS
	}

	private String				ftpServer;
	private String				ftpUser;
	private String				ftpPass;
	private String				ftpFolder;
	private FtpConnectionType	connectionType;

	public FtpWatcherSettings(String server, String pass, String user, String folder, FtpConnectionType connectionType) {
		this(server, pass, user, folder, null, connectionType);
	}

	public FtpWatcherSettings(String server, String pass, String user, String folder, String companyId, FtpConnectionType connectionType) {
		super(companyId, WatchFolderMode.FTP);
		this.ftpServer = server;
		this.ftpPass = pass;
		this.ftpUser = user;
		this.ftpFolder = folder;
		this.connectionType = connectionType;
	}

	public String getFtpServer() {
		return this.ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public String getFtpUser() {
		return this.ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPass() {
		return this.ftpPass;
	}

	public void setFtpPass(String ftpPass) {
		this.ftpPass = ftpPass;
	}

	public String getFtpFolder() {
		return this.ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public FtpConnectionType getConnectionType() {
		return this.connectionType;
	}

	public void setConnectionType(FtpConnectionType connectionType) {
		this.connectionType = connectionType;
	}

	public static FtpConnectionType parseFtpConnectionType(String property) {
		if (property == null) {
			return FtpConnectionType.FTP;
		}
		return FtpConnectionType.valueOf(property);
	}
}