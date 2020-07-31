package com.opentach.common.tacho.data;

import java.util.Date;

public class Calibrado extends AbstractData {

	public Integer	tpCalibrado		= null;
	public String	numTrjTaller	= null;
	public String	nombTaller		= null;
	public Date		fecProximo		= null;
	public String	matricula		= null;
	public int		constante;
	public int		coeficiente;
	public int		TyreCircumference;

	public Calibrado() {}

	public int getConstante() {
		return this.constante;
	}

	public void setConstante(int constante) {
		this.constante = constante;
	}

	public int getTyreCircumference() {
		return this.TyreCircumference;
	}

	public void setTyreCircumference(int TyreCircumference) {
		this.TyreCircumference = TyreCircumference;
	}

	public int getCoeficiente() {
		return this.coeficiente;
	}

	public void setCoeficiente(int coeficiente) {
		this.coeficiente = coeficiente;
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public Date getFecProximo() {
		return this.fecProximo;
	}

	public void setFecProximo(Date fecProximo) {
		this.fecProximo = fecProximo;
	}

	public Integer getTpCalibrado() {
		return this.tpCalibrado;
	}

	public void setTpCalibrado(Integer tpCalibrado) {
		this.tpCalibrado = tpCalibrado;
	}

	public String getNumTrjTaller() {
		return this.numTrjTaller;
	}

	public void setNumTrjTaller(String numTrjTaller) {
		this.numTrjTaller = numTrjTaller.trim();
	}

}
