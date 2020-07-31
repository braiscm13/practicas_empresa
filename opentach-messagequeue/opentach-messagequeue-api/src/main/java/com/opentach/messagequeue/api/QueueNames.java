package com.opentach.messagequeue.api;

public final class QueueNames {
	public static final String POST_FINISH_RECEIVING = "filereception.postfinishreceiving";
	public static final String	LOCATION_IDENTIFY_SMARTPHONE	= "location.identifysmartphone";
	public static final String	LOCATION_CHANGE_SMARTPHONE		= "location.changesmartphonelocation";
	public static final String	CLOUDFILE_FILE_RECEIVED			= "cloudfile.filereceived";

	public static final String	RECOMPUTE_DIRTY					= "recompute.dirty";
	public static final String	RECOMPUTE_DONE					= "recompute.done";

	private QueueNames() {
		super();
	}
}
