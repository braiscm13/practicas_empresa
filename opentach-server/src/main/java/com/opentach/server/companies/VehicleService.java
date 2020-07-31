package com.opentach.server.companies;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.companies.IVehicleService;
import com.utilmize.server.services.UAbstractService;

public class VehicleService extends UAbstractService implements IVehicleService {

	private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

	public VehicleService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
	}

	public boolean hasNewVehicleSpace(String cif) throws Exception {
		try {
			int maxVehicles = -1;
			Hashtable<String, Object> cv = new Hashtable<>();
			cv.put("CIF", cif);
			TableEntity eDfEmp = (TableEntity) this.getEntity("EEmpreReq");
			EntityResult resEmp = eDfEmp.query(cv, EntityResultTools.attributes("U_NUM_MAXIMO"), TableEntity.getEntityPrivilegedId(eDfEmp));
			CheckingTools.checkValidEntityResult(resEmp, "NOTFOUND", true, true, new Object[] {});

			Object obMaxVehicles = resEmp.getRecordValues(0).get("U_NUM_MAXIMO");
			if (obMaxVehicles != null) {
				maxVehicles = ((Number) obMaxVehicles).intValue();
			}
			if (maxVehicles != -1) {
				// TODO, pedir el count
				TableEntity eVehicles = (TableEntity) this.getEntity("EVehiculosEmp");
				EntityResult res = eVehicles.query(cv, EntityResultTools.attributes("MATRICULA"), TableEntity.getEntityPrivilegedId(eVehicles));
				if (res.calculateRecordNumber() >= maxVehicles) {
					return false;
				}
			}
		} catch (Exception err) {
			VehicleService.logger.error(null, err);
		}
		return true;
	}

}
