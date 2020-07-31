package com.opentach.common.process;

import java.rmi.Remote;
import java.util.List;

public interface ITachoFileProcessService extends Remote {

	/** The Constant ID. */
	final static String	ID				= "TachoFileProcessService";

	static final String	FILE_PROCESS	= "file_process";

	void setProcessEnabled(boolean enable, int sessionId) throws Exception;

	boolean isProcessEnabled(int sessionId) throws Exception;

	void addPriorityFiles(List<Number> lFile, int sessionId) throws Exception;

	void processNow(Number idFile, int sessionId) throws Exception;

	FileProcessServiceStatus getStatus(int sessionId) throws Exception;

}