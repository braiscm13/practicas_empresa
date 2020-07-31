package com.opentach.server.labor.labor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.StretchType;
import com.ontimize.jee.common.tools.Chronometer;
import com.opentach.common.labor.util.IntervalDate;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.util.AbstractDelegate;

/**
 * The Class ServerInfractionAnalyzer.
 */
public class LaborTaskQueueExecutor extends AbstractDelegate {

	/** The Constant logger. */
	private static final Logger					logger			= LoggerFactory.getLogger(LaborTaskQueueExecutor.class);

	/** The Constant PRIORITY_LOW. */
	private static final int					PRIORITY_LOW	= 0;

	/** The executor. */
	private final PriorityThreadPoolExecutor	executor;

	/**
	 * Instantiates a new server infraction analyzer.
	 *
	 * @param locator
	 *            the locator
	 */
	protected LaborTaskQueueExecutor(IOpentachServerLocator locator) {
		super(locator);
		this.executor = PoolExecutors.newPriorityPoolExecutor("LaborTaskExecutor", 40, 10, TimeUnit.SECONDS);
	}

	public List<DriverSettings> analyzeDriverSettings(List<Object> driverList, String cif, final int sessionId) throws InterruptedException, ExecutionException {
		Chronometer chrono = new Chronometer().start();
		List<Future<DriverSettings>> submitList = new ArrayList<>();
		List<DriverSettings> res = new ArrayList<>();
		final List<Object> cleanDriverList = this.filterDrivers(driverList);
		for (Object driver : cleanDriverList) {
			Future<DriverSettings> submit = this.executor.submit(new DriverSettingsTask(cif, driver, this.getLocator(), LaborTaskQueueExecutor.PRIORITY_LOW));
			submitList.add(submit);
		}
		LaborTaskQueueExecutor.logger.debug("DS Prepare time {}", chrono.elapsedMs());
		for (Future<DriverSettings> submit : submitList) {
			DriverSettings driverSetting = submit.get();
			res.add(driverSetting);
			}
		LaborTaskQueueExecutor.logger.debug("DS Get tasks time {}", chrono.elapsedMs());
		return res;
	}

	/**
	 * Analyze.
	 *
	 * @param contract
	 *            the contract
	 * @param cardNumber
	 * @param driverList
	 *            the driver list
	 * @param groupDays
	 * @param beginDate
	 *            the begin date
	 * @param endDate
	 *            the end date
	 * @param queryPeriods
	 * @param av
	 *            the av
	 * @param sesionId
	 *            the session id
	 * @return the entity result
	 * @throws Exception
	 *             the exception
	 */
	public List<LaborResult> analyze(List<DriverSettings> driverList, IntervalDate queryInterval, boolean groupDays, final int sesionId) throws Exception {
		Chronometer chrono = new Chronometer().start();
		List<Future<LaborResult>> submitList = new ArrayList<>();
		List<LaborResult> res = new ArrayList<>();
		for (DriverSettings driver : driverList) {
			Future<LaborResult> submit = this.executor.submit(new LaborTask(driver, queryInterval, groupDays, this.getLocator(), LaborTaskQueueExecutor.PRIORITY_LOW));
			submitList.add(submit);
		}
		LaborTaskQueueExecutor.logger.debug("LR Prepare time {}", chrono.elapsedMs());
		for (Future<LaborResult> submit : submitList) {
			LaborResult laborResult = submit.get();
			res.add(laborResult);
		}
		LaborTaskQueueExecutor.logger.debug("LR Get tasks time {}", chrono.elapsedMs());
		return res;
	}

	/**
	 * Analyze status time.
	 *
	 * @param driverList
	 *            the driver list
	 * @param intervalDate
	 *            the interval date
	 * @param activityTypes
	 *            the activity types
	 * @param groupDays
	 *            the group days
	 * @param sessionId
	 *            the session id
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	public List<LaborResult> analyzeStatusTime(List<DriverSettings> driverList, IntervalDate intervalDate, List<StretchType> activityTypes, boolean groupDays, int sessionId)
			throws Exception {
		Chronometer chrono = new Chronometer().start();
		List<Future<LaborResult>> submitList = new ArrayList<>();
		List<LaborResult> res = new ArrayList<>();
		for (DriverSettings driver : driverList) {
			Future<LaborResult> submit = this.executor
					.submit(new LaborTaskStatusTime(driver, intervalDate, groupDays, activityTypes, this.getLocator(), LaborTaskQueueExecutor.PRIORITY_LOW));
			submitList.add(submit);
		}
		LaborTaskQueueExecutor.logger.debug("STLR Prepare time {}", chrono.elapsedMs());
		for (Future<LaborResult> submit : submitList) {
			LaborResult laborResult = submit.get();
			res.add(laborResult);
		}
		LaborTaskQueueExecutor.logger.debug("STLR Get tasks time {}", chrono.elapsedMs());
		return res;
	}

	private List<Object> filterDrivers(List<Object> driverList) {
		HashSet<Object> set = new HashSet<Object>();
		set.addAll(driverList);
		return new ArrayList<Object>(set);
	}

}
