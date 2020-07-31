package com.opentach.common.tacho.data;

import java.util.Date;

public class Control extends AbstractData implements Comparable<Control> {
	public Integer	tpControl		= null;
	public Date		fechaHora		= null;
	public String	numTrjControl	= null;
	public String	matricula		= null;


	public Control() {
		super();
	}

	public String getMatricula() {
		return this.matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = this.getCleanVehicleNumber(matricula);
	}

	public Integer getTpControl() {
		return this.tpControl;
	}

	public void setTpControl(Integer tpControl) {
		this.tpControl = tpControl;
	}

	public Date getFechaHora() {
		return this.fechaHora;
	}

	public void setFechaHora(Date fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getNumTrjControl() {
		return this.numTrjControl;
	}

	public void setNumTrjControl(String numTrjControl) {
		this.numTrjControl = numTrjControl.trim();
	}

	@Override
	public int compareTo(Control o) {
		return this.fechaHora.compareTo(o.fechaHora);
	}
}
