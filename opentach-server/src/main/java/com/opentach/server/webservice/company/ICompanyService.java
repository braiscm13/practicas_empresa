package com.opentach.server.webservice.company;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.opentach.server.webservice.company.beans.Driver5BListResponse;
import com.opentach.server.webservice.company.beans.DriverListResponse;
import com.opentach.server.webservice.company.beans.VehicleListResponse;
import com.opentach.server.webservice.company.beans.VehiclePlateListResponse;
import com.opentach.server.webservice.utils.OpentachWSException;

@WebService
public interface ICompanyService {

	@WebResult(name = "response")
	Driver5BListResponse getCompany5Bs(@WebParam(name = "cif") String companyCif) throws OpentachWSException;

	@WebResult(name = "response")
	DriverListResponse getCompanyDrivers(@WebParam(name = "cif") String companyCif, @WebParam(name = "driver5B") List<String> driver5Bs) throws OpentachWSException;

	@WebResult(name = "response")
	VehiclePlateListResponse getCompanyPlates(@WebParam(name = "cif") String companyCif) throws OpentachWSException;

	@WebResult(name = "response")
	VehicleListResponse getCompanyVehicles(@WebParam(name = "cif") String companyCif, @WebParam(name = "plate") List<String> vehiclePlates) throws OpentachWSException;
}
