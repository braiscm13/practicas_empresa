package com.opentach.server.webservice.driverAnalysis;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.opentach.server.webservice.driverAnalysis.beans.DriverNextRestRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverNextRestResponse;
import com.opentach.server.webservice.utils.OpentachWSException;

@WebService
public interface IDriverNextRestService {

	@WebResult(name = "response")
	DriverNextRestResponse queryNextRest(@WebParam(name = "filters") DriverNextRestRequest filters) throws OpentachWSException;

}
