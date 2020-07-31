package com.opentach.server.entities;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;

public class ESprintYears extends TableEntity {

	private static final String	E_NOT_IMPLEMENTED_EXCEPTION	= "ESprintYears entity is an only query entity";
	private static final int	YEARS_AFTER					= 10;
	private static final int	YEARS_BEFORE				= 2;

	public ESprintYears(EntityReferenceLocator locator, DatabaseConnectionManager connectionManager, int port) throws Exception {
		super(locator, connectionManager, port);
	}

	@Override
	public EntityResult query(Hashtable kv, Vector av, int sessionId, Connection con) throws Exception {
		Integer currentYear = (Integer) kv.get("CURRENTYEAR");
		Vector<Integer> currentYearVector = new Vector<Integer>();
		Vector<Integer> otherYearsVector = new Vector<Integer>();

		if (currentYear == null) {
			currentYear = Calendar.getInstance().get(Calendar.YEAR);
		}

		for (int i = currentYear - ESprintYears.YEARS_AFTER; i <= (currentYear + ESprintYears.YEARS_BEFORE); i++) {
			currentYearVector.add(currentYear);
			otherYearsVector.add(i);
		}

		EntityResult res = new EntityResult();
		res.put("CURRENTYEAR", currentYearVector);
		res.put("OTHERYEARS", otherYearsVector);
		return res;
	}

	@Override
	public EntityResult insert(Hashtable kv, int sessionId, Connection con) throws Exception {
		throw new Exception(ESprintYears.E_NOT_IMPLEMENTED_EXCEPTION);
	}

	@Override
	public EntityResult delete(Hashtable kv, int sessionId, Connection con) throws Exception {
		throw new Exception(ESprintYears.E_NOT_IMPLEMENTED_EXCEPTION);
	}

	@Override
	public EntityResult update(Hashtable av, Hashtable kv, int sessionId, Connection con) throws Exception {
		throw new Exception(ESprintYears.E_NOT_IMPLEMENTED_EXCEPTION);
	}
}
