package com.opentach.server.webservice.company;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.util.SecurityTools;
import com.opentach.server.webservice.company.beans.Driver5BListResponse;
import com.opentach.server.webservice.company.beans.DriverListResponse;
import com.opentach.server.webservice.company.beans.DriverRecord;
import com.opentach.server.webservice.company.beans.VehicleListResponse;
import com.opentach.server.webservice.company.beans.VehiclePlateListResponse;
import com.opentach.server.webservice.company.beans.VehicleRecord;
import com.opentach.server.webservice.driverAnalysis.DriverAnalysisService;
import com.opentach.server.webservice.security.SecurityContextHolder;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.opentach.server.webservice.utils.WsTools;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.tools.exception.URuntimeException;

@WebService(endpointInterface = "com.opentach.server.webservice.company.ICompanyService", serviceName = "company")
public class CompanyService implements ICompanyService {

	private static final Logger	logger					= LoggerFactory.getLogger(DriverAnalysisService.class);

	private static final int	CODE_UNKNOW_ERROR		= -1;
	private static final String	MSG_UNKNOW_ERROR		= "Se ha producido un error inesperado.";
	private static final int	CODE_NO_CIF_PERMISSION	= -2;
	private static final String	MSG_NO_CIF_PERMISSION	= "No tiene permisos para consultar el CIF.";
	private static final int	CODE_NO_SOURCE			= -3;
	private static final String	MSG_NO_SOURCE			= "No se ha especificado ningún origen.";

	@Resource
	private WebServiceContext	context;

	@Override
	public Driver5BListResponse getCompany5Bs(String companyCif) throws OpentachWSException {
		this.checkCifPermission(companyCif);

		try {
			TableEntity eCondCont = this.getEntityReference("EConductorCont");
			EntityResult er = eCondCont.query(EntityResultTools.keysvalues("CIF", companyCif, "F_BAJA", new SearchValue(SearchValue.NULL, null)),
					EntityResultTools.attributes(OpentachFieldNames.IDCONDUCTOR_FIELD), this.getSessionId());
			CheckingTools.checkValidEntityResult(er);
			return new Driver5BListResponse((List) er.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
		} catch (Exception err) {
			CompanyService.logger.error(null, err);
			throw new OpentachWSException(CompanyService.CODE_UNKNOW_ERROR, CompanyService.MSG_UNKNOW_ERROR);
		}
	}

	@Override
	public DriverListResponse getCompanyDrivers(String companyCif, List<String> driver5Bs) throws OpentachWSException {
		this.checkCifPermission(companyCif);

		try {
			TableEntity eCondCont = this.getEntityReference("EConductorCont");

			EntityResult er = eCondCont.query(EntityResultTools.keysvalues(//
					"CIF", companyCif, //
					"F_BAJA", new SearchValue(SearchValue.NULL, null), //
					"IDCONDUCTOR", new SearchValue(SearchValue.IN, new Vector<>(driver5Bs))//
					), EntityResultTools.attributes("DNI", "F_ALTA", "NOMBRE", "APELLIDOS", "EMAIL", "MOVIL"), this.getSessionId());
			CheckingTools.checkValidEntityResult(er);

			DriverListResponse res = new DriverListResponse();
			int nrecord = er.calculateRecordNumber();
			for (int i = 0; i < nrecord; i++) {
				Map record = er.getRecordValues(i);
				res.add(new DriverRecord((String) record.get("DNI"), (String) record.get("NOMBRE"), (String) record.get("APELLIDOS"), WsTools.date2ws((Date) record.get("F_ALTA")),
						(String) record.get("EMAIL"), (String) record.get("MOVIL")));
			}
			return res;
		} catch (Exception err) {
			CompanyService.logger.error(null, err);
			throw new OpentachWSException(CompanyService.CODE_UNKNOW_ERROR, CompanyService.MSG_UNKNOW_ERROR);
		}
	}

	@Override
	public VehiclePlateListResponse getCompanyPlates(String companyCif) throws OpentachWSException {
		this.checkCifPermission(companyCif);

		try {
			TableEntity eCondCont = this.getEntityReference("EVehiculoCont");
			EntityResult er = eCondCont.query(EntityResultTools.keysvalues("CIF", companyCif, "F_BAJA", new SearchValue(SearchValue.NULL, null)),
					EntityResultTools.attributes(OpentachFieldNames.MATRICULA_FIELD), this.getSessionId());
			CheckingTools.checkValidEntityResult(er);
			return new VehiclePlateListResponse((List) er.get(OpentachFieldNames.MATRICULA_FIELD));
		} catch (Exception err) {
			CompanyService.logger.error(null, err);
			throw new OpentachWSException(CompanyService.CODE_UNKNOW_ERROR, CompanyService.MSG_UNKNOW_ERROR);
		}
	}

	@Override
	public VehicleListResponse getCompanyVehicles(String companyCif, List<String> vehiclePlates) throws OpentachWSException {
		this.checkCifPermission(companyCif);
		if ((vehiclePlates == null) || vehiclePlates.isEmpty()) {
			throw new OpentachWSException(CompanyService.CODE_NO_SOURCE, CompanyService.MSG_NO_SOURCE);
		}

		try {
			TableEntity eCondCont = this.getEntityReference("EVehiculoCont");

			EntityResult er = eCondCont.query(EntityResultTools.keysvalues(//
					"CIF", companyCif, //
					"F_BAJA", new SearchValue(SearchValue.NULL, null), //
					"MATRICULA", new SearchValue(SearchValue.IN, new Vector<>(vehiclePlates))//
					), EntityResultTools.attributes("F_ALTA", "MATRICULA"), this.getSessionId());
			CheckingTools.checkValidEntityResult(er);

			VehicleListResponse res = new VehicleListResponse();
			int nrecord = er.calculateRecordNumber();
			for (int i = 0; i < nrecord; i++) {
				Map record = er.getRecordValues(i);
				res.add(new VehicleRecord((String) record.get("MATRICULA"), WsTools.date2ws((Date) record.get("F_ALTA"))));
			}
			return res;
		} catch (Exception err) {
			CompanyService.logger.error(null, err);
			throw new OpentachWSException(CompanyService.CODE_UNKNOW_ERROR, CompanyService.MSG_UNKNOW_ERROR);
		}
	}

	private void checkCifPermission(String companyCif) throws OpentachWSException {
		try {
			SecurityTools.checkPermissions(this.getLocator(), companyCif, this.getSessionId());
		} catch (Exception err) {
			CompanyService.logger.error(null, err);
			throw new OpentachWSException(CompanyService.CODE_NO_CIF_PERMISSION, CompanyService.MSG_NO_CIF_PERMISSION);
		}
	}

	protected OpentachServerLocator getLocator() {
		ServletContext servletContext = (ServletContext) this.context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		return (OpentachServerLocator) servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
	}

	protected TableEntity getEntityReference(String entityName) {
		return (TableEntity) this.getLocator().getEntityReferenceFromServer(entityName);
	}

	public <T extends UAbstractService> T getService(Class<T> clazz) {
		try {
			return (T) this.getLocator().getRemoteReference((String) ReflectionTools.getFieldValue(clazz, "ID"));
		} catch (Exception e) {
			throw new URuntimeException(null, e);
		}
	}

	protected int getSessionId() {
		return SecurityContextHolder.getContext().getSessionId();
	}

}