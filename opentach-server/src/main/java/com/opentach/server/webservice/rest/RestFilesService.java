package com.opentach.server.webservice.rest;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.MapTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.Company;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.ws.common.rest.beans.RestLogginRequest;
import com.opentach.ws.common.rest.beans.RestLogginResponse;
import com.utilmize.tools.exception.UException;

public class RestFilesService {

	private static final Logger	logger	= LoggerFactory.getLogger(RestFilesService.class);

	@Resource
	private WebServiceContext	context;

	////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// DRIVER /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////

	@POST
	@Path("/searchFiles")
	@Consumes({ "application/json" })
	@Produces({ "application/json" })
	public RestLogginResponse searchFiles(@WebParam(name = "loggin") final RestLogginRequest request) throws OpentachWSException {
		try {
			return (RestLogginResponse) new OntimizeSessionConnectionTemplate(request.getUsrLogin(), request.getUsrPsswd()) {

				@Override
				protected Object doTask(Connection con) throws UException {
					try {
						return RestFilesService.this.searchFiles(request, this.getSessionId());
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}

			}.execute(OpentachServerLocator.getLocator(), true);
		} catch (Exception e) {
			RestFilesService.logger.error(e.getMessage());
			return new RestLogginResponse(-1);
		}
	}

	private Object searchFiles(RestLogginRequest logginRequest, int sessionId) throws Exception {
		Company company = RestServiceUtils.getCompany(logginRequest, sessionId);
		return new RestLogginResponse(sessionId, this.getFiles(company, sessionId));
	}

	private HashMap<Object, Object> getFiles(Company company, int sessionId) throws Exception {
		HashMap<Object, Object> data = new HashMap<>();

		String cif = company.getCif();
		Object activeContract = company.getActiveContract();

		EntityResult erFicherosSubidos = RestServiceUtils.getEntity("EFicherosSubidos").query(
				EntityResultTools.keysvalues("CG_CONTRATO", activeContract, "CIF", cif, "F_ALTA", new SearchValue(SearchValue.MORE_EQUAL, RestServiceUtils.getFilterDate())),
				EntityResultTools.attributes("FECINI", "FECFIN", "TIPO", "IDORIGEN", "DSCR_COND", "OBSR_CLIENT", OpentachFieldNames.FILENAME_PROCESSED_FIELD, "NOMB",
						"F_DESCARGA_DATOS", "F_PROCESADO", "F_ALTA", "USUARIO_ALTA"),
				sessionId);

		if (erFicherosSubidos.calculateRecordNumber() > 0) {
			for (String attr : Arrays.asList("FECINI", "FECFIN", "F_DESCARGA_DATOS", "F_PROCESADO", "F_ALTA")) {
				erFicherosSubidos.put(attr, RestServiceUtils.getFormatDate((Vector) erFicherosSubidos.get(attr)));
			}
		}

		/* FILES data */
		MapTools.safePut(data, "FILE_INFO", erFicherosSubidos);
		return data;
	}

}