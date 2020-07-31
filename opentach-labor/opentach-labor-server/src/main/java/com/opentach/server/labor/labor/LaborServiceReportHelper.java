package com.opentach.server.labor.labor;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.common.labor.util.IntervalDate;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;
import com.utilmize.tools.exception.UException;
import com.utilmize.tools.exception.URuntimeException;
import com.utilmize.tools.report.JRReportUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * The Class LaborServiceReportHelper.
 *
 * @author joaquin.romero
 */
public class LaborServiceReportHelper extends AbstractDelegate {

	/**
	 * Instantiates a new labor service report helper.
	 *
	 * @param locator
	 *            the locator
	 */
	public LaborServiceReportHelper(IOpentachServerLocator locator) {
		super(locator);
	}

	/**
	 * Update limits.
	 *
	 * @param limits
	 *            the limits
	 * @param partialLimit
	 *            the partial limit
	 */
	public void updateLimits(boolean[] limits, boolean[] partialLimit) {
		for (int i = 0; i < limits.length; i++) {
			limits[i] = limits[i] & partialLimit[i];
		}

	}

	/** The Constant DAY. */
	public static final int	DAY		= 0;

	/** The Constant WEEK. */
	public static final int	WEEK	= 1;

	/** The Constant BIWEEK. */
	public static final int	BIWEEK	= 2;
	
	/** The Constant FOUR_WEEK. */
	public static final int	FOUR_WEEK	= 3;

	/** The Constant MONTH. */
	public static final int	MONTH	= 4;
	
	/** The Constant FOUR_MONTH. */
	public static final int	FOUR_MONTH	= 5;

	/** The Constant ANNUAL. */
	public static final int	ANNUAL	= 6;
	
	/**
	 * Choose labor report limit.
	 *
	 * @param contract
	 *            the contract
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the boolean[]
	 * @throws Exception
	 *             the exception
	 */
	public boolean[] chooseLaborReportLimit(DriverContract contract, IntervalDate queryInterval) throws Exception {
		boolean[] options = new boolean[LaborServiceReportHelper.ANNUAL + 1];
		Arrays.fill(options, true);
		Calendar calFrom = DateTools.createCalendar(queryInterval.getFrom());
		Calendar calTo = DateTools.createCalendar(queryInterval.getTo());
		int fromDay = calFrom.get(Calendar.DAY_OF_MONTH);
		int fromMonth = calFrom.get(Calendar.MONTH);
		int fromWeekDay = calFrom.get(Calendar.DAY_OF_WEEK);
		int toDay = calTo.get(Calendar.DAY_OF_MONTH);
		int toMonth = calTo.get(Calendar.MONTH);
		int toWeekDay = calTo.get(Calendar.DAY_OF_WEEK);

		if (!((fromDay == 1) && (fromMonth == Calendar.JANUARY) && (toDay == 31) && (toMonth == Calendar.DECEMBER))) {
			// no se puede calcular el anual
			options[LaborServiceReportHelper.ANNUAL] = false;
		}
		if (!((fromDay == 1) && (toDay == calTo.getActualMaximum(Calendar.DAY_OF_MONTH)))) {
			// no se puede calcular el mensual
			options[LaborServiceReportHelper.MONTH] = false;
			options[LaborServiceReportHelper.FOUR_MONTH] = false;
		}
		if (!((fromWeekDay == Calendar.MONDAY) && (toWeekDay == Calendar.SUNDAY))) {
			// no se puede calcular el semanal/bisemanal
			options[LaborServiceReportHelper.WEEK] = false;
			options[LaborServiceReportHelper.BIWEEK] = false;
			options[LaborServiceReportHelper.FOUR_WEEK] = false;
		}
		for (int i = LaborServiceReportHelper.DAY; i <= LaborServiceReportHelper.ANNUAL; i++) {
			switch (contract.getContractType()) {
				case PARTIAL_TIME:
					switch (i) {
						case DAY:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingDailyTimeForPartialContract());
							break;
						case WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingWeeklyTimeForPartialContract());
							break;
						case BIWEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingBiweeklyTimeForPartialContract());
							break;
						case FOUR_WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingFourweeklyTimeForPartialContract());
							break;
						case MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingMonthlyTimeForPartialContract());
							break;
						case FOUR_MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorking4MonthlyTimeForPartialContract());
							break;
						case ANNUAL:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingAnnualTimeForPartialContract());
							break;
					}
					break;
				case FULL_TIME:
					switch (i) {
						case DAY:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getDailyTimeLimit());
							break;
						case WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getWeeklyTimeLimit());
							break;
						case BIWEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getBiweeklyTimeLimit());
							break;
						case FOUR_WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getFourweeklyTimeLimit());
							break;
						case MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getMonthlyTimeLimit());
							break;
						case FOUR_MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getFourMonthlyTimeLimit());
							break;
						case ANNUAL:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getAnnualTimeLimit());
							break;
					}
					break;
			}
		}

		return options;
	}

	private boolean isLimitEstablished(Number limit) {
		return (limit != null) && (limit.intValue() != 0);
	}

	/**
	 * Gets the higher limit.
	 *
	 * @param options
	 *            the options
	 * @return the higher limit
	 */
	public String getHigherLimit(boolean[] options) {
		// ahora nos quedamos con el mas alto
		return this.indexToString(this.getHigherIndex(options));
	}

	public String indexToString(int i) {
		switch (i) {
			case DAY:
				return "DAILY";
			case WEEK:
				return "WEEKLY";
			case BIWEEK:
				return "BIWEEKLY";
			case FOUR_WEEK:
				return "FOUR_WEEKLY";
			case MONTH:
				return "MONTHLY";
			case FOUR_MONTH:
				return "FOUR_MONTHLY";
			case ANNUAL:
				return "ANNUAL";
		}
		return null;

	}

	public int getHigherIndex(boolean[] options) {
		// ahora nos quedamos con el mas alto
		for (int i = LaborServiceReportHelper.ANNUAL; i >= LaborServiceReportHelper.DAY; i--) {
			if (options[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Creates the pdf report.
	 *
	 * @param reportPath
	 *            the report path
	 * @param driverIds
	 *            the driver ids
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param parameters
	 *            the parameters
	 * @param con
	 *            the con
	 * @param sessionId
	 *            the session id
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	public BytesBlock createPdfReport(String reportPath, List<Object> driverIds, String companyCif, IntervalDate queryInterval, Map<String, Object> parameters, Connection con,
			int sessionId) throws Exception {
		// obtenemos el tipo de convenio y las caracteristicas laborales del trabajador
		List<LaborResult> laborResults = this.getService(LaborService.class).analyze(driverIds, companyCif, queryInterval, true, sessionId);
		List<JasperPrint> concat = new ArrayList<>(laborResults.size());
		for (LaborResult laborResult : laborResults) {
			JasperPrint jp = this.buildReport(reportPath, laborResult, parameters);
			concat.add(jp);
		}
		return this.toPdfBytesBlock(concat);
	}

	/**
	 * Builds the report.
	 *
	 * @param reportPath
	 *            the report path
	 * @param laborResult
	 *            the labor result
	 * @param extraParameters
	 *            the extra parameters
	 * @return the jasper print
	 * @throws JRException
	 *             the JR exception
	 * @throws Exception
	 *             the exception
	 */
	public JasperPrint buildReport(String reportPath, LaborResult laborResult, Map<String, Object> extraParameters) throws JRException, Exception {
		DriverSettings driverSettings = laborResult.getDriverSettings();
		List<DailyWorkRecord> dailyWorkRecord = laborResult.getDailyRecords();
		Collections.sort(dailyWorkRecord);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("DriverSettings", driverSettings);
		parameters.putAll(extraParameters);
		return JasperFillManager.fillReport(JRReportUtil.getJasperReport(reportPath), parameters, new JRBeanCollectionDataSource(dailyWorkRecord));
	}

	/**
	 * To pdf bytes block.
	 *
	 * @param concat
	 *            the concat
	 * @return the bytes block
	 * @throws Exception
	 *             the exception
	 */
	public BytesBlock toPdfBytesBlock(List<JasperPrint> concat) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRReportUtil.exportToPDF(concat, baos);
		baos.close();
		return new BytesBlock(baos.toByteArray());
	}

	/**
	 * Save labor report into warehouse.
	 *
	 * @param jp
	 *            the jp
	 * @param driverId
	 *            the driver id
	 * @param companyCif
	 *            the company cif
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param sessionId
	 *            the session id
	 * @param con
	 *            the con
	 * @throws Exception
	 *             the exception
	 */
	public void saveLaborReportIntoWarehouse(JasperPrint jp, Object driverId, String companyCif, IntervalDate queryInterval, int sessionId, Connection con) throws Exception {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRReportUtil.exportToPDF(jp, baos);
		TransactionalEntity entity = this.getEntity("ELaborReportWarehouse");
		Hashtable<Object, Object> attributesValues = EntityResultTools.keysvalues("IDCONDUCTOR", driverId, //
				"CIF", companyCif, //
				"REP_CONTENT", baos.toByteArray(), //
				"REP_BEGINDATE", queryInterval.getFrom(), //
				"REP_ENDDATE", queryInterval.getTo()//
				);
		entity.insert(attributesValues, sessionId, con);

	}

	public Pair<IntervalDate, String> combineContracts(DriverSettings driverSetting, IntervalDate queryInterval) throws UException {
		boolean[] limits = new boolean[LaborServiceReportHelper.ANNUAL + 1];
		Arrays.fill(limits, true);
		Date beginContractDate = null;
		Date endContractDate = DateTools.createDate(1969, 1, 1);
		for (DriverContract contract : driverSetting.getDriverContracts()) {
			if ((beginContractDate == null) || beginContractDate.after(contract.getFrom())) {
				beginContractDate = contract.getFrom();
			}
			// null es contrato indefinido
			if ((contract.getTo() == null) || ((endContractDate != null) && endContractDate.before(contract.getTo()))) {
				endContractDate = contract.getTo();
			}
			// obtenemos la informacion comun de los contrato
			boolean[] partialLimit = this.contractToLimitArray(contract);
			this.updateLimits(limits, partialLimit);
		}

		IntervalDate contractInterval = new IntervalDate(beginContractDate, endContractDate == null ? DateTools.createDate(3000, 1, 1) : endContractDate);

		Date availableFrom = queryInterval.getFrom().before(beginContractDate) ? beginContractDate : queryInterval.getFrom();
		Date availableTo = endContractDate == null ? queryInterval.getTo() : (queryInterval.getTo().before(endContractDate) ? queryInterval.getTo() : endContractDate);
		IntervalDate availableQueryInterval = new IntervalDate(availableFrom, availableTo);

		if (availableQueryInterval.computeNumberOfDays() < 200) {
			limits[LaborServiceReportHelper.ANNUAL] = false;
		}
		try {
			return this.adjustToAnnual(limits, contractInterval, availableQueryInterval);
		} catch (Exception error) {
			SimpleDateFormat sdf = new SimpleDateFormat("E dd/MM/yyyy");
			throw new UException("E_NO_LIMIT_FOUND",
					new Object[] { driverSetting.getDriverId(), sdf.format(availableQueryInterval.getFrom()), sdf.format(availableQueryInterval.getTo()) }, error);
		}
	}


	private Pair<IntervalDate, String> adjustToAnnual(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.ANNUAL]) {
			return this.adjustToFourMonth(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_YEAR, from.getMinimum(Calendar.DAY_OF_YEAR));
		to.set(Calendar.DAY_OF_YEAR, to.getMaximum(Calendar.DAY_OF_YEAR));

		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			return this.adjustToFourMonth(limits, contractInterval, availableQueryInterval);
		}

		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.ANNUAL));
	}
	
	private Pair<IntervalDate, String> adjustToFourMonth(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
			if (!limits[LaborServiceReportHelper.FOUR_MONTH]) {
			return this.adjustToMonth(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_MONTH, from.getMinimum(Calendar.DAY_OF_MONTH));
		to.set(Calendar.DAY_OF_MONTH, to.getMaximum(Calendar.DAY_OF_MONTH + 3));
		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.MONTH)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.MONTH))) {
				from.add(Calendar.MONTH, 1);
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
				return this.adjustToMonth(limits, contractInterval, availableQueryInterval);
			}
		}
		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.FOUR_MONTH));
	}

	private Pair<IntervalDate, String> adjustToMonth(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.MONTH]) {
			return this.adjustToFourWeek(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_MONTH, from.getMinimum(Calendar.DAY_OF_MONTH));
		to.set(Calendar.DAY_OF_MONTH, to.getMaximum(Calendar.DAY_OF_MONTH));

		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.MONTH)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.MONTH))) {
				from.add(Calendar.MONTH, 1);
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
				return this.adjustToFourWeek(limits, contractInterval, availableQueryInterval);
			}
		}
		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.MONTH));
	}

	private Pair<IntervalDate, String> adjustToFourWeek(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.FOUR_WEEK]) {
			return this.adjustToBiWeek(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		boolean added = false;
		if (((to.getTimeInMillis() - from.getTimeInMillis()) / DateTools.MILLISECONDS_PER_DAY) < 28) {
			added = true;
			to.add(Calendar.DAY_OF_YEAR, 7);
		}

		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.WEEK_OF_YEAR)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.WEEK_OF_YEAR))) {
				from.add(Calendar.WEEK_OF_YEAR, 1);
				if (added) {
					to.add(Calendar.WEEK_OF_YEAR, 1);
				}
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
				return this.adjustToBiWeek(limits, contractInterval, availableQueryInterval);
			}
		}
		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.FOUR_WEEK));
	}
	
	
	private Pair<IntervalDate, String> adjustToBiWeek(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.BIWEEK]) {
			return this.adjustToWeek(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		boolean added = false;
		if (((to.getTimeInMillis() - from.getTimeInMillis()) / DateTools.MILLISECONDS_PER_DAY) < 14) {
			added = true;
			to.add(Calendar.DAY_OF_YEAR, 7);
		}

		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.WEEK_OF_YEAR)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.WEEK_OF_YEAR))) {
				from.add(Calendar.WEEK_OF_YEAR, 1);
				if (added) {
					to.add(Calendar.WEEK_OF_YEAR, 1);
				}
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
				return this.adjustToWeek(limits, contractInterval, availableQueryInterval);
			}
		}
		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.BIWEEK));
	}

	private Pair<IntervalDate, String> adjustToWeek(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.WEEK]) {
			return this.adjustToDay(limits, contractInterval, availableQueryInterval);
		}
		Calendar from = Calendar.getInstance();
		from.setTime(availableQueryInterval.getFrom());
		Calendar to = Calendar.getInstance();
		to.setTime(availableQueryInterval.getTo());
		from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		to.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.WEEK_OF_YEAR)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.WEEK_OF_YEAR))) {
				from.add(Calendar.WEEK_OF_YEAR, 1);
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!(from.get(Calendar.YEAR) + "_" + from.get(Calendar.WEEK_OF_YEAR)).equals(to.get(Calendar.YEAR) + "_" + to.get(Calendar.WEEK_OF_YEAR))) {
				to.add(Calendar.WEEK_OF_YEAR, -1);
				availableQueryInterval = new IntervalDate(from.getTime(), to.getTime());
			}
			if (!contractInterval.isInto(from.getTime()) || !contractInterval.isInto(to.getTime())) {
				return this.adjustToDay(limits, contractInterval, availableQueryInterval);
			}
		}
		return new Pair<>(new IntervalDate(from.getTime(), to.getTime()), this.indexToString(LaborServiceReportHelper.WEEK));
	}

	private Pair<IntervalDate, String> adjustToDay(boolean[] limits, IntervalDate contractInterval, IntervalDate availableQueryInterval) {
		if (!limits[LaborServiceReportHelper.DAY]) {
			throw new URuntimeException("E_CAN_NOT_COMPUTE_INTERVAL");
		}
		Date fromQuery = availableQueryInterval.getFrom();
		Date toQuery = availableQueryInterval.getTo();
		if (!contractInterval.isInto(fromQuery) && fromQuery.before(contractInterval.getFrom())) {
			fromQuery = contractInterval.getFrom();
		}
		if (!contractInterval.isInto(toQuery) && toQuery.after(contractInterval.getTo())) {
			toQuery = contractInterval.getTo();
		}
		IntervalDate res = new IntervalDate(fromQuery, toQuery);
		if (!contractInterval.hasInto(res)) {
			throw new URuntimeException("E_CAN_NOT_COMPUTE_INTERVAL");
		}
		return new Pair<>(res, this.indexToString(LaborServiceReportHelper.DAY));
	}

	private boolean[] contractToLimitArray(DriverContract contract) {
		boolean[] options = new boolean[LaborServiceReportHelper.ANNUAL + 1];
		Arrays.fill(options, true);
		for (int i = LaborServiceReportHelper.DAY; i <= LaborServiceReportHelper.ANNUAL; i++) {
			switch (contract.getContractType()) {
				case PARTIAL_TIME:
					switch (i) {
						case DAY:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingDailyTimeForPartialContract());
							break;
						case WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingWeeklyTimeForPartialContract());
							break;
						case BIWEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingBiweeklyTimeForPartialContract());
							break;
						case FOUR_WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingFourweeklyTimeForPartialContract());
							break;
						case MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingMonthlyTimeForPartialContract());
							break;
						case FOUR_MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorking4MonthlyTimeForPartialContract());
							break;
						case ANNUAL:
							options[i] = options[i] & this.isLimitEstablished(contract.getWorkingAnnualTimeForPartialContract());
							break;
					}
					break;
				case FULL_TIME:
					switch (i) {
						case DAY:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getDailyTimeLimit());
							break;
						case WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getWeeklyTimeLimit());
							break;
						case BIWEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getBiweeklyTimeLimit());
							break;
						case FOUR_WEEK:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getFourweeklyTimeLimit());
							break;
						case MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getMonthlyTimeLimit());
							break;
						case FOUR_MONTH:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getFourMonthlyTimeLimit());
							break;
						case ANNUAL:
							options[i] = options[i] & this.isLimitEstablished(contract.getLaborAgreement().getAnnualTimeLimit());
							break;
					}
					break;
			}
		}
		return options;
	}
}
