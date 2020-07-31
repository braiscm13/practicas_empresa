package com.opentach.common.process;

import java.io.Serializable;
import java.util.Set;

public class FileProcessServiceSlotStatus implements Serializable {

	private Set<String>	contracts;
	private String		fileType;
	private Number		idFile;
	private String		idSource;
	private String		name;
	private long		startTime;
	private long		duration;

	public FileProcessServiceSlotStatus() {
		super();
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setName(String nombProcesado) {
		this.name = nombProcesado;
	}

	public void setIdSource(String idSource) {
		this.idSource = idSource;
	}

	public void setIdFile(Number idFile) {
		this.idFile = idFile;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setContracts(Set<String> contracts) {
		this.contracts = contracts;
	}

	public Set<String> getContracts() {
		return this.contracts;
	}

	public String getFileType() {
		return this.fileType;
	}

	public Number getIdFile() {
		return this.idFile;
	}

	public String getIdSource() {
		return this.idSource;
	}

	public String getName() {
		return this.name;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public long getDuration() {
		return this.duration;
	}

}