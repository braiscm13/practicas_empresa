package com.ontimize.util.alerts;

import java.rmi.Remote;
import java.util.Hashtable;

import com.ontimize.db.EntityResult;

public interface IAlertSystem extends Remote {

	public static final String	ALERT_SYSTEM			= "alertSystem";

	public static final String	TASK_GROUP_FIELD		= "TASK_GROUP";
	public static final String	TASK_NAME_FIELD			= "TASK_NAME";
	public static final String	CRON_GROUP_FIELD		= "CRON_GROUP";
	public static final String	CRON_NAME_FIELD			= "CRON_NAME";
	public static final String	TASK_CLASS_FIELD		= "TASK_CLASS";
	public static final String	TASK_CRON				= "TASK_CRON";
	public static final String	TASK_STATE				= "STATE";
	public static final String	TASK_CRONNAME_PREFIX	= "CRON";
	public static final String	TASK_CRONGROUP_PREFIX	= "CRONGRP";

	public static final String	STARTTIME_FIELD			= "STARTTIME";
	public static final String	ENDTIME_FIELD			= "ENDTIME";
	public static final String	NEXTTIMEFIRE_FIELD		= "NEXTTIMEFIRE";
	public static final String	STATE_FIELD				= "STATE";

	public static final String	NOTICE_FROM_PARAMETER	= "NOTICE_FROM_PARAMETER";
	public static final String	NOTICE_TO_PARAMETER		= "NOTICE_TO_PARAMETER";
	public static final String	NOTICE_SUBJECT			= "NOTICE_SUBJECT";
	public static final String	NOTICE_CONTENT			= "NOTICE_CONTENT";
	public static final String	NOTICE_RESPONSE_REQUEST	= "NOTICE_RESPONSE_REQUEST";
	public static final String	NOTICE_FORCE_READ		= "NOTICE_FORCE_READ";
	public static final String	NOTICE_SEND_MAIL		= "NOTICE_SEND_MAIL";
	public static final String	NOTICE_MAILTO_PARAMETER	= "NOTICE_MAILTO_PARAMETER";

	public static final String	STATE_NORMAL			= "STATE_NORMAL";
	public static final String	STATE_PAUSED			= "STATE_PAUSED";
	public static final String	STATE_BLOCKED			= "STATE_BLOCKED";
	public static final String	STATE_COMPLETE			= "STATE_COMPLETE";
	public static final String	STATE_ERROR				= "STATE_ERROR";
	public static final String	STATE_NONE				= "STATE_NONE";

	public String[] getAlertGroups() throws Exception;

	public EntityResult getAlertsGroupData(String group) throws Exception;

	public EntityResult getAllAlertsData() throws Exception;

	public EntityResult getAlertData(String taskName, String taskGroup, String cronName, String cronGroup) throws Exception;

	public boolean pauseAlert(String name, String group) throws Exception;

	public boolean resumeAlert(String name, String group) throws Exception;

	public void updateAlertConfiguration(Hashtable alertConfiguration) throws Exception;
}
