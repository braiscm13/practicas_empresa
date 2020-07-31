package com.opentach.common.tacho.data;

import java.util.Vector;

public class VehiculoInsp extends AbstractData {

	public String	matricula			= null;
	public String	estadoMiembro	= null;
	public Vector<Object>	usoVehiculo		= null;
	public Vector<Object>	fallos				= null;
	public Vector<Object>	calibrado			= null;
	public Vector<Object>	controles			= null;

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public String getEstadoMiembro() {
		return this.estadoMiembro;
	}

	public void setEstadoMiembro(String estadoMiembro) {
		this.estadoMiembro = estadoMiembro.trim();
	}

	public void setUsoVehiculo(Vector<Object> v) {
		this.usoVehiculo = v;
	}

	public Vector<Object> getUsoVehiculo() {
		return this.usoVehiculo;
	}

	public void addUsoVehiculo(Object a) {
		if (this.usoVehiculo == null) {
			this.usoVehiculo = new Vector<Object>();
		}
		this.usoVehiculo.add(a);
	}

	public void setFallo(Vector<Object> v) {
		this.fallos = v;
	}

	public Vector<Object> getFallo() {
		return this.fallos;
	}

	public void addFallo(Object a) {
		if (this.fallos == null) {
			this.fallos = new Vector<Object>();
		}
		this.fallos.add(a);
	}

	public void setCalibrado(Vector<Object> v) {
		this.usoVehiculo = v;
	}

	public Vector<Object> getCalibrado() {
		return this.calibrado;
	}

	public void addCalibrado(Object a) {
		if (this.calibrado == null) {
			this.calibrado = new Vector<Object>();
		}
		this.calibrado.add(a);
	}

	public void setControles(Vector<Object> v) {
		this.controles = v;
	}

	public Vector<Object> getControles() {
		return this.controles;
	}

	public void addControl(Object a) {
		if (this.controles == null) {
			this.controles = new Vector<Object>();
		}
		this.controles.add(a);
	}

}
