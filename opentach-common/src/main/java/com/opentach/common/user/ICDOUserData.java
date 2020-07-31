package com.opentach.common.user;

public interface ICDOUserData extends IUserData {

	boolean isUSBDownloadUser();

	boolean isMonitor();

	String getPrintConfig();

	String getExpressReport();

	String getDscr();

}
