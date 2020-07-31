package com.opentach.common.tacho;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.CardActivityDailyRecord;
import com.imatia.tacho.model.tc.CardControlActivityDataRecord;
import com.imatia.tacho.model.tc.CardEventRecord;
import com.imatia.tacho.model.tc.CardFaultRecord;
import com.imatia.tacho.model.tc.CardIdentification;
import com.imatia.tacho.model.tc.CardVehicleRecord;
import com.imatia.tacho.model.tc.DriverCardHolderIdentification;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.model.vu.ActivityChangeInfo;
import com.imatia.tacho.model.vu.EquipmentType;
import com.imatia.tacho.model.vu.PlaceRecord;
import com.imatia.tacho.model.vu.SpecificConditionRecord;
import com.imatia.tacho.model.vu.SpecificConditionType;
import com.imatia.tacho.model.vu.VehicleRegistrationIdentification;
import com.opentach.common.tacho.data.AbstractData;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Calibrado;
import com.opentach.common.tacho.data.CambioHorario;
import com.opentach.common.tacho.data.CompanyLocksDataVU;
import com.opentach.common.tacho.data.Conductor;
import com.opentach.common.tacho.data.Control;
import com.opentach.common.tacho.data.Fallo;
import com.opentach.common.tacho.data.FueraAmbito;
import com.opentach.common.tacho.data.IdentificacionVU;
import com.opentach.common.tacho.data.Incidente;
import com.opentach.common.tacho.data.PeriodoTrabajo;
import com.opentach.common.tacho.data.RegistroKmConductor;
import com.opentach.common.tacho.data.RegistroKmVehiculo;
import com.opentach.common.tacho.data.SensorPairedVU;
import com.opentach.common.tacho.data.Tarjeta;
import com.opentach.common.tacho.data.TransTren;
import com.opentach.common.tacho.data.UsoTarjeta;
import com.opentach.common.tacho.data.UsoVehiculo;
import com.opentach.common.tacho.data.VehiculoInfrac;
import com.opentach.common.tacho.data.VehiculoInsp;
import com.opentach.common.tacho.data.Velocidad;

public class TcFileParser implements IFileParser {

	private static final Logger	logger		= LoggerFactory.getLogger(TcFileParser.class);

	public static final String	DRIVER_CARD	= "TC";
	protected TCFile			file;

	public TcFileParser(TCFile file) {
		super();
		this.file = file;
	}

	@Override
	public List<Actividad> getActividades() {
		List<Actividad> activs = new ArrayList<>();
		try {
			if (this.file.getEfDriverActivityData().getJoinedData().isCorrupted()) {
				return activs;
			}
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			String idconductor = this.getDriverId(id);

			List<CardActivityDailyRecord> activsDay = this.file.getEfDriverActivityData().getJoinedData().getCardDriverActivity().getCardActivityDailyRecords();
			ActivityChangeInfo startaci = null, endaci = null;
			long start = 0, end = 0;
			if ((activsDay == null) || (activsDay.isEmpty())) {
				return activs;
			}
			// Empiezo por el dia mas antiguo activsDays[lenght-1]
			for (int i = activsDay.size() - 1; (i >= 0); i--) {
				CardActivityDailyRecord item = activsDay.get(i);
				Date day = item.getActivityRecordDate().getDate();
				List<ActivityChangeInfo> acis = item.getActivityChangeInfos();
				for (int j = 0; (acis != null) && (j < acis.size()); j++) {
					endaci = acis.get(j);
					end = day.getTime() + (((long) endaci.getMinutosCambio()) * 60 * 1000);
					if (startaci != null) {
						Actividad act = this.createActivity(id, idconductor, startaci, start, end);
						activs.add(act);
					}
					if (!this.isCardWithdrawal(endaci)) {
						startaci = endaci;
						start = end;
					} else {
						startaci = null;
					}
				}
				// Si existe una actividad con inicio pero sin fin de un dia
				// se cierra a las 00:00 horas del dia siguiente.
				// Salvo que se trate del ultimo dia registrado.
				if ((startaci != null) && (i > 0)) {
					end = day.getTime() + (24l * 60l * 60l * 1000l);
					Actividad act = this.createActivity(id, idconductor, startaci, start, end);
					activs.add(act);
					startaci = null;
				}
			}
			this.mapearMatriculas(activs);
		} catch (NullPointerException nex) {
			TcFileParser.logger.error("No se han encontrado Actividades. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return activs;
	}

	private Actividad createActivity(CardIdentification id, String idconductor, ActivityChangeInfo startaci, long start, long end) {
		Actividad act = new Actividad();
		// OJO el codigo de las actividades en Java es de 1-4
		// mientras que en los datos de la tarjeta es de 0-3
		act.tpActividad = Integer.valueOf(startaci.getActividad() + 1);
		act.setOrigen(this.isManualEntry(startaci) ? "M" : "A");
		act.setProcedencia(TcFileParser.DRIVER_CARD);
		act.estTrjRanura = Byte.toString(startaci.getEstadoTarjeta());
		act.ranura = Byte.toString(startaci.getRanura());
		act.regimen = Byte.toString(startaci.getRegimenConductor());
		act.setFecComienzo(new Date(start));
		act.setFecFin(new Date(end));
		act.setIdConductor(idconductor);
		act.setNumTarjeta(id.getCardNumber().getFullCardNumber());
		return act;
	}

	@Override
	public List<UsoVehiculo> getUsosVehiculo() {
		List<UsoVehiculo> uvehs = new ArrayList<>();
		try {
			if (this.file.getEfVehiclesUsed().getJoinedData().isCorrupted()) {
				return uvehs;
			}
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			String idconductor = this.getDriverId(id);

			List<CardVehicleRecord> vehRecords = this.file.getEfVehiclesUsed().getJoinedData().getCardVehiclesUsed().getCardVehicleRecords();
			if ((vehRecords == null) || (vehRecords.isEmpty())) {
				return uvehs;
			}

			for (int i = 0; (i < vehRecords.size()); i++) {
				CardVehicleRecord item = vehRecords.get(i);
				// Solamente se registran usos de vehiculo cerrados, con
				// fecha de extraccion..completos.
				if (this.isCardVehicleRecordClosed(item)) {
					UsoVehiculo uv = new UsoVehiculo();
					uv.setFechaHoraIni(item.getVehicleFirstUse().getDate());
					uv.setKmIni(item.getVehicleOdometerBegin().getValue());
					uv.setFechaHoraFin(item.getVehicleLastUse().getDate());
					uv.setKmFin(item.getVehicleOdometerEnd().getValue());
					uv.setIdConductor(idconductor);
					uv.setNumTrjConductor(id.getCardNumber().getFullCardNumber());
					uv.setMatricula(this.getMatricula(item.getVehicleRegistration()));
					uv.setProcedencia(TcFileParser.DRIVER_CARD);
					if (uv.fechaHoraFin.after(uv.fechaHoraIni)) {
						uvehs.add(uv);
					}
				} else {
					TcFileParser.logger.info("Uso de vehiculo sin cerrar");
				}
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.error("No se han encontrado Usos de Vehiculo. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return uvehs;
	}

	@Override
	public List<PeriodoTrabajo> getPeriodosTrabajo() {
		List<PeriodoTrabajo> pts = new ArrayList<>();
		try {
			if (this.file.getEfPlaces().getJoinedData().isCorrupted()) {
				return pts;
			}
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			String idconductor = this.getDriverId(id);
			List<PlaceRecord> pRecords = this.file.getEfPlaces().getJoinedData().getCardPlaceDailyWorkPeriod().getPlaceRecords();
			PeriodoTrabajo pt;
			for (int i = 0; (pRecords != null) && (i < pRecords.size()); i++) {
				PlaceRecord item = pRecords.get(i);
				pt = new PeriodoTrabajo();
				pt.setFecIni(item.getEntryTime().getDate());
				pt.setTpPeriodo(item.getEntryTypeDailyWorkPeriod().getValue());
				pt.setKm(item.getVehicleOdometerValue().getValue());
				if (255 == item.getDailyWorkPeriodCountry().getValue()) {
					pt.setPais("" + -1);
				}else {
					pt.setPais("" + item.getDailyWorkPeriodCountry().getValue());
				}
				pt.setRegion("" + item.getDailyWorkPeriodRegion().getValue());
				pt.idConductor = idconductor;
				pt.setProcedencia(TcFileParser.DRIVER_CARD);
				pts.add(pt);
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.error("No se han encontrado Periodos Trabajo. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return pts;
	}

	@Override
	public List<Conductor> getConductores() {
		List<Conductor> l = new ArrayList<>();
		try {
			Conductor c = new Conductor();
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			DriverCardHolderIdentification driverid = this.file.getEfIdentification().getJoinedData().getDriverCardHolderIdentification();
			c.IdConductor = this.getDriverId(id);
			// El numero de tarjeta se construye concatenando los caracteres de
			// renovacion y replazo.
			c.setNumTrjCondu(id.getCardNumber().getFullCardNumber());

			c.setApellidos(driverid.getCardHolderName().getHolderSurname().getValue());
			c.setNombre(driverid.getCardHolderName().getHolderFirstNames().getValue());
			c.setEquipmentType(EquipmentType.DRIVER_CARD);
			c.setNationNumeric(id.getCardIssuingMemberState().getValue());
			c.setExpiredDateNumTrjCondu(id.getCardExpiryDate().getDate());
			l.add(c);
		} catch (NullPointerException nex) {
			TcFileParser.logger.error("No se han encontrado Conductores. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return l;
	}

	@Override
	public List<RegistroKmConductor> getRegistroKmConductor() {
		List<RegistroKmConductor> regkms = new ArrayList<>();
		try {
			if (this.file.getEfDriverActivityData().getJoinedData().isCorrupted()) {
				return regkms;
			}
			List<CardActivityDailyRecord> activsDay = this.file.getEfDriverActivityData().getJoinedData().getCardDriverActivity().getCardActivityDailyRecords();
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			String idconductor = this.getDriverId(id);
			for (int i = activsDay.size() - 1; (activsDay != null) && (i >= 0); i--) {
				CardActivityDailyRecord item = activsDay.get(i);
				RegistroKmConductor kmrec = new RegistroKmConductor();
				kmrec.setFecha(item.getActivityRecordDate().getDate());
				kmrec.setKilometrosRecorridos(item.getActivityDayDistance());
				kmrec.setIdConductor(idconductor);
				kmrec.setNumTrjConductor(id.getCardNumber().getFullCardNumber());
				regkms.add(kmrec);
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.error("No se han encontrado Actividades. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return regkms;
	}

	protected void mapearMatriculas(List<Actividad> activs) {
		List<CardVehicleRecord> vehRecords = this.file.getEfVehiclesUsed().getJoinedData().getCardVehiclesUsed().getCardVehicleRecords();
		ArrayList<Actividad> temactivs = new ArrayList<>(activs);
		for (int i = 0; (vehRecords != null) && (i < vehRecords.size()); i++) {
			CardVehicleRecord cvr = vehRecords.get(i);
			// OJO:Los tiempos de inicio-fin de los usos vehiculo estan en resolucion de segundos,mientras que la activdiad es por miniutos.
			// Es necesario truncar la fecha de inicio (0 segundos del minito) y ampliar la de finalizaci�n (el inicio del siguiente minuto).
			Date fechaStart = cvr.getVehicleFirstUse().getDate();
			Calendar cStart = Calendar.getInstance();
			cStart.setTime(fechaStart);
			cStart.set(Calendar.SECOND, 0);
			long ciwstart = cStart.getTimeInMillis();
			long ciwend = 0;
			boolean ciwclosed = this.isCardVehicleRecordClosed(cvr);
			if (ciwclosed) {
				Date fechaEnd = cvr.getVehicleLastUse().getDate();
				Calendar cend = Calendar.getInstance();
				cend.setTime(fechaEnd);
				cend.set(Calendar.MINUTE, cend.get(Calendar.MINUTE) + 1);
				cend.set(Calendar.SECOND, 0);
				ciwend = cend.getTimeInMillis();
			}
			for (Iterator<Actividad> iterator = temactivs.iterator(); iterator.hasNext();) {
				Actividad activ = iterator.next();
				if (ciwclosed) {
					if ((ciwstart <= activ.getFecComienzo().getTime()) && (ciwend >= activ.getFecFin().getTime())) {
						activ.setMatricula(this.getMatricula(cvr.getVehicleRegistration()));
						iterator.remove();
					}
				}
				// Si el uso de vehiculo que no esta cerrado es el ultimo dia mapeo las actividades existentes
				else {
					if (ciwstart <= activ.getFecComienzo().getTime()) {
						activ.setMatricula(this.getMatricula(cvr.getVehicleRegistration()));
						iterator.remove();
					}
				}
			}
		}
	}

	@Override
	public List<Incidente> getIncidentes() {
		List<Incidente> events = new ArrayList<>();
		try {
			if (this.file.getEfEventsData().getJoinedData().isCorrupted()) {
				return events;
			}
			String trjconductor = this.file.getEfIdentification().getJoinedData().getCardIdentification().getCardNumber().getFullCardNumber();
			List<CardEventRecord> evRecords = this.file.getEfEventsData().getJoinedData().getCardEventData().getCardEventRecords();
			for (int i = 0; (evRecords != null) && (i < evRecords.size()); i++) {
				CardEventRecord item = evRecords.get(i);
				Incidente ev = new Incidente();
				ev.setFechHoraIni(item.getEventBeginTime().getDate());
				ev.setFechHoraFin(item.getEventEndTime().getDate());
				ev.setTipo(item.getEventype().getValue());
				ev.setNumTrjConduIni(trjconductor);

				ev.setMatricula(this.getMatricula(item.getEventVehicleRegistration()));
				ev.setPais(Integer.toString(item.getEventVehicleRegistration().getVehicleRegistrationNation().getValue()));
				ev.setProcedencia(TcFileParser.DRIVER_CARD);
				// ev.setNumInciSimilares(new Integer(item.similarEventsNumber));
				events.add(ev);
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado Incidentes. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return events;
	}

	@Override
	public List<Fallo> getFallos() {
		List<Fallo> faults = new ArrayList<>();
		try {
			if (this.file.getEfEventsData().getJoinedData().isCorrupted()) {
				return faults;
			}
			String trjconductor = this.file.getEfIdentification().getJoinedData().getCardIdentification().getCardNumber().getFullCardNumber();
			List<CardFaultRecord> fRecords = this.file.getEfFaultsData().getJoinedData().getCardFaultData().getCardFaultRecords();
			Fallo f;
			for (int i = 0; (fRecords != null) && (i < fRecords.size()); i++) {
				CardFaultRecord item = fRecords.get(i);
				f = new Fallo();
				f.setfecHoraIni(item.getFaultBeginTime().getDate());
				f.setfecHoraFin(item.getFaultEndTime().getDate());
				f.setTpFallo(item.getFaultType().getValue());

				f.setMatricula(this.getMatricula(item.getFaultVehicleRegistration()));
				f.setNumTrjConduIni(trjconductor);
				f.setNumTrjConduFin(trjconductor);

				faults.add(f);
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado Fallos. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return faults;
	}

	@Override
	public List<FueraAmbito> getFueraAmbito() {
		List<FueraAmbito> rtn = new ArrayList<>();
		try {
			if (this.file.getEfSpecificConditions().getJoinedData().isCorrupted()) {
				return rtn;
			}
			List<SpecificConditionRecord> pcal = this.file.getEfSpecificConditions().getJoinedData().getSpecificConditions().getSpecificConditionRecords();
			FueraAmbito fa = null;
			for (int i = 0; (pcal != null) && (i < pcal.size()); i++) {
				SpecificConditionRecord item = pcal.get(i);
				if (item.getSpecificConditionType().getValue() == SpecificConditionType.FUERA_AMBITO_COMIENZO) {
					fa = new FueraAmbito();
					fa.begin = item.getEntryTime().getDate();
				} else if ((item.getSpecificConditionType().getValue() == SpecificConditionType.FUERA_AMBITO_FIN) && (fa != null)) {
					fa.end = item.getEntryTime().getDate();
					rtn.add(fa);
				}
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado FueraAmbito. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return rtn;
	}

	@Override
	public List<TransTren> getTransTren() {
		List<TransTren> rtn = new ArrayList<TransTren>();
		try {
			if (this.file.getEfSpecificConditions().getJoinedData().isCorrupted()) {
				return rtn;
			}
			List<SpecificConditionRecord> pcal = this.file.getEfSpecificConditions().getJoinedData().getSpecificConditions().getSpecificConditionRecords();
			TransTren fa = null;
			for (int i = 0; (pcal != null) && (i < pcal.size()); i++) {
				SpecificConditionRecord item = pcal.get(i);
				if (item.getSpecificConditionType().getValue() == SpecificConditionType.PUENTE_PASO_NIVEL_COMIENZO) {
					fa = new TransTren(item.getEntryTime().getDate());
					rtn.add(fa);
				}
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado TransTren. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return rtn;
	}

	public List<Tarjeta> getTarjetas() {
		ArrayList<Tarjeta> l = new ArrayList<>();
		try {
			Tarjeta tarj = new Tarjeta();
			CardIdentification id = this.file.getEfIdentification().getJoinedData().getCardIdentification();
			tarj.setDriverIdentification(this.getDriverId(id));
			tarj.setCardConsecutiveIndex(id.getCardNumber().getCardReplacementIndex().getValue().charAt(0));
			tarj.setCardRenewalIndex(id.getCardNumber().getCardRenewalIndex().getValue().charAt(0));
			tarj.setCardReplacementIndex(id.getCardNumber().getCardReplacementIndex().getValue().charAt(0));
			tarj.setOwnerIdentification(id.getCardNumber().getIdentification());
			l.add(tarj);
		} catch (NullPointerException nex) {
			TcFileParser.logger.error(null, nex);
		}
		return l;
	}

	@Override
	public List<Control> getControles() {
		ArrayList<Control> lres = new ArrayList<>();
		try {
			if (this.file.getEfControlActivityData().getJoinedData().isCorrupted()) {
				return lres;
			}
			CardControlActivityDataRecord cr = this.file.getEfControlActivityData().getJoinedData().getCardControlActivityDataRecord();
			if (cr != null) {
				Control c = new Control();
				c.setFechaHora(cr.getControlTime().getDate());
				c.setTpControl(Integer.valueOf(cr.getControlType().getValue()));
				c.setNumTrjControl(cr.getControlCardNumber().getCardNumber().getIdentification());
				c.setMatricula(this.getMatricula(cr.getControlVehicleRegistration()));
				lres.add(c);
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado Controles. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return lres;
	}

	/**
	 * Establece los valores de Fuera de Ambito para las activiades.
	 *
	 * <pre>
	 * FueraAmbito. Indica si se produjo la Condici�n Espec�fica &quot;Fuera de �mbito&quot;. Puede valer S/N. Se puede dar tanto en conducci�n como en descanso.
	 * 	 Origen Tarjeta: EF Specific_Conditions --  SpecificConditionRecord --  specificConditionType
	 * 	 Origen VU (DDT_030) :VuSpecificConditionData --  SpecificConditionRecord --  specificConditionType
	 * </pre>
	 */

	@Override
	public void mapFueraAmbito(Actividad[] actividades, FueraAmbito[] fambito) {
		Actividad act = null;
		FueraAmbito lastfa = null;
		if (fambito.length == 0) {
			return;
		}
		long lbegin = 0;
		long lend = 0;
		for (int i = 0; (actividades != null) && (i < actividades.length); i++) {
			act = actividades[i];
			// a.setFueraAmbito("N");
			// Si no me vale el ultimo uso vehiculo localizo uno nuevo.
			if ((lastfa == null) || (act.getFecComienzo().getTime() < lbegin) || (act.getFecFin().getTime() > lend)) {
				lastfa = null;
				for (int j = 0; (lastfa == null) && (fambito != null) && (j < fambito.length); j++) {
					// hemos detectado algun caso raro en el que la fecha de inicio es posterior a la fecha de fin
					// esos ambitos los ignoramos
					if (fambito[j].begin.after(fambito[j].end)) {
						continue;
					}
					// OJO:Los tiempos de inicio-fin de los usos vehiculo estan en resolucion de segundos,
					// mientras que la activdiad es por miniutos.
					// Es necesario truncar la fecha de inicio (0 segundos del minito) y ampliar la de finalizaci�n (el inicio del siguiente minuto).
					lbegin = fambito[j].begin.getTime();
					lbegin -= (lbegin % (60 * 1000));
					lend = fambito[j].end.getTime();
					lend = ((lend / (60 * 1000)) + 1) * 60 * 1000;
					// El fuera de ambito envuelve a la actividad.
					if ((act.getFecComienzo().getTime() >= lbegin) && (act.getFecFin().getTime() <= lend)) {
						lastfa = fambito[j];
					}
					// La actividad envuelve al fuera de ambito.
					else if ((act.getFecComienzo().getTime() <= lbegin) && (act.getFecFin().getTime() >= lend)) {
						act.setFueraAmbito("S");
					}
				}
			}
			// Si la actividad se encuentra dentro de un periodo de fuera de ambito la marco como "S"
			if (lastfa != null) {
				act.setFueraAmbito("S");
			}
		}
	}

	/**
	 * Establece los valores de TransTren para las activiades.
	 *
	 * <pre>
	 * 	 TrasnTren. Indica si se produjo la Condici�n Espec�fica &quot;En Transbordador/Tren&quot;. Puede valer S/N. S�lo puede darse en  actividad &quot;Descanso&quot;.
	 * 	 Origen Tarjeta: EF Specific_Conditions -- SpecificConditionRecord -- specificConditionType
	 * 	 Origen VU (DDT_030) :VuSpecificConditionData -- SpecificConditionRecord -- specificConditionType
	 * </pre>
	 *
	 * @param actividades
	 * @param transtren
	 */

	@Override
	public void mapTransTren(Actividad[] actividades, TransTren[] transtren) {
		Actividad a = null;
		TransTren tt = null;
		long tt_time = 0;
		for (int j = 0; (transtren != null) && (j < transtren.length); j++) {
			tt = transtren[j];
			tt_time = tt.time.getTime();
			for (int i = 0; (actividades != null) && (i < actividades.length); i++) {
				a = actividades[i];
				// Trans Tren solo puede darse en actividad de descanso. NO LO ESTOY COMPROBANDO.
				if ((a.getFecComienzo().getTime() <= tt_time) && (a.getFecFin().getTime() >= tt_time)) {
					a.setTransTren("S");
					break;
				}
			}
		}
	}

	@Override
	public List<VehiculoInsp> getVehiculoInsp() {
		List<VehiculoInsp> l = new ArrayList<>();
		try {
			List<CardVehicleRecord> vr = this.file.getEfVehiclesUsed().getJoinedData().getCardVehiclesUsed().getCardVehicleRecords();
			List<String> carnumbers = new ArrayList<>();
			for (int i = 0; (vr != null) && (i < vr.size()); i++) {
				CardVehicleRecord cvr = vr.get(i);
				String matricula = this.getMatricula(cvr.getVehicleRegistration());

				if ((matricula.length() > 4) && !carnumbers.contains(matricula)) {
					carnumbers.add(matricula);
					VehiculoInsp vi = new VehiculoInsp();
					vi.setMatricula(matricula);
					vi.setEstadoMiembro("" + cvr.getVehicleRegistration().getVehicleRegistrationNation().getValue());
					l.add(vi);
				}
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado VehiculoInsp. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return l;
	}

	@Override
	public List<VehiculoInfrac> getVehiculoInfrac() {
		List<VehiculoInfrac> l = new ArrayList<>();
		try {
			List<CardVehicleRecord> vr = this.file.getEfVehiclesUsed().getJoinedData().getCardVehiclesUsed().getCardVehicleRecords();
			List<String> carnumbers = new ArrayList<>();

			for (int i = 0; (vr != null) && (i < vr.size()); i++) {
				CardVehicleRecord cvr = vr.get(i);
				String matricula = this.getMatricula(cvr.getVehicleRegistration());
				if ((matricula.length() > 4) && !carnumbers.contains(matricula)) {
					carnumbers.add(matricula);
					VehiculoInfrac vi = new VehiculoInfrac();
					vi.setMatricula(matricula);
					l.add(vi);
				}
			}
		} catch (NullPointerException nex) {
			TcFileParser.logger.warn("No se han encontrado VehiculoInfrac. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return l;
	}

	@Override
	public Map<String, List<? extends AbstractData>> parseFile() {
		try {
			Map<String, List<? extends AbstractData>> h = new HashMap<>();

			List<? extends AbstractData> dl = this.getConductores();
			List<? extends AbstractData> al = this.getActividades();
			List<? extends AbstractData> uvl = this.getUsosVehiculo();
			List<? extends AbstractData> fal = this.getFueraAmbito();
			List<? extends AbstractData> ttl = this.getTransTren();
			// Mapeo las matriculas de las actividades.

			// this.mapMatriculas(al.toArray(new Actividad[0]), uvl.toArray(new UsoVehiculo[0]));
			// this.mapConductores(al.toArray(new Actividad[0]), uvl.toArray(new UsoVehiculo[0]));
			if (!fal.isEmpty()) {
				this.mapFueraAmbito(al.toArray(new Actividad[0]), fal.toArray(new FueraAmbito[0]));
			}
			if (!ttl.isEmpty()) {
				this.mapTransTren(al.toArray(new Actividad[0]), ttl.toArray(new TransTren[0]));
			}

			// Driver card no tiene fuera de ambito ni trans tren. <-- JOK: mentira?
			h.put(VehiculoInfrac.class.getName(), this.getVehiculoInfrac());
			h.put(VehiculoInsp.class.getName(), this.getVehiculoInsp());
			h.put(Conductor.class.getName(), dl);
			h.put(Actividad.class.getName(), al);
			h.put(UsoVehiculo.class.getName(), uvl);
			h.put(Incidente.class.getName(), this.getIncidentes());
			h.put(Fallo.class.getName(), this.getFallos());
			h.put(PeriodoTrabajo.class.getName(), this.getPeriodosTrabajo());
			h.put(Calibrado.class.getName(), this.getCalibrados());
			h.put(Control.class.getName(), this.getControles());

			h.put(Tarjeta.class.getName(), this.getTarjetas());

			h.put(RegistroKmConductor.class.getName(), this.getRegistroKmConductor());

			return h;
		} catch (Exception e) {
			TcFileParser.logger.error(null, e);
			return Collections.EMPTY_MAP;
		}
	}

	public Map<String, List<? extends AbstractData>> parseFile(String filename) {
		try {
			TCFile tcFile = (TCFile) TachoFile.readTachoFile(new File(filename));
			this.file = tcFile;
			return this.parseFile();
		} catch (Exception e) {
			TcFileParser.logger.error(null, e);
			return Collections.EMPTY_MAP;
		}
	}

	@Override
	public List<Velocidad> getVelocidades() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<UsoTarjeta> getUsosTarjeta() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<RegistroKmVehiculo> getRegistroKmVehiculo() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<CambioHorario> getCambiosHorarios() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<SensorPairedVU> getIdentificacionSensor() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<IdentificacionVU> getIdentificacionVU() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<CompanyLocksDataVU> getVuCompanyLocksRecord() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Calibrado> getCalibrados() {
		// La tarjeta de conductor no presenta registros de calibrado.
		return Collections.EMPTY_LIST;
	}

	/**
	 * Establece las matricula que le corresponde a cada actividad. Los usos de vehiculo se registran con resoluci�n de segundos mientras que las actividades en resoluci�n de
	 * minutos, para la comparaci�n entre ambos hay que truncar la fechainicio del uso y ampliar la fecha de fin al siguiente minuto.
	 *
	 * @param actividades
	 * @param usosvehiculo
	 */

	@Override
	public void mapMatriculas(Actividad[] actividades, UsoVehiculo[] usosvehiculo) {}

	@Override
	public void mapConductores(Actividad[] actividades, UsoVehiculo[] usosvehiculo) {}

	@Override
	public List<CompanyLocksDataVU> getElementTechnicalData() {
		return null;
	}

	public boolean isManualEntry(ActivityChangeInfo aci) {
		// La informacion manual unicamente se registra en la tarjeta
		return ((aci.getEstadoTarjeta() == ActivityChangeInfo.ESTADO_TARJETA_NO_INSERTADA) && (aci.getRegimenConductor() == ActivityChangeInfo.REGIMEN_KNOWN));
	}

	public boolean isCardWithdrawal(ActivityChangeInfo aci) {
		return ((aci.getEstadoTarjeta() == ActivityChangeInfo.ESTADO_TARJETA_NO_INSERTADA) && (aci.getRegimenConductor() == ActivityChangeInfo.REGIMEN_UNKNOWN));
	}

	/**
	 * Return true if the Record is closed. The CardVehicleRecord is closed when the driver extract the card.
	 *
	 * @param item
	 *
	 * @return
	 */
	public boolean isCardVehicleRecordClosed(CardVehicleRecord item) {
		// Si la tarjeta no se extrae en la fecha fin aparece -1000.
		return ((item.getVehicleLastUse() != null) && (item.getVehicleLastUse().getValue() != null) && (item.getVehicleLastUse().getValue() > 0));
	}

	private String getDriverId(CardIdentification id) {
		String idconductor = null;
		try {
			idconductor = id.getCardNumber().getIdentification().trim();
			if (idconductor.length() == 0) {
				idconductor = Conductor.UNKNOWN_DRIVER;
			}
		} catch (Exception e) {
			idconductor = Conductor.UNKNOWN_DRIVER;
		}
		return idconductor;
	}

	private String getMatricula(VehicleRegistrationIdentification vri) {
		String matricula = vri.getVehicleRegistrationNumber().getValue();
		matricula = matricula.replaceAll(" ", "");
		return matricula;
	}

}
