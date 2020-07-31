package com.opentach.client.util.directorywatcher.ftp;

import java.util.Calendar;

public interface IFtpFile {

	String getName();

	boolean isFile();

	Calendar getTimestamp();
}