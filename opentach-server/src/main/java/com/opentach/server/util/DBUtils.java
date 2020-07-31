package com.opentach.server.util;

import java.sql.Connection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.ObjectTools;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;
import com.opentach.common.user.UserData;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.companies.ContractService;
import com.opentach.server.entities.EConductoresEmp;
import com.opentach.server.entities.EDfEmp;
import com.opentach.server.mail.MailService;
import com.opentach.server.util.mail.AltaBajaAutoMailComposer;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public final class DBUtils {
	private final static Logger logger = Logger.getLogger(DBUtils.class);

	private DBUtils() {}

	public final static IUserData getSimpleUserDataFromDB(final IOpentachServerLocator srl, final String user) throws Exception {
		return new OntimizeConnectionTemplate<IUserData>() {
			@Override
			protected IUserData doTask(Connection con) throws UException {
				try {
					IUserData du = null;
					TableEntity entUsuario = (TableEntity) srl.getEntityReferenceFromServer("Usuario");
					Hashtable<String, Object> cv = new Hashtable<String, Object>(1);
					cv.put("USUARIO", user);
					Vector<Object> vq = new Vector<Object>(7);
					vq.add("USUARIO");
					vq.add("NIVEL_CD");
					vq.add("NOMBRE_PERFIL");
					vq.add("SENDMAIL");
					vq.add("DSCR");
					vq.add("PRINTCONFIG");
					vq.add("EXPRESSREPORT");
					vq.add("CARGO");
					vq.add("NOMBRE_FIRMANTE");
					vq.add("APELLIDO_FIRMANTE");
					vq.add("MONIT");
					vq.add("DMAXLOGIN");
					vq.add("USBPOSITION");
					vq.add("CONTRACT_COMPANY");
					EntityResult res = entUsuario.query(cv, vq, TableEntity.getEntityPrivilegedId(entUsuario), con);
					if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && !res.isEmpty()) {
						Hashtable<String, Object> valores = res.getRecordValues(0);
						final String nivel = (String) valores.get("NIVEL_CD");
						final String nivelDscr = (String) valores.get("NOMBRE_PERFIL");
						final String sendMail = (String) valores.get("SENDMAIL");
						final String dscr = (String) valores.get("DSCR");
						final String printConfig = (String) valores.get("PRINTCONFIG");
						final String expressReport = (String) valores.get("EXPRESSREPORT");
						final String nfirmante = (String) valores.get("NOMBRE_FIRMANTE");
						final String afirmante = (String) valores.get("APELLIDOS_FIRMANTE");
						final String cargo = (String) valores.get("CARGO");
						final String monitor = (String) valores.get("MONIT");
						final String usbleft = (String) valores.get("USBPOSITION");
						final boolean bSendMail = "S".equalsIgnoreCase(sendMail);
						final boolean bMonitor = "S".equalsIgnoreCase(monitor);
						final boolean busbleft = "S".equalsIgnoreCase(usbleft);
						final Date dMaxLogin = (Date) valores.get("DMAXLOGIN");
						final String contractCompany = (String) valores.get("CONTRACT_COMPANY");
						List<String[]> lEmpresas = Collections.emptyList();
						List<String[]> lEmpresasTodas = Collections.emptyList();
						Map<String, Number> lDeleg = Collections.emptyMap();
						Map<String, Number> lGrupos = Collections.emptyMap();
						Map<String, String> mContratos = Collections.emptyMap();
						du = new UserData(user, null, nivel, nivelDscr, lEmpresas, lEmpresasTodas, lDeleg, lGrupos, mContratos, new Date(), dscr, bSendMail, expressReport,
								printConfig, bMonitor, dMaxLogin, busbleft, nfirmante, afirmante, cargo, contractCompany);
					}
					return du;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(srl.getConnectionManager(), true);
	}

	public final static IUserData getUserDataFromDB(final IOpentachServerLocator srl, final int sessionID) throws Exception {
		return new OntimizeConnectionTemplate<IUserData>() {

			@Override
			protected IUserData doTask(Connection conn) throws UException {
				try {
					IUserData du = null;
					TableEntity entUsuario = (TableEntity) srl.getEntityReferenceFromServer(srl.getLoginEntityName(sessionID));
					Hashtable<String, Object> cv = new Hashtable<String, Object>(1);
					cv.put("USUARIO", srl.getUser(sessionID));
					Vector<Object> vq = new Vector<Object>();
					vq.add("USUARIO");
					vq.add("NIVEL_CD");
					vq.add("NOMBRE_PERFIL");
					vq.add("SENDMAIL");
					vq.add("DSCR");
					vq.add("PRINTCONFIG");
					vq.add("EXPRESSREPORT");
					vq.add("CARGO");
					vq.add("NOMBRE_FIRMANTE");
					vq.add("APELLIDOS_FIRMANTE");
					vq.add("DMAXLOGIN");
					vq.add("USBPOSITION");
					vq.add("MONIT");
					vq.add("CONTRACT_COMPANY");
					EntityResult res = entUsuario.query(cv, vq, TableEntity.getEntityPrivilegedId(entUsuario), conn);
					if ((res.getCode() == EntityResult.OPERATION_SUCCESSFUL) && !res.isEmpty()) {
						Hashtable<String, Object> valores = res.getRecordValues(0);
						final String user = (String) valores.get("USUARIO");
						final String nivel = (String) valores.get("NIVEL_CD");
						final String nivelDscr = (String) valores.get("NOMBRE_PERFIL");
						final String sendMail = (String) valores.get("SENDMAIL");
						final String dscr = (String) valores.get("DSCR");
						final String printConfig = (String) valores.get("PRINTCONFIG");
						final String expressReport = (String) valores.get("EXPRESSREPORT");
						final String monitor = (String) valores.get("MONIT");
						final String usbleft = (String) valores.get("USBPOSITION");
						final String nfirmante = (String) valores.get("NOMBRE_FIRMANTE");
						final String afirmante = (String) valores.get("APELLIDOS_FIRMANTE");
						final String cargo = (String) valores.get("CARGO");
						final boolean bSendMail = "S".equalsIgnoreCase(sendMail);
						final boolean bMonitor = "S".equalsIgnoreCase(monitor);
						final boolean busbleft = "S".equalsIgnoreCase(usbleft);
						final Date dMaxLogin = (Date) valores.get("DMAXLOGIN");
						final String contractCompany = (String) valores.get("CONTRACT_COMPANY");

						ContractService contractService = srl.getService(ContractService.class);
						List<String[]> lEmpresasCoop = contractService.getEmpresasUsuario(user, sessionID, conn);
						Vector<String[]> lEmpresas = contractService.getEmpresasTodasUsuario(user, sessionID, conn);
						Vector<String> lCifs = this.parseCompanies(lEmpresas);

						Map<String, Number> lDeleg = contractService.getDelegacionesUsuario(user, sessionID, conn);
						Map<String, Number> lGrupos = contractService.getGruposUsuario(user, sessionID, conn);
						boolean isAdmin = IUserData.NIVEL_SUPERVISOR.equals(nivel);
						boolean allowAllCompanies = UserData.isAnonymousUser(nivel) || ObjectTools.isIn(nivel, IUserData.NIVEL_OPERADOR);
						Map<String, String> mContratos = contractService.getContratosEmpresas(lCifs, isAdmin, allowAllCompanies, sessionID, conn);
						du = new UserData(user, null, nivel, nivelDscr, lEmpresasCoop, lEmpresas, lDeleg, lGrupos, mContratos, new Date(), dscr, bSendMail, expressReport,
								printConfig, bMonitor, dMaxLogin, busbleft, nfirmante, afirmante, cargo, contractCompany);
						if (lEmpresas.isEmpty() && (IUserData.NIVEL_EMPRESA.equals(du.getLevel()) || IUserData.NIVEL_AGENTE.equals(du.getLevel()) || IUserData.NIVEL_TITULARCDO
								.equals(du.getLevel()))) {
							throw new Exception("M_ERROR_NO_TIENE_EMPRESAS_ASIGNADAS");
						}
					}
					return du;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}

			private Vector<String> parseCompanies(Vector<String[]> lEmpresas) {
				Vector<String> res = new Vector<String>();
				for (String[] pair : lEmpresas) {
					res.add(pair[0]);
				}
				return res;
			}
		}.execute(srl.getConnectionManager(), true);

	}

	public static final Set<String> getMonitorizedUsers(IOpentachServerLocator srl) {
		Set<String> sUsers = null;
		try {
			TableEntity te = (TableEntity) srl.getEntityReferenceFromServer("Usuario");
			int sessionID = TableEntity.getEntityPrivilegedId(te);
			Vector<Object> vq = new Vector<Object>(1);
			vq.add("USUARIO");
			Hashtable<String, Object> htq = new Hashtable<String, Object>();
			htq.put("MONIT", "S");
			EntityResult er = te.query(htq, vq, sessionID);
			if (er.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				Vector<String> vData = (Vector<String>) er.get("USUARIO");
				if (vData != null) {
					sUsers = new HashSet<String>(vData);
				}
			}
		} catch (Exception e) {
			DBUtils.logger.error(null, e);
		}
		return sUsers;
	}

	public static final void generateMailAuditDriverVeh(IOpentachServerLocator osl, AltaBajaAutoMailComposer.Type type, Hashtable data, String companyName, int sessionID) {
		try {
			DBUtils.logger.debug("Sending auditing mail ....");
			IUserData ud = osl.getUserData(sessionID);
			if (ud == null) {
				return;
			}
			MailService ms = osl.getService(MailService.class);
			String mailto = ms.getMailAuditAddress();
			if (mailto == null) {
				return;
			}
			String user = ud.getLogin();
			Locale locale = ud.getLocale();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(AltaBajaAutoMailComposer.TYPE, type);
			String matricula = (String) data.get(OpentachFieldNames.MATRICULA_FIELD);
			String dni = (String) data.get(OpentachFieldNames.IDCONDUCTOR_FIELD);
			String cgContrato = (String) data.get(OpentachFieldNames.CG_CONTRATO_FIELD);
			String cif = (String) data.get(OpentachFieldNames.CIF_FIELD);
			String nombre = (String) data.get(OpentachFieldNames.NAME_FIELD);
			String apellidos = (String) data.get(OpentachFieldNames.SURNAME_FIELD);
			if (matricula != null) {
				params.put(OpentachFieldNames.MATRICULA_FIELD, matricula);
				params.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
				params.put(OpentachFieldNames.CIF_FIELD, cif);
				params.put(OpentachFieldNames.NAME_FIELD, companyName);
			} else if (dni != null) {
				if ((nombre != null) || (apellidos != null)) {
					String nc = (apellidos != null ? apellidos + "," : "") + (nombre != null ? nombre : "");
					params.put(AltaBajaAutoMailComposer.CONDUCTOR, nc);
				} else {
					String nc = (String) params.get(OpentachFieldNames.NAME_FIELD);
					params.put(AltaBajaAutoMailComposer.CONDUCTOR, nc);
				}
				params.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
				params.put(OpentachFieldNames.CIF_FIELD, cif);
				params.put(OpentachFieldNames.NAME_FIELD, companyName);
			} else {
				return;
			}
			params.put(AltaBajaAutoMailComposer.USER, user);
			AltaBajaAutoMailComposer abc = new AltaBajaAutoMailComposer(mailto, null, locale, params);
			ms.sendMail(abc);
		} catch (Exception e) {
			DBUtils.logger.error(null, e);
		}
	}

	public static final String getCompanyName(IOpentachServerLocator srl, String cif, int sessionID) throws Exception {
		EDfEmp companies = (EDfEmp) srl.getEntityReferenceFromServer(CompanyNaming.ENTITY);
		Map<String, Object> companyInfo = companies.getCompanyInfo(cif, -1, null, "NOMB");

		return (String) companyInfo.get("NOMB");
	}

	public static final String getDriverName(IOpentachServerLocator srl, String dni, String cif, int sessionID) throws Exception {
		EConductoresEmp eDrivers = (EConductoresEmp) srl.getEntityReferenceFromServer("EConductoresEmp");
		// Map<String, Object> driverInfo = eDrivers.getDriverInfoByDni(dni, cif, null, OpentachFieldNames.NAME_FIELD, OpentachFieldNames.SURNAME_FIELD);

		// TODO: IDCONDUCTOR-DNI
		Map<String, Object> driverInfo = eDrivers.getDriverInfoByIdConductor(dni, cif, null, OpentachFieldNames.NAME_FIELD, OpentachFieldNames.SURNAME_FIELD);

		if (driverInfo.isEmpty()) {
			return null;
		}
		String name = (String) driverInfo.get(OpentachFieldNames.NAME_FIELD);
		String surname = (String) driverInfo.get(OpentachFieldNames.SURNAME_FIELD);
		String nc = (surname != null ? surname + "," : "") + (name != null ? name : "");
		return nc;
	}

}
