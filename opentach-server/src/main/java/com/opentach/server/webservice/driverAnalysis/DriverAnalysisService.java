package com.opentach.server.webservice.driverAnalysis;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.Analyzer.AnalyzerEngine;
import com.imatia.tacho.infraction.AnalyzerUtils;
import com.imatia.tacho.infraction.Infraction;
import com.imatia.tacho.infraction.Period;
import com.imatia.tacho.infraction.Period.PeriodClass;
import com.imatia.tacho.infraction.Period.PeriodType;
import com.imatia.tacho.infraction.RestClass;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TableEntity;
import com.ontimize.gui.SearchValue;
import com.ontimize.gui.ServerLauncherServlet;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.DateTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.jee.common.tools.Pair;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.user.IUserData;
import com.opentach.server.OpentachServerLocator;
import com.opentach.server.activities.ActivitiesAnalyzerInServer;
import com.opentach.server.activities.InfractionAnalyzerInServer;
import com.opentach.server.webservice.driverAnalysis.beans.ActivityList;
import com.opentach.server.webservice.driverAnalysis.beans.DriverActivityRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverActivityResponse;
import com.opentach.server.webservice.driverAnalysis.beans.DriverAnalysisRequest;
import com.opentach.server.webservice.driverAnalysis.beans.DriverAnalysisResponse;
import com.opentach.server.webservice.driverAnalysis.beans.InfractionList;
import com.opentach.server.webservice.driverAnalysis.beans.WorkPeriod;
import com.opentach.server.webservice.driverAnalysis.beans.WorkPeriodList;
import com.opentach.server.webservice.utils.OpentachWSException;
import com.utilmize.server.tools.sqltemplate.OntimizeConnectionTemplate;
import com.utilmize.tools.exception.UException;

@WebService(endpointInterface = "com.opentach.server.webservice.driverAnalysis.IDriverAnalisysService", serviceName = "driverAnalysis")
public class DriverAnalysisService implements IDriverAnalisysService {

	private static final Logger	logger								= LoggerFactory.getLogger(DriverAnalysisService.class);

	private static final String	ERROR_UNKNOWED						= "UNKNOWED_ERROR";
	private static final String	ERROR_REQUIRED_FILTERS				= "REQUIRED_FILTERS";
	private static final String	ERROR_USER_INVALID_CREDENTIALS		= "INVALID_CREDENTIALS";
	private static final String	ERROR_USER_REQUIRED_USER_AND_PASS	= "INVALID_CREDENTIALS-REQUIRED_USER_AND_PASS";
	private static final String	ERROR_USER_WITHOUT_COMPANIES		= "INVALID_CREDENTIALS-USER_WITHOUT_COMPANIES_ASSOCIATED";
	private static final String	ERROR_DRIVER_REQUIRED_DRIVER		= "INVALID_DRIVER_CREDENTIALS-REQUIRED_DRIVER";
	private static final String	ERROR_DRIVER_INVALID_CREDENTIALS	= "INVALID_DRIVER_CREDENTIALS";
	private static final String	ERROR_DATES_REQUIRED_PERIOD			= "INVALID_PERIOD-REQUIRED_BEGIND_AND_END_DATE";
	private static final String	ERROR_DATES_INVALID_PERIOD			= "INVALID_PERIOD-NOT_VALID_TIME_PERIOD";
	private static final String	ERROR_ANALIZING						= "ERROR_ANALIZING";
	private static final String	ERROR_RESULT_EMPTY_ANALYSIS			= "EMPTY_ANALYSIS_RESULT";
	private static final String	ERROR_RESULT_PROCESSING				= "ERROR_PROCESSING_RESULT";

	private static final int	CODE_UNKNOWED_ERROR_CODE			= -1;
	private static final int	CODE_VALIDATING_INPUTS				= -2;
	private static final int	CODE_VALIDATING_INPUTS_USER			= -3;
	private static final int	CODE_VALIDATING_INPUTS_DRIVER		= -4;
	private static final int	CODE_VALIDATING_INPUTS_DATES		= -5;
	private static final int	CODE_ANALIZING						= -6;
	private static final int	CODE_POST_ANALIZING					= -7;

	@Resource
	private WebServiceContext	context;

	/***************************************************************************************************************/
	/************************************************* ANALYZE *****************************************************/
	/***************************************************************************************************************/

	@Override
	public DriverAnalysisResponse analize(final DriverAnalysisRequest filters) throws OpentachWSException {
		ServletContext servletContext = (ServletContext) this.context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		final OpentachServerLocator locator = (OpentachServerLocator) servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
		try {
			if (filters == null) {
				throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS, DriverAnalysisService.ERROR_REQUIRED_FILTERS);
			}
			return new OntimizeConnectionTemplate<DriverAnalysisResponse>() {
				@Override
				protected DriverAnalysisResponse doTask(Connection con) throws UException {
					try {
						return DriverAnalysisService.this.analize(filters, locator, con);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(locator, true);
		} catch (OpentachWSException ex) {
			DriverAnalysisService.logger.error(null, ex);
			throw ex;
		} catch (Throwable error) {
			DriverAnalysisService.logger.error(null, error);
			throw new OpentachWSException(DriverAnalysisService.CODE_UNKNOWED_ERROR_CODE, DriverAnalysisService.ERROR_UNKNOWED + " " + error.getMessage());
		}
	}

	protected DriverAnalysisResponse analize(DriverAnalysisRequest filters, OpentachServerLocator locator, Connection con) throws Exception {
		int sessionId = -1;
		try {
			// Step 1 : Validate user credentials ans start session
			sessionId = this.validateUserAndStartSession(filters.getUser(), filters.getPass(), locator);

			// Step 2: Validate driver
			Pair<String, String> pair = this.validateDriver(filters.getDriver(), locator, con, sessionId);
			String numReq = pair.getFirst();
			String cif = pair.getSecond();

			// Step 3 : Validate period
			this.validatePeriod(filters.getInitDate(), filters.getEndDate());

			// Step 4 : Analize with input data
			Pair<AnalysisResult, List<Activity>> result = this.analize(filters, numReq, cif, locator, con, sessionId);

			// Step 6 : Convert to response
			return DriverAnalysisService.this.convertResultToAnalysisResponse(result);
		} finally {
			// Step 5: End session
			if (sessionId >= 0) {
				locator.endSession(sessionId);
			}
		}
	}

	/**
	 * Authenticate user/pass in server to operate from this moment under this sessionId.
	 *
	 * @param user
	 * @param pass
	 * @return
	 * @throws OpentachWSException
	 */
	private int validateUserAndStartSession(String user, String pass, OpentachServerLocator locator) throws OpentachWSException {
		if ((user == null) || (pass == null)) {
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_USER, DriverAnalysisService.ERROR_USER_REQUIRED_USER_AND_PASS);
		}
		try {
			int startSession = locator.startSessionWS(user, pass, null);
			// if (!IUserData.NIVEL_TDI.equals(locator.getUserData(startSession).getLevel())) {
			// throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_USER, DriverAnalysisService.ERROR_USER_INVALID_CREDENTIALS);
			// }
			return startSession;
		} catch (Exception e) {
			DriverAnalysisService.logger.error("INVALD_AUTHENTICATION", e);
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_USER, DriverAnalysisService.ERROR_USER_INVALID_CREDENTIALS);
		}
	}

	/**
	 * Check from input driver identifier and get NUMREQ or CG_CONTRATO
	 *
	 * @param driver
	 * @param sessionId
	 * @return
	 * @throws OpentachWSException
	 */
	protected Pair<String, String> validateDriver(String driver, OpentachServerLocator locator, Connection con, int sessionId) throws OpentachWSException {
		if (driver == null) {
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_DRIVER, DriverAnalysisService.ERROR_DRIVER_REQUIRED_DRIVER);
		}
		try {
			IUserData userData = locator.getUserData(sessionId);
			List<String> cifs = userData.getCompaniesList();
			if ((cifs == null) || cifs.isEmpty()) {
				throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_USER, DriverAnalysisService.ERROR_USER_WITHOUT_COMPANIES);
			}
			// Try to localice the contract that match with this driver, this companies and is active
			TableEntity entity = (TableEntity) locator.getEntityReferenceFromServer("EConductorCont");
			Hashtable<Object, Object> kv = EntityResultTools.keysvalues("IDCONDUCTOR", driver, "CIF", //
					new SearchValue(SearchValue.IN, new Vector<String>(cifs)), //
					"F_BAJA", new SearchValue(SearchValue.NULL, null));
			EntityResult erDrivers = entity.query(kv, EntityResultTools.attributes(OpentachFieldNames.CG_CONTRATO_FIELD, OpentachFieldNames.CIF_FIELD), sessionId, con);
			CheckingTools.checkValidEntityResult(erDrivers, "INVALID_DRIVER_CREDENTIALS-DRIVER_NOT_FOUND", true, true, (Object[]) null);
			String contractId = (String) erDrivers.getRecordValues(0).get(OpentachFieldNames.CG_CONTRATO_FIELD);
			String cif = (String) erDrivers.getRecordValues(0).get(OpentachFieldNames.CIF_FIELD);
			return new Pair<>(contractId, cif);
		} catch (Exception e) {
			DriverAnalysisService.logger.error("INVALD_DRIVER", e);
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_DRIVER, DriverAnalysisService.ERROR_DRIVER_INVALID_CREDENTIALS);
		}
	}

	/**
	 * Check input date filters
	 *
	 * @param initDate
	 * @param endDate
	 * @throws OpentachWSException
	 */
	protected void validatePeriod(Date initDate, Date endDate) throws OpentachWSException {
		if ((initDate == null) || (endDate == null)) {
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_DATES, DriverAnalysisService.ERROR_DATES_REQUIRED_PERIOD);
		}
		if (initDate.after(endDate)) {
			throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS_DATES, DriverAnalysisService.ERROR_DATES_INVALID_PERIOD);
		}
	}

	/**
	 * Analize with inputs
	 *
	 * @param filters
	 * @param locator
	 * @param con
	 * @param sessionId
	 * @param numReq
	 * @param result
	 * @return
	 * @throws OpentachWSException
	 */
	private Pair<AnalysisResult, List<Activity>> analize(DriverAnalysisRequest filters, String numReq, String cif, OpentachServerLocator locator, Connection con, int sessionId)
			throws OpentachWSException {
		try {
			InfractionAnalyzerInServer analyzer = InfractionAnalyzerInServer.getInstance(locator);
			return analyzer.analyzeFullInfo(con, numReq, cif, filters.getDriver(), filters.getInitDate(), filters.getEndDate(), AnalyzerEngine.DEFAULT, sessionId);
		} catch (Exception ex) {
			DriverAnalysisService.logger.error(null, ex);
			throw new OpentachWSException(DriverAnalysisService.CODE_ANALIZING, DriverAnalysisService.ERROR_ANALIZING);
		}
	}

	/**
	 * Process analysis results and format to WS output
	 *
	 * @param result
	 * @param resWorkPeriods
	 * @return
	 * @throws OpentachWSException
	 */
	protected DriverAnalysisResponse convertResultToAnalysisResponse(Pair<AnalysisResult, List<Activity>> pair) throws OpentachWSException {
		AnalysisResult result = pair.getFirst();
		List<Activity> activities = pair.getSecond();
		if ((result == null) || this.isEmpty(result) || activities.isEmpty()) {
			throw new OpentachWSException(DriverAnalysisService.CODE_POST_ANALIZING, DriverAnalysisService.ERROR_RESULT_EMPTY_ANALYSIS);
		}

		try {
			DriverAnalysisResponse response = new DriverAnalysisResponse();

			response.setInfractions(this.extractInfractions(result.getInfractions()));
			response.setDailyWorkPeriods(this.processWorkPeriods(activities, result.getPeriods().get(PeriodClass.PDD)));
			response.setWeeklyWorkPeriods(this.processWorkPeriods(activities, result.getPeriods().get(PeriodClass.PDS)));
			response.setBiweeklyWorkPeriods(this.processWorkPeriods(activities, result.getPeriods().get(PeriodClass.PCBS)));
			List<Period> mondayToSundayPeriods = this.generateMondayToSundayPeriods(activities.get(0).getBeginDate(), activities.get(activities.size() - 1).getEndDate());
			response.setMondayToSundayPeriods(this.processWorkPeriods(activities, mondayToSundayPeriods));
			return response;
		} catch (Exception ex) {
			DriverAnalysisService.logger.error(null, ex);
			throw new OpentachWSException(DriverAnalysisService.CODE_POST_ANALIZING, DriverAnalysisService.ERROR_RESULT_PROCESSING);
		}
	}

	private List<Period> generateMondayToSundayPeriods(Date beginDate, Date endDate) {
		List<Period> res = new ArrayList<Period>();
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		calendar.setTime(DateTools.truncate(beginDate));
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		while (calendar.getTime().before(endDate)) {
			Date begin = calendar.getTime();
			calendar.add(Calendar.DAY_OF_YEAR, 7);
			Date end = calendar.getTime();
			res.add(new Period(PeriodClass.PDD, begin, end, 0, PeriodType.REGULATIVE));
		}

		return res;
	}

	private boolean isEmpty(AnalysisResult result) {
		boolean emptyPeriods = true;
		for (PeriodClass pc : PeriodClass.values()) {
			if (result.getPeriods().containsKey(pc) && !result.getPeriods().get(pc).isEmpty()) {
				emptyPeriods = false;
			}
		}

		boolean emptyRests = true;
		for (RestClass rc : RestClass.values()) {
			if (result.getRests().containsKey(rc) && !result.getRests().get(rc).isEmpty()) {
				emptyRests = false;
			}
		}

		return result.getInfractions().isEmpty() && result.getStretchs().isEmpty() && emptyPeriods && emptyRests;
	}

	private List<Period> filterPeriods(Map<PeriodClass, List<Period>> periods, PeriodClass pdd) {
		if ((periods == null) || !periods.containsKey(pdd) || periods.get(pdd).isEmpty()) {
			return null;
		}
		return periods.get(pdd);
	}

	private InfractionList extractInfractions(List<Infraction> infractions) {
		if (infractions == null) {
			return null;
		}
		InfractionList list = new InfractionList();
		list.setInfraction(new ArrayList<com.opentach.server.webservice.driverAnalysis.beans.Infraction>());
		for (Infraction inInfra : infractions) {
			com.opentach.server.webservice.driverAnalysis.beans.Infraction wsInfra = new com.opentach.server.webservice.driverAnalysis.beans.Infraction();
			wsInfra.setInit(inInfra.getBeginDate());
			wsInfra.setEnd(inInfra.getEndDate());
			wsInfra.setInfractionType(inInfra.getType().name());
			wsInfra.setSeverity(inInfra.getScaleEntry().getCgNatu());
			list.getInfraction().add(wsInfra);
		}
		return list.getInfraction().isEmpty() ? null : list;
	}

	/**
	 * Iterate over all work periods (consider to start in the first "start" -> TPPERIODO = 0,2,4) and sum all data for each strechtype
	 *
	 * @param resWorkPeriods
	 * @param stretchs
	 * @return
	 */
	private WorkPeriodList processWorkPeriods(List<Activity> stretchs, List<Period> rests) {

		if ((stretchs == null) || stretchs.isEmpty() || (rests == null) || (rests.size() == 0)) {
			return null;
		}

		WorkPeriodList list = new WorkPeriodList();
		list.setPeriod(new ArrayList<com.opentach.server.webservice.driverAnalysis.beans.WorkPeriod>());

		for (Period rest : rests) {
			Date startPeriod = rest.getBeginPeriodDate();
			Date endPeriod = rest.getEndPeriodDate();
			int periodRest = -1;
			if (rest.getPeriodClass().equals(PeriodClass.PDS) || rest.getPeriodClass().equals(PeriodClass.PDD)) {
				periodRest = rest.getTime();
			}
			WorkPeriod newWorkPeriod = this.buildWorkPeriod(startPeriod, endPeriod, stretchs, periodRest);
			if (newWorkPeriod != null) {
				list.getPeriod().add(newWorkPeriod);
			}
		}

		return list.getPeriod().isEmpty() ? null : list;
	}

	private WorkPeriod buildWorkPeriod(Date startPeriod, Date endPeriod, List<Activity> stretchs, int periodRest) {
		WorkPeriod wPeriod = new WorkPeriod();
		wPeriod.setInit(startPeriod);
		wPeriod.setEnd(endPeriod);

		ListIterator<Activity> listIterator = stretchs.listIterator();

		wPeriod.setWorkDuration(this.calculeWorkPeriodDuration(listIterator, startPeriod, endPeriod, StretchType.WORK));
		wPeriod.setRestDuration(this.calculeWorkPeriodDuration(listIterator, startPeriod, endPeriod, StretchType.REST));
		wPeriod.setAvailableDuration(this.calculeWorkPeriodDuration(listIterator, startPeriod, endPeriod, StretchType.AVAILABLE));
		wPeriod.setDrivingDuration(this.calculeWorkPeriodDuration(listIterator, startPeriod, endPeriod, StretchType.DRIVING));
		wPeriod.setIndeterminateDuration(this.calculeWorkPeriodDuration(listIterator, startPeriod, endPeriod, StretchType.INDETERMINATE));
		if (periodRest >= 0) {
			wPeriod.setPeriodRestDuration(periodRest);
		}

		return wPeriod.isEmpty() ? null : wPeriod;
	}

	private Integer calculeWorkPeriodDuration(ListIterator<Activity> listIterator, Date beginPeriodDate, Date endPeriodDate, StretchType type) {
		// Localize first interesting record
		if (!listIterator.hasNext()) {
			if (!listIterator.hasPrevious()) {
				return 0;
			}
			listIterator.previous();
		}
		Activity current = listIterator.next();
		while (listIterator.hasPrevious() && current.getEndDate().after(beginPeriodDate)) {
			current = listIterator.previous();
		}
		// Put into the first one record
		if (!listIterator.hasPrevious() && listIterator.hasNext()) {
			current = listIterator.next();
		}
		while (listIterator.hasNext() && current.getEndDate().before(beginPeriodDate)) {
			current = listIterator.next();
		}
		// Iterate until last interesting recopiling info
		int totalDuration = 0;
		boolean someEntry = false;
		while (current.getBeginDate().before(endPeriodDate)) {
			// Catch data
			if (current.getType().equals(type)) {
				int currentDuration = this.getDurationInPeriod(current, beginPeriodDate, endPeriodDate);
				someEntry = true;
				totalDuration += currentDuration;
			}
			// Consider to get next
			if (!listIterator.hasNext()) {
				break;
			}
			current = listIterator.next();
		}

		// Compose resultant period
		return someEntry ? totalDuration : null;
	}

	private int getDurationInPeriod(Activity current, Date beginPeriodDate, Date endPeriodDate) {
		Date begin = current.getBeginDate().before(beginPeriodDate) ? beginPeriodDate : current.getBeginDate();
		Date end = current.getEndDate().after(endPeriodDate) ? endPeriodDate : current.getEndDate();
		return AnalyzerUtils.minutesBetween(end, begin);
	}

	/***************************************************************************************************************/
	/************************************************* ACTIVITY ****************************************************/
	/***************************************************************************************************************/
	@Override
	public DriverActivityResponse queryActivities(final DriverActivityRequest filters) throws OpentachWSException {
		ServletContext servletContext = (ServletContext) this.context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		final OpentachServerLocator locator = (OpentachServerLocator) servletContext.getAttribute(ServerLauncherServlet.COM_ONTIMIZE_GUI_LOCATOR_ATTRIBUTE_CONTEXT);
		try {
			if (filters == null) {
				throw new OpentachWSException(DriverAnalysisService.CODE_VALIDATING_INPUTS, DriverAnalysisService.ERROR_REQUIRED_FILTERS);
			}
			return new OntimizeConnectionTemplate<DriverActivityResponse>() {
				@Override
				protected DriverActivityResponse doTask(Connection con) throws UException {
					try {
						return DriverAnalysisService.this.queryActivities(filters, locator, con);
					} catch (Exception ex) {
						throw new UException(ex);
					}
				}
			}.execute(locator, true);
		} catch (OpentachWSException ex) {
			DriverAnalysisService.logger.error(null, ex);
			throw ex;
		} catch (Throwable error) {
			DriverAnalysisService.logger.error(null, error);
			throw new OpentachWSException(DriverAnalysisService.CODE_UNKNOWED_ERROR_CODE, DriverAnalysisService.ERROR_UNKNOWED + " " + error.getMessage());
		}
	}

	protected DriverActivityResponse queryActivities(DriverActivityRequest filters, OpentachServerLocator locator, Connection con) throws Exception {
		int sessionId = -1;
		try {
			// Step 1 : Validate user credentials ans start session
			sessionId = this.validateUserAndStartSession(filters.getUser(), filters.getPass(), locator);

			// Step 2: Validate driver
			Pair<String, String> pair = this.validateDriver(filters.getDriver(), locator, con, sessionId);
			String numReq = pair.getFirst();
			String cif = pair.getSecond();

			// Step 3 : Validate period
			this.validatePeriod(filters.getInitDate(), filters.getEndDate());

			// Step 4 : Analize with input data
			List<Activity> activities = ActivitiesAnalyzerInServer.getInstance(locator).queryActivityList(numReq, null, null, Arrays.asList(new Object[] { filters.getDriver() }),
					filters.getInitDate(), filters.getEndDate(), sessionId);

			// Step 6 : Convert to response
			return DriverAnalysisService.this.convertResultToActivityResponse(activities);
		} finally {
			// Step 5: End session
			if (sessionId >= 0) {
				locator.endSession(sessionId);
			}
		}
	}

	private DriverActivityResponse convertResultToActivityResponse(List<Activity> activities) {
		DriverActivityResponse response = new DriverActivityResponse();
		List<com.opentach.server.webservice.driverAnalysis.beans.Activity> activitiesRes = new ArrayList<>();
		ActivityList activityListRes = new ActivityList();
		activityListRes.setActivities(activitiesRes);
		response.setActivities(activityListRes);

		for (Activity activity : activities) {
			activitiesRes.add(this.toRestActivity(activity));
		}
		return response;

	}

	private com.opentach.server.webservice.driverAnalysis.beans.Activity toRestActivity(Activity activity) {
		com.opentach.server.webservice.driverAnalysis.beans.Activity res = new com.opentach.server.webservice.driverAnalysis.beans.Activity();
		res.setBeginDate(activity.getBeginDate());
		res.setCardNumber(activity.getCardNumber());
		res.setEndDate(activity.getEndDate());
		res.setOrigin(activity.getOrigin() == null ? null : activity.getOrigin().toString());
		res.setOutOfScope(activity.getOutOfScope());
		res.setPlateNumber(activity.getPlateNumber());
		res.setRegimen(activity.getRegimen() == null ? null : activity.getRegimen().toString());
		res.setTrainTrans(activity.getTrainTrans());
		res.setType(activity.getType() == null ? null : activity.getType().toString());
		return res;
	}
}