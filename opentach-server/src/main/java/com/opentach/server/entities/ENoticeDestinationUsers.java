package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.SearchValue;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.FileTableEntity;

public class ENoticeDestinationUsers extends FileTableEntity {
	private static final String			USER			= "USER_";
	private static final List<String>	lColumnsToGroup	= Arrays.asList(new String[] { "PROVINCIA", "NOMB_EMP", "IDVEHICLETYPE" });

	public ENoticeDestinationUsers(EntityReferenceLocator b, DatabaseConnectionManager g, int p) throws Exception {
		super(b, g, p);
	}

	public ENoticeDestinationUsers(EntityReferenceLocator locator, DatabaseConnectionManager dbConnectionManager, int port, Properties prop,
			Properties aliasProp) throws Exception {
		super(locator, dbConnectionManager, port, prop, aliasProp);
	}

	/**
	 * En la query nos llegan usuarios repetidos ya que nos vienen para las diferentes empresas, los usuarios que tienen. Lo que hacemos en la query
	 * es agrupar los usuarios por provincia y por nombre de empresa.
	 */
	@Override
	public EntityResult query(Hashtable avOrig, Vector av, int sesionId, Connection con) throws Exception {
		if (avOrig.containsKey("NO_USER")) {
			avOrig.put("USER_", new SearchValue(SearchValue.NOT_EQUAL, avOrig.remove("NO_USER")));
		}
		EntityResult res = super.query(avOrig, av, sesionId, con);
		EntityResult resTotal = new EntityResult(av);
		HashMap<String, Integer> users = new HashMap<String, Integer>();
		for (int i = 0; i < res.calculateRecordNumber(); i++) {
			Hashtable<String, Object> rowData = res.getRecordValues(i);
			// Group results
			if (users.containsKey(rowData.get(ENoticeDestinationUsers.USER))) {
				for (String columnToGroup : ENoticeDestinationUsers.lColumnsToGroup) {
					this.groupColumn(resTotal, users, rowData, columnToGroup);
				}
			} else {
				users.put((String) rowData.get(ENoticeDestinationUsers.USER), resTotal.calculateRecordNumber());
				for (String columnToGroup : ENoticeDestinationUsers.lColumnsToGroup) {
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
			int indexUser = users.get(rowData.get(ENoticeDestinationUsers.USER));
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
