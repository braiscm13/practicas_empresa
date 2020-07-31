package com.opentach.common.tachofiletransfer.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class TachoFileUploadRequest {

	private boolean	analyze;
	private String	cif;
	private String	downloadDateTime;
	private String	latitude;
	private String	longitude;
	private String	reportMail;
	private String	sourceType;
	private String	user;
	private String	pin;

	public TachoFileUploadRequest() {
		super();
	}

	public boolean isAnalyze() {
		return this.analyze;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}

	/**
	 * @return the cif
	 */
	public String getCif() {
		return this.cif;
	}

	/**
	 * @param cif
	 *            the cif to set
	 */
	public void setCif(String cif) {
		this.cif = cif;
	}

	/**
	 * @return the downloadDateTime
	 */
	public String getDownloadDateTime() {
		return this.downloadDateTime;
	}

	/**
	 * @param downloadDateTime
	 *            the downloadDateTime to set
	 */
	public void setDownloadDateTime(String downloadDateTime) {
		this.downloadDateTime = downloadDateTime;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return this.latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return this.longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the reportMail
	 */
	public String getReportMail() {
		return this.reportMail;
	}

	/**
	 * @param reportMail
	 *            the reportMail to set
	 */
	public void setReportMail(String reportMail) {
		this.reportMail = reportMail;
	}

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return this.sourceType;
	}

	/**
	 * @param sourceType
	 *            the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "TachoFileUploadRequest [analyze=" + this.analyze + ", cif=" + this.cif + ", downloadDateTime=" + this.downloadDateTime + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", reportMail=" + this.reportMail + ", sourceType=" + this.sourceType + ", user=" + this.user + "]";
	}

	/**
	 * @return the pin
	 */
	public String getPin() {
		return this.pin;
	}

	/**
	 * @param pin
	 *            the pin to set
	 */
	public void setPin(String pin) {
		this.pin = pin;
	}

}
