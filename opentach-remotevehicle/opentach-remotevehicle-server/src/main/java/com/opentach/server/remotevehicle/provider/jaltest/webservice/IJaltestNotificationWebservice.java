package com.opentach.server.remotevehicle.provider.jaltest.webservice;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;

@WebService
public interface IJaltestNotificationWebservice {

	@WebResult(name = "response")
	JaltestTachoFileEventResponse fileAvailable(@WebParam(name = "fileAvailableRequest") JaltestTachoFileEventRequest tachoFileEvent) throws RemoteVehicleException;

	@WebResult(name = "response")
	JaltestTachoFileEventResponse fileErrorEvent(@WebParam(name = "fileErrorRequest") JaltestTachoFileErrorEventRequest tachoFileErrorEvent) throws RemoteVehicleException;

}
