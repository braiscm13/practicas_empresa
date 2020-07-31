package com.opentach.common.smartphonelocation;

import java.rmi.Remote;
import java.util.Date;

/**
 * Service to manipulate all concepts around smartphone (Android, IPhone?, Blackberry??) localization.
 */
public interface ISmartphoneLocationService extends Remote {

	public final static String	ID	= "SmartphoneLocationService";

	/**
	 * Return location data of smartphone by his identifier (PIN).
	 *
	 * @param pin
	 *            the identifier (PIN) of the smartphone
	 * @return location data of smartphone
	 * @throws Exception
	 *             if error ocurrs querying
	 */
	public LocationInfo getLocationInfo(String pin) throws Exception;

	/**
	 * Send push notification to smarphone, asking for location. It's needed to query for GCM token id to send push notification, else abort operation
	 *
	 * @param pin
	 *            the identifier (PIN) of the smartphone
	 * @return true if it is possible to send notification, false in other case
	 * @throws Exception
	 */
	public boolean sendPushLocation(String pin) throws Exception;

	/**
	 * Identify smartphone matching between smartphone PIN value and the TOKEN_ID used to send notifications (Google Cloud Messaging - GCM).
	 *
	 * @param smartphoneId
	 *            the smartphone PIN unique identifier (not SIM PIN)
	 * @param tokenId
	 *            the TOKEN_ID unique (to identify in GCM)
	 * @return void
	 * @throws Exception
	 *             When something fails
	 */
	public void configureDeviceForPush(String pin, String tokenId) throws Exception;

	/**
	 * Update location of smartphone. With this method we can store this as the last knowed position (and with reception time).
	 *
	 * @param smartphoneId
	 *            the smartphone PIN unique identifier (not SIM PIN)
	 * @param latitude
	 *            the latitude of location
	 * @param longitude
	 *            the longitude of location
	 * @return void
	 * @throws Exception
	 *             When something fails
	 */
	public void locationChanged(String pin, Number latitude, Number longitude, Number accuracy, Date date) throws Exception;
}