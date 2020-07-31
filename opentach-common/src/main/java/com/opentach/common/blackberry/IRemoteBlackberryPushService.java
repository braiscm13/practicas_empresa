package com.opentach.common.blackberry;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota para el envío de mensajes push
 */
public interface IRemoteBlackberryPushService extends Remote, Serializable {
	public static final String	BLACKBERRY_PUSH_SERVICE_RMI_NAME	= "BlackberryPushService";

	void push(String id, String message) throws RemoteException, Exception;

}
