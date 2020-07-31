package com.opentach.server.alerts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.TableEntity;
import com.ontimize.locator.SecureReferenceLocator;
import com.ontimize.util.alerts.MySendAutomaticNotice;
import com.ontimize.util.notice.INoticeSystem;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.util.DateUtil;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.entities.EInformeGestor;
import com.opentach.server.report.ReportService;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.server.tools.sqltemplate.UpdateJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class AlarmaInformeGestor extends MySendAutomaticNotice {

	private static final Logger		logger	= LoggerFactory.getLogger(AlarmaInformeGestor.class);
	private Hashtable				htResul	= null;
	private static final String[]	MONTHS	= { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };

	private final class EmpInfo {
		private final String	cif;
		private final String	nomb;
		private final String	cgContrato;
		private final int		dGen;
		private final int		dEnd;
		private final Date		dLast;
		private final String	mail;
		private final Locale	locale;

		public EmpInfo(String cif, String nomb, String cgContrato, int dGen, int dEnd, Date dLast, String mail, Locale locale) {
			this.cif = cif;
			this.nomb = nomb;
			this.cgContrato = cgContrato;
			this.dGen = dGen;
			this.dEnd = dEnd;
			this.dLast = dLast;
			this.mail = mail;
			this.locale = locale;
		}

		public Date getDLast() {
			return this.dLast;
		}

		public String getCif() {
			return this.cif;
		}

		public int getDGen() {
			return this.dGen;
		}

		public int getDEnd() {
			return this.dEnd;
		}

		public String getMail() {
			return this.mail;
		}

		public String getNomb() {
			return this.nomb;
		}

		public String getCGContrato() {
			return this.cgContrato;
		}

		public Locale getLocale() {
			return this.locale;
		}
	}

	public AlarmaInformeGestor() {
		super();
	}

	@Override
	public void doOnConditionTrue() throws Exception {
		try {
			List lEmp = this.generateReports();
			if ((lEmp != null) && (lEmp.size() > 0)) {
				EmpInfo ei = null;
				this.htResul = new Hashtable();
				String msg = "Se ha enviado el informe gestor correspondiente a ";
				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) - 1;
				for (int i = 0; i < lEmp.size(); i++) {
					ei = (EmpInfo) lEmp.get(i);
					this.htResul.put(ei.getCif(), msg + AlarmaInformeGestor.MONTHS[month % 12] + " de la empresa: " + ei.getNomb());
				}
				this.sendNotice();
			}
		} catch (Exception e) {
			this.htResul = null;
		}
	}

	public static Hashtable<String, Object> getQueryData() {
		Hashtable<String, Object> queryData = new Hashtable<String, Object>(3);
		queryData.put("entity", "EUsuariosTodos");
		Hashtable<String, Object> cv = new Hashtable<String, Object>();
		queryData.put("cv", cv);
		Vector<Object> av = new Vector<Object>();
		queryData.put("av", av);
		return queryData;
	}

	@Override
	public void doOnConditionFalse() throws Exception {
		super.doOnConditionFalse();
	}

	@Override
	public boolean evaluateCondition() throws Exception {
		return true;
	}

	// TODO: obtener los usuarios a los que avisar

	private Hashtable<String, Object> getMessageData(String cif, String msg) {
		Hashtable<String, Object> messageData = super.getMessageData();
		Vector<Object> v = new Vector<Object>();
		v.add("SUPERVISOR");
		messageData.put("UsuarioDestino", v);
		String contenido = msg;
		messageData.put(INoticeSystem.NOTICE_CONTENT, contenido);
		return new Hashtable<String, Object>(messageData);
	}

	@Override
	protected void sendNotice() {
		if ((this.htResul == null) || (this.htResul.size() == 0)) {
			return;
		}
		Enumeration<Object> eCifs = this.htResul.keys();
		while (eCifs.hasMoreElements()) {
			try {
				String cif = (String) eCifs.nextElement();
				String msg = (String) this.htResul.get(cif);
				Hashtable<String, Object> messageData = this.getMessageData(cif, msg);
				((INoticeSystem) this.locator).sendNotice(messageData, TableEntity
						.getEntityPrivilegedId(((SecureReferenceLocator) this.locator).getEntityReferenceFromServer(((INoticeSystem) this.locator).getNoticeEntityName())), false);
			} catch (Exception e) {
				AlarmaInformeGestor.logger.error(null, e);
			}
		}
	}

	private List<EmpInfo> getEmpInfo() throws Exception {
		return new OntimizeConnectionTemplate<List<EmpInfo>>() {
			@Override
			protected List<EmpInfo> doTask(Connection con) throws UException {
				try {
					return AlarmaInformeGestor.this.getEmpInfo(con);
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute((SecureReferenceLocator) this.locator, false);

	}

	private List<EmpInfo> getEmpInfo(Connection conn) throws Exception {
		String sql = "SELECT DFEMP.CIF, DFEMP.NOMB, CDEMPRE_REQ.NUMREQ,CDREPORTCONFIG_DFEMP.DGEN, CDREPORTCONFIG_DFEMP.DEND, CDREPORTCONFIG_DFEMP.DULTIMO_ENVIO, DFEMP.EMAIL, DFEMP.LOCALE FROM CDREPORTCONFIG_DFEMP,CDEMPRE_REQ, DFEMP WHERE CDREPORTCONFIG_DFEMP.CIF = DFEMP.CIF AND DFEMP.CIF = CDEMPRE_REQ.CIF AND CDEMPRE_REQ.F_BAJA IS NULL AND DFEMP.EMAIL IS NOT NULL";
		return new QueryJdbcTemplate<List<EmpInfo>>() {
			@Override
			protected List<EmpInfo> parseResponse(ResultSet rset) throws UException {
				try {
					List<EmpInfo> lres = new ArrayList<EmpInfo>();
					while (rset.next()) {
						String sLocale = rset.getString(8);
						Locale locale = null;
						if (sLocale != null) {
							locale = new Locale(sLocale);
						} else {
							locale = null;
						}
						lres.add(new EmpInfo(rset.getString(1), rset.getString(2), rset.getString(3), rset.getInt(4), rset.getInt(5), rset.getDate(6), rset.getString(7), locale));
					}
					return lres;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(conn, sql);
	}

	private void saveEmpInfo(final String cif) throws Throwable {
		final String sql = "UPDATE CDREPORTCONFIG_DFEMP SET DULTIMO_ENVIO = SYSDATE WHERE CIF = ?";
		new OntimizeConnectionTemplate<Void>() {
			@Override
			protected Void doTask(Connection con) throws UException {
				try {
					new UpdateJdbcTemplate().execute(con, sql, cif);
					return null;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute((SecureReferenceLocator) this.locator, false);
	}

	private List<EmpInfo> generateReports() throws Exception {
		List<EmpInfo> lEmp = this.getEmpInfo();
		List<EmpInfo> lSend = new ArrayList<EmpInfo>();
		EmpInfo ei = null;
		Date endDate = null;
		EInformeGestor einf = null;
		for (int i = 0; i < lEmp.size(); i++) {
			ei = lEmp.get(i);
			if ((ei.getMail() == null) && (ei.getDEnd() != -1) && (ei.getDGen() != -1)) {
				continue;
			}
			if (!this.isToday(ei.getDLast(), ei.getDGen())) {
				continue;
			}
			endDate = this.getDEndDate(ei.getDGen(), ei.getDEnd());
			try {
				int sessionID = TableEntity.getEntityPrivilegedId(einf);
				((OpentachServerLocator) this.locator).getService(ReportService.class).generateManagementReport(ei.getCif(), ei.getNomb(), ei.getCGContrato(), ei.getMail(),
						endDate, EngineAnalyzer.DEFAULT, ei.getLocale(), true, sessionID);
				this.saveEmpInfo(ei.getCif());
				lSend.add(ei);
			} catch (Throwable e) {
				AlarmaInformeGestor.logger.error(null, e);
			}
		}
		return lSend;
	}

	private Date getDNow() {
		Date dNow = new Date();
		return dNow;
	}

	private boolean isToday(Date dLast, int dGen) {
		Date dNow = this.getDNow();
		dNow = DateUtil.trunc(dNow);
		if (dLast != null) {
			dLast = DateUtil.trunc(dLast);
			if (dLast.equals(dNow)) {
				return false;
			}
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(dNow);
		int inow = cal.get(Calendar.DAY_OF_MONTH);
		int maxMonthDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return ((inow == dGen) || ((dGen >= maxMonthDay) && (inow == maxMonthDay)));
	}

	private Date getDEndDate(int dgen, int iend) {
		Date dNow = this.getDNow();
		dNow = DateUtil.trunc(dNow);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dNow);
		if (iend > dgen) {
			cal.add(Calendar.MONTH, -1);
		}
		cal.set(Calendar.DAY_OF_MONTH, iend);
		return cal.getTime();
	}

}
