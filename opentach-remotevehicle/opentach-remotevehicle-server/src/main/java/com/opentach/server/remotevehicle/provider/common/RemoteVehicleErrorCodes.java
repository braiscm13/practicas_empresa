package com.opentach.server.remotevehicle.provider.common;

public final class RemoteVehicleErrorCodes {
	public static final int	OK						= 0;
	public static final int	ERR_UNKNOW_FILE_FORMAT	= -1;
	public static final int	ERR_BAD_EF_SIGNATURE	= -2;
	public static final int	ERR_UNKNOW_CIF			= -3;
	public static final int	ERR_OTHER_ERROR			= -98;
	public static final int	UNKNOW_ERROR			= -99;

	private RemoteVehicleErrorCodes() {
		super();
	}
}
