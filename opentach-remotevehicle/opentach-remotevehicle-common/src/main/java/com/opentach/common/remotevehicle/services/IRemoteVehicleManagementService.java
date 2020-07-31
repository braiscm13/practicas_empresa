package com.opentach.common.remotevehicle.services;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;

public interface IRemoteVehicleManagementService {
	EntityResult downloadConfigAllQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult downloadDriverConfigQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult downloadVehicleConfigQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult providerQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult companySetupQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult companyResponsableQuery(Map<?, ?> keysValues, List<?> attributes) throws RemoteVehicleException;

	EntityResult companyResponsableInsert(Map<?, ?> attributes) throws RemoteVehicleException;

	EntityResult companyResponsableUpdate(Map<?, ?> attributes, Map<?, ?> keysValues) throws RemoteVehicleException;

	EntityResult companyResponsableDelete(Map<?, ?> keysValues) throws RemoteVehicleException;
}
