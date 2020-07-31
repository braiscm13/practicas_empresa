package com.opentach.server.webservice.driverAnalysis;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.opentach.server.webservice.driverAnalysis.beans.DriverActivityRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverActivityResponse;
import com.opentach.server.webservice.driverAnalysis.beans.DriverAnalysisRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverAnalysisResponse;
import com.opentach.server.webservice.utils.OpentachWSException;

@WebService
public interface IDriverAnalisysService {

	@WebResult(name = "response")
	DriverAnalysisResponse analize(@WebParam(name = "filters") DriverAnalysisRequest filters) throws OpentachWSException;

	@WebResult(name = "response")
	DriverActivityResponse queryActivities(@WebParam(name = "filters") DriverActivityRequest filters) throws OpentachWSException;

}
