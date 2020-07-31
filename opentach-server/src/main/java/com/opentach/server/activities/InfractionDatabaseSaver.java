package com.opentach.server.activities;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.imatia.tacho.infraction.Infraction;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.server.OpentachServerLocator;
import com.utilmize.server.tools.sqltemplate.BatchUpdateJdbcTemplate;

public class InfractionDatabaseSaver {
	public void saveInfractions(List<Infraction> infractionList, final Object contract, final Object cif, final Object driver, String tableName, Connection con) throws Exception {

		TableEntity eEmpreReq = (TableEntity) OpentachServerLocator.getLocator().getEntityReferenceFromServer("EEmpreReq");
		EntityResult erEmpre = eEmpreReq.query(EntityResultTools.keysvalues("NUMREQ", contract, "CIF", cif), EntityResultTools.attributes("NOMB"),
				TableEntity.getEntityPrivilegedId(eEmpreReq), con);
		CheckingTools.checkValidEntityResult(erEmpre, "COMPANY_NOT_FOUND", false, true, (Object[]) null);
		final Map<String, Object> companyInfo = erEmpre.getRecordValues(0);

		new BatchUpdateJdbcTemplate<Infraction>(infractionList) {

			@Override
			protected Object[] beanToParametersArray(int idx, Infraction infraction) {
				String tipo = null;
				switch (infraction.getType()) {
					case ECD1:
					case ECD2:
						tipo = "ECD";
						break;
					case ECI:
						tipo = "ECI";
						break;
					case ECS:
						tipo = "ECS";
						break;
					case ECB:
						tipo = "ECB";
						break;
					case FDD1:
					case FDD2:
					case FDD3:
					case FDD5:
						tipo = "FDD";
						break;
					case FDS:
						tipo = "FDS";
						break;
					case FDSR:
						tipo = "FDSR";
						break;
					case FDS45:
						tipo = "FDS45";
						break;
				}
				Integer tcp = null;
				Integer tdp = null;
				Integer excon = null;
				Integer fades = null;

				switch (infraction.getType()) {
					case ECB:
					case ECD1:
					case ECD2:
					case ECI:
					case ECS:
						excon = infraction.getExcess();
						tcp = infraction.getTime();
						break;
					case FDD1:
					case FDD2:
					case FDD3:
					case FDD5:
					case FDS45:
					case FDS:
					case FDSR:
						fades = infraction.getExcess();
						tdp = infraction.getTime();
						break;
					default:
						break;
				}

				Object[] array = new Object[17];

				array[0] = cif;
				array[1] = companyInfo.get("NOMB");
				array[2] = contract;
				array[3] = driver;
				array[4] = tipo;
				array[5] = infraction.getBeginDate();
				array[6] = infraction.getEndDate();
				array[7] = excon;
				array[8] = fades;
				array[9] = infraction.getScaleEntry().getCgNatu();
				array[10] = infraction.getScaleEntry().getAmount();
				array[11] = infraction.getScaleEntry().getBare1();
				array[12] = infraction.getScaleEntry().getBare2();
				array[13] = infraction.getScaleEntry().getBare34();
				array[14] = infraction.getScaleEntry().getBare56();
				array[15] = tdp;
				array[16] = tcp;

				return array;
			}
		}.execute(
				con,
				"insert into " + tableName + " (CIF,NOMEMP,NUMREQ,IDCONDUCTOR,TIPO,FECHORAINI,FECHORAFIN,EXCON,FADES,NATURALEZA,IMPORTE,BARE1,BARE2,BARE34,BARE56,TDP,TCP) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	}
}
