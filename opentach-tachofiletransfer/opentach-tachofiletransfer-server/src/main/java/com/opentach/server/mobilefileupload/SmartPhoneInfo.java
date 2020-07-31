package com.opentach.server.mobilefileupload;
public class SmartPhoneInfo {
	private final String	pin;
	private final String	cif;
	private final String	reportMail;
	private final String	blackberryMail;
	private final Object	id;
	private final Double	latitude;
	private final Double	longitude;
	private final boolean	analize;

	public SmartPhoneInfo(String pin, Object id, String cif, String reportMail, String blackberryMail, Double latitude, Double longitude,
			boolean analize) {
		super();
		this.pin = pin;
		this.cif = cif;
		this.reportMail = reportMail;
		this.blackberryMail = blackberryMail;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.analize = analize;
	}

	public String getReportMail() {
		return this.reportMail;
	}

	public String getBlackberryEmail() {
		return this.blackberryMail;
	}

	public Object getId() {
		return this.id;
	}

	public String getPin() {
		return this.pin;
	}

	public String getCif() {
		return this.cif;
	}

	public Double getLongitude() {
		return this.longitude;
	}

	public Double getLatitude() {
		return this.latitude;
	}

	public boolean isAnalize() {
		return this.analize;
	}
}