package com.opentach.server.process;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.mail.MailService;
import com.opentach.server.report.ReportService;
import com.opentach.server.util.AbstractDelegate;

/**
 * The Class FileProcessTaskPostOperationDelegate.
 */
public class FileProcessTaskPostOperationDelegate extends AbstractDelegate implements IFileProcessTaskPostOperationHelper {

	/** The Constant logger. */
	private static final Logger			logger	= LoggerFactory.getLogger(FileProcessTaskPostOperationDelegate.class);


	/**
	 * Instantiates a new file process task post operation delegate.
	 *
	 * @param locator
	 *            the locator
	 */
	public FileProcessTaskPostOperationDelegate(IOpentachServerLocator locator) {
		super(locator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.process.IFileProcessTaskPostOperationHelper#onFileProcessed(com.opentach.server.process.FileInfo)
	 */
	@Override
	public void onFileProcessed(FileInfo fInfo) {
		try {
			if (fInfo.isMovil()) {
				this.sendMessages(fInfo);
			}
		} catch (Throwable error) {
			FileProcessTaskPostOperationDelegate.logger.error(null, error);
		}
	}

	/**
	 * Send messages.
	 *
	 * @param fExtInfo
	 *            the f ext info
	 * @throws Exception
	 *             the exception
	 */
	private void sendMessages(FileInfo fExtInfo) throws Exception {
		Map<String, Object> next = new HashMap<String, Object>();
		Number idFile = fExtInfo.getIdFile();
		String sEmail = fExtInfo.getEmail();
		String sAnalizar = fExtInfo.isAnalizar() ? "S" : "N";
		String sContrato = (String) fExtInfo.getContracts().toArray()[0];
		String sIdSource = fExtInfo.getIdSource();
		String sType = fExtInfo.getFileType();
		Date dExtract = fExtInfo.getDExtract();
		String sNombProcesado = fExtInfo.getNombProcesado();
		next.put(OpentachFieldNames.IDFILE_FIELD, idFile);
		next.put(ITGDFileConstants.EMAIL_FIELD, sEmail);
		next.put(ITGDFileConstants.ANALIZAR_FIELD, sAnalizar);
		next.put(OpentachFieldNames.IDORIGEN_FIELD, sIdSource);
		next.put(OpentachFieldNames.CG_CONTRATO_FIELD, sContrato);
		next.put(ITGDFileConstants.FILEKIND_FIELD, sType);
		next.put(ITGDFileConstants.EXTRACT_DATE_FIELD, dExtract);
		next.put(OpentachFieldNames.FILENAME_PROCESSED_FIELD, sNombProcesado);
		if (fExtInfo.getNotifUrl() != null) {
			next.put(ITGDFileConstants.NOTIF_FIELD, fExtInfo.getNotifUrl());
		}

		try {
			this.getService(ReportService.class).generateAndSendMobileExpressReport(idFile + "", new String[] { fExtInfo.getEmail() });
		} catch (Exception ex) {
			FileProcessTaskPostOperationDelegate.logger.error("Error sending express mobile report", ex);
		}
		if (fExtInfo.isSendNotificationByMail()) {
			// Se manda un mail con el resultado del análisis
			this.getService(MailService.class).sendMail(new PDAMailNotificationComposer(fExtInfo.getNotifUrl(), null, null, next));
		}
	}

	/**
	 * Gets the extra mail info.
	 *
	 * @param next
	 *            the next
	 * @return the extra mail info
	 */
	protected Hashtable<String, Object> getExtraMailInfo(Map<String, Object> next) {
		try {
			Entity ent = (Entity) this.getEntity("EConductorCont");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, next.get(OpentachFieldNames.CG_CONTRATO_FIELD));
			cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, next.get(OpentachFieldNames.IDORIGEN_FIELD));
			EntityResult rs = ent.query(cv, new Vector<Object>(0), TableEntity.getEntityPrivilegedId(ent));
			int nregs = rs.calculateRecordNumber();
			if (nregs < 1) {
				throw new Exception("NO_DRIVER_INFO");
			} else if (nregs > 1) {
				throw new Exception("TOO_MANY_DRIVER_INFO");
			} else {
				return rs.getRecordValues(0);
			}
		} catch (Exception e) {
			FileProcessTaskPostOperationDelegate.logger.error(null, e);
			return new Hashtable<String, Object>();
		}
	}
}
