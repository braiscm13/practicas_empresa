package com.opentach.server.remotevehicle.provider.jaltest.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.MessageCodeException;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.filereception.UploadSourceType;
import com.opentach.common.remotevehicle.exceptions.RemoteVehicleException;
import com.opentach.common.tachofiletransfer.beans.TachoFileUploadRequest;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.remotevehicle.provider.common.RemoteVehicleErrorCodes;
import com.opentach.server.remotevehicle.provider.common.event.IEventRegister;
import com.opentach.server.remotevehicle.provider.jaltest.wsinvoker.JaltestInvoker;
import com.opentach.server.remotevehicle.uploader.IUploader;
import com.opentach.server.tachofiles.TachoFileTools;

@WebService(endpointInterface = "com.opentach.server.remotevehicle.provider.jaltest.webservice.IJaltestNotificationWebservice", serviceName = "jaltestNotificationWebservice")
public class JaltestNotificationWebservice implements IJaltestNotificationWebservice {

	private static final Logger	logger					= LoggerFactory.getLogger(JaltestNotificationWebservice.class);

	@Resource
	private WebServiceContext	context;

	@Override
	public JaltestTachoFileEventResponse fileAvailable(JaltestTachoFileEventRequest tachoFileEvent) throws RemoteVehicleException {
		try {
			JaltestNotificationWebservice.logger.info("Nueva recepcion remota jaltest");
			String companyCif = tachoFileEvent.getCompanyCif();
			Date downloadDate = new SimpleDateFormat(JaltestInvoker.FORMAT_DATETIME).parse(tachoFileEvent.getDownloadDate());
			byte[] fileContent = tachoFileEvent.getFileContent();
			JaltestNotificationWebservice.logger.info("Cif: {} DownloadDate: {}", companyCif, downloadDate);
			this.saveFile(fileContent, downloadDate, companyCif);
			JaltestNotificationWebservice.logger.info("ok");
			this.getEventRegister().saveEvent(1, companyCif, null, downloadDate, RemoteVehicleErrorCodes.OK, "Fichero recibido correctamente");
			return new JaltestTachoFileEventResponse(0, "OK");
		} catch (MessageCodeException err) {
			JaltestNotificationWebservice.logger.error(null, err);
			Date downloadDate = this.parseJaltestDate(tachoFileEvent.getDownloadDate());
			this.getEventRegister().saveEvent(1, tachoFileEvent.getCompanyCif(), null, downloadDate, err.getCode(), err.getMessage());
			return new JaltestTachoFileEventResponse(err.getCode(), err.getMessage());
		} catch (Throwable err) {
			JaltestNotificationWebservice.logger.error(null, err);
			Date downloadDate = this.parseJaltestDate(tachoFileEvent.getDownloadDate());
			this.getEventRegister().saveEvent(1, tachoFileEvent.getCompanyCif(), null, downloadDate, RemoteVehicleErrorCodes.ERR_OTHER_ERROR, err.getMessage());
			return new JaltestTachoFileEventResponse(RemoteVehicleErrorCodes.UNKNOW_ERROR, "Unknow error");
		}
	}

	@Override
	public JaltestTachoFileEventResponse fileErrorEvent(JaltestTachoFileErrorEventRequest tachoFileErrorEvent) throws RemoteVehicleException {
		Date requestDate = this.parseJaltestDate(tachoFileErrorEvent.getRequestDate());
		this.getEventRegister().saveEvent(1, tachoFileErrorEvent.getCompanyCif(), tachoFileErrorEvent.getRequestObjectId(), requestDate,
				tachoFileErrorEvent.getCode(),
				tachoFileErrorEvent.getMessage());
		// TODO enviar la notificacion al cliente
		// siempre respondemos ok a este servicio
		return new JaltestTachoFileEventResponse(0, "OK");
	}

	private IEventRegister getEventRegister() {
		return this.getLocator().getBean(IEventRegister.class);
	}

	private Date parseJaltestDate(String requestDateStr) {
		try {
			return requestDateStr == null ? null : new SimpleDateFormat(JaltestInvoker.FORMAT_DATETIME).parse(requestDateStr);
		} catch (ParseException err) {
			JaltestNotificationWebservice.logger.warn(null, err);
			return null;
		}
	}

	public IOpentachServerLocator getLocator() {
		ServletContext servletContext = (ServletContext) this.context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		return (IOpentachServerLocator) servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
	}

	private void saveFile(byte[] file, Date downloadDate, String companyCif) throws Exception {
		this.checkCif(companyCif);

		TachoFile tachofile = TachoFileTools.getTachoFile(file);
		TachoFileUploadRequest parameters = new TachoFileUploadRequest();
		parameters.setCif(companyCif);
		parameters.setAnalyze(false);
		parameters.setSourceType(UploadSourceType.REMOTA_JALTEST.toString());
		this.getLocator().getBean(IUploader.class).uploadFile(file,
				tachofile.computeFileName(null, TachoFile.FILENAME_FORMAT_SPAIN, DateTools.createCalendar(downloadDate)),
				parameters);
	}

	private void checkCif(String companyCif) throws Exception {
		if (companyCif == null) {
			throw new MessageCodeException("Invalid cif", RemoteVehicleErrorCodes.ERR_UNKNOW_CIF);
		}
		Entity dfEmp = this.getLocator().getEntityReferenceFromServer(CompanyNaming.ENTITY);
		EntityResult er = dfEmp.query(EntityResultTools.keysvalues("CIF", companyCif), EntityResultTools.attributes("CIF"), TableEntity.getEntityPrivilegedId(dfEmp));
		if ((er == null) || (er.calculateRecordNumber() != 1)) {
			throw new MessageCodeException("Invalid cif", RemoteVehicleErrorCodes.ERR_UNKNOW_CIF);
		}
	}

}