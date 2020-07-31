package com.opentach.server.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.imatia.tacho.StretchType;
import com.imatia.tacho.activity.Activity;
import com.imatia.tacho.infraction.AnalysisParameters;
import com.imatia.tacho.infraction.AnalysisResult;
import com.imatia.tacho.infraction.AnalyzerFDS.RestToCompensate;
import com.imatia.tacho.infraction.AnalyzerUtils;
import com.imatia.tacho.infraction.Period;
import com.imatia.tacho.infraction.Period.PeriodClass;
import com.imatia.tacho.infraction.Period.PeriodType;
import com.imatia.tacho.infraction.Rest;
import com.imatia.tacho.infraction.RestClass;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.tools.Template;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.activities.IInfractionService;
import com.opentach.common.activities.IInfractionService.EngineAnalyzer;
import com.opentach.common.report.util.IJRConstants;
import com.opentach.common.util.DateUtil;
import com.opentach.server.IOpentachServerLocator;
import com.opentach.server.activities.InfractionAnalyzerInServer;
import com.opentach.server.activities.InfractionService;
import com.opentach.server.util.ContractUtils;
import com.opentach.server.util.db.FileTableEntity;
import com.utilmize.server.tools.sqltemplate.QueryJdbcTemplate;
import com.utilmize.tools.exception.UException;

public class ReportHelperDriverRestAndDrivings {

	public ReportHelperDriverRestAndDrivings() {}

	public void help(Map<String, Object> cv, Connection conn, Integer sessionID, IOpentachServerLocator locator) throws Exception {
		Date fFin = new Date() ;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fFin);
		calendar.add(Calendar.DAY_OF_MONTH, -28);
		 Date fIni = calendar.getTime();
//		
//		calendar.add(Calendar.DAY_OF_MONTH, -11);
//		fFin  = calendar.getTime();
//		
//		calendar.add(Calendar.DAY_OF_MONTH, -15);
//		Date fIni  = calendar.getTime();
		
		Object numreq = cv.get(IJRConstants.JRNUMREQ);
		numreq = ContractUtils.checkContratoFicticio(locator, numreq, sessionID, conn);
		Object idConductor = cv.get(IJRConstants.JRIDCONDUCTOR);
		Object cif = cv.get("CIF");
		if (idConductor == null) {
			throw new Exception("E_MUST_SPECIFY_DRIVER");
		}
		
		String sql = new Template("sql/EConductoresEmp.sql").getTemplate();
		EntityResult entityName = new QueryJdbcTemplate<EntityResult>() {

			@Override
			protected EntityResult parseResponse(ResultSet rs) throws UException {
				try {
					EntityResult res = new EntityResult();
					FileTableEntity.resultSetToEntityResult(rs, res);
					return res;
				} catch (Exception ex) {
					throw new UException(ex);
				}
			}
		}.execute(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, conn, sql, cif, idConductor);

		if (entityName != null && entityName.calculateRecordNumber() ==1) {
			cv.put(OpentachFieldNames.DNI_FIELD, entityName.getRecordValues(0).get(OpentachFieldNames.DNI_FIELD));
			cv.put("NOMBRE", entityName.getRecordValues(0).get("NOMBRE"));
			cv.put("APELLIDOS", entityName.getRecordValues(0).get("APELLIDOS"));
			cv.put("IDCONDUCTOR", entityName.getRecordValues(0).get("IDCONDUCTOR"));
			cv.put("EXTERNAL_EMPLOYEE_ID", entityName.getRecordValues(0).get("EXTERNAL_EMPLOYEE_ID")!=null ? entityName.getRecordValues(0).get("EXTERNAL_EMPLOYEE_ID"):new NullValue(Types.VARCHAR));
			cv.put("F_DESCARGA_DATOS", entityName.getRecordValues(0).get("F_DESCARGA_DATOS")!= null ?  (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format((Date)entityName.getRecordValues(0).get("F_DESCARGA_DATOS")): new NullValue(Types.VARCHAR) );
		}
		Pair<AnalysisResult, List<Activity>> analyzeFullInfo = locator.getService(InfractionService.class).analyzeFullInfo(conn, numreq, cif, idConductor, fIni, fFin,
				(EngineAnalyzer) cv.get(IInfractionService.ENGINE_ANALYZER), sessionID.intValue());

		AnalysisResult analysisResult = analyzeFullInfo.getFirst();
		List<Activity> activities = analyzeFullInfo.getSecond();

		SimpleDateFormat sdfDateHour = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

		Date firstDateOfWeek = AnalyzerUtils.firstDateOfWeek(fFin);
		Date lastDateOfWeek = AnalyzerUtils.lastDateOfWeek(fFin);
		String currentWeek = sdfDate.format(firstDateOfWeek) + " - " + sdfDate.format(lastDateOfWeek);
		String lastRecordedActivityDate = "-";

		String addedWeekDrivingTime = "-";
		String addedBiweekDrivingTime = "-";
		String availableDrivingTime = "-";

		String lastWeeklyRest = "-";
		String timeLastWeeklyRest = "-";
		String nextWeeklyRest = "-";
		String toCompensateRestTime = "-";

		String availableReducedRest = "-";
		String availableMoreDriving = "-";
		String expandedDriving = "-";
		String nextrestType= "-";
		String availableReducedRest_evaluated = "-";

		boolean updated = true;
		if (activities.size() > 0) {
			AnalysisParameters parameters = InfractionAnalyzerInServer.getInstance(locator).getParameters();

			lastRecordedActivityDate = this.calculateLastRecoredActivityDate(activities, sdfDateHour);
			Date dlast = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).parse(lastRecordedActivityDate);
			if (lastRecordedActivityDate!="-") {
				if (dlast.before(firstDateOfWeek)) {
					updated = false;
				}
				
			}else {
				updated = false;
			}
			if (!updated) {
				
				addedWeekDrivingTime="-";
				addedBiweekDrivingTime="-";
				availableDrivingTime="-";
				lastWeeklyRest="-";
				timeLastWeeklyRest = "";
				nextWeeklyRest="-";
				toCompensateRestTime="-";
				availableReducedRest="-";
				availableReducedRest_evaluated ="-";
				expandedDriving="-";
				availableMoreDriving="-";
				nextrestType="-";
			}else {
				int weekDriving = this.calculateAddedWeekDrivingTime(activities, firstDateOfWeek, lastDateOfWeek);
				addedWeekDrivingTime = DateUtil.parsearTiempoDisponible(weekDriving, false);
				int biweekDriving = this.calculateAddedWeekDrivingTime(activities, AnalyzerUtils.addDays(firstDateOfWeek, -7), lastDateOfWeek);
				addedBiweekDrivingTime = DateUtil.parsearTiempoDisponible(biweekDriving, false);
	
				int weekAvailable = parameters.getP15() - weekDriving;
				int biweekAvailable = parameters.getP14() - biweekDriving;
				int min = Math.min(biweekAvailable, weekAvailable);
				availableDrivingTime = DateUtil.parsearTiempoDisponible(min, false);
	
				List<Rest> weeklyRestList = analysisResult.getRests().get(RestClass.DS);
	
				Rest weekRest = this.calculateLastWeeklyRest(weeklyRestList);
				if (weekRest != null) {
					SimpleDateFormat dfsh = new SimpleDateFormat("(dd) HH:mm");
					Date dRest = new Date(weekRest.getEndDate().getTime() - weekRest.getBeginDate().getTime());
					Calendar c = Calendar.getInstance();
					c.setTime(dRest);
					c.add(Calendar.DAY_OF_MONTH, -1);
					timeLastWeeklyRest =  dfsh.format(c.getTime());
					lastWeeklyRest = sdfDateHour.format(weekRest.getEndDate());
					Date nextWR = AnalyzerUtils.addMinutes(weekRest.getEndDate(), parameters.getP11());
					nextWeeklyRest = sdfDateHour.format(nextWR);
	
					toCompensateRestTime = this.calculateRestToCompensate(analysisResult, toCompensateRestTime, parameters);
					
					if (!toCompensateRestTime.equals("-")) { 
						// si hay tiempo de descanso a compensar veo si este es realmente a compensar o es que aun no ha terminado la semana
						Date day = AnalyzerUtils.addMinutes(dlast, - parameters.getP11());
						if (nextWR.after(day)) {
							toCompensateRestTime += "**";
						}
						
					}
	
					List<Rest> dailyRestList = analysisResult.getRests().get(RestClass.DD);
					List<Period> dailyDrivingPeriods = analysisResult.getPeriods().get(PeriodClass.PCD);
					int numReducedDailyRests = this.calculateReducedDailyRests(dailyRestList, weekRest.getEndDate(), parameters);
					if (numReducedDailyRests > 3) {
						availableReducedRest  = String.valueOf(0);
					
					}
					else {
						availableReducedRest = String.valueOf(3 - numReducedDailyRests);
					}
					availableReducedRest_evaluated = String.valueOf(3 - numReducedDailyRests);
					int numExpandedDrivings = this.calculateExpandedDrivings(dailyDrivingPeriods, weekRest.getEndDate());
					expandedDriving = String.valueOf(numExpandedDrivings);
					availableMoreDriving = String.valueOf(2 - numExpandedDrivings);
					
					Pair<String, String>  pair1 = this.calculateNextRestType( dailyRestList, weeklyRestList, nextWeeklyRest);
					nextrestType = pair1.getFirst();
					nextWeeklyRest = pair1.getSecond();
				}
			}
		}
		
		
		cv.put("current_week", currentWeek);
		cv.put("last_recorded_activity_date", lastRecordedActivityDate);
		cv.put("added_week_driving_time", addedWeekDrivingTime);
		cv.put("added_biweek_driving_time", addedBiweekDrivingTime);
		cv.put("available_driving_time", availableDrivingTime);
		cv.put("last_weekly_rest", lastWeeklyRest);
		cv.put("next_weekly_rest", nextWeeklyRest);
		cv.put("to_compensate_rest_time", toCompensateRestTime);
		cv.put("available_reduced_rest", availableReducedRest);
		cv.put("available_reduced_rest_eval", availableReducedRest_evaluated);
		cv.put("expanded_driving", expandedDriving);
		cv.put("available_more_driving", availableMoreDriving);
		cv.put("timeLastWeeklyRest", timeLastWeeklyRest);
		cv.put("next_rest_type", nextrestType);
		cv.put("updated", updated ? "S" : "N");
	}
	
	
	private Pair<String, String>  calculateNextRestType(List<Rest> dailyRestList, List<Rest> weeklyRestList, String nextWeeklyRest) {
		
		Calendar calFirstWeek = Calendar.getInstance();
		calFirstWeek.add(Calendar.DAY_OF_MONTH, -7);
		calFirstWeek.get(Calendar.DAY_OF_WEEK);
		Date firstDayFirstWeek = AnalyzerUtils.firstDateOfWeek(calFirstWeek.getTime());
		
		
		Calendar calEndWeek = Calendar.getInstance();
		calEndWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calEndWeek.set(Calendar.HOUR_OF_DAY, 23);
		calEndWeek.set(Calendar.MINUTE, 59);
		String nextWeeklyRestActual = (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(calEndWeek.getTime());

		Rest rest0 =null;
		Rest resth1a =null;
		Rest resth2a =null;
		if (weeklyRestList.size()<3 ) return new Pair< String, String>("-", nextWeeklyRest);
		
		Rest h2aS =  weeklyRestList.get(weeklyRestList.size()-1);
		Rest h1aS =  weeklyRestList.get(weeklyRestList.size()-2);
		Rest h0S = weeklyRestList.size()>2 ? weeklyRestList.get(weeklyRestList.size()-3) : null;
		
		Vector<Rest> pDSLast2WeeksNormal = new Vector<Rest>();
		Vector<Rest> pDSLast2WeeksReduced = new Vector<Rest>();
		
		for (int i = 1; i< weeklyRestList.size();i++) {
			if (weeklyRestList.get(weeklyRestList.size()-i).getEndDate().after(firstDayFirstWeek)) {
				if (weeklyRestList.get(weeklyRestList.size()-i).getPeriodType().equals(PeriodType.REGULATIVE)) {
					pDSLast2WeeksNormal.add(weeklyRestList.get(weeklyRestList.size()-i));
				}else {
					pDSLast2WeeksReduced.add(weeklyRestList.get(weeklyRestList.size()-i));
				}
			}else {
				break;
			}
		}
		
		
		Rest h2aD = dailyRestList.size()>0 ? dailyRestList.get(dailyRestList.size()-1) : null;
		
		System.out.println(h0S.toString());
		System.out.println(h1aS.toString());
		System.out.println(h2aS.toString());
		System.out.println("------------------------------");
		System.out.println(h2aD.toString());
		
		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -69);
		System.out.println(cal.getTime());
		if (h2aD.getEndDate().after(h2aS.getEndDate())) { //hay un descanso diario posterior al último descanso semanal
			if (h1aS.getPeriodType().equals(PeriodType.REGULATIVE)) {
				rest0 = h1aS;
				resth1a = h2aS;
				resth2a =h2aD;
			}else {
				if (h0S.getPeriodType().equals(PeriodType.REGULATIVE)) {
					if (h0S == null) return new Pair< String, String>("-", nextWeeklyRest);
					rest0 = h0S;
					resth1a = h1aS;
					resth2a =h2aS;
				}
				
			}
			
		}else {
			if (h0S == null) return new Pair< String, String>("-", nextWeeklyRest);
			rest0 = h0S;
			resth1a = h1aS;
			resth2a =h2aS;
		}
		if (pDSLast2WeeksNormal.size()==0) { // si no hay ningun descanso normal en las 2 semanas
			return new Pair< String, String>("NORMAL", nextWeeklyRestActual);
		}
		if (pDSLast2WeeksNormal.size()==1 && pDSLast2WeeksNormal.get(0).getBeginDate().before(firstDayFirstWeek)) {
			return new Pair< String, String>("NORMAL*", nextWeeklyRest);
		}
	
		if (rest0.getPeriodType().equals(PeriodType.REGULATIVE)) {
			if (resth1a.getPeriodType().equals(PeriodType.REGULATIVE)){
					return new Pair< String, String>("NORMAL/REDUCIDO", nextWeeklyRest);
					
			}else {
				return new Pair< String, String>("NORMAL", nextWeeklyRest);
			}
		}else {
			System.out.println("ERROR");
		}
		return new Pair< String, String>("-", nextWeeklyRest);
	}

	private String calculateRestToCompensate(AnalysisResult analysisResult, String toCompensateRestTime, AnalysisParameters parameters) {
		List<RestToCompensate> weeklyRestToCompensate = analysisResult.getWeeklyRestToCompensate();
		if (weeklyRestToCompensate.size() > 0) {
			int accum = 0;
			for (RestToCompensate rest : weeklyRestToCompensate) {
				accum += parameters.getP12() - rest.getRest().getTime();
			}
			return DateUtil.parsearTiempoDisponible(accum, false);
		}
		return "-";
	}

	private int calculateExpandedDrivings(List<Period> dailyDrivingPeriods, Date endDate) {
		int accum = 0;
		for (Period period : dailyDrivingPeriods) {
			if (period.getBeginPeriodDate().after(endDate)) {
				if (period.getPeriodType().equals(PeriodType.REDUCED)) {
					accum++;
				}
			}
		}
		return accum;
	}

	private int calculateReducedDailyRests(List<Rest> dailyRestList, Date endDate, AnalysisParameters parameters) {
		int accum = 0;
		for (Rest rest : dailyRestList) {
			if (rest.getBeginDate().after(endDate)) {
				if ((rest.getTime() >= parameters.getP08()) && (rest.getTime() < parameters.getP07()) && !rest.getPeriodType().equals(PeriodType.FRACTIONAL)) {
					accum++;
				}
			}
		}
		return accum;
	}

	private Rest calculateLastWeeklyRest(List<Rest> weeklyRestList) {
		if (weeklyRestList.size() == 0) {
			return null;
		}
		return weeklyRestList.get(weeklyRestList.size() - 1);
	}

	private int calculateAddedWeekDrivingTime(List<Activity> activities, Date firstDateOfWeek, Date lastDateOfWeek) {
		int accum = 0;
		for (Activity act : activities) {
			if (act.getEndDate().after(firstDateOfWeek) && act.getBeginDate().before(lastDateOfWeek) && StretchType.DRIVING.equals(act.getType())) {
				if (act.getBeginDate().before(firstDateOfWeek)) {
					accum += AnalyzerUtils.minutesBetween(act.getEndDate(), firstDateOfWeek);
				} else if (act.getEndDate().after(lastDateOfWeek)) {
					accum += AnalyzerUtils.minutesBetween(lastDateOfWeek, act.getBeginDate());
				} else {
					accum += AnalyzerUtils.minutesBetween(act.getEndDate(), act.getBeginDate());
				}
			}
			if (act.getBeginDate().after(lastDateOfWeek)) {
				break;
			}
		}
		return accum;
	}

	private String calculateLastRecoredActivityDate(List<Activity> activities, SimpleDateFormat sdf) {
		return sdf.format(activities.get(activities.size() - 1).getEndDate());
	}

}
