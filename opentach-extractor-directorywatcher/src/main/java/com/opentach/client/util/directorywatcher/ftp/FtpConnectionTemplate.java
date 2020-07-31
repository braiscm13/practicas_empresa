package com.opentach.client.util.directorywatcher.ftp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.opentach.client.util.directorywatcher.DirectoryWatcherException;
import com.opentach.client.util.directorywatcher.ftp.ftp.FTPFtpEngineStrategy;
import com.opentach.client.util.directorywatcher.ftp.sftp.SFTPFtpEngineStrategy;

public class FtpConnectionTemplate {

	private static final Logger logger = LoggerFactory.getLogger(FtpConnectionTemplate.class);

	public <T> T execute(FtpWatcherSettings info, IFtpOperation<T> operation) throws DirectoryWatcherException {
		return this.connect(info, operation);
	}

	// protected abstract T doOperation(FtpWatcherSettings info, IFtpEngineStrategy ftp) throws DirectoryWatcherException;

	protected <T> T connect(FtpWatcherSettings info, IFtpOperation<T> operation) throws DirectoryWatcherException {
		IFtpEngineStrategy ftp = null;
		// check
		try {
			// Connect and logon to FTP Server
			ftp = this.getStrategy(info);
			T res = operation.doOperation(ftp);
			return res;
		} catch (DirectoryWatcherException err) {
			throw err;
		} catch (Exception err) {
			throw new DirectoryWatcherException(err.getMessage(), err);
		} finally {
			if (ftp != null) {
				// Logout from the FTP Server and disconnect
				ftp.logout();
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException f) {
						// do nothing
					}
				}
			}
		}
	}

	private IFtpEngineStrategy getStrategy(FtpWatcherSettings info) throws JSchException, SocketException, UnknownHostException, DirectoryWatcherException, IOException {
		switch (info.getConnectionType()) {
			case SFTP:
				return SFTPFtpEngineStrategy.connect(info);
			case FTP:
				return FTPFtpEngineStrategy.connect(false, info);
			case FTPS:
				return FTPFtpEngineStrategy.connect(true, info);
		}
		throw new DirectoryWatcherException("Invalid ftp connection type:" + info.getConnectionType());
	}

	@FunctionalInterface
	public static interface IFtpOperation<T> {
		T doOperation(IFtpEngineStrategy ftp) throws DirectoryWatcherException, IOException;
	}
}