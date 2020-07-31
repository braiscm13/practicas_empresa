package com.opentach.server.checktrucker.entities;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.db.SQLStatementBuilder.BasicField;
import com.ontimize.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.user.Company;
import com.opentach.server.checktrucker.services.SurveyReportService;
import com.opentach.server.util.db.FileTableEntity;
import com.opentach.server.webservice.rest.RestServiceUtils;

public class EInformeCheckTrucker extends FileTableEntity {

	// private static final Logger logger = LoggerFactory.getLogger(EInformeActivCond.class);

	public EInformeCheckTrucker(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		BasicExpression beCif = this.getBeCIF(cv, sesionId);

		BasicExpression beDate = this.getBeDate(cv);

		BasicExpression beDni = this.getBeDNI(cv);

		BasicExpression beIdSurvey = this.getBeIdSurvey(cv);

		BasicExpression be = this.concatBasicExpressions(beCif, beDate, beDni, beIdSurvey);

		return this.getService(SurveyReportService.class).getCompanyCorrectWrongDrivers(be, null, sesionId);
	}

	private BasicExpression concatBasicExpressions(BasicExpression... basicExpressions) {
		BasicExpression beTotal = null;
		for (BasicExpression be : basicExpressions) {
			if (be == null) {
				continue;
			}
			if (beTotal == null) {
				beTotal = be;
			} else {
				beTotal = new BasicExpression(beTotal, BasicOperator.AND_OP, be);
			}
		}
		return beTotal;
	}

	private BasicExpression getBeDate(Hashtable cv) {
		BasicExpression beDate1 = null;
		BasicExpression beDate2 = null;

		if (cv.containsKey("FECINI")) {
			beDate1 = new BasicExpression(new BasicField("SURVEY_RESPONSE_DATE"), BasicOperator.MORE_EQUAL_OP, cv.get("FECINI"));
		}
		if (cv.containsKey("FECFIN")) {
			beDate2 = new BasicExpression(new BasicField("SURVEY_RESPONSE_DATE"), BasicOperator.LESS_EQUAL_OP, cv.get("FECFIN"));
		}
		if ((beDate1 == null) && (beDate2 != null)) {
			return beDate2;
		} else if ((beDate1 != null) && (beDate2 == null)) {
			return beDate1;
		} else if ((beDate1 != null) && (beDate2 != null)) {
			return new BasicExpression(beDate1, BasicOperator.AND_OP, beDate2);
		}
		return null;
	}

	private BasicExpression getBeCIF(Hashtable cv, int sesionId) throws Exception {
		List<String> cifs = new ArrayList<>();
		if (cv.containsKey("CIF")) {
			cifs.add((String) cv.get("CIF"));
		} else {
			Company company = cv.containsKey("CIF") ? RestServiceUtils.getCompany(cv.get("CIF"), sesionId) : RestServiceUtils.getCompany(sesionId);
			cifs.add(company.getCif());
		}
		if (cifs.size() == 0) {
			return null;
		} else {
			BasicExpression b1 = new BasicExpression(new BasicField("con.CIF"), BasicOperator.IN_OP, cifs);
			BasicExpression b2 = new BasicExpression(new BasicField("pers.CIF"), BasicOperator.IN_OP, cifs);
			return new BasicExpression(b1, BasicOperator.OR_OP, b2);
		}
	}

	private BasicExpression getBeDNI(Hashtable cv) throws Exception {
		List<String> dnis = new ArrayList<>();
		String field = "IDCONDUCTOR";
		if (cv.containsKey("IDCONDUCTOR")) {
			field = "IDCONDUCTOR";
			if (cv.get("IDCONDUCTOR") instanceof String){
				String idConductor = (String) cv.get("IDCONDUCTOR");
				dnis.add(idConductor);
			}
			else {
				SearchValue sv = (SearchValue) cv.get("IDCONDUCTOR");
				Vector<String> v = (Vector<String>)sv.getValue();
				dnis.addAll(v);
			}
		} else if (cv.containsKey("IDPERSONAL")) {
			field = "IDPERSONAL";
			if (cv.get("IDPERSONAL") instanceof String){
				String idPersonal = (String) cv.get("IDPERSONAL");
				dnis.add(idPersonal);
			}
			else {
				SearchValue sv = (SearchValue) cv.get("IDPERSONAL");
				Vector<String> v = (Vector<String>)sv.getValue();
				dnis.addAll(v);
			}
		}
		if (dnis.size() == 0) {
			return null;
		} else {
			return new BasicExpression(new BasicField(field), BasicOperator.IN_OP, dnis);
		}
	}

	private BasicExpression getBeIdSurvey(Hashtable cv) throws Exception {
		Object idSurvey = null;
		if (cv.containsKey("ID_SURVEY")) {
			idSurvey = cv.get("ID_SURVEY");
		} else {
			return null;
		}
		return new BasicExpression(new BasicField("SR.ID_SURVEY"), BasicOperator.EQUAL_OP, idSurvey);
	}

	public static Vector<String> getDrivers(Object oidConductor, boolean failIfNoDrivers) throws Exception {
		Vector<String> vDrivers = new Vector<>();
		if (oidConductor instanceof String) {
			if (oidConductor.equals("%")) {
				throw new Exception("E_MUST_SPECIFY_DRIVER");
			}
			vDrivers.add((String) oidConductor);
		} else if ((oidConductor instanceof SearchValue) && ((((SearchValue) oidConductor).getCondition() == SearchValue.OR) || (((SearchValue) oidConductor)
				.getCondition() == SearchValue.IN) || (((SearchValue) oidConductor).getCondition() == SearchValue.BETWEEN))) {
			Vector<String> v = (Vector<String>) ((SearchValue) oidConductor).getValue();
			vDrivers.addAll(v);
		}

		if ((vDrivers.size() == 0) && failIfNoDrivers) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		return vDrivers;
	}

}
