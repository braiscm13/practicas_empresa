package com.opentach.server.labor.entities;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.DatabaseConnectionManager;
import com.ontimize.db.EntityResult;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.server.util.db.DefaultFileTableEntity;

public class EManualJournal extends DefaultFileTableEntity {

	private static final Logger	logger	= LoggerFactory.getLogger(EManualJournal.class);

	public EManualJournal(EntityReferenceLocator bref, DatabaseConnectionManager gcon, int port) throws Exception {
		super(bref, gcon, port, "com/opentach/server/labor/entities/prop/EManualJournal.properties");
	}

	public EManualJournal(EntityReferenceLocator b, DatabaseConnectionManager g, int p, String props) throws Exception {
		super(b, g, p, props);
	}

	@Override
	public EntityResult query(Hashtable cv, Vector av, int sesionId, Connection con) throws Exception {
		return super.query(cv, av, sesionId, con);
	}

	@Override
	public EntityResult insert(Hashtable av, int sesionId, Connection con) throws Exception {
		Number yearField = (Number) av.get("MAJ_DAY_YEAR");
		Number monthField = av.get("MAJ_DAY_MONTH") instanceof Number ? (Number) av.get("MAJ_DAY_MONTH") : Integer.valueOf((String) av.get("MAJ_DAY_MONTH"));
		Number dayField = (Number) av.get("MAJ_DAY_DAY");
		Date beginDate = (Date) av.get("MAJ_BEGINDATE");
		Date endDate = (Date) av.get("MAJ_ENDDATE");
		Number workTimeField = (Number) av.get("MAJ_WORK_TIME");

		// all fields are filled
		CheckingTools.failIf(ObjectTools.isIn(null, yearField, monthField, dayField, beginDate, endDate, workTimeField), "E_FIELDS_MANDATORY");
		// Check day
		int year = yearField.intValue();
		int month = monthField.intValue();
		int day = dayField.intValue();
		CheckingTools.failIf(year < 0, "E_YEAR_POSITIVE");
		CheckingTools.failIf(day < 0, "E_DAY_POSITIVE");
		Calendar calDay = DateTools.createCalendar(year, month - 1, day);
		CheckingTools.failIf(day != calDay.get(Calendar.DAY_OF_MONTH), "E_INVALID_DAY");

		int workingMinutes = workTimeField.intValue();
		CheckingTools.failIf(workingMinutes > (24 * 60), "E_INVALID_WORKING_TIME");
		// Check dates contains day
		Calendar beginCal = Calendar.getInstance();
		beginCal.setTime(beginDate);
		beginCal = DateTools.createCalendar(beginCal.get(Calendar.YEAR), beginCal.get(Calendar.MONTH), beginCal.get(Calendar.DAY_OF_MONTH) - 1, 23, 59, 59, 999);
		CheckingTools.failIf(!(calDay.getTime().after(beginCal.getTime()) && calDay.getTime().before(endDate)), "E_INVALID_DATE_RANGE_FOR_DAY");

		av.put("MAJ_DAY", String.format("%04d-%02d-%02d", year, month, day));
		EntityResult res = super.insert(av, sesionId, con);
		res.putAll(av);
		res.put("MAJ_DAY", String.format("%04d-%02d-%02d", year, month, day));
		return res;
	}

	@Override
	public EntityResult update(Hashtable atributosValoresA, Hashtable clavesValoresA, int sesionId, Connection con) throws Exception {
		throw new Exception("OP_NOT_ALLOWED");
	}

}