package com.opentach.server.remotevehicle.provider.common.event;

import java.util.Date;

public interface IEventRegister {
	void saveEvent(Object providerId, String companyCif, String srcId, Date date, Integer code, String message);

	void saveEvent(Object providerId, String companyCif, String srcId, Date date, Integer code, String message, Throwable error);
}
