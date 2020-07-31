package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.locator.EntityReferenceLocator;

public class EUsuariosManagement extends Usuario {
	private static final String			USER			= "USUARIO";
	private static final List<String>	lColumnsToGroup	= Arrays.asList(new String[] { "PROVINCIA", "NOMB_EMP" });

	public EUsuariosManagement(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector v, int sesionId, Connection con) throws Exception {
		EntityResult res = super.query(cv, v, sesionId, con);
		EntityResult resTotal = new EntityResult(v);
		HashMap<String, Integer> users = new HashMap<String, Integer>();
		for (int i = 0; i < res.calculateRecordNumber(); i++) {
			Hashtable<String, Object> rowData = res.getRecordValues(i);
			// Group results
			if (users.containsKey(rowData.get(EUsuariosManagement.USER))) {
				for (String columnToGroup : EUsuariosManagement.lColumnsToGroup) {
					this.groupColumn(resTotal, users, rowData, columnToGroup);
				}
			} else {
				users.put((String) rowData.get(EUsuariosManagement.USER), resTotal.calculateRecordNumber());
				for (String columnToGroup : EUsuariosManagement.lColumnsToGroup) {
					String value = rowData.get(columnToGroup) == null ? "" : (String) rowData.get(columnToGroup);
					rowData.put(columnToGroup, value);
				}
				resTotal.addRecord(rowData, resTotal.calculateRecordNumber());
			}
		}
		return resTotal;
	}

	private void groupColumn(EntityResult resTotal, HashMap<String, Integer> users, Hashtable<String, Object> rowData, String column) {
		String value = (String) rowData.get(column);
		if (value != null) {
			int indexUser = users.get(rowData.get(EUsuariosManagement.USER));

			String valueResTotal = ((Vector<String>) resTotal.get(column)).get(indexUser);
			if (valueResTotal.isEmpty()) {
				((Vector<String>) resTotal.get(column)).set(indexUser, value);
			} else if (!valueResTotal.contains(value)) {
				String valueConcat = valueResTotal + "; " + value;
				((Vector<String>) resTotal.get(column)).set(indexUser, valueConcat);
			}
		}
	}
}
