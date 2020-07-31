package com.opentach.server.remotevehicle.provider.jaltest.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "companyCif", "downloadDate", "fileContent" })
public class JaltestTachoFileEventRequest {
	private String	companyCif;
	private String	downloadDate;
	private byte[]	fileContent;

	public JaltestTachoFileEventRequest() {
		super();
	}

	public String getCompanyCif() {
		return this.companyCif;
	}

	public void setCompanyCif(String companyCif) {
		this.companyCif = companyCif;
	}

	public String getDownloadDate() {
		return this.downloadDate;
	}

	public void setDownloadDate(String downloadDate) {
		this.downloadDate = downloadDate;
	}

	public byte[] getFileContent() {
		return this.fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

}
