package com.opentach.server.process;

import java.util.Date;
import java.util.Map;

public class ProcessedRecord {
	public Number	idfile;
	public String	owner;
	public Date		oldestActivityTimestamp;
	public Date		newestActivityTimestamp;
	public Date		processedTimestamp;
	public Date		extractTimestamp;
	public String	normalizedName;

	public String	file;
	public String	text;

	public Map		data;

	public ProcessedRecord(Number idfile, String file, String text) {
		this(idfile, file, text, null);
	}

	public ProcessedRecord(String file, String text, Map data) {
		this(null, file, text, data);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("RegistroProcesado ");
		if (this.idfile != null) {
			sb.append("File ").append(this.idfile.intValue());
		}
		sb.append(" obsr: " + this.text);
		return sb.toString();
	}

	public void setNewestActivityTimeStamp(Date newestActivityTimeStamp) {
		this.newestActivityTimestamp = newestActivityTimeStamp;
	}

	public ProcessedRecord(Number idfile, String owner, String file, String text, Map data) {
		this.idfile = idfile;
		this.file = file;
		this.owner = owner;
		this.processedTimestamp = new Date();
		this.text = text;
		this.data = data;
	}

	public ProcessedRecord(Number idfile, String file, String text, Map data) {
		this(idfile, null, file, text, data);
	}

	public Date getNewestActivityTimeStamp() {
		return this.newestActivityTimestamp;
	}

	public void setOldestActivityTimeStamp(Date oldestActivityTimestamp) {
		this.oldestActivityTimestamp = oldestActivityTimestamp;
	}

	public Date getOldestActivityTimeStamp() {
		return this.oldestActivityTimestamp;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Date getExtractTimestamp() {
		return this.extractTimestamp;
	}

	public void setExtractTimestamp(Date extractTimestamp) {
		this.extractTimestamp = extractTimestamp;
	}

	public String getNormalizedName() {
		return this.normalizedName;
	}

	public void setNormalizedName(String normalizedName) {
		this.normalizedName = normalizedName;
	}

}