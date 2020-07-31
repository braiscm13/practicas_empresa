package com.opentach.common.tacho;

import java.util.List;
import java.util.Map;

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
import com.opentach.common.tacho.data.TransTren;
import com.opentach.common.tacho.data.UsoTarjeta;
import com.opentach.common.tacho.data.UsoVehiculo;
import com.opentach.common.tacho.data.VehiculoInfrac;
import com.opentach.common.tacho.data.VehiculoInsp;
import com.opentach.common.tacho.data.Velocidad;

public interface IFileParser {
	public List<Actividad> getActividades();

	public List<Conductor> getConductores();

	public List<Calibrado> getCalibrados();

	public List<Control> getControles();

	public List<Fallo> getFallos();

	public List<Incidente> getIncidentes();

	public List<PeriodoTrabajo> getPeriodosTrabajo();

	public List<UsoVehiculo> getUsosVehiculo();

	public List<FueraAmbito> getFueraAmbito();

	/**
	 * Los usos de tacografo representan los regisros de isnerci�n extracci�n de tarjetas (VuIWCardRecord) en los ficheros de tacografo.
	 *
	 * @return
	 */
	public List<UsoTarjeta> getUsosTarjeta();

	/**
	 * Informaci�n recuperada de VU directamente
	 *
	 * @return Registro del cuenta kilometros del vehiculo al inicio del dia.
	 */
	public List<RegistroKmVehiculo> getRegistroKmVehiculo();

	/**
	 * @return Registro de kilometros diarios recorridos por el conductor.
	 */

	public List<RegistroKmConductor> getRegistroKmConductor();

	/**
	 * Asocia los conductores a las actividades.
	 *
	 * @param actividades
	 * @param usosvehiculo
	 */
	public void mapConductores(Actividad[] actividades, UsoVehiculo[] usosvehiculo);

	// /**
	// * Asocia los conductores a las velocidades.
	// *
	// * @param actividades
	// * @param usosvehiculo
	// */
	//
	// public void mapConductores(Velocidad[] velocidades, UsoVehiculo[] usosvehiculo);

	/**
	 * Asocia las matriculas a las actividades.
	 *
	 * @param actividades
	 * @param usosvehiculo
	 */

	public void mapMatriculas(Actividad[] actividades, UsoVehiculo[] usosvehiculo);

	/**
	 * Establece las actividades que estan fuera de ambito.
	 *
	 * @param actividades
	 * @param usosvehiculo
	 */

	public void mapFueraAmbito(Actividad[] actividades, FueraAmbito[] fambito);

	/**
	 * Establece las actividades contienen un transbordo/tren
	 *
	 * @param actividades
	 * @param usosvehiculo
	 */

	public void mapTransTren(Actividad[] actividades, TransTren[] transtren);

	public List<TransTren> getTransTren();

	public List<VehiculoInfrac> getVehiculoInfrac();

	public List<VehiculoInsp> getVehiculoInsp();

	public List<Velocidad> getVelocidades();

	public List<SensorPairedVU> getIdentificacionSensor();

	public List<IdentificacionVU> getIdentificacionVU();

	public List<CambioHorario> getCambiosHorarios();

	public List<CompanyLocksDataVU> getVuCompanyLocksRecord();

	public List<CompanyLocksDataVU> getElementTechnicalData();

	/**
	 * Procesa todos los datos del fichero retornando un Map con clave el nombre de la clase y valor la lista con objetos. Realiza tambien el mapeado de conductores, matriculas, etc.
	 *
	 * @param filename
	 * @return Map con clave nombre clase (p.e. com.imatia.tacho.infrac.data.Actividad) y valor la lista de objetos.
	 */
	// public Map parseFile(String filename);
	public Map<String, List<? extends AbstractData>> parseFile();

}
