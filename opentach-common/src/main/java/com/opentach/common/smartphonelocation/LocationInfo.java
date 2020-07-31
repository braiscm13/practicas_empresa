package com.opentach.common.smartphonelocation;

import java.io.Serializable;
import java.util.Date;

import com.ontimize.jee.common.tools.ObjectTools;

public class LocationInfo implements Serializable {

	private final String	gcmTokenId;
	private final Date		gcmDate;

	private final Date		date;
	private final Number	latitude;
	private final Number	longitude;
	private final Number	accuracy;

	public LocationInfo(String gcmTokenId, Date gcmDate, Date date, Number latitude, Number longitude, Number accuracy) {
		this.gcmTokenId = gcmTokenId;
		this.gcmDate = gcmDate;
		this.date = date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}

	public String getGcmTokenId() {
		return this.gcmTokenId;
	}

	public Date getGcmDate() {
		return this.gcmDate;
	}

	public Date getDate() {
		return this.date;
	}

	public Number getLatitude() {
		return this.latitude;
	}

	public Number getLongitude() {
		return this.longitude;
	}

	public Number getAccuracy() {
		return this.accuracy;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || !(obj instanceof LocationInfo)){
			return false;
		}
		if (obj == this) {
			return true;
		}
		return ObjectTools.safeIsEquals(this.getDate(), ((LocationInfo) obj).getDate()) && ObjectTools.safeIsEquals(this.getLatitude(),
				((LocationInfo) obj).getLatitude()) && ObjectTools.safeIsEquals(this.getLongitude(),
						((LocationInfo) obj).getLongitude()) && ObjectTools.safeIsEquals(this.getAccuracy(), ((LocationInfo) obj).getAccuracy());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = (31 * hash) + (null == this.getDate() ? 0 : this.getDate().hashCode());
		hash = (31 * hash) + (null == this.getLatitude() ? 0 : this.getLatitude().hashCode());
		hash = (31 * hash) + (null == this.getLongitude() ? 0 : this.getLongitude().hashCode());
		hash = (31 * hash) + (null == this.getAccuracy() ? 0 : this.getAccuracy().hashCode());
		return hash;
	}
}
