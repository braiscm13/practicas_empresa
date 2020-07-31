package com.opentach.common.tacho.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conductor extends AbstractData {

	private static final Logger	logger					= LoggerFactory.getLogger(Conductor.class);

	public String				IdConductor				= null;
	public String				Nombre					= null;
	public String				Apellidos				= null;
	public String				NumTrjCondu				= null;
	public Date					ExpiredDateNumTrjCondu	= null;
	public Date					BirthdayConductor		= null;

	public String				regss					= null;

	public int					ranura					= -1;
	public String				manualInputFlag			= null;
	public int					equipment				= -1;
	public int					nationNumeric			= -1;
	public List<Object>			usoVehiculo				= null;
	public List<Object>			actividades				= null;
	public List<Object>			periodosTrabajo			= null;

	public String				usuarioModif			= null;
	public Date					fAlta					= null;
	public String				usuarioAlta				= null;

	public static String		UNKNOWN_DRIVER			= "XXXXXXXXX";

	public Conductor() {
		super();
	}

	public Date getBirthdayConductor() {
		return this.BirthdayConductor;
	}

	public void setBirthdayConductor(Date BirthdayConductor) {
		this.BirthdayConductor = BirthdayConductor;
	}

	public Date getFAlta() {
		return this.fAlta;
	}

	public void setFAlta(Date fAlta) {
		this.fAlta = fAlta;
	}

	public String getUsuarioModif() {
		return this.usuarioModif;
	}

	public void setUsuarioModif(String usuarioModif) {
		this.usuarioModif = usuarioModif.trim();
	}

	public String getUsuarioAlta() {
		return this.usuarioAlta;
	}

	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta.trim();
	}

	public String getRegss() {
		return this.regss;
	}

	public void setRegss(String regss) {
		this.regss = regss.trim();
	}

	public String getIdConductor() {
		return this.IdConductor;
	}

	public void setIdConductor(String IdConductor) throws NullPointerException {
		this.IdConductor = IdConductor != null ? IdConductor.trim() : Conductor.UNKNOWN_DRIVER;
	}

	public String getNumTrjCondu() {
		return this.NumTrjCondu;
	}

	public void setNumTrjCondu(String NumTrjCondu) {
		this.NumTrjCondu = NumTrjCondu.trim();
	}

	public Date getExpiredDateNumTrjCondu() {
		return this.ExpiredDateNumTrjCondu;
	}

	public void setExpiredDateNumTrjCondu(Date ExpiredDateNumTrjCondu) {
		this.ExpiredDateNumTrjCondu = ExpiredDateNumTrjCondu;
	}

	public void setEquipmentType(int equipment) {
		this.equipment = equipment;
	}

	public void setNationNumeric(int nationNumeric) {
		this.nationNumeric = nationNumeric;
	}

	public String getNombre() {
		return this.Nombre;
	}

	public void setNombre(String Nombre) {
		this.Nombre = Nombre.trim();
	}

	public String getApellidos() {
		return this.Apellidos;
	}

	public void setApellidos(String Apellidos) {
		this.Apellidos = Apellidos.trim();
	}

	public List<Object> getActividades() {
		return this.actividades;
	}

	public void setActividades(List<Object> actividades) {
		if (actividades != null) {
			this.actividades = actividades;
		}
	}

	// public void addActividades(Object actividad) {
	// if (this.actividades == null) {
	// this.actividades = new ArrayList<Object>();
	// }
	// if (actividad instanceof List) {
	// for (int i = 0, k = ((List<Object>) actividad).size(); i < k; i++) {
	// this.actividades.add(((List<Object>) actividad).get(i));
	// }
	// } else {
	// this.actividades.add(actividad);
	// }
	// }

	public List<Object> getUsoVehiculo() {
		return this.usoVehiculo;
	}

	public void setUsoVehiculo(List<Object> usoVehiculo) {
		if (usoVehiculo != null) {
			this.usoVehiculo = usoVehiculo;
		}
	}

	public void addUsoVehiculo(Object usoVehiculo) {
		if (usoVehiculo == null) {
			usoVehiculo = new ArrayList<Object>();
		}
		this.usoVehiculo.add(usoVehiculo);
	}

	public List<Object> getPeriodoTrabajo() {
		return this.periodosTrabajo;
	}

	public void setPeriodoTrabajo(List<Object> periodosTrabajo) {
		if (periodosTrabajo != null) {
			this.periodosTrabajo = periodosTrabajo;
		}
	}

	public void addPeriodoTrabajo(Object periodoTrabajo) {
		if (this.periodosTrabajo == null) {
			this.periodosTrabajo = new Vector<Object>();
		}
		this.periodosTrabajo.add(periodoTrabajo);
	}


}
