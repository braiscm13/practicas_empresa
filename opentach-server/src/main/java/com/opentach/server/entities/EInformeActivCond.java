package com.opentach.server.entities;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.server.activities.ActivityService;
import com.opentach.server.util.db.FileTableEntity;

public class EInformeActivCond extends FileTableEntity {

	//	private static final Logger	logger	= LoggerFactory.getLogger(EInformeActivCond.class);

	public EInformeActivCond(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		if (cv.containsKey(EInformeInfrac.DO_QUERY)) {
			return super.query(cv, v, sesionId, con);
		}
		Object cgContrato = this.getNumReq(cv, sesionId, con);
		Timestamp[] fComienzoFfinal = this.getQueryDates(cv);
		Date beginDate = fComienzoFfinal[0];
		Date endDate = fComienzoFfinal[1];
		String cardNumber = (String) cv.get("NUM_TARJ");

		Vector<Object> vDrivers = EInformeActivCond.getDrivers(cv.get(OpentachFieldNames.IDCONDUCTOR_FIELD), true);

		EntityResult res = this.getService(ActivityService.class).flatActivities(null, cgContrato, cardNumber, beginDate,
				endDate,
				cv.containsKey("PERIODOS"), vDrivers, sesionId, v);
		return res;
	}


	public static Vector<Object> getDrivers(Object oidConductor, boolean failIfNoDrivers) throws Exception {
		Vector<Object> vDrivers = new Vector<>();
		if (oidConductor instanceof String) {
			if (oidConductor.equals("%")) {
				throw new Exception("E_MUST_SPECIFY_DRIVER");
			}
			vDrivers.add(oidConductor);
		} else if ((oidConductor instanceof SearchValue) && ((((SearchValue) oidConductor).getCondition() == SearchValue.OR) || (((SearchValue) oidConductor)
				.getCondition() == SearchValue.IN) || (((SearchValue) oidConductor).getCondition() == SearchValue.BETWEEN))) {
			Vector<Object> v = (Vector<Object>) ((SearchValue) oidConductor).getValue();
			vDrivers.addAll(v);
		}

		if ((vDrivers.size() == 0) && failIfNoDrivers) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		return vDrivers;
	}

	private Timestamp[] getQueryDates(Hashtable cv) throws Exception {

		SearchValue vbFComienzo = (SearchValue) cv.get("FEC_COMIENZO");
		if (vbFComienzo == null) {
			vbFComienzo = (SearchValue) cv.get(OpentachFieldNames.FECINI_FIELD);
		}

		Timestamp fComienzo = null;
		Timestamp fFinal = null;

		if (vbFComienzo != null) {
			switch (vbFComienzo.getCondition()) {
				case SearchValue.BETWEEN:
					Vector<Object> vv = (Vector<Object>) vbFComienzo.getValue();
					fComienzo = new Timestamp(((Date) vv.firstElement()).getTime());
					fFinal = new Timestamp(((Date) vv.lastElement()).getTime());
					break;
				case SearchValue.LESS_EQUAL:
					fFinal = (Timestamp) vbFComienzo.getValue();
					break;
				case SearchValue.MORE_EQUAL:
					fComienzo = (Timestamp) vbFComienzo.getValue();
					break;
			}
		}
		if ((fComienzo == null) || (fFinal == null)) {
			throw new Exception("FECHA_NULL");
		}

		return new Timestamp[] { fComienzo, fFinal };
	}

	private Object getNumReq(Hashtable cv, int sesionId, Connection conn) throws Exception {
		String cif = (String) cv.get(OpentachFieldNames.CIF_FIELD);
		String numReq = (String) cv.get(OpentachFieldNames.CG_CONTRATO_FIELD);

		if (numReq == null) {
			Object oNumReq = cv.get(OpentachFieldNames.NUMREQ_FIELD);
			if (oNumReq instanceof String) {
				numReq = (String) oNumReq;
			} else if (oNumReq instanceof SearchValue) {
				if (((SearchValue) oNumReq).getCondition() == 8) {
					Vector<Object> vv = (Vector<Object>) ((SearchValue) oNumReq).getValue();
					numReq = (String) vv.firstElement();
				} else {
					numReq = (String) ((SearchValue) oNumReq).getValue();
				}
			}
		}
		return numReq;
	}
}
