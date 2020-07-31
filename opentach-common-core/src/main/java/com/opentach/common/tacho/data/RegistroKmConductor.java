package com.opentach.common.tacho.data;

import java.util.Date;

public class RegistroKmConductor extends AbstractData implements Comparable<RegistroKmConductor> {
	public String		idConductor;
	public String		numTrjConductor;
	public Date			fecha;
	public Integer	kilometrosRecorridos;

	@Override
	public int compareTo(RegistroKmConductor o) {
		return this.fecha.compareTo(o.fecha);
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor;
	}

	public String getNumTrjConductor() {
		return this.numTrjConductor;
	}

	public void setNumTrjConductor(String numTrjConductor) {
		this.numTrjConductor = numTrjConductor;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Integer getKilometrosRecorridos() {
		return this.kilometrosRecorridos;
	}

	public void setKilometrosRecorridos(Integer kilometrosRecorridos) {
		this.kilometrosRecorridos = kilometrosRecorridos;
	}

}
