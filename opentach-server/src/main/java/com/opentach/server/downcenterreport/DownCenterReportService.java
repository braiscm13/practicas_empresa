package com.opentach.server.downcenterreport;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.db.util.MaxDBFunctionName;
import com.ontimize.gui.SearchValue;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.ITGDFileConstants;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.downcenterreport.AbstractReportDto;
import com.opentach.common.downcenterreport.IDownCenterReportService;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.ZipUtils;
import com.opentach.server.AbstractOpentachServerLocator;
import com.opentach.server.IOpentachServerLocator;
import com.utilmize.server.services.UAbstractService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

/**
 * The Class DowncenterReportGenerator.
 */
public class DownCenterReportService extends UAbstractService implements IDownCenterReportService {

	/** The Constant logger. */
	private static final Logger		logger			= LoggerFactory.getLogger(DownCenterReportService.class);

	private static final int		MAXHTMLREPORTS	= 5;

	/** The tc helper. */
	private final TcReportGenerator	tcHelper;

	/** The vu helper. */
	private final VuReportGenerator	vuHelper;
	private Semaphore				semHTMLReport	= null;

	/**
	 * Instantiates a new downcenter report generator.
	 *
	 * @param locator
	 *            the locator
	 */
	public DownCenterReportService(int port, EntityReferenceLocator locator, Hashtable params) throws Exception {
		super(port, locator, params);
		this.semHTMLReport = new Semaphore(DownCenterReportService.MAXHTMLREPORTS, true);
		this.tcHelper = new TcReportGenerator((IOpentachServerLocator) this.getLocator());
		this.vuHelper = new VuReportGenerator((IOpentachServerLocator) this.getLocator());
	}

	@Override
	public AbstractReportDto queryReportData(DownCenterReportType reportType, String source, boolean limitedInfo, int sessionID) throws Exception {
		final IUserData ud = ((IOpentachServerLocator) this.getLocator()).getUserData(sessionID);
		if (ud == null) {
			return null;
		}

		Hashtable<Object, Object> kv = EntityResultTools.keysvalues(//
				"TIPO", DownCenterReportType.TCType.equals(reportType) ? "TC" : "VU", //
						"IDORIGEN", source, //
				"F_ALTA", new SearchValue(SearchValue.MORE, DateTools.add(new Date(), Calendar.HOUR_OF_DAY, -1)));

		TableEntity entity = (TableEntity) this.getEntity("EFicherosTGD");
		EntityResult res = entity.query(kv, new Vector<Object>(
				Arrays.asList(new MaxDBFunctionName(OpentachFieldNames.IDFILE_FIELD, OpentachFieldNames.IDFILE_FIELD, true))),
				TableEntity.getEntityPrivilegedId(entity));
		CheckingTools.checkValidEntityResult(res, "E_FILE_NOT_FOUND", true, true, new Object[] {});
		Object fileID = res.getRecordValues(0).get(OpentachFieldNames.IDFILE_FIELD);

		DownCenterReportService.logger.info("queryReportData FILE={} 	TYPE={} 	RESTRICTEDMODE={} 	USER={}", fileID, reportType.name(), limitedInfo, ud.getLogin());

		Locale locale = ud.getLocale();
		if (locale == null) {
			locale = AbstractOpentachServerLocator.DEFAULTLOCALE;
		}
		try {
			this.semHTMLReport.acquireUninterruptibly();
			return this.generateReport(fileID, reportType, locale, limitedInfo, sessionID);
		} catch (Exception ex) {
			DownCenterReportService.logger.error(null, ex);
			throw new Exception(ex.getMessage());
		} finally {
			this.semHTMLReport.release();
		}
	}

	/**
	 * Generate report.
	 *
	 * @param fileID
	 *            the file id
	 * @param type
	 *            the type
	 * @param locale
	 *            the locale
	 * @param limitedInfo
	 * @param sessionID
	 *            the session id
	 * @return the report dto
	 * @throws Exception
	 *             the exception
	 */
	private AbstractReportDto generateReport(final Object fileID, final DownCenterReportType type, final Locale locale, final boolean limitedInfo, final int sessionID)
			throws Exception {
		return new OntimizeConnectionTemplate<AbstractReportDto>() {

			@Override
			protected AbstractReportDto doTask(Connection conn) throws UException {
				try {
					return DownCenterReportService.this.generateReport(fileID, type, locale, limitedInfo, sessionID, conn);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(this.getConnectionManager(), true);
	}

	/**
	 * Generate report.
	 *
	 * @param idFile
	 *            the id file
	 * @param type
	 *            the type
	 * @param locale
	 *            the locale
	 * @param sessionID
	 *            the session id
	 * @param conn
	 *            the conn
	 * @return the report dto
	 * @throws Exception
	 *             the exception
	 */
	private AbstractReportDto generateReport(Object idFile, DownCenterReportType type, Locale locale, boolean limitedInfo, int sessionID, Connection conn) throws Exception {
		FileInfoReport fi = this.getFileInfo(idFile, conn, sessionID);
		if (fi == null) {
			return null;
		}
		AbstractReportDto report = null;
		if (DownCenterReportType.TCType.equals(type)) {
			report = this.tcHelper.fill(idFile, fi, limitedInfo, conn, sessionID);
		} else {
			report = this.vuHelper.fill(idFile, fi, conn, sessionID);
		}
		return report;
	}

	/**
	 * Gets the file info.
	 *
	 * @param fileID
	 *            the file id
	 * @param conn
	 *            the conn
	 * @param sessionID
	 *            the session id
	 * @return the file info
	 * @throws Exception
	 *             the exception
	 */
	private FileInfoReport getFileInfo(Object fileID, Connection conn, int sessionID) throws Exception {
		TableEntity eFicheros = (TableEntity) this.getEntity(ITGDFileConstants.FILE_ENTITY);
		Hashtable<String, Object> kv = new Hashtable<String, Object>();
		kv.put(OpentachFieldNames.IDFILE_FIELD, fileID);
		Vector<Object> av = new Vector<Object>(1);
		av.add("F_PROCESADO");
		av.add("FICHERO");
		EntityResult res = eFicheros.query(kv, av, sessionID, conn);
		final int count = res.calculateRecordNumber();
		if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && (count == 1)) {
			Hashtable<String, Object> htData = res.getRecordValues(0);
			BytesBlock bb = (BytesBlock) htData.get("FICHERO");
			Date fp = (Date) htData.get("F_PROCESADO");
			List<ByteArrayInputStream> lOut = ZipUtils.unzip(new ByteArrayInputStream(bb.getBytes()));
			ByteArrayInputStream bais = lOut.get(0);
			return new FileInfoReport(bais, fp);
		}
		return null;
	}
}
