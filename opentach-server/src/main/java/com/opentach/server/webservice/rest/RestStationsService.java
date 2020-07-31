package com.opentach.server.webservice.rest;

import java.util.Hashtable;
import java.util.Vector;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.StringTools;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestResponseStations;
import com.opentach.ws.common.rest.beans.Station;
import com.opentach.ws.common.rest.beans.StationList;

public class RestStationsService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestStationsService.class);

	@Resource
	private WebServiceContext	context;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// PUBLIC METHODS ////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/getStations")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestResponseStations getStations() throws OpentachWSException {
		try {
			Entity entity = RestServiceUtils.getEntity("EUsuariosCDO");
			Vector<String> cols = EntityResultTools.attributes("USUARIO", "NAME", "PAIS", "PROVINCIA", "POSTAL_CODE", "POBL", "UBIC", "OP_GAS",
					"OP_STORE", "OP_CASH", "OP_BED", "OP_MEAL", "OP_24H", "LATITUDE", "LONGITUDE");
			// We only can see public CDOs
			EntityResult resQuery = entity.query(EntityResultTools.keysvalues("PRIVATE", "N"), cols, TableEntity.getEntityPrivilegedId(entity));
			RestResponseStations result = new RestResponseStations(this.convertEntityResultToStationList(resQuery));
			return result;
		} catch (Exception e) {
			RestStationsService.logger.error("E_GETTING_STATIONS", e);
			return new RestResponseStations(-1, "E_GETTING_STATIONS");
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////// INNER IMPLEMENTATIONS
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// /////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Utility to convert EntityResult to Map, to allow send values to client side.
	 *
	 * @param resQuery
	 * @return
	 * @throws Exception
	 */
	private StationList convertEntityResultToStationList(EntityResult resQuery) throws Exception {
		if (resQuery == null) {
			return null;
		}
		if (resQuery.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
			throw new Exception(resQuery.getMessage());
		}
		if (resQuery.calculateRecordNumber() == 0) {
			return null;
		}

		StationList stationList = new StationList();
		int num = resQuery.calculateRecordNumber();
		for (int i = 0; i < num; i++) {
			Station station = this.buildStation(resQuery.getRecordValues(i));
			if (station != null) {
				stationList.addStation(station);
			}
		}
		return stationList;
	}

	private Station buildStation(Hashtable recordValues) {
		String id = (String) recordValues.get("USUARIO");
		String name = (String) recordValues.get("NAME");
		String province = (String) recordValues.get("PROVINCIA");

		String ubic = StringTools.isEmpty((String) recordValues.get("UBIC")) ? "" : (String) recordValues.get("UBIC") + " ";
		String pc = StringTools.isEmpty((String) recordValues.get("POSTAL_CODE")) ? "" : (String) recordValues.get("POSTAL_CODE") + " ";
		String city = StringTools.isEmpty((String) recordValues.get("POBL")) ? "" : (String) recordValues.get("POBL") + " ";
		String address = StringTools.concat(ubic, pc, city);

		Number latitude = (Number) recordValues.get("LATITUDE");
		Number longitude = (Number) recordValues.get("LONGITUDE");
		Station station = new Station(id, name, address, province, latitude == null ? null : latitude.doubleValue(),
				longitude == null ? null : longitude.doubleValue());

		station.setServicePetrol(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_GAS"), false));
		station.setServiceMarket(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_STORE"), false));
		station.setServiceBank(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_CASH"), false));
		station.setServiceAccomodation(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_BED"), false));
		station.setServiceRestaurant(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_MEAL"), false));
		station.setServiceFullDay(ParseUtilsExtended.getBoolean((String) recordValues.get("OP_OP_24HGAS"), false));

		return station;
	}

}