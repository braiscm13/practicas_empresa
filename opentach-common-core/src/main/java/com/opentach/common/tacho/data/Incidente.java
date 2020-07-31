package com.opentach.common.tacho.data;

import java.util.Date;

public class Incidente extends AbstractData implements Comparable<Incidente> {

	public Integer	tipo							= null;
	public Integer	proposito					= null;
	public Date			fechHoraIni				= null;
	public Date			fechHoraFin				= null;
	public Integer	numInciSimilares	= null;
	public Integer	velocidadMax			= null;
	public Integer	velocidadMedia		= null;
	public String		numTrjConduIni		= null;
	public String		numTrjConduFin		= null;
	public String		numTrjCopiIni			= null;
	public String		numTrjCopiFin			= null;
	public String		matricula					= null;
	public String		procedencia				= null;
	public String		pais							= null;
	public String		observaciones			= null;

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones.trim();
		if (this.observaciones.length() > 255) {
			this.observaciones = this.observaciones.substring(0, 255);
		}
	}

	public String getObservaciones() {
		return this.observaciones;
	}

	public void setPais(String pais) {
		this.pais = pais.trim();
	}

	public String getPais() {
		return this.pais;
	}

	public Integer getTipo() {
		return this.tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Date getFechHoraIni() {
		return this.fechHoraIni;
	}

	public void setFechHoraIni(Date fechHoraIni) {
		this.fechHoraIni = fechHoraIni;
	}

	public Date getFechHoraFin() {
		return this.fechHoraFin;
	}

	public void setFechHoraFin(Date fechHoraFin) {
		this.fechHoraFin = fechHoraFin;
	}

	public Integer getNumInciSimilares() {
		return this.numInciSimilares;
	}

	public void setNumInciSimilares(Integer numInciSimilares) {
		this.numInciSimilares = numInciSimilares;
	}

	public Integer getVelocidadMax() {
		return this.velocidadMax;
	}

	public void setVelocidadMax(Integer velocidadMax) {
		this.velocidadMax = velocidadMax;
	}

	public Integer getVelocidadMedia() {
		return this.velocidadMedia;
	}

	public void setVelocidadMedia(Integer velocidadMedia) {
		this.velocidadMedia = velocidadMedia;
	}

	public String getNumTrjConduIni() {
		return this.numTrjConduIni;
	}

	public void setNumTrjConduIni(String numTrjConduIni) {
		this.numTrjConduIni = numTrjConduIni.trim();
	}

	public String getNumTrjConduFin() {
		return this.numTrjConduFin;
	}

	public void setNumTrjConduFin(String numTrjConduFin) {
		this.numTrjConduFin = numTrjConduFin.trim();
	}

	public String getNumTrjCopiIni() {
		return this.numTrjCopiIni;
	}

	public void setNumTrjCopiIni(String numTrjCopiIni) {
		this.numTrjCopiIni = numTrjCopiIni.trim();
	}

	public String getNumTrjCopiFin() {
		return this.numTrjCopiFin;
	}

	public void setNumTrjCopiFin(String numTrjCopiFin) {
		this.numTrjCopiFin = numTrjCopiFin.trim();
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		// if (matricula != null) {
		this.matricula = this.getCleanVehicleNumber(matricula);
		// }
		// // else
		// this.matricula = matricula;
	}

	public String getProcedencia() {
		return this.procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia;
	}

	public Integer getProposito() {
		return this.proposito;
	}

	public void setProposito(Integer proposito) {
		this.proposito = proposito;
	}

	@Override
	public int compareTo(Incidente o) {
		return this.fechHoraIni.compareTo(o.fechHoraIni);
	}


}
