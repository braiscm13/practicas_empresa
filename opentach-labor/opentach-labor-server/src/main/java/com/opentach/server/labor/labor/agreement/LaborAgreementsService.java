package com.opentach.server.labor.labor.agreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.TransactionalEntity;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.EntityResultTools;
import com.ontimize.locator.EntityReferenceLocator;
import com.opentach.common.labor.laboral.ILaborAgreementsService;
import com.opentach.server.labor.labor.agreement.LaborAgreement.LaborAgreementModality;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlus2HoursLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlus10min;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlus15minIf6hours;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlus20minSandwich;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlus40min;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlus5minIf6hours;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlusAvailableLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlusAvailablePlus15minSandwich;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlusAvailablePlus90minSandwich;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlusAvailablePlusIntermediateRestLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.DrivingPlusWorkPlusWaitings;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.ILaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.InsertToExtractionLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.InsertToExtractionMinusAvailableLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.dailyworkalgorithm.InsertToExtractionMinusRestMore1hLaborAgreementAlgorithm;
import com.opentach.server.labor.labor.agreement.extratimealgorithm.AsturiasLaborAgreementExtraTimeAlgorithm;
import com.opentach.server.labor.labor.agreement.extratimealgorithm.DefaultLaborAgreementExtraTimeAlgorithm;
import com.opentach.server.labor.labor.agreement.extratimealgorithm.ILaborAgreementExtraTimeAlgorithm;
import com.utilmize.server.services.UAbstractService;

public class LaborAgreementsService extends UAbstractService implements ILaborAgreementsService {

	private final Map<String, Class<? extends ILaborAgreementAlgorithm>>			algorithms;
	private final Map<String, Class<? extends ILaborAgreementExtraTimeAlgorithm>>	limitAlgorithms;

	private final Map<Object, LaborAgreement>										agreements;

	public LaborAgreementsService(int port, EntityReferenceLocator erl, Hashtable hconfig) throws Exception {
		super(port, erl, hconfig);
		this.agreements = new HashMap<>();
		this.algorithms = new HashMap<>();
		this.limitAlgorithms = new HashMap<>();
		this.algorithms.put("[Conducción]", DrivingLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [60 min toma y deje]+[60 min comida]", DrivingPlus2HoursLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [Otros trabajos]", DrivingPlusWorkLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [40 min (10prep+15toma+15deje)]", DrivingPlusWorkPlus40min.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [20 min bocadillo]", DrivingPlusWorkPlus20minSandwich.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [10 min]", DrivingPlusWorkPlus10min.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad]", DrivingPlusWorkPlusAvailableLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad] + [15 min bocadillo]", DrivingPlusWorkPlusAvailablePlus15minSandwich.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad]+ [Pausas intermedias]",
				DrivingPlusWorkPlusAvailablePlusIntermediateRestLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad]+ [Esperas]", DrivingPlusWorkPlusWaitings.class);
		this.algorithms.put("[Inserción a extracción de tarjeta]", InsertToExtractionLaborAgreementAlgorithm.class);
		this.algorithms.put("[Inserción a extracción de tarjeta] - [Descanso (+1h)]", InsertToExtractionMinusRestMore1hLaborAgreementAlgorithm.class);
		this.algorithms.put("[Inserción a extracción de tarjeta] - [Disponibilidad]", InsertToExtractionMinusAvailableLaborAgreementAlgorithm.class);
		this.algorithms.put("[Conducción] + [Otros trabajos]+ [15 min cada 6h de trabajo continuado]", DrivingPlusWorkPlus15minIf6hours.class);
		this.algorithms.put("[Conducción] + [Otros trabajos]+ [5 min cada 6h de trabajo continuado]", DrivingPlusWorkPlus5minIf6hours.class);

		// italia
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad] - [90 min comida/cena]", DrivingPlusWorkPlusAvailablePlus90minSandwich.class);
		this.algorithms.put("[Conducción] + [Otros trabajos] + [Disponibilidad] - [45 min cada 45min] - [30min/45min por jornada entre 6-9h/>9h]", DrivingPlusWorkPlusAvailablePlus90minSandwich.class);
		
		
		this.limitAlgorithms.put("General", DefaultLaborAgreementExtraTimeAlgorithm.class);
		this.limitAlgorithms.put("Asturias", AsturiasLaborAgreementExtraTimeAlgorithm.class);
	}

	public void refresh() {
		this.agreements.clear();
	}

	public LaborAgreement getAgreement(Object agreementId) throws Exception {
		return this.createAgreement(agreementId);
	}

	public List<String> getAgreementAlgorithmImplementations() {
		return new ArrayList<>(this.algorithms.keySet());
	}

	public List<String> getAgreementLimitAlgorithmImplementations() {
		return new ArrayList<>(this.limitAlgorithms.keySet());
	}

	private LaborAgreement createAgreement(Object agreementId) throws Exception {
		Entity entity = (Entity) this.getEntity("ELaborAgreement");
		Hashtable<Object, Object> filter = EntityResultTools.keysvalues("AGR_ID", agreementId);
		Vector<String> attributes = EntityResultTools.attributes("AGR_ID", "AGR_NAME", "AGR_MODALITY", "AGR_ALGORITHM", "AGR_DAILY_MINUTES", "AGR_WEEKLY_MINUTES", "AGR_FOUR_WEEKLY_MINUTES",
				"AGR_MONTHLY_MINUTES", "AGR_FOUR_MONTHLY_MINUTES", "AGR_BIWEEKLY_MINUTES", "AGR_ANNUAL_MINUTES", "AGR_LIMIT_ALGORITHM");
		EntityResult res = entity.query(filter, attributes, this.getEntityPrivilegedId((TransactionalEntity) entity));
		CheckingTools.checkValidEntityResult(res, "E_NO_AGREEMENT_FOUND", true, true, (Object[]) null);
		return this.toAgreement(res.getRecordValues(0));
	}

	private LaborAgreement toAgreement(Hashtable record) throws InstantiationException, IllegalAccessException {
		Object agrId = record.get("AGR_ID");
		String name = (String) record.get("AGR_NAME");
		LaborAgreementModality modality = null;
		if (record.get("AGR_MODALITY") != null) {
			modality = LaborAgreementModality.fromString((String) record.get("AGR_MODALITY"));
		}
		ILaborAgreementAlgorithm algorithm = this.algorithms.get(record.get("AGR_ALGORITHM")).newInstance();
		ILaborAgreementExtraTimeAlgorithm limitAlgorithm = null;
		Object limitAlgorithmStr = record.get("AGR_LIMIT_ALGORITHM");
		if (limitAlgorithmStr == null) {
			limitAlgorithm = new DefaultLaborAgreementExtraTimeAlgorithm();
		} else {
			limitAlgorithm = this.limitAlgorithms.get(limitAlgorithmStr).newInstance();
		}

		Number dailyTimeLimit = (Number) record.get("AGR_DAILY_MINUTES");
		Number weeklyTimeLimit = (Number) record.get("AGR_WEEKLY_MINUTES");
		Number biweeklyTimeLimit = (Number) record.get("AGR_BIWEEKLY_MINUTES");
		Number fourweeklyTimeLimit = (Number) record.get("AGR_FOUR_WEEKLY_MINUTES");
		Number monthlyTimeLimit = (Number) record.get("AGR_MONTHLY_MINUTES");
		Number fourMonthlyTimeLimit = (Number) record.get("AGR_FOUR_MONTHLY_MINUTES");
		Number annualTimeLimit = (Number) record.get("AGR_ANNUAL_MINUTES");

		LaborAgreement agreement = new LaborAgreement();
		agreement.setModality(modality);
		agreement.setName(name);
		agreement.setAlgorithm(algorithm);
		// TODO implementar caceres y badajoz
		agreement.setExtraTimeAlgorithm(limitAlgorithm);
		agreement.setAnnualTimeLimit(annualTimeLimit);
		agreement.setWeeklyTimeLimit(weeklyTimeLimit);
		agreement.setBiweeklyTimeLimit(biweeklyTimeLimit);
		agreement.setFourweeklyTimeLimit(fourweeklyTimeLimit);
		agreement.setMonthlyTimeLimit(monthlyTimeLimit);
		agreement.setFourMonthlyTimeLimit(fourMonthlyTimeLimit);
		agreement.setDailyTimeLimit(dailyTimeLimit);
		return agreement;
	}

}
