package com.opentach.common.companies;

import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResult;
import com.opentach.common.exception.OpentachException;

public interface IAssetGroupService {

	EntityResult assetGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	EntityResult assetGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;

	EntityResult assetGroupDelete(Map<?, ?> keysValues) throws OpentachException;

	EntityResult assetGroupInsert(Map<?, ?> keysValues) throws OpentachException;

	EntityResult vehicleGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	EntityResult vehicleGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;

	EntityResult vehicleGroupDelete(Map<?, ?> keysValues) throws OpentachException;

	EntityResult vehicleGroupInsert(Map<?, ?> keysValues) throws OpentachException;

	EntityResult driverGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	EntityResult driverGroupUpdate(Map<?, ?> attributesValues, Map<?, ?> keysValues) throws OpentachException;

	EntityResult driverGroupDelete(Map<?, ?> keysValues) throws OpentachException;

	EntityResult driverGroupInsert(Map<?, ?> keysValues) throws OpentachException;

	EntityResult availableVehicleGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

	EntityResult availableDriverGroupQuery(Map<?, ?> keysValues, List<?> attributes) throws OpentachException;

}
