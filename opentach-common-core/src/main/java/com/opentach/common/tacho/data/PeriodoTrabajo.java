package com.opentach.common.tacho.data;

import java.util.Date;

public class PeriodoTrabajo extends AbstractData implements Comparable<PeriodoTrabajo> {

	public Integer	tpPeriodo	= null;
	public Date		fecIni		= null;
	public String	pais		= null;
	public String	region		= null;
	public String	procedencia	= null;
	public String	idConductor	= Conductor.UNKNOWN_DRIVER;
	public Integer	km			= null;

	public Integer getTpPeriodo() {
		return this.tpPeriodo;
	}

	public void setTpPeriodo(Integer tpPeriodo) {
		this.tpPeriodo = tpPeriodo;
	}

	public String getIdConductor() {
		return this.idConductor;
	}

	public void setIdConductor(String idConductor) {
		this.idConductor = idConductor != null ? idConductor.trim() : Conductor.UNKNOWN_DRIVER;
	}

	public void setKm(Integer km) {
		this.km = km;
	}

	public Integer getKm() {
		return this.km;
	}

	public Date getFecIni() {
		return this.fecIni;
	}

	public void setFecIni(Date fecIni) {
		this.fecIni = fecIni;
	}

	public String getPais() {
		return this.pais;
	}

	public void setPais(String pais) {
		this.pais = pais.trim();
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region.trim();
	}

	public String getProcedencia() {
		return this.procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia.trim();
	}

	@Override
	public int compareTo(PeriodoTrabajo o) {
		return this.fecIni.compareTo(o.fecIni);
	}

}
