package com.opentach.common.tacho;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.vu.ActivityChangeInfo;
import com.imatia.tacho.model.vu.FullCardNumber;
import com.imatia.tacho.model.vu.SensorPaired;
import com.imatia.tacho.model.vu.SpecificConditionRecord;
import com.imatia.tacho.model.vu.SpecificConditionType;
import com.imatia.tacho.model.vu.VUElementActivities;
import com.imatia.tacho.model.vu.VUFile;
import com.imatia.tacho.model.vu.VehicleRegistrationIdentification;
import com.imatia.tacho.model.vu.VehicleRegistrationIdentificationForSummary;
import com.imatia.tacho.model.vu.VuActivityDailyData;
import com.imatia.tacho.model.vu.VuCalibrationRecord;
import com.imatia.tacho.model.vu.VuCardIWData;
import com.imatia.tacho.model.vu.VuCardIWRecord;
import com.imatia.tacho.model.vu.VuCompanyLocksRecord;
import com.imatia.tacho.model.vu.VuControlActivityRecord;
import com.imatia.tacho.model.vu.VuDetailedSpeedBlock;
import com.imatia.tacho.model.vu.VuEventRecord;
import com.imatia.tacho.model.vu.VuFaultRecord;
import com.imatia.tacho.model.vu.VuIdentification;
import com.imatia.tacho.model.vu.VuOverSpeedingEventRecord;
import com.imatia.tacho.model.vu.VuPlaceDailyWorkPeriodRecord;
import com.imatia.tacho.model.vu.VuTimeAdjustmentRecord;
import com.opentach.common.tacho.data.AbstractData;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Calibrado;
import com.opentach.common.tacho.data.CalibradoDataVU;
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
import com.opentach.common.tacho.data.TransTren;
import com.opentach.common.tacho.data.UsoTarjeta;
import com.opentach.common.tacho.data.UsoVehiculo;
import com.opentach.common.tacho.data.VehiculoInfrac;
import com.opentach.common.tacho.data.VehiculoInsp;
import com.opentach.common.tacho.data.Velocidad;
import com.opentach.common.util.DateUtil;

public class VuFileParser implements IFileParser {

	private static final Logger logger = LoggerFactory.getLogger(VuFileParser.class);

	public static final String TACHO_VU = "VU";
	public static final int NOF_SLOTS = 2;
	protected VUFile file;

	public VuFileParser(VUFile file) {
		this.file = file;
	}

	@Override
	public List<UsoTarjeta> getUsosTarjeta() {
		List<UsoTarjeta> utaco = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());

			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				if (ablock.isCorrupted()) {
					continue;
				}
				List<VuCardIWRecord> vehRecords = ablock.getVuCardIWData().getRecords();
				for (int i = 0; (vehRecords != null) && (i < vehRecords.size()); i++) {
					VuCardIWRecord item = vehRecords.get(i);
					// No se aceptan registros con fecha fin < fecha inicio.
					// Un uso vehiculo no esta completo hasta que se existe
					// la fecha de retirada de la tarjeta
					// POR LO TANTO SI EL USO NO ESTA CERRADO DEBE DESCARTARSE.
					if (item.isVuCardIWRecordClosed()) {
						UsoTarjeta ut = new UsoTarjeta();
						ut.setFechaHoraInsercion(item.getCardInsertionTime().getDate());
						ut.setRanura(Integer.toString(item.getCardSlotNumber().getValue()));
						ut.setNumTrjConductor(item.getFullCardNumber().getCardNumber().getFullCardNumber());
						ut.setIdConductor(this.getDriverId(item.getFullCardNumber()));
						ut.setNombre(item.getCardHolderName().getHolderFirstNames().getValue());
						ut.setApellidos(item.getCardHolderName().getHolderSurname().getValue());
						ut.setKmInsercion(item.getVehicleOdometerValueAtInsertion().getValue());
						ut.setFechaHoraExtraccion(item.getCardWithdrawalTime().getDate());
						ut.setKmExtraccion(item.getVehicleOdometerValueAtWithdrawal().getValue());
						ut.setMatricula(matricula);
						// Informacion del anterior uso
						ut.setFechaHoraExtAnterior(item.getPreviousVehicleInfo().getCardWithdrawalTime().getDate());

						ut.setMatriculaAnterior(this
								.getMatricula(item.getPreviousVehicleInfo().getVehicleRegistrationIdentification()));
						utaco.add(ut);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Usos Vehiculo. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return utaco;
	}

	@Override
	public List<PeriodoTrabajo> getPeriodosTrabajo() {
		List<PeriodoTrabajo> pts = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				if (ablock.isCorrupted()) {
					continue;
				}
				List<VuPlaceDailyWorkPeriodRecord> pRecords = ablock.getVuPlaceDailyWorkerPeriodData().getRecords();
				for (int i = 0; (pRecords != null) && (i < pRecords.size()); i++) {
					VuPlaceDailyWorkPeriodRecord item = pRecords.get(i);
					PeriodoTrabajo pt = new PeriodoTrabajo();
					pt.setFecIni(item.getPlaceRecord().getEntryTime().getDate());
					pt.setIdConductor(this.getDriverId(item.getFullCardNumber()));
					pt.setTpPeriodo(item.getPlaceRecord().getEntryTypeDailyWorkPeriod().getValue());
					pt.setKm(item.getPlaceRecord().getVehicleOdometerValue().getValue());
					pt.setPais("" + item.getPlaceRecord().getDailyWorkPeriodCountry().getValue());
					pt.setRegion("" + item.getPlaceRecord().getDailyWorkPeriodRegion().getValue());
					pt.setProcedencia(VuFileParser.TACHO_VU);
					pts.add(pt);
				}
			}
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
		}
		return pts;
	}

	@Override
	public List<Conductor> getConductores() {
		List<Conductor> drivers = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			List<String> locateddrivers = new ArrayList<>();

			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				// Solo proceso bloques no corruptos.
				if (ablock.isCorrupted()) {
					continue;
				}
				List<VuCardIWRecord> vehRecords = ablock.getVuCardIWData().getRecords();
				for (int i = 0; (vehRecords != null) && (i < vehRecords.size()); i++) {
					VuCardIWRecord item = vehRecords.get(i);
					// Se tienen que aceptar todos los registros de
					// inserción extraccción de tarjeta.
					// registros con fecha fin < fecha inicio.
					String iddriver = this.getDriverId(item.getFullCardNumber());
					if (!locateddrivers.contains(iddriver)) {
						Conductor driver = new Conductor();
						driver.IdConductor = iddriver;
						// El numero de tarjeta se construye concatenando
						// los caracteres de renovacion y replazo.
						driver.setNumTrjCondu(item.getFullCardNumber().getCardNumber().getFullCardNumber());
						driver.setApellidos(item.getCardHolderName().getHolderSurname().getValue());
						driver.setNombre(item.getCardHolderName().getHolderFirstNames().getValue());
						driver.setEquipmentType(item.getFullCardNumber().getCardType().getValue());
						driver.setNationNumeric(item.getFullCardNumber().getCardIssuingMemberState().getValue());
						locateddrivers.add(iddriver);
						drivers.add(driver);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Controles. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return drivers;
	}

	@Override
	public List<Actividad> getActividades() {
		List<Actividad> activs = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());

			long MSDAY = (long) 24 * 60 * 60 * 1000;
			long MIN = 60 * 1000;

			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {

				VUElementActivities ablock = iter.next();
				Date day = ablock.getActivityDate().getDate();

				// Proceso datos de registros no manipulados.
				if (ablock.isCorrupted()) {
					continue;
				}
				VuActivityDailyData activDailyData = ablock.getVuActivityDailyData();
				// Los registros de cambio aparecen de las dos ranuras.
				ActivityChangeInfo[] startaci = new ActivityChangeInfo[VuFileParser.NOF_SLOTS];
				ActivityChangeInfo[] endaci = new ActivityChangeInfo[VuFileParser.NOF_SLOTS];
				long[] start = new long[VuFileParser.NOF_SLOTS];
				long[] end = new long[VuFileParser.NOF_SLOTS];

				// Empiezo por el dia más antiguo.
				// for (int i = activsDay.length - 1; activsDay != null && i
				// >= 0; i--) {
				long daytime = day.getTime() - (day.getTime() % MSDAY);
				List<ActivityChangeInfo> acis = activDailyData.getRecords();

				int slot = 0;

				List<Actividad> dayActivs = new ArrayList<>();
				for (int j = 0; (acis != null) && (j < acis.size()); j++) {
					ActivityChangeInfo aci = acis.get(j);
					slot = aci.getRanura();
					endaci[slot] = aci;
					end[slot] = daytime + ((long) endaci[slot].getMinutosCambio() * 60 * 1000);
					if (startaci[slot] != null) {
						Actividad act = new Actividad();

						// OJO el codigo de las actividades en Java es de
						// 1-4 mientras que en los datos de la tarjeta es de
						// 0-3
						act.tpActividad = Integer.valueOf(startaci[slot].getActividad() + 1);

						// La informacion manual unicamente se registra en la tarjeta
						act.setOrigen("A");
						act.setProcedencia(VuFileParser.TACHO_VU);
						act.estTrjRanura = Byte.toString(startaci[slot].getEstadoTarjeta());
						act.ranura = Byte.toString(startaci[slot].getRanura());
						act.regimen = Byte.toString(startaci[slot].getRegimenConductor());
						act.setFecComienzo(new Date(start[slot]));
						act.setFecFin(new Date(end[slot]));
						act.setMatricula(matricula);
						activs.add(act);
						dayActivs.add(act);
					}
					// En VU tras la extracción de una tarjeta si que existe
					// actividad.
					startaci[slot] = endaci[slot];
					start[slot] = end[slot];
				}
				// Si existe una actividad con inicio pero sin fin de un dia
				// se cierra a las 00:00 horas del dia siguiente.
				for (slot = 0; slot < VuFileParser.NOF_SLOTS; slot++) {
					// Cierro las actividades de todos los dias salvo el
					// ultimo dia.
					if ((startaci[slot] != null) && iter.hasNext()) {
						Actividad act = new Actividad();
						act.tpActividad = Integer.valueOf(startaci[slot].getActividad() + 1);
						// La informacion manual unicamente se registra en la tarjeta
						act.setOrigen("A");
						act.setProcedencia(VuFileParser.TACHO_VU);
						act.setEstTrjRanura(Byte.toString(startaci[slot].getEstadoTarjeta()));
						act.ranura = Byte.toString(startaci[slot].getRanura());
						act.regimen = Byte.toString(startaci[slot].getRegimenConductor());
						act.setFecComienzo(new Date(start[slot]));
						// Final del dia=Inicio del dia siguiente.
						end[slot] = daytime + MSDAY;
						act.setFecFin(new Date(end[slot]));
						act.setMatricula(matricula);
						activs.add(act);
						dayActivs.add(act);
						startaci[slot] = null;
					}
				}

				// El modelo Storenidge registra las CardIWData de diferente
				// forma al siemens por lo que
				// el mapeo de los conductores debe realizarse de forma diaria.
				// Mapeo los conductores a las activiades del dia.
				VuCardIWData cardIWData = ablock.getVuCardIWData();

				List<VuCardIWRecord> cardrecords = cardIWData.getRecords();
				for (int i = 0; (cardrecords != null) && (i < cardrecords.size()); i++) {
					VuCardIWRecord ciw = cardrecords.get(i);
					if ((ciw.getCardInsertionTime().getDate() == null)) {
						continue;
					}
					// OJO:Los tiempos de inicio-fin de los usos vehiculo estan
					// en resolucion de segundos,
					// mientras que la activdiad es por miniutos.
					// Es necesario truncar la fecha de inicio (0 segundos del
					// minito) y ampliar la de finalizacion (el inicio del
					// siguiente minuto).

					long ciwstart = ciw.getCardInsertionTime().getDate().getTime();
					ciwstart -= (ciwstart % (MIN));
					long ciwend = 0;
					boolean ciwclosed = ciw.isVuCardIWRecordClosed();
					if (ciwclosed) {
						ciwend = ciw.getCardWithdrawalTime().getDate().getTime();
						ciwend = ((ciwend / MIN) + 1) * MIN;
					}

					for (Iterator<Actividad> iterator = dayActivs.iterator(); iterator.hasNext();) {
						Actividad activ = iterator.next();
						if ((ciw.getCardSlotNumber().getValue() == Integer.parseInt(activ.getRanura()))
								&& (ciwstart <= activ.getFecComienzo().getTime())
								&& ((ciwend >= activ.getFecFin().getTime()) || !ciwclosed)) {
							// EN OPENTACH SE UTILIZA EL IDCONDUCTOR LOS 14
							// CARACTERES DEL driver IDENTIFICACION.
//							System.out.println(activ.getFecComienzo() +"  - " + activ.getFecFin());
							activ.idConductor = this.getDriverId(ciw.getFullCardNumber());
							iterator.remove();
						} else {
							// System.out.println();
						}
					}
				}
			} // EOF day...Next ActivityBlock (next registered day).

		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Actividades. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return activs;
	}

	@Override
	public List<RegistroKmVehiculo> getRegistroKmVehiculo() {
		List<RegistroKmVehiculo> lReg = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());

			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				// Proceso datos de registros no manipulados.
				if (ablock.isCorrupted()) {
					continue;
				}
				RegistroKmVehiculo rveh = new RegistroKmVehiculo();
				rveh.setKilometros(new Integer(ablock.getOdometerValueMidnight().getValue()));
				rveh.setFecha(ablock.getActivityDate().getDate());
				rveh.setMatricula(matricula);
				lReg.add(rveh);
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Actividades. " + nex + ". " + nex.getStackTrace()[0]);
		}

		for (RegistroKmVehiculo o : lReg) {
			RegistroKmVehiculo rkm = o;
			Date date = rkm.getFecha();
			rkm.setFecha(DateUtil.truncToEnd(date));
		}
		return lReg;
	}

	@Override
	public List<Calibrado> getCalibrados() {
		List<Calibrado> lres = new ArrayList<>();

		try {
			if ((this.file.getTechnicalData() != null) && !this.file.getTechnicalData().isCorrupted()) {
				List<VuCalibrationRecord> cRecords = this.file.getTechnicalData().getVuCalibrationData().getRecords();
				for (int i = 0; (cRecords != null) && (i < cRecords.size()); i++) {
					VuCalibrationRecord cr = cRecords.get(i);
					if (cr != null) {
						Calibrado cal = new Calibrado();
						cal.setTpCalibrado(cr.getCalibrationPurpose().getValue());
						cal.nombTaller = cr.getWorkshopName().getValue();
						cal.setFecProximo(cr.getNextClaibrationDate().getDate());
						cal.setNumTrjTaller(cr.getWorkshopCardNumber().getCardNumber().getIdentification());
						cal.setMatricula(this.getMatricula(cr.getVehicleRegistrationIdentification()));
						cal.setCoeficiente(cr.getwVehicleCharacteristicConstant().getValue());
						cal.setConstante(cr.getkConstantOfRecordingEquipment().getValue());
						cal.setTyreCircumference(cr.getlTyreCircumference().getValue());
						lres.add(cal);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Calibrados. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception e) {
			VuFileParser.logger.error(null, e);
		}
		return lres;

	}

	/**
	 * Retorna listado de incidentes. Se consideran incidentes los regsitrados en
	 * EventData asi como los excesos de velocidad ( registrados en
	 * VuOverSpeedingEventData).
	 *
	 * @param file
	 * @return
	 */
	@Override
	public List<Incidente> getIncidentes() {
		List<Incidente> events = new ArrayList<>();
		try {
			if (!this.file.getIncidencesAndFails().isCorrupted()) {
				String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
				String pais = "" + this.file.getSummary().getVehicleRegistrationIdentification()
						.getVehicleRegistrationNation().getValue();

				if (this.file.getIncidencesAndFails().getVuEventData() != null) {
					List<VuEventRecord> evRecords = this.file.getIncidencesAndFails().getVuEventData().getRecords();

					for (int i = 0; (evRecords != null) && (i < evRecords.size()); i++) {
						VuEventRecord item = evRecords.get(i);
						Incidente ev = new Incidente();
						ev.setFechHoraIni(item.getEventBeginTime().getDate());
						ev.setFechHoraFin(item.getEventEndTime().getDate());
						ev.setTipo(item.getEventType().getValue());
						ev.setProposito(item.getEventRecordPurpose().getValue());
						ev.setNumTrjConduIni(item.getCardNumberDriverSlotBegin().getCardNumber().getFullCardNumber());
						ev.setNumTrjConduFin(item.getCardNumberDriverSlotEnd().getCardNumber().getFullCardNumber());
						ev.setNumTrjCopiIni(item.getCardNumberCodriverSlotBegin().getCardNumber().getFullCardNumber());
						ev.setNumTrjCopiFin(item.getCardNumberCodriverSlotEnd().getCardNumber().getFullCardNumber());
						ev.setNumInciSimilares(item.getSimilarEventsNumber().getValue());
						ev.setPais(pais);
						ev.setProcedencia(VuFileParser.TACHO_VU);

						ev.setMatricula(matricula);
						events.add(ev);
					}
				}
				if (this.file.getIncidencesAndFails().getVuOverSpeedingEventData() != null) {
					List<VuOverSpeedingEventRecord> osRecords = this.file.getIncidencesAndFails()
							.getVuOverSpeedingEventData().getRecords();
					for (int i = 0; (osRecords != null) && (i < osRecords.size()); i++) {
						VuOverSpeedingEventRecord item = osRecords.get(i);
						Incidente ev = new Incidente();
						ev.setFechHoraIni(item.getEventBeginTime().getDate());
						ev.setFechHoraFin(item.getEventEndTime().getDate());
						ev.setTipo(item.getEventType().getValue());
						ev.setProposito(item.getEventRecordPurpose().getValue());
						ev.setMatricula(matricula);
						ev.setNumTrjConduIni(item.getCardNumberDriverSlotBegin().getCardNumber().getFullCardNumber());
						ev.setVelocidadMax(item.getMaxSpeedValue());
						ev.setVelocidadMedia(item.getAverageSpeedValue());
						ev.setNumInciSimilares(item.getSimilarEventsNumber().getValue());
						ev.setPais(pais);
						ev.setProcedencia(VuFileParser.TACHO_VU);
						events.add(ev);
					}
				}
			}
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
		}
		return events;
	}

	@Override
	public List<Fallo> getFallos() {
		List<Fallo> faults = new ArrayList<>();
		try {
			if (this.file.getIncidencesAndFails().isCorrupted()) {
				return faults;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());

			if (this.file.getIncidencesAndFails().getVuFaultData() != null) {
				List<VuFaultRecord> fRecords = this.file.getIncidencesAndFails().getVuFaultData().getRecords();
				for (int i = 0; (fRecords != null) && (i < fRecords.size()); i++) {
					VuFaultRecord item = fRecords.get(i);
					Fallo fail = new Fallo();
					fail.setTpFallo(item.getFaultType().getValue());
					fail.setfecHoraIni(item.getFaultBeginTime().getDate());
					fail.setfecHoraFin(item.getFaultEndTime().getDate());
					fail.setProposito(item.getFaultRecordPurpose().getValue());
					fail.setMatricula(matricula);
					fail.setNumTrjConduIni(item.getCardNumberDriverSlotBegin().getCardNumber().getFullCardNumber());
					fail.setNumTrjConduFin(item.getCardNumberDriverSlotEnd().getCardNumber().getFullCardNumber());
					fail.setNumTrjCopiIni(item.getCardNumberCodriverSlotBegin().getCardNumber().getFullCardNumber());
					fail.setNumTrjCopiFin(item.getCardNumberCodriverSlotEnd().getCardNumber().getFullCardNumber());
					faults.add(fail);
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Fallos. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
		}
		return faults;
	}

	@Override
	public List<FueraAmbito> getFueraAmbito() {
		List<FueraAmbito> rtn = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				if (ablock.isCorrupted()) {
					continue;
				}
				List<SpecificConditionRecord> pcal = ablock.getVuSpecificConditionData().getRecords();
				FueraAmbito fa = null;
				for (int i = 0; (pcal != null) && (i < pcal.size()); i++) {
					SpecificConditionRecord item = pcal.get(i);
					if (SpecificConditionType.FUERA_AMBITO_COMIENZO == item.getSpecificConditionType().getValue()) {
						fa = new FueraAmbito();
						fa.begin = item.getEntryTime().getDate();
					} else if ((SpecificConditionType.FUERA_AMBITO_FIN == item.getSpecificConditionType().getValue())
							&& (fa != null)) {
						fa.end = item.getEntryTime().getDate();
						rtn.add(fa);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado FueraAmbito. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return rtn;
	}

	@Override
	public List<VehiculoInsp> getVehiculoInsp() {
		List<VehiculoInsp> lres = new ArrayList<>();
		try {
			if (this.file.getSummary().isCorrupted()) {
				return lres;
			}
			int estado = this.file.getSummary().getVehicleRegistrationIdentification().getVehicleRegistrationNation()
					.getValue();
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			VehiculoInsp vi = new VehiculoInsp();
			vi.setMatricula(matricula);
			vi.setEstadoMiembro("" + estado);
			lres.add(vi);
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado VehiculosInsp. " + nex + ". " + nex.getStackTrace()[0]);
		}

		return lres;
	}

	@Override
	public List<VehiculoInfrac> getVehiculoInfrac() {
		List<VehiculoInfrac> lres = new ArrayList<>();
		try {
			if (this.file.getSummary().isCorrupted()) {
				return lres;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			VehiculoInfrac vh = new VehiculoInfrac();
			vh.setMatricula(matricula);
			lres.add(vh);
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado VehiculoInfrac. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return lres;

	}

	@Override
	public List<TransTren> getTransTren() {
		List<TransTren> rtn = new ArrayList<>();
		try {
			List<VUElementActivities> listBlocks = this.file.getActivities();
			for (Iterator<VUElementActivities> iter = listBlocks.iterator(); iter.hasNext();) {
				VUElementActivities ablock = iter.next();
				if (ablock.isCorrupted()) {
					continue;
				}
				List<SpecificConditionRecord> pcal = ablock.getVuSpecificConditionData().getRecords();
				TransTren fa = null;
				for (int i = 0; (pcal != null) && (i < pcal.size()); i++) {
					SpecificConditionRecord item = pcal.get(i);
					if (item.getSpecificConditionType()
							.getValue() == SpecificConditionType.PUENTE_PASO_NIVEL_COMIENZO) {
						fa = new TransTren(item.getEntryTime().getDate());
						rtn.add(fa);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado TransTren. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return rtn;
	}

	@Override
	public List<Control> getControles() {
		List<Control> lres = new ArrayList<>();
		try {
			if (this.file.getSummary().isCorrupted()) {
				return lres;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			List<VuControlActivityRecord> cRecords = this.file.getSummary().getVuControlActivityData().getRecords();
			for (int i = 0; (cRecords != null) && (i < cRecords.size()); i++) {
				VuControlActivityRecord cr = cRecords.get(i);
				if (cr != null) {
					Control c = new Control();
					c.setFechaHora(cr.getControlTime().getDate());
					c.setTpControl(Integer.valueOf(cr.getControlType().getValue()));
					c.setNumTrjControl(cr.getControlCardNumber().getCardNumber().getIdentification());
					c.setMatricula(matricula);
					lres.add(c);
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Controles. " + nex + ". " + nex.getStackTrace()[0]);
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
					// OJO:Los tiempos de inicio-fin de los usos vehiculo estan en resolucion de
					// segundos,
					// mientras que la activdiad es por miniutos.
					// Es necesario truncar la fecha de inicio (0 segundos del minito) y ampliar la
					// de finalizaci�n (el inicio del siguiente minuto).
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
			// Si la actividad se encuentra dentro de un periodo de fuera de ambito la marco
			// como "S"
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
		Actividad act = null;
		TransTren tt = null;
		long tt_time = 0;
		for (int j = 0; (transtren != null) && (j < transtren.length); j++) {
			tt = transtren[j];
			tt_time = tt.time.getTime();
			for (int i = 0; (actividades != null) && (i < actividades.length); i++) {
				act = actividades[i];
				// Trans Tren solo puede darse en actividad de descanso. NO LO ESTOY
				// COMPROBANDO.
				if ((act.getFecComienzo().getTime() <= tt_time) && (act.getFecFin().getTime() >= tt_time)) {
					act.setTransTren("S");
					break;
				}
			}
		}
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

			this.mapMatriculas(al.toArray(new Actividad[0]), uvl.toArray(new UsoVehiculo[0]));
			this.mapConductores(al.toArray(new Actividad[0]), uvl.toArray(new UsoVehiculo[0]));
			if (!fal.isEmpty()) {
				this.mapFueraAmbito(al.toArray(new Actividad[0]), fal.toArray(new FueraAmbito[0]));
			}
			if (!ttl.isEmpty()) {
				this.mapTransTren(al.toArray(new Actividad[0]), ttl.toArray(new TransTren[0]));
			}

			h.put(VehiculoInsp.class.getName(), this.getVehiculoInsp());
			h.put(VehiculoInfrac.class.getName(), this.getVehiculoInfrac());
			h.put(Conductor.class.getName(), dl);
			h.put(Actividad.class.getName(), al);
			h.put(UsoVehiculo.class.getName(), uvl);
			h.put(Incidente.class.getName(), this.getIncidentes());
			h.put(Fallo.class.getName(), this.getFallos());
			h.put(PeriodoTrabajo.class.getName(), this.getPeriodosTrabajo());
			h.put(Calibrado.class.getName(), this.getCalibrados());
			h.put(Control.class.getName(), this.getControles());

			h.put(UsoTarjeta.class.getName(), this.getUsosTarjeta());
			h.put(RegistroKmVehiculo.class.getName(), this.getRegistroKmVehiculo());
			h.put(Velocidad.class.getName(), this.getVelocidades());

			h.put(CambioHorario.class.getName(), this.getCambiosHorarios());
			h.put(IdentificacionVU.class.getName(), this.getIdentificacionVU());
			h.put(SensorPairedVU.class.getName(), this.getIdentificacionSensor());

			h.put(CompanyLocksDataVU.class.getName(), this.getVuCompanyLocksRecord());
			h.put(CalibradoDataVU.class.getName(), this.getCalibradosDatosTecnicos());

			return h;
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
			return Collections.EMPTY_MAP;
		}
	}

	public Map<String, List<? extends AbstractData>> parseFile(String filename) {
		try {
			this.file = (VUFile) TachoFile.readTachoFile(new File(filename));
			return this.parseFile();
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
			return Collections.EMPTY_MAP;
		}
	}

	@Override
	public List<Velocidad> getVelocidades() {

		List<Velocidad> rtn = new ArrayList<>();
		try {
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			if ((this.file.getSpeed() != null) && (this.file.getSpeed().getVuDetailedSpeedData() != null)
					&& (this.file.getSpeed().getVuDetailedSpeedData().getRecords() != null)
					&& !this.file.getSpeed().isCorrupted()) {

				List<VuDetailedSpeedBlock> sb = this.file.getSpeed().getVuDetailedSpeedData().getRecords();
				int vprev = -1;
				long vant = -1;
				for (int i = 0; (sb != null) && (i < sb.size()); i++) {
					VuDetailedSpeedBlock s = sb.get(i);

					Velocidad v = new Velocidad();
					v.setFechaHora(s.getSpeedBlockBeginDate().getDate());
					v.setMatricula(matricula);
					if (i == 0) {
						vprev = s.getSpeedsPerSecond()[0];
					} else if ((s.getSpeedBlockBeginDate().getDate().getTime() - 60000) != vant) {
						vprev = 0;
					}
					v.setVelocidades(s.getSpeedsPerSecond(), vprev);
					rtn.add(v);
					vprev = s.getSpeedsPerSecond()[59];
					vant = s.getSpeedBlockBeginDate().getDate().getTime();
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger
					.warn("No se han encontrado registro de velocidades. " + nex + ". " + nex.getStackTrace()[0]);
		}
		return rtn;
	}

	@Override
	public List<CambioHorario> getCambiosHorarios() {
		List<CambioHorario> rtn = new ArrayList<>();
		try {
			if ((this.file.getIncidencesAndFails() == null) || this.file.getIncidencesAndFails().isCorrupted()) {
				return rtn;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());

			if (this.file.getIncidencesAndFails().getVuTimeAdjustmentData() != null) {
				List<VuTimeAdjustmentRecord> evTimeAdjs = this.file.getIncidencesAndFails().getVuTimeAdjustmentData()
						.getRecords();

				for (int i = 0; (evTimeAdjs != null) && (i < evTimeAdjs.size()); i++) {
					VuTimeAdjustmentRecord item = evTimeAdjs.get(i);
					CambioHorario ch = new CambioHorario();
					ch.setOldTime(item.getOldTimeValue().getDate());
					ch.setNewTime(item.getNewTimeValue().getDate());
					ch.setCentroEnsayo(item.getWorkshopName().getValue());
					ch.setDireccionCentroEnsayo(item.getWorkshopAddress().getValue());
					if (item.getWorkshopCardNumber() != null) {
						ch.setTrjCentroEnsayo(item.getWorkshopCardNumber().getCardNumber().getFullCardNumber());
						ch.setMatricula(matricula);
						rtn.add(ch);
					}
				}
			}
		} catch (Exception ex) {
			VuFileParser.logger.error(null, ex);
		}
		return rtn;
	}

	public List<CalibradoDataVU> getCalibradosDatosTecnicos() {
		List<CalibradoDataVU> l = new ArrayList<>();

		try {
			if ((this.file.getTechnicalData() != null) && !this.file.getTechnicalData().isCorrupted()) {

				String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
				List<VuCalibrationRecord> cRecords = this.file.getTechnicalData().getVuCalibrationData().getRecords();
				for (int i = 0; (cRecords != null) && (i < cRecords.size()); i++) {
					VuCalibrationRecord cr = cRecords.get(i);
					if (cr != null) {
						CalibradoDataVU cal = new CalibradoDataVU();
						cal.matricula = matricula;
						cal.establecimiento = cr.getWorkshopName().getValue();
						cal.dir_establecimiento = cr.getWorkshopAddress().getValue();
						cal.num_tarjeta = cr.getWorkshopCardNumber().getCardNumber().getFullCardNumber();
						cal.f_expedicion_tarj = cr.getWorkshopCardExpiryDate().getDate();
						cal.identificacion_veh = this.getMatricula(cr.getVehicleRegistrationIdentification());
						cal.nacionalidad_veh = cr.getVehicleRegistrationIdentification().getVehicleRegistrationNation()
								.getValue();
						cal.coeficiente_veh = cr.getkConstantOfRecordingEquipment().getValue();
						cal.constante_control = cr.getwVehicleCharacteristicConstant().getValue();
						cal.circuns_neumaticos = cr.getlTyreCircumference().getValue();
						cal.dir_neumaticos = cr.getTyreSize().getValue();
						cal.velocidad_auto = cr.getAuthorisedSpeed();
						cal.old_odometro = cr.getOldOdometerValue().getValue();
						cal.new_odometro = cr.getNewOdometerValue().getValue();
						cal.f_old_value = cr.getOldTimeValue().getDate();
						cal.f_new_value = cr.getNewTimeValue().getDate();
						cal.f_next_calibrado = cr.getNextClaibrationDate().getDate();
						l.add(cal);
					}
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Calibrados. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception e) {
			VuFileParser.logger.error(null, e);
		}
		return l;

	}

	@Override
	public List<SensorPairedVU> getIdentificacionSensor() {
		List<SensorPairedVU> lres = new ArrayList<>();

		String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
		SensorPaired sp = this.file.getTechnicalData().getSensorPaired();
		if (sp != null) {
			SensorPairedVU s = new SensorPairedVU();
			s.matricula = matricula;
			s.numeroSerie = sp.getSensorSerialNumber().getValue();
			s.numeroAprobacion = sp.getSensorApprovalNumber().getValue();
			s.fechaPrimerAcoplamiento = sp.getSensorPairingDateFirst().getDate();
			lres.add(s);
		}
		return lres;
	}

	@Override
	public List<IdentificacionVU> getIdentificacionVU() {
		List<IdentificacionVU> lres = new ArrayList<>();
		try {
			if ((this.file.getTechnicalData() == null) || this.file.getTechnicalData().isCorrupted()) {
				return lres;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			VuIdentification cr = this.file.getTechnicalData().getVuIdentification();
			if (cr != null) {
				IdentificacionVU c = new IdentificacionVU();
				c.matricula = matricula;
				c.direccionFabricante = cr.getVuManufacturerAddress().getValue();
				c.fechaFabricacion = cr.getVuManufacturingDate().getDate();
				c.nombreFabricante = cr.getVuManufacturerName().getValue();
				c.numeroHomologacion = cr.getVuApprovalNumber().getValue();
				c.numeroPiezaTacografo = cr.getVuPartNumber().getValue();
				c.numeroSerieTacografo = cr.getVuSerialNumber().getValue();
				c.versionSoftware = cr.getVuSoftwareIdentification().getVuSoftwareVersion().getValue();
				c.versionSoftwareDate = cr.getVuSoftwareIdentification().getVuSoftInstallactionDate().getDate();
				lres.add(c);
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Calibrados. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception e) {
			VuFileParser.logger.error(null, e);
		}
		return lres;
	}

	@Override
	public List<CompanyLocksDataVU> getVuCompanyLocksRecord() {
		List<CompanyLocksDataVU> lres = new ArrayList<>();
		try {

			if ((this.file.getTechnicalData() == null) || this.file.getTechnicalData().isCorrupted()) {
				return lres;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			List<VuCompanyLocksRecord> cr = this.file.getSummary().getVuCompanyLocksData().getRecords();
			if (cr != null) {
				for (int i = 0; (cr != null) && (i < cr.size()); i++) {
					VuCompanyLocksRecord item = cr.get(i);
					CompanyLocksDataVU cld = new CompanyLocksDataVU();
					cld.matricula = matricula;
					cld.lockInTime = item.getLockInTime().getDate();
					cld.lockOutTime = item.getLockOutTime().getDate();
					cld.companyName = item.getCompanyName().getValue();
					cld.companyAddress = item.getCompanyAddress().getValue();
					cld.companyCardNumber = item.getCompanyCardNumber().getCardNumber().getFullCardNumber();
					lres.add(cld);
				}
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Bloqueos. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception e) {
			VuFileParser.logger.error(null, e);
		}
		return lres;
	}

	@Override
	public List<CompanyLocksDataVU> getElementTechnicalData() {
		List<CompanyLocksDataVU> lres = new ArrayList<>();
		try {

			if ((this.file.getTechnicalData() == null) || this.file.getTechnicalData().isCorrupted()) {
				return lres;
			}
			String matricula = this.getMatricula(this.file.getSummary().getVehicleRegistrationIdentification());
			List<VuCompanyLocksRecord> cr = this.file.getSummary().getVuCompanyLocksData().getRecords();
			for (int i = 0; (cr != null) && (i < cr.size()); i++) {
				VuCompanyLocksRecord item = cr.get(i);

				CompanyLocksDataVU c = new CompanyLocksDataVU();
				c.matricula = matricula;
				c.lockInTime = item.getLockInTime().getDate();
				c.lockOutTime = item.getLockOutTime().getDate();
				c.companyName = item.getCompanyName().getValue();
				c.companyAddress = item.getCompanyAddress().getValue();
				c.companyCardNumber = item.getCompanyCardNumber().getCardNumber().getFullCardNumber();
				lres.add(c);
			}
		} catch (NullPointerException nex) {
			VuFileParser.logger.warn("No se han encontrado Bloqueos. " + nex + ". " + nex.getStackTrace()[0]);
		} catch (Exception e) {
			VuFileParser.logger.error(null, e);
		}
		return lres;
	}

	private String getDriverId(FullCardNumber fullCardNumber) {
		try {
			String iddriver = fullCardNumber.getCardNumber().getIdentification().trim();
			if (iddriver.length() == 0) {
				iddriver = Conductor.UNKNOWN_DRIVER;
			}
			return iddriver;
		} catch (Exception ex) {
			VuFileParser.logger.warn(null, ex);
			return Conductor.UNKNOWN_DRIVER;
		}
	}

	private String getMatricula(VehicleRegistrationIdentificationForSummary vri) {
		String matricula = vri.getVehicleRegistrationNumber().getValue();
		matricula = matricula.replaceAll(" ", "");
		return matricula;
	}

	private String getMatricula(VehicleRegistrationIdentification vri) {
		String matricula = vri.getVehicleRegistrationNumber().getValue();
		matricula = matricula.replaceAll(" ", "");
		return matricula;
	}

	@Override
	public void mapMatriculas(Actividad[] actividades, UsoVehiculo[] usosvehiculo) {
		// DUMMY METHOD
	}

	/**
	 * En los ficheros de tacografo no aparece directamente la informaci�n de usos
	 * de vehiculo
	 */
	@Override
	public List<UsoVehiculo> getUsosVehiculo() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void mapConductores(Actividad[] actividades, UsoVehiculo[] usosvehiculo) {
		// El mapeado de los conductores y activiades se realiza diariamente.
	}

	@Override
	public List<RegistroKmConductor> getRegistroKmConductor() {
		return Collections.EMPTY_LIST;
	}

}
