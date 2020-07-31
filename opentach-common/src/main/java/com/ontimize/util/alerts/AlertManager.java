package com.ontimize.util.alerts;

import java.util.Hashtable;

import com.ontimize.db.EntityResult;

public interface AlertManager {

	public static String	ALERT_CONFIG_ENTITY	= "EAlertConfig";
	public static String	PROPERTIES_PATH		= "com/opentach/server/alerts";

	public String[] getAlertGroups();

	public EntityResult getAlertsGroupData(String group);

	public EntityResult getAllAlertsData();

	public EntityResult getAlertData(String alertName, String alertGroup, String cronName, String cronGroup);

	public boolean pauseAlert(String name, String group);

	public boolean resumeAlert(String name, String group);

	public void updateAlertConfiguration(Hashtable<String,Object> alertConfiguration);
}
