package com.opentach.server.smartphonelocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.exception.OpentachRuntimeException;
import com.opentach.common.smartphonelocation.ISmartphoneLocationService;
import com.opentach.common.smartphonelocation.LocationInfo;
import com.opentach.messagequeue.api.IMessageQueueListener;
import com.opentach.messagequeue.api.IMessageQueueManager;
import com.opentach.messagequeue.api.IMessageQueueMessage;
import com.opentach.messagequeue.api.QueueNames;
import com.opentach.messagequeue.api.messages.LocationChangeQueueMessage;
import com.opentach.messagequeue.api.messages.LocationIdentifyQueueMessage;
import com.opentach.server.AbstractOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;

public class SmartphoneLocationService extends UAbstractService implements ISmartphoneLocationService, IMessageQueueListener {

	private static final String	PUSH_MESSAGE_LOCALIZE	= "localize";

	private static final Logger	logger					= LoggerFactory.getLogger(SmartphoneLocationService.class);

	// API_KEY for Google services -> GCM
	// TODO to set with generated to this project
	public static final String	API_KEY					= "AIzaSyB2PzPKy7awroOzKH_hN5EunxkP3j4Klb0";

	public SmartphoneLocationService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
	}

	@Override
	protected void init(Hashtable hconfig) {
		super.init(hconfig);
		((AbstractOpentachServerLocator) this.getLocator()).getBean(IMessageQueueManager.class).registerListener(QueueNames.LOCATION_CHANGE_SMARTPHONE, this);
		((AbstractOpentachServerLocator) this.getLocator()).getBean(IMessageQueueManager.class).registerListener(QueueNames.LOCATION_IDENTIFY_SMARTPHONE, this);
	}

	@Override
	public void onQueueEvent(String queueName, IMessageQueueMessage event) {
		try {
			if (QueueNames.LOCATION_CHANGE_SMARTPHONE.equals(queueName)) {
				LocationChangeQueueMessage msg = (LocationChangeQueueMessage) event;
				this.locationChanged(msg.getSmartphoneId(), msg.getLatitude(), msg.getLongitude(), msg.getAccuracy(), msg.getDate());
			} else if (QueueNames.LOCATION_IDENTIFY_SMARTPHONE.equals(queueName)) {
				LocationIdentifyQueueMessage msg = (LocationIdentifyQueueMessage) event;
				this.configureDeviceForPush(msg.getSmartphoneId(), msg.getTokenId());
			}
		} catch (Exception err) {
			throw new OpentachRuntimeException(err);
		}
	}

	@Override
	public LocationInfo getLocationInfo(String pin) throws Exception {
		TableEntity entity = (TableEntity) this.getEntity("EBlackberry");
		Date d = new Date();
		EntityResult resQuery = entity.query(EntityResultTools.keysvalues("PIN", pin),
				EntityResultTools.attributes("GCMTOKENID", "GCMDATE", "LOCLONGITUDE", "LOCLATITUDE", "LOCDATE", "LOCACCURACY"), TableEntity.getEntityPrivilegedId(entity));
		CheckingTools.checkValidEntityResult(resQuery, "E_QUERYING_LOCATION_INFO", true, true, new Object[0]);
		Hashtable<String, Object> recordValues = resQuery.getRecordValues(0);
		return new LocationInfo((String) recordValues.get("GCMTOKENID"), (Date) recordValues.get("GCMDATE"), (Date) recordValues.get("LOCDATE"),
				(Number) recordValues.get("LOCLATITUDE"), (Number) recordValues.get("LOCLONGITUDE"), (Number) recordValues.get("LOCACCURACY"));
	}

	@Override
	public boolean sendPushLocation(String pin) throws Exception {
		try {
			LocationInfo locationInfo = this.getLocationInfo(pin);
			if (locationInfo.getGcmTokenId() == null) {
				throw new Exception("UNKNOWED_PUSH_TOKEN_ID");
			}
			SmartphoneLocationService.sendPush(SmartphoneLocationService.PUSH_MESSAGE_LOCALIZE, locationInfo.getGcmTokenId());
			return true;
		} catch (Exception ex) {
			SmartphoneLocationService.logger.error("E_GETTING_DEVICE_INFO", ex);
			return false;
		}
	}

	@Override
	public void configureDeviceForPush(String pin, String tokenId) throws Exception {
		// First step, validate device ID
		Object deviceId = this.checkValidDeviceId(pin);

		// Second step update PUSH info
		this.updateDeviceInfo(deviceId, EntityResultTools.keysvalues("GCMTOKENID", this.assertNull(tokenId), "GCMDATE", new Date()));
	}

	@Override
	public void locationChanged(String pin, Number latitude, Number longitude, Number accuracy, Date date) throws Exception {
		// First step, validate device ID
		Object deviceId = this.checkValidDeviceId(pin);

		// Second step, update location
		this.updateDeviceInfo(deviceId, EntityResultTools.keysvalues("LOCLATITUDE", this.assertNull(latitude), "LOCLONGITUDE", this.assertNull(longitude), "LOCDATE",
				this.assertNull(date), "LOCACCURACY", this.assertNull(accuracy)));
	}

	private Object checkValidDeviceId(String pin) throws Exception {
		TableEntity entity = (TableEntity) this.getEntity("EBlackberry");
		int sessionId = TableEntity.getEntityPrivilegedId(entity);
		EntityResult resQuery = entity.query(EntityResultTools.keysvalues("PIN", pin), EntityResultTools.attributes("IDBLACKBERRY"), sessionId);
		CheckingTools.checkValidEntityResult(resQuery, "E_QUERYING_DEVICE_INFO", true, true, new Object[0]);
		return resQuery.getRecordValues(0).get("IDBLACKBERRY");
	}

	private void updateDeviceInfo(Object id, Hashtable toUpdate) throws Exception {
		TableEntity entity = (TableEntity) this.getEntity("EBlackberry");
		int sessionId = TableEntity.getEntityPrivilegedId(entity);
		EntityResult resUpdate = entity.update(toUpdate, EntityResultTools.keysvalues("IDBLACKBERRY", id), sessionId);
		CheckingTools.checkValidEntityResult(resUpdate);
	}

	private Object assertNull(Object input) {
		return input == null ? new NullValue() : input;
	}

	private static void sendPush(String message, String deviceToken) {
		try {
			// Prepare JSON containing the GCM message content. What to send and where to send.
			JSONObject jGcmData = new JSONObject();
			JSONObject jData = new JSONObject();
			jData.put("message", message.trim());

			// Where to send GCM message.
			if ((deviceToken != null) && !"".equals(deviceToken)) {
				jGcmData.put("to", deviceToken.trim());
			} else {
				jGcmData.put("to", "/topics/global");
			}
			// What to send in GCM message.
			jGcmData.put("data", jData);

			// Create connection to send GCM Message request.
			URL url = new URL("https://android.googleapis.com/gcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "key=" + SmartphoneLocationService.API_KEY);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);

			// Send GCM message content.
			OutputStream outputStream = conn.getOutputStream();
			outputStream.write(jGcmData.toString().getBytes());

			// Read GCM response.
			InputStream inputStream = conn.getInputStream();
			String resp = IOUtils.toString(inputStream);
			SmartphoneLocationService.logger.info("Response {}", resp);
		} catch (IOException e) {
			SmartphoneLocationService.logger.error("Unable to send GCM message.", e);
		}
	}

}