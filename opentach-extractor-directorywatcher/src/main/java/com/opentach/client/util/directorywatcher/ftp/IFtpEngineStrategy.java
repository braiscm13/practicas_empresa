package com.opentach.client.util.directorywatcher.ftp;

import java.io.IOException;
import java.io.OutputStream;


public interface IFtpEngineStrategy {

	void changeWorkingDirectory(String ftpFolder) throws IOException;

	IFtpFile[] listDirectories() throws IOException;

	boolean makeDirectory(String name) throws IOException;

	void retrieveFile(String name, OutputStream os) throws IOException;

	boolean rename(String from, String to) throws IOException;

	IFtpFile[] listFiles() throws IOException;

	void logout();

	boolean isConnected();

	void disconnect() throws IOException;

}
