package com.opentach.server.blackberry;

import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.RMIUtilities;
import com.opentach.common.blackberry.IBlackberryService;
import com.opentach.common.blackberry.IRemoteBlackberryPushService;
import com.utilmize.server.services.UAbstractService;

public class BlackberryService extends UAbstractService implements IBlackberryService {

	private static final Logger	logger	= LoggerFactory.getLogger(BlackberryService.class);

	public BlackberryService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	@Override
	public void sendPushBlackberry(Vector v, String sms) {
		try {
			for (int i = 0; i < v.size(); i++) {

				((IRemoteBlackberryPushService) RMIUtilities.getRegistry().lookup(IRemoteBlackberryPushService.BLACKBERRY_PUSH_SERVICE_RMI_NAME)).push((String) v.get(i), sms);
			}
		} catch (Throwable e) {
			BlackberryService.logger.error(null, e);
		}
	}
}
