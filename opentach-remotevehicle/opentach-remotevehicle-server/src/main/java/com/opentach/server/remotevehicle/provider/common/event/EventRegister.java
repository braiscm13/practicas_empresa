package com.opentach.server.remotevehicle.provider.common.event;

import java.util.Date;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.remotevehicle.RemoteVehicleNaming;
import com.opentach.common.util.OpentachCheckingTools;
import com.opentach.common.util.StringUtils;
import com.opentach.server.remotevehicle.dao.RemoteProviderEventDao;

@Component("RemoteEventRegistger")
public class EventRegister implements IEventRegister {

	private static final Logger		logger	= LoggerFactory.getLogger(EventRegister.class);
	@Autowired
	private RemoteProviderEventDao eventDao;

	@Override
	public void saveEvent(Object providerId, String companyCif, String srcId, Date date, Integer code, String message) {
		this.saveEvent(providerId, companyCif, srcId, date, code, message, null);
	}

	@Override
	public void saveEvent(Object providerId, String companyCif, String srcId, Date date, Integer code, String message, Throwable error) {
		try {
			Hashtable<String, Object> av = new Hashtable<>();
			MapTools.safePut(av, RemoteVehicleNaming.RDP_ID, providerId);
			MapTools.safePut(av, RemoteVehicleNaming.COM_ID, companyCif);
			MapTools.safePut(av, RemoteVehicleNaming.SRC_ID, StringUtils.trimToSize(srcId, 20));
			MapTools.safePut(av, RemoteVehicleNaming.RPE_DATE, date);
			MapTools.safePut(av, RemoteVehicleNaming.RPE_CODE, code);
			if (error != null) {
				message = String.format(message + "\r\n%s", OpentachCheckingTools.getStackTrace(error));
			}
			MapTools.safePut(av, RemoteVehicleNaming.RPE_MESSAGE, message);
			this.eventDao.insert(av);
		} catch (Exception err) {
			EventRegister.logger.error("Error guardando evento de empresa {} para {}", companyCif, srcId, err);
		}
	}
}
