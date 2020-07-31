package com.opentach.server.labor.labor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor.Priorizable;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.ActivitiesAnalyzerInServerTask;
import com.opentach.server.labor.labor.DriverContract.ContractType;
import com.opentach.server.labor.labor.agreement.LaborAgreementsService;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

public class DriverSettingsTask extends AbstractDelegate implements Callable<DriverSettings>, Priorizable {

	private static final Logger				logger	= LoggerFactory.getLogger(ActivitiesAnalyzerInServerTask.class);

	/** The priority. */
	private final String					cif;
	private final Object					driver;
	private final IOpentachServerLocator	locator;
	private final int						priority;

	public DriverSettingsTask(String cif, Object driver, IOpentachServerLocator locator, int priority) {
		super(locator);
		this.cif = cif;
		this.driver = driver;
		this.priority = priority;
		this.locator = locator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.opentach.server.util.concurrent.PriorityExecutor.Priorizable#getPriority()
	 */
	@Override
	public int getPriority() {
		return this.priority;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public DriverSettings call() throws Exception {
		return new OntimizeConnectionTemplate<DriverSettings>() {

			@Override
			protected DriverSettings doTask(Connection con) throws UException, SQLException {
				return DriverSettingsTask.this.analize(con);
			}
		}.execute(this.locator.getConnectionManager(), true);
	}

	protected DriverSettings analize(Connection con) throws UException, SQLException {
		try {
			return this.getDriverSettings(this.driver, this.cif, con);
		} catch (SQLException | UException err) {
			throw err;
		} catch (Exception err) {
			throw new UException(err.getMessage(), err);
		}
	}

	/**
	 * Gets the driver settings.
	 *
	 * @param driverId
	 *            the driver id
	 * @param companyCif
	 *            the company cif
	 * @param con
	 *            the con
	 * @return the driver settings
	 * @throws Exception
	 *             the exception
	 */
	private DriverSettings getDriverSettings(Object driverId, String companyCif, Connection con) throws Exception {
		TransactionalEntity eDriver = this.getEntity("EConductoresEmp");
		Hashtable<Object, Object> filter = EntityResultTools.keysvalues("IDCONDUCTOR", driverId, "CIF", companyCif);
		Vector<String> av = EntityResultTools.attributes("DNI", "NOMBRE", "APELLIDOS", "NOMBRE_EMPRESA", "NAF", "CCC");
		EntityResult er = eDriver.query(filter, av, this.getEntityPrivilegedId(eDriver), con);
		CheckingTools.checkValidEntityResult(er, "ErrorQueryingDriver", true, true, (Object[]) null);
		Hashtable record = er.getRecordValues(0);
		DriverSettings settings = new DriverSettings();
		settings.setCompanyCif(companyCif);
		settings.setDriverId(driverId);
		settings.setCompanyName((String) record.get("NOMBRE_EMPRESA"));
		settings.setCompanyCcc((String) record.get("CCC"));
		settings.setDriverDni((String) record.get("DNI"));
		settings.setDriverName((String) record.get("NOMBRE"));
		settings.setDriverSurname((String) record.get("APELLIDOS"));
		settings.setDriverNaf((String) record.get("NAF"));

		eDriver = this.getEntity("EConductorCont");
		av = EntityResultTools.attributes("CG_CONTRATO");
		er = eDriver.query(filter, av, this.getEntityPrivilegedId(eDriver), con);
		CheckingTools.checkValidEntityResult(er, "ErrorQueryingDriver", true, true, (Object[]) null);
		record = er.getRecordValues(0);
		settings.setOpentachContract(record.get("CG_CONTRATO"));

		TransactionalEntity eDriverContracts = this.getEntity("EDriverContracts");
		av = EntityResultTools.attributes("DRC_ID", "DRC_TYPE", "AGR_ID", "DRC_IRREGULAR_JOURNAL", "DRC_DATE_FROM", "DRC_DATE_TO",
				"DRC_PARTIAL_DAILY_MINUTES", "DRC_PARTIAL_WEEKLY_MINUTES", "DRC_PARTIAL_BIWEEKLY_MINUTES", "DRC_PARTIAL_4WEEKLY_MINUTES", "DRC_PARTIAL_MONTHLY_MINUTES", "DRC_PARTIAL_4MONTHLY_MINUTES", "DRC_PARTIAL_ANNUAL_MINUTES",
				"DRC_COMPL_DAILY_MINUTES", "DRC_COMPL_WEEKLY_MINUTES", "DRC_COMPL_BIWEEKLY_MINUTES", "DRC_PARTIAL_4WEEKLY_MINUTES","DRC_COMPL_MONTHLY_MINUTES", "DRC_COMPL_4MONTHLY_MINUTES", "DRC_COMPL_ANNUAL_MINUTES");
		er = eDriverContracts.query(filter, av, this.getEntityPrivilegedId(eDriverContracts), con);
		List<DriverContract> driverContracts = new ArrayList<>();
		int nrecord = er.calculateRecordNumber();
		for (int i = 0; i < nrecord; i++) {
			record = er.getRecordValues(i);
			Object id = record.get("DRC_ID");
			ContractType contractType = ContractType.fromString((String) record.get("DRC_TYPE"));
			Object laborAgreementId = record.get("AGR_ID");
			if (laborAgreementId != null && !laborAgreementId.equals(-1)) {
				boolean irregularJournal = "S".equals(record.get("DRC_IRREGULAR_JOURNAL"));
				Date from = (Date) record.get("DRC_DATE_FROM");
				Date to = (Date) record.get("DRC_DATE_TO");
				if (to != null) {
					to = DateTools.lastMilisecond(to);
				}
				DriverContract contract = new DriverContract();
				contract.setIdContract(id);
				contract.setWorkingDailyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_DAILY_MINUTES"));
				contract.setWorkingWeeklyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_WEEKLY_MINUTES"));
				contract.setWorkingBiweeklyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_BIWEEKLY_MINUTES"));
				contract.setWorkingFourweeklyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_4WEEKLY_MINUTES"));
				contract.setWorkingMonthlyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_MONTHLY_MINUTES"));
				contract.setWorkingFourMonthlyMinutesForPartialContract((Number) record.get("DRC_PARTIAL_4MONTHLY_MINUTES"));
				contract.setWorkingAnnualMinutesForPartialContract((Number) record.get("DRC_PARTIAL_ANNUAL_MINUTES"));
				contract.setComplementaryDailyMinutesForPartialContract((Number) record.get("DRC_COMPL_DAILY_MINUTES"));
				contract.setComplementaryWeeklyMinutesForPartialContract((Number) record.get("DRC_COMPL_WEEKLY_MINUTES"));
				contract.setComplementaryBiweeklyMinutesForPartialContract((Number) record.get("DRC_COMPL_BIWEEKLY_MINUTES"));
				contract.setComplementaryFourweeklyMinutesForPartialContract((Number) record.get("DRC_COMPL_4WEEKLY_MINUTES"));
				contract.setComplementaryMonthlyMinutesForPartialContract((Number) record.get("DRC_COMPL_MONTHLY_MINUTES"));
				contract.setComplementaryFourMonthlyMinutesForPartialContract((Number) record.get("DRC_COMPL_4MONTHLY_MINUTES"));
				contract.setComplementaryAnnualMinutesForPartialContract((Number) record.get("DRC_COMPL_ANNUAL_MINUTES"));
				contract.setFrom(from);
				contract.setTo(to);
				contract.setAllowIrregularJournal(irregularJournal);
				contract.setContractType(contractType);
				contract.setLaborAgreement(this.getService(LaborAgreementsService.class).getAgreement(laborAgreementId));
				driverContracts.add(contract);
			}
		}
		settings.setDriverContracts(driverContracts);
		return settings;
	}

}