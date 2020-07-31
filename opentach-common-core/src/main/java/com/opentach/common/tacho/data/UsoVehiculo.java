package com.opentach.common.tacho.data;

import java.util.Date;

public class UsoVehiculo extends AbstractData implements Comparable<UsoVehiculo> {

	public Date			fechaHoraIni		= null;
	public Date			fechaHoraFin		= null;
	public Integer		KmIni				= null;
	public Integer		KmFin				= null;
	public String		procedencia			= null;
	public String		numTrjConductor		= null;
	public String		idConductor			= null;


	public String		nombre				= null;
	public String		apellidos			= null;


	public String		matricula			= AbstractData.UNKNOWN_VEHICLE_NUMBER;
	public Integer		numDisco			= null;
	public String		origen				= null;
	public String		destino				= null;
	public String		ranura				= null;

	public String getRanura() {
		return this.ranura;
	}

	public void setRanura(String ranura) {
		this.ranura = ranura;
	}

	public String getOrigen() {
		return this.origen;
	}

	public void setOrigen(String origen) {
		if (origen == null) {
			this.origen = null;
		} else {
			this.origen = origen.trim();
		}
	}

	public String getDestino() {
		return this.destino;
	}

	public void setDestino(String destino) {
		this.destino = destino.trim();
	}

	public UsoVehiculo() {
	}

	public Integer getNumDisco() {
		return this.numDisco;
	}

	public void setNumDisco(Integer numDisco) {
		this.numDisco = numDisco;
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor != null ? idConductor.trim() : Conductor.UNKNOWN_DRIVER;

	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public Date getFechaHoraIni() {
		return this.fechaHoraIni;
	}

	public void setFechaHoraIni(Date fechaHoraIni) {
		this.fechaHoraIni = fechaHoraIni;
	}

	public Date getFechaHoraFin() {
		return this.fechaHoraFin;
	}

	public void setFechaHoraFin(Date fechaHoraFin) {
		this.fechaHoraFin = fechaHoraFin;
	}

	public Integer getKmIni() {
		return this.KmIni;
	}

	public void setKmIni(Integer KmIni) {
		this.KmIni = KmIni;
	}

	public Integer getKmFin() {
		return this.KmFin;
	}

	public void setKmFin(Integer KmFin) {
		this.KmFin = KmFin;
	}

	public String getProcedencia() {
		return this.procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia.trim();
	}

	public String getNumTrjConductor() {
		return this.numTrjConductor;
	}

	public void setNumTrjConductor(String numTrjConductor) {
		this.numTrjConductor = numTrjConductor;
	}


	@Override
	public int compareTo(UsoVehiculo o) {
		return this.fechaHoraIni.compareTo(o.fechaHoraIni);
	}

}
